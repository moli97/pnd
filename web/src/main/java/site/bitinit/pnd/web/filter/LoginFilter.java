package site.bitinit.pnd.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import site.bitinit.pnd.web.config.StorageHandler;
import site.bitinit.pnd.web.controller.dto.ResponseDto;
import site.bitinit.pnd.web.exception.UnauthorizedException;
import site.bitinit.pnd.web.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@WebFilter(value = "LoginFilter", urlPatterns = { "/*" })
public class LoginFilter implements Filter {

	@Autowired
	StorageHandler storageHandler;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		String accessToken = httpRequest.getHeader("accessToken");
		if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(storageHandler.get(accessToken))) {
			storageHandler.clearInvalid();
			log.error(accessToken);
			//throw new UnauthorizedException("无访问权限");
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@ExceptionHandler
	public ResponseEntity<ResponseDto> unauthorizedException(UnauthorizedException e) {
		return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body(ResponseDto.fail(e.getMessage()));
	}
}
