package net.pusuo.cms.web.filter;

import net.pusuo.cms.web.result.UserEnum;
import net.pusuo.cms.web.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class CommonFilter extends OncePerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(CommonFilter.class);

	private final UserService userService = new UserService();
	private final String loginPath = "/user/login";
	private final String[] skipPath = {loginPath, "/user/getCaptcha", "/user/loginin"};

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		String currentPath = request.getServletPath();

		//	css,js,img等资源文件的加载不做限制
		if (currentPath.contains(".")) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = request.getSession();
		logger.info("======>>>>>> " + currentPath + "||sessionId: " + session.getId() + "||ctime:" + session.getCreationTime()
				+ "||lastaccessTime:" + session.getLastAccessedTime()
				+ "||MaxInactiveInterval:" + session.getMaxInactiveInterval()
				+ "||isNew: " + session.isNew());

		for (String p : skipPath) {
			if (p.equals(currentPath)) {
				chain.doFilter(request, response);
				return;
			}
		}

		if (UserEnum.SUCCESS != userService.checkLoginToken(session)) {
			response.sendRedirect(loginPath);
			return;
		}

		chain.doFilter(request, response);
	}
}