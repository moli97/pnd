package site.bitinit.pnd.web.util;

import com.google.common.base.Predicate;
import site.bitinit.pnd.web.controller.dto.FileDisplayDto;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDisplayUtil {

	public static List<FileDisplayDto> getSubfolders(File file, boolean showAll) {
		return getSubfolders(file, o -> showAll ? true : !o.isHidden());
	}

	public static List<FileDisplayDto> getSubfolders(File file, Predicate<File> predicate) {
		return Stream.of(file.listFiles())
						.filter(predicate)
						.map(FileDisplayDto::new)
						.sorted()
						.collect(Collectors.toList());
	}

	public enum DisplayType {
		DIRECTORY("directory", 1, File::isDirectory),
		FILE("file", 2, File::isFile),
		;

		DisplayType(String type, int priority, Predicate<File> predicate) {
			this.type = type;
			this.priority = priority;
			this.predicate = predicate;
		}

		public String type;
		public int priority; //小的优先级高
		public Predicate<File> predicate;

		public static DisplayType getDisplayType(File file) {
			for (DisplayType type : values()) {
				if (type.predicate.apply(file)) {
					return type;
				}
			}
			throw new RuntimeException("can not find file displayType");
		}
	}

}
