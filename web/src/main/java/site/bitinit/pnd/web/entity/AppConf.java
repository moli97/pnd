package site.bitinit.pnd.web.entity;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Alias("appConf")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppConf {

	public static final String ALIAS = "appConf";
	private Long id;
	private String confKey;
	private String confValue;
	private String descInfo;
}
