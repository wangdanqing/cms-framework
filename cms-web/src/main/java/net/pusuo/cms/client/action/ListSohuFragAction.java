package net.pusuo.cms.client.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionForm;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import com.hexun.cms.util.Util;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Template;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.CommonFrag;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.FragLog;

import com.hexun.cms.auth.Perm;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.Permission;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

public class ListSohuFragAction extends BaseAction
{
	private static final Log log = LogFactory.getLog( ListSohuFragAction.class );
	public ActionForward list(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response )
	{
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try
		{
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch(UnauthenticatedException e) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
		return mapping.findForward("list");
	}

	

}

