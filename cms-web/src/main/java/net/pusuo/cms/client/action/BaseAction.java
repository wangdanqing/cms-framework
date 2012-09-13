package net.pusuo.cms.client.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.MessageResources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.core.EntityItem;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.*;
import com.hexun.cms.client.auth.*;
import com.hexun.cms.client.util.*;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

/**
 * <p>The <code>BaseAction</code> class as the parent class of all action-classes
 * is used to provide the templat <code>perform</code> method in which the
 * the uniform <code>preProcess</code> method,
 * the <code>processs</code> method as default and
 * the <code>postProcess</code> method are be transacted in turn.
 * The child action class of the BaseAction can implement the process method.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: hexun</p>
 * @author: 
 * @version 1.0
 */

public class BaseAction extends DispatchAction {

    private static final Log log = LogFactory.getLog(BaseAction.class);

    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {
        ActionForward preAF = preProcess(mapping,form,request,response);
        ActionForward af = null;
        if (preAF == null)
            af = process(mapping,form,request,response);
        postProcess(mapping,form,request,response);
        return  (preAF == null? af : preAF);
    }

    public ActionForward preProcess(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
    {   

	if (!validatePermissions(request,response)) {
		return mapping.findForward("unauthorized");
	}
        //init...
        return null;
    }

    // forward to the default page
    public ActionForward process(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {
	try {
	    return super.execute(mapping,form,request,response);
	} catch (Exception e) {
	    return mapping.findForward("sysException");
	}
    }

    // close the connection
    public void postProcess(ActionMapping mapping,
		            ActionForm form,
		       	    HttpServletRequest request,
			    HttpServletResponse response) 
    {
        //some close action,such as session
    }

    // check the permissions 
    public boolean validatePermissions(HttpServletRequest request,HttpServletResponse response) {
        boolean validated = false;
        
	String action =  retrievePermission();
	if (null == action) {
		//handle the url request for validate
        	action = request.getServletPath(); 
		action = action.substring(1,action.indexOf(".do"));
	}
	//log.info("action:" + action);

	try {
        	Authentication auth = AuthenticationFactory.getAuthentication(request,response);
        	if (null != auth) {

        		Permission perm = auth.getUserPermission();
			//log.info(perm);
        		if (perm.get(action)) {
        			validated = true;	
        		}
        	}
	} catch(UnauthenticatedException ue) {
		validated = false;	
	}
	//log.info("auth:" + validated);
        return validated;
    }

    public String retrievePermission() {
	return null;
    }

}
