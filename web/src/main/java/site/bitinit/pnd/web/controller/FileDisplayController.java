package site.bitinit.pnd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.bitinit.pnd.web.config.PndProperties;
import site.bitinit.pnd.web.controller.dto.FileDisplayDto;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.util.FileDisplayUtil;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/baobao-admin")
public class FileDisplayController {

	@Autowired
	private PndProperties pndProperties;

	@GetMapping("fileDisplay")
	public ResponseDto getFileList(@RequestParam String path, @RequestParam(value = "showAll", defaultValue = "false") boolean showAll) {
		File parentFile = new File(pndProperties.getDisplayPath() + path);
		if (!parentFile.isDirectory()) {
			throw new RuntimeException("不是文件夹");
		}
		List<FileDisplayDto> subfolders = FileDisplayUtil.getSubfolders(parentFile, showAll);
		subfolders.forEach(o -> o.transform(pndProperties.getDisplayPath(), pndProperties.getDisplayUrl()));
		return ResponseDto.success(subfolders);
	}
}
