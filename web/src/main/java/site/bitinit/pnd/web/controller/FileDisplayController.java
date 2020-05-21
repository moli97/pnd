package site.bitinit.pnd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.bitinit.pnd.web.AppContext;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.config.PndProperties;
import site.bitinit.pnd.web.controller.dto.FileDisplayDto;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.exception.DataFormatException;
import site.bitinit.pnd.web.exception.UnauthorizedException;
import site.bitinit.pnd.web.util.FileDisplayUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(Constants.API_VERSION)
public class FileDisplayController {

	@Autowired
	private PndProperties pndProperties;
	@Autowired
	private AppContext appContext;

	@GetMapping("/fileDisplay")
	public ResponseDto getFileList(@RequestParam String path, @RequestParam(value = "showAll", defaultValue = "false") boolean showAll) {
		String allow = appContext.getConf(Constants.ALLOW_ACCESS_DISPLAY, Constants.ALLOW_ACCESS_DISPLAY_DEFAULT);
		if (!Objects.equals(allow, Boolean.TRUE.toString())) {
			throw new UnauthorizedException("不允许访问");
		}
		File parentFile = new File(pndProperties.getDisplayPath() + path);
		if (!parentFile.isDirectory()) {
			throw new DataFormatException("不是文件夹");
		}
		List<FileDisplayDto> subfolders = FileDisplayUtil.getSubfolders(parentFile, showAll);
		subfolders.forEach(o -> o.transform(pndProperties.getDisplayPath(), pndProperties.getDisplayUrl()));
		return ResponseDto.success(subfolders);
	}
}
