package site.bitinit.pnd.web.controller.dto;

import lombok.Getter;
import lombok.Setter;
import site.bitinit.pnd.web.config.FileType;
import site.bitinit.pnd.web.util.FileDisplayUtil.DisplayType;
import site.bitinit.pnd.web.util.FileUtils;

import java.io.File;

@Setter
@Getter
public class FileDisplayDto implements Comparable<FileDisplayDto> {

	private String name;
	private String path;
	private String url;
	private FileType fileType;
	private DisplayType displayType;

	public FileDisplayDto(File file) {
		this.name = file.getName();
		this.path = file.getPath();
		this.fileType = FileUtils.getFileType(file.getName());
		this.displayType = DisplayType.getDisplayType(file);
	}

	@Override
	public int compareTo(FileDisplayDto o) {
		return displayType.equals(o.displayType) ? name.compareTo(o.name) : displayType.priority - o.displayType.priority;
	}

	public void transform(String rootPath, String baseUrl) {
		this.path = this.path.replace(rootPath, "");
		this.url = baseUrl + this.path;
	}
}
