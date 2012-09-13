package net.pusuo.cms.client.util;

import javax.servlet.http.*;
import java.text.*;
import java.util.*;

/**
 * A collection of utility methods for use in Cms Skins. Because these
 * methods make skin development much easier, skin authors should study them
 * carefully.<p>
 *
 */
public class SkinUtils {

    /**
     * Returns the specified cookie, or <code>null</code> if the cookie
     * does not exist. 
     *
     * @param request the HttpServletRequest object
     * @param name the name of the cookie.
     * @return the Cookie object if it exists, otherwise <code>null</code>.
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie cookies[] = request.getCookies();
        // Return null if there are no cookies or the name is invalid.
        if(cookies == null || name == null || name.length() == 0) {
            return null;
        }
        // Otherwise, we  do a linear scan for the cookie.
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals(name) ) {
                return cookies[i];
            }
        }
        return null;
    }

    /**
     * Deletes the specified cookie.
     *
     * @param response the HttpServletResponse object in a JSP page
     * @param cookie the cookie object to be deleted.
     */
    public static void deleteCookie(HttpServletResponse response, Cookie cookie)
    {
        if (cookie != null) {
            // Invalidate the cookie
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    /**
     * Stores a value in a cookie. This cookie will persist for 30 days.
     *
     * @param response the HttpServletResponse object
     * @param name a name to identify the cookie.
     * @param value the value to store in the cookie.
     */
    public static void setCookie(HttpServletResponse response, String name,
            String value)
    {
        // Save the cookie value for 1 day
        setCookie(response, name, value, 60*60*24*30);
    }

    /**
     * Stores a value in a cookie. This cookie will persist for the amount
     * specified in the <tt>saveTime</tt> parameter.
     *
     * @param response the HttpServletResponse object
     * @param name a name to identify the cookie.
     * @param value the value to store in the cookie.
     * @param maxAge the time (in seconds) this cookie should live.
     */
    public static void setCookie(HttpServletResponse response, String name,
            String value, int maxAge)
    {
        // Check to make sure the new value is not null (appservers like Tomcat
        // 4 blow up if the value is null).
        if (value == null) {
            value = "";
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
		cookie.setDomain(".cms.pusuo.net");
        response.addCookie(cookie);
    }
    
}
