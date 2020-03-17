package site.bitinit.pnd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.exception.UnauthorizedException;
import site.bitinit.pnd.web.storage.RespAgent;
import site.bitinit.pnd.web.storage.StorageHandler;

import java.util.UUID;

@RestController
@RequestMapping(Constants.API_VERSION)
public class TokenController {

	@Autowired
	private StorageHandler storageHandler;

	@PostMapping("/login")
	public ResponseEntity<ResponseDto> login(String userName, String password) {
		if (!"123456".equals(password)) {
			throw new UnauthorizedException();
		}
		String accessToken = UUID.randomUUID().toString();
		storageHandler.put(accessToken, userName);
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
		throw new UnauthorizedException("无访问权限");
	}
}
