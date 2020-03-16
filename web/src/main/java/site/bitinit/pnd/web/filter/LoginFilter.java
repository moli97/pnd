package site.bitinit.pnd.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.bitinit.pnd.web.config.StorageHandler;

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
		/**if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(storageHandler.get(accessToken))) {
		 storageHandler.clearInvalid();
		 log.error(accessToken);
		 HttpServletResponse response = (HttpServletResponse) servletResponse;
		 response.setStatus(HttpStatus.UNAUTHORIZED.value());
		 ResponseUtil.out(response, ResponseUtil.resultMap("无访问权限"));
		 return;
		 }*/
		servletRequest.getRequestDispatcher("/v1/login").forward(servletRequest, servletResponse);
		//filterChain.doFilter(servletRequest, servletResponse);
	}
}
