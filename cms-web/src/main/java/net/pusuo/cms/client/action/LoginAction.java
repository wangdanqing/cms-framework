package net.pusuo.cms.client.action;


import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.auth.*;
import com.hexun.cms.client.util.*;
import com.hexun.cms.client.auth.exception.*;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author jeff
 */

public final class LoginAction extends Action {


    private static final Log log = LogFactory.getLog(LoginAction.class);

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception Exception if error occurs
     */
    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {

	ActionErrors errors = new ActionErrors();
	
	// Acqurie the request parameters specified by the user
	String username = (String)((BaseForm) form).get("username");
	String password = (String)((BaseForm) form).get("password");
        
	// check for the existence of an authorization token
        String nextPage = "default";
	boolean autoLogin = true;

        try {
            Authentication authToken = AuthenticationFactory.getAuthentication(username, password);
	    request.getSession().setAttribute(AuthenticationFactory.SESSION_AUTHENTICATION, authToken);
	    //log.info(" ==== Put the valid token in the session ==== ");                 
	    
	    if (autoLogin) {                    
		SkinUtils.setCookie(response,AuthenticationFactory.COOKIE_AUTOLOGIN, AuthenticationFactory.encodePasswordCookie(username, password));
		//log.info(" ==== Optionally set a cookie with the user's username and password === ");               
	    }
        } catch (UnauthenticatedException ue) {
            errors.add("errors.login", new ActionError("errors.login"));
        }
        
    	// Forward control to the specified URI
	if (!errors.isEmpty()) {
        	saveErrors(request, errors);
                nextPage = "failure";
        } else {
		nextPage = "success";
	}
	return (mapping.findForward(nextPage));

    }
}
