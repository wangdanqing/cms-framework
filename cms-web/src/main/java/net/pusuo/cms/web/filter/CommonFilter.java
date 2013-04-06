package net.pusuo.cms.web.filter;

import javax.servlet.*;
import java.io.IOException;


public class CommonFilter implements Filter {
    private final static String USER_SRC_COOKIE_DOMAIN = ".pusuo.net";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

//    private Cookie getSrcCookie(HttpServletRequest request) {
//        String value = ToolKit.getCookie(request, USER_SRC_COOKIE_KEY);
//        String age = ToolKit.getCookie(request, USER_SRC_COOKIE_AGE_KEY);
//        if (value == null) return null;
//        int a = 0;
//        if(age !=null){
//            try {
//                a = Integer.parseInt(age);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        Cookie cookie = new Cookie(USER_SRC_COOKIE_KEY, value);
//        cookie.setMaxAge(a);
//        return cookie;
//    }


}