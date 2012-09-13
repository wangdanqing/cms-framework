/*
 * Created on 2005-9-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.util;

import javax.servlet.http.Cookie;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CookieUtil {

	/**
	 * get cookie value
	 * @param cookies
	 * @param cookieName
	 * @param defaultValue
	 * @return
	 */
    public static String getCookieValue(Cookie[] cookies, String cookieName, String defaultValue)
    {
        if(cookies == null)
            return defaultValue;

        Cookie cookie;
        for(int i = 0; i < cookies.length; i++)
        {
            cookie = cookies[i];
            if(cookieName.equals(cookie.getName()))
                return cookie.getValue();
        }
        return defaultValue;
    }
    
    public static String encode(String text)
    {
        return encode(text, "UTF-8");
    }
    
    public static String encode(String text, String encoding)
    {
        String ret = null;
        
        if (text == null || encoding == null)
            return null;
        
        try
        {
            ret = URLEncoder.encode(text, encoding);
        }
        catch (Exception e)
        {
        }
        
        return ret;        
    }
    
    public static String decode(String text)
    {
        return decode(text, "UTF-8");
    }
    
    public static String decode(String text, String encoding)
    {
        String ret = null;
        
        if (text == null || encoding == null)
            return null;
        
        try
        {
            ret = URLDecoder.decode(text, encoding);
        }
        catch (Exception e)
        {
        }
        
        return ret;
    }
    
}
