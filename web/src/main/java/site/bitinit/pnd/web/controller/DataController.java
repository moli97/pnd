package site.bitinit.pnd.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.controller.dto.ResponseDto;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(Constants.API_VERSION)
public class DataController {

	private String localPath;

	@PostMapping(value = "/upload")
	public ResponseEntity<ResponseDto> upload(@RequestParam(value = "files", required = false) MultipartFile[] files) {

		if (files == null || files.length == 0) {
			log.error("文件空");
			return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fail("文件为空"));
		}

		Map<String, List<String>> res = new HashMap<>();
		List<String> succ = new ArrayList<>();
		List<String> error = new ArrayList<>();
		res.put("succ", succ);
		res.put("error", error);
		for (MultipartFile file : files) {
			String filename = file.getOriginalFilename();
			try {
				LocalDate now = LocalDate.now();
				String path = localPath + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/" + filename;
				File file0 = new File(path);
				if (!file0.exists()) {
					file0.mkdirs();
					log.error("创建文件夹路径：{}", path);
				}
				file.transferTo(file0);
				succ.add(filename);
				log.error("上传成功：{}", filename);
			} catch (Exception e) {
				error.add(filename + ":" + e.getMessage());
				log.error("异常：{}", e.getMessage());
				continue;
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success(res));
	}

}
