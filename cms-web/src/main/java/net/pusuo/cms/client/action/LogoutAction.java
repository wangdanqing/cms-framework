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

public final class LogoutAction extends Action {


    private static final Log log = LogFactory.getLog(LogoutAction.class);

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
     * @exception Exception
     */
    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {
	ActionErrors errors = new ActionErrors();
	
	// check for the existence of an authorization token
        String nextPage = "default";

        try {
	    request.getSession().removeAttribute(AuthenticationFactory.SESSION_AUTHENTICATION);
	    log.info(" ==== clear the session === ");               
	    
	    Cookie cookie = SkinUtils.getCookie(request,AuthenticationFactory.COOKIE_AUTOLOGIN);
	    if (null != cookie) {
 	    	SkinUtils.deleteCookie(response,cookie);
		log.info(" ==== clear the cookie ==== ");
	    }
                
            nextPage = "success";
        } catch (Exception e) {
            errors.add("errors.logout", new ActionError("logout failed!"));
            nextPage = "failure";
        }
        
    	// Forward control to the specified success URI
	return (mapping.findForward(nextPage));

    }
}
