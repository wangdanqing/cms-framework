package net.pusuo.cms.client.auth;

import javax.servlet.http.*;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.util.SkinUtils;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

/**
 * �ṩ��֤����ĳ����ࣺ
 * ʵ�ִ�Session/Cookie����ȡ��֤��Ϣ�ķ�����
 * ʵ�ָ��username/password����ݿ����ȡ��֤��Ϣ�ķ����� 
 *
 * @author Fei Gao
 */
public abstract class AuthenticationFactory {

    private static final Log LOG = LogFactory.getLog(AuthenticationFactory.class);

    public static final String SESSION_AUTHENTICATION = "cms.authentication";

    public static final String COOKIE_AUTOLOGIN = "cms.authentication.autologin";

    private static String className =
        "com.hexun.cms.client.auth.DbAuthenticationFactory";

    private static AuthenticationFactory factory = null;
    
    /**
     * Builds a cookie string containing a username and password.<p>
     *
     *
     * @param username The username.
     * @param password The password.
     * @return String encoding the input parameters, an empty string if one of
     *      the arguments equals <code>null</code>.
     */
    public static String encodePasswordCookie (String username, String password)
    {
	if (username == null || password == null) {
            throw new NullPointerException("Username or password was null.");
        }
        return (username + "::" + password);
    }

    /**
     * Unrafels a cookie string containing a username and password.
     * @param value The cookie value.
     * @return String[] containing the username at index 0 and the password at
     *      index 1, or <code>{ null, null }</code> if cookieVal equals
     *      <code>null</code> or the empty string.
     */
     public static String[] decodePasswordCookie( String cookieVal ) {

	// check that the cookie value isn't null or zero-length
	if( cookieVal == null || cookieVal.length() <= 0 ) {
	    return null;
	}
	
	StringTokenizer st = new StringTokenizer(cookieVal,"::");
	String username = st.nextToken().trim();
	String password = st.nextToken().trim();

	return new String[] { username, password};
    }

    /**
     * Returns the Authentication token associated with the specified username
     * and password. If the username and password do not match the record of
     * any user in the system, the method throws an UnauthenticatedException.<p>
     *
     * @param username the username to create an Authentication with.
     * @param password the password to create an Authentication with.
     * @return an Authentication token if the username and password are correct.
     * @throws UnauthenticatedException if the username and password do not match
     *         any existing user.
     */
    public static Authentication getAuthentication(String username,String password) throws UnauthenticatedException
    {
        loadAuthenticationFactory();
        return factory.createAuthentication(username, password);
    }

    /**
     * Returns the Authentication token associated with the information in a
     * session object. If no authentication information is found, the method 
     * throws an UnauthenticatedException.
     *
     * @param request an HttpServletRequest object.
     * @param request an HttpServletResponse object.
     * @throws UnauthenticatedException if no authentication information is found.
     */
    public static Authentication getAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws UnauthenticatedException
    {
        loadAuthenticationFactory();
        return factory.createAuthentication(request, response);
    }

    /**
     * Creates Authentication tokens for users. This method must be implemented
     * by concrete subclasses of AuthenticationFactory.
     *
     * @param username the username to create an Authentication with.
     * @param password the password to create an Authentication with.
     * @return an Authentication token if the username and password are correct.
     * @throws UnauthenticatedException if the username and password do not match
     *         any existing user.
     */
    protected abstract Authentication createAuthentication(String username,String password) throws UnauthenticatedException;

    /**
     * Creates Authentication tokens based on information from servlet request
     * and response objects. This method is <b>optionally</b> implemented by
     * concrete subclasses of AuthenticationFactory.<p>
     *
     * @param request an HttpServletRequest object.
     * @param request an HttpServletResponse object.
     * @throws UnauthenticatedException if no authentication information is found.
     */
    protected Authentication createAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws UnauthenticatedException
    {
        HttpSession session = request.getSession();

        // Check 1: look for the CMS authentication token in the user's session.
        Authentication authToken = (Authentication)session.getAttribute(
                SESSION_AUTHENTICATION);
        if (authToken != null) {
        	if(LOG.isDebugEnabled()){
        		LOG.debug("==== get from session ====");
        	}
            return authToken;
        }

        // Check 2: see if a cookie storing the username and password is there.
        Cookie cookie = SkinUtils.getCookie(request, COOKIE_AUTOLOGIN);
        if (cookie != null) {
            try {
                // We found a cookie, so get the username and password from it,
                // create an Authentication token, then store it in the session.
                String [] authInfo = decodePasswordCookie(cookie.getValue());
                if (authInfo != null) {
                    String username = authInfo[0];
                    String password = authInfo[1];
                    // Try to validate the user based on the info from the cookie.
                    authToken = getAuthentication(username, password);
		    if (authToken != null) {
                    	session.setAttribute(SESSION_AUTHENTICATION, authToken);
			LOG.info("===== get from cookie ====");
                    	return authToken;
		    }
                }
                else {
                    // We must have found an old cookie format, so delete it.
                    SkinUtils.deleteCookie(response, cookie);
                }
            }
            catch (UnauthenticatedException ue) {
                // Remove the authentication cookie as the exception indicates
                // the username and/or password are no longer valid
                SkinUtils.deleteCookie(response, cookie);
                throw ue;
            }
        }
        throw new UnauthenticatedException();
    }

    /**
     * Loads a concrete AuthenticationFactory that can be used generate
     * Authentication tokens for authorized users.<p>
     *
     */
    private static void loadAuthenticationFactory() {
        if (factory == null) {
            //Use className as a convenient object to get a lock on.
            synchronized(className) {
                if (factory == null) {
                    try {
                        Class c = Class.forName(className);
                        factory = (AuthenticationFactory)c.newInstance();
                    }
                    catch (Exception e) {
                        System.err.println("Exception loading class: " + e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

