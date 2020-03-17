package site.bitinit.pnd.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.bitinit.pnd.web.Constants;
import site.bitinit.pnd.web.storage.RespAgent;
import site.bitinit.pnd.web.storage.StorageHandler;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@WebFilter(value = "LoginFilter", urlPatterns = { "/*" })
public class LoginFilter implements Filter {

	@Autowired
	StorageHandler storageHandler;
	private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Constants.API_VERSION + "/login")));

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
		if (!ALLOWED_PATHS.contains(path)) {
			String accessToken = request.getHeader(Constants.ACCESS_TOKEN);
			if (StringUtils.isBlank(storageHandler.get(accessToken))) {
				storageHandler.clearInvalid();
				servletRequest.getRequestDispatcher(Constants.API_VERSION + "/error401").forward(servletRequest, servletResponse);
				return;
			}
			RespAgent.get().withAccessToken(accessToken);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
