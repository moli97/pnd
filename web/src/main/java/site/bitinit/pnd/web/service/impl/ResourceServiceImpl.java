package site.bitinit.pnd.web.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import site.bitinit.pnd.common.ResponseEntity;
import site.bitinit.pnd.common.exception.IllegalDataException;
import site.bitinit.pnd.common.exception.PndException;
import site.bitinit.pnd.common.util.Assert;
import site.bitinit.pnd.common.util.CommonUtils;
import site.bitinit.pnd.common.util.ResponseUtils;
import site.bitinit.pnd.web.config.Properties;
import site.bitinit.pnd.web.config.SystemConstants;
import site.bitinit.pnd.web.controller.dto.ResourceConfigDto;
import site.bitinit.pnd.web.controller.dto.ResourceUploadResponseDto;
import site.bitinit.pnd.web.dao.ResourceDao;
import site.bitinit.pnd.web.model.PndFile;
import site.bitinit.pnd.web.model.PndResource;
import site.bitinit.pnd.web.model.PndResourceState;
import site.bitinit.pnd.web.service.*;
import site.bitinit.pnd.web.utils.PathUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: john
 * @date: 2019/4/19
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private Properties properties;
    @Autowired
    private ResourceStateCacheService cacheService;
    @Autowired
    private PersistResourceService persistResourceService;
    @Autowired
    private PathUtils pathUtils;

    @Override
    public ResourceConfigDto getConfig() {
        ResourceConfigDto configDto = new ResourceConfigDto();
        configDto.setMaxConcurrentUploadNumbers(properties.getMaxConcurrentUploadNumbers());
        configDto.setChunkByteSize(properties.getChunkByteSize());
        configDto.setClientId(CommonUtils.uuid());
        return configDto;
    }

    @Override
    public Map<String, Object> resourceExists(String fingerPrint) {
        Assert.notEmpty(fingerPrint, "fileFingerPrint can't be empty");
        Map<String, Object> result = new HashMap<>(2);
        PndResource resource = resourceDao.findByFingerPrint(fingerPrint, SystemConstants.ResourceState.succeeded);
        if (Objects.isNull(resource)){
            result.put("exist", false);
        } else {
            result.put("exist", true);
            result.put("resourceId", resource.getId());
        }
        return result;
    }

    @Override
    public Map<String, Object> prepareFileUpload(String clientId, String md5, Long size, Long parentId, String fileName) {
        Assert.notEmpty(md5, "文件md5不能为空");
        Assert.notEmpty(clientId, "客户端id不能为空");
        Assert.notNull(size, "size不能为空");
        Assert.notEmpty(fileName, "文件名不能为空");

        PndResource resource = new PndResource();
        resource.setMd5(md5);
        resource.setStatus(SystemConstants.ResourceState.pending.toString());
        resource.setUuid(CommonUtils.uuid()+ CommonUtils.extractFileExtensionName(fileName));
        long currentTime = System.currentTimeMillis();
        resource.setGmtCreate(currentTime);
        resource.setGmtModified(currentTime);
        resource.setLink(0);
        resource.setPath(pathUtils.getResourceSubfolder());
        resource.setSize(size);

        Long id = transactionTemplate.execute(transactionStatus -> {
            long resourceId = resourceDao.save(resource);
            resource.setId(resourceId);

            File pathFile = new File(pathUtils.getResourceAbsolutionPath(resource.getPath()));
            File file = new File(pathFile, resource.getUuid());
            try {
                if (!pathFile.exists()){
                    pathFile.mkdirs();
                }
                file.createNewFile();
                PndResourceState.PndResourceStateBuilder builder = PndResourceState.builder();
                builder.fileName(fileName)
                        .parentId(parentId)
                        .file(file)
                        .pndResource(resource);

                cacheService.addResource(clientId, resourceId, builder);
            } catch (IOException e) {
                logger.error("create " + fileName + " error");
                throw new PndException();
            }
            return resourceId;
        });

        Map<String, Object> result =new HashMap<>(1);
        result.put("resourceId", id);
        return result;
    }

    @Override
    public void updateResourceState(long resourceId, SystemConstants.ResourceState resourceState) {
        resourceDao.updateState(resourceId, resourceState);
    }

    @Override
    public void fileUpload(String clientId, Long resourceId
            , InputStream is, HttpServletRequest request) {
        PndResourceState state = cacheService.getResourceState(clientId, resourceId);

        final AsyncContext context = request.startAsync();
        context.addListener(new UploadAsyncListener(state));
        final ResourceProcessCallback processCallback = new ResourceProcessCallback() {
            @Override
            public void onStart(PndResourceState state) {
            }

            @Override
            public void onComplete(PndResourceState state) {
                context.complete();
            }

            @Override
            public void onSuccess(PndResourceState state) {
                //TODO 添加事务支持
                PndFile file = new PndFile();
                file.setParentId(state.getParentId());
                file.setType(SystemConstants.getFileType(state.getFileName()).toString());
                file.setResourceId(state.getId());
                file.setName(state.getFileName());
                fileService.createFile(file);
                updateResourceState(state.getId(), SystemConstants.ResourceState.succeeded);
                cacheService.deleteResource(clientId, state.getId());
                context.complete();
            }

            @Override
            public void onError(Exception e) throws Exception {
                // TODO 处理错误
                context.complete();
            }

        };

        persistResourceService.process(state, is, processCallback);
    }

    @Override
    public void changeResourceState(String clientId, Long resourceId, String type) {
        Assert.notEmpty(clientId, "客户端id不能为空");
        Assert.notNull(resourceId);
        if (!PndResourceState.PAUSE.equals(type) && !PndResourceState.RESUME.equals(type)){
            throw new IllegalDataException("type must be pause or resume");
        }

        PndResourceState state = cacheService.getResourceState(clientId, resourceId);
        if (state == null){
            throw new IllegalDataException("没有 resourceId=" + resourceId + "的文件正在上传");
        }
        if (PndResourceState.PAUSE.equals(type)){
            state.setPaused(true);
        } else {
            state.setPaused(false);
        }
    }

    @Override
    public Resource loadResource(Long resourceId) {
        Assert.notNull(resourceId, "资源id不能为空");

        PndResource pndResource = resourceDao.findById(resourceId);
        if (Objects.isNull(pndResource)){
            throw new IllegalDataException("没有该文件");
        }

        try {
            Path filePath = new File(pathUtils.getResourceAbsolutionPath(pndResource.getPath() + File.separator + pndResource.getUuid())).toPath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return resource;
            } else {
                throw new IllegalDataException("没有该文件");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalDataException("没有该文件");
        }
    }

    class UploadAsyncListener implements AsyncListener{

        private PndResourceState state;

        public UploadAsyncListener(PndResourceState state) {
            this.state = state;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            ResponseEntity entity;
            if (state.isPaused()){
                entity = ResponseUtils.ok(ResourceUploadResponseDto.paused(state.getFinishedUploadBytes()));
            } else {
                entity = ResponseUtils.ok(ResourceUploadResponseDto.success(state.getFinishedUploadBytes()));
            }

            ServletResponse response = event.getSuppliedResponse();
            response(entity, response);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }

        private void response(ResponseEntity entity, ServletResponse response) throws IOException {
            response.setContentType("application/json; charset=utf8");
            ServletOutputStream outputStream = response.getOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(outputStream, entity);
            outputStream.flush();
        }
    }
}