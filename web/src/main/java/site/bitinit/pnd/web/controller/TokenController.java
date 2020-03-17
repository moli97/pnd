package site.bitinit.pnd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.dao.UserMapper;
import site.bitinit.pnd.web.entity.User;
import site.bitinit.pnd.web.exception.UnauthorizedException;
import site.bitinit.pnd.web.storage.RespAgent;
import site.bitinit.pnd.web.storage.StorageHandler;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constants.API_VERSION)
public class TokenController {

	@Autowired
	private StorageHandler storageHandler;
	@Autowired
	private UserMapper userMapper;

	@PostMapping("/login")
	public ResponseEntity<ResponseDto> login(String username, String password) {
		User user = userMapper.findByName(username);
		if (Objects.isNull(user) || !Objects.equals(user.getPassword(), password)) {
			throw new UnauthorizedException("用户名或密码错误");
		}
		String accessToken = UUID.randomUUID().toString();
		storageHandler.put(accessToken, username);
		return ResponseEntity.ok(ResponseDto.success(accessToken));
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseDto> logout() {
		String accessToken = RespAgent.get().getAccessToken();
		storageHandler.remove(accessToken);
		return ResponseEntity.ok(ResponseDto.success());
	}

	@RequestMapping("/error401")
	public ResponseEntity<ResponseDto> error() {
		throw new UnauthorizedException("登录已失效");
	}
}
