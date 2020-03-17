package site.bitinit.pnd.web.entity;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Alias("user")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

	public static final String ALIAS = "user";
	private Long id;
	private String username;
	private String password;
}
