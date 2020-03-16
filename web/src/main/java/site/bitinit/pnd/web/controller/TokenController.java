package site.bitinit.pnd.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.config.StorageHandler;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.exception.UnauthorizedException;

import java.util.UUID;

@RestController
@RequestMapping(Constants.API_VERSION)
public class TokenController {

	@Autowired
	StorageHandler storageHandler;

	@PostMapping("/login")
	public ResponseEntity<ResponseDto> login(String userName, String password) {
		if (!"123456".equals(password)) {
			throw new UnauthorizedException();
		}
		String token = UUID.randomUUID().toString();
		storageHandler.put(token, userName);
		return ResponseEntity.ok(ResponseDto.success(token));
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseDto> logout() {
		storageHandler.remove("");
		return ResponseEntity.ok(ResponseDto.success("testaaa"));
	}
}
