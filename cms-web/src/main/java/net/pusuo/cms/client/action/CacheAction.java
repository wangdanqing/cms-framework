/*
 * 
 * @author chenqj
 * Created on 2004-9-8
 *
 */
package net.pusuo.cms.client.action;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.cache.exception.CacheException;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

/**
 * @author chenqj
 *
 **/
public class CacheAction extends BaseAction {
	private static final Log LOG = LogFactory.getLog(CacheAction.class);

	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		List entries = null;
		long time = 0;
		try {
			long start = System.currentTimeMillis();
			entries = getList(mapping, form, request, response, "list");
			long end = System.currentTimeMillis();
			time = end - start;
		} catch (Exception e) {
			LOG.error("reset list error" + e);
			return mapping.findForward("failure");
		}

		request.setAttribute("entries", entries);
		request.setAttribute("use_time", new Long(time));
		return mapping.findForward("list");

	}

	public ActionForward reset(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		List entries = null;
		long time = 0;
		try {
			long start = System.currentTimeMillis();
			entries = getList(mapping, form, request, response, "reset");
			long end = System.currentTimeMillis();
			time = end - start;
		} catch (Exception e) {
			LOG.error("reset list error" + e);
			return mapping.findForward("failure");
		}

		request.setAttribute("entries", entries);
		request.setAttribute("use_time", new Long(time));
		return mapping.findForward("list");

	}

	private List getList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String actionType) throws Exception {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Authentication auth = null;

		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			throw new Exception("auth.failure");
		}

		int id = 0;
		int type = 0;
		try {
			id = ((Integer) dForm.get("id")).intValue();
			type = ((Integer) dForm.get("type")).intValue();
		} catch (Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			saveErrors(request, errors);
			throw new Exception("CacheAction list error . " + e.toString());
		}

		if (id == -1) {
			return null;
		}

		List entries = null;

		if ("reset".equals(actionType)) {
			entries = ListCacheClient.getInstance().resetList(id, type);
		} else {
			entries = ListCacheClient.getInstance().TimeFilter(id, type, 0, -1);
		}
		if (entries == null) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			saveErrors(request, errors);
			throw new Exception("ListCacheClient.resetList return null");
		}

		return entries;
	}

	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer id = ((Integer) dForm.get("id"));
		Integer type = ((Integer) dForm.get("type"));

		
		return mapping.findForward("list");
	}
	
	public ActionForward info(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		
		Properties sysInfo = new Properties();
		try {
			sysInfo = ListCacheClient.getInstance().sysInfo();
		} catch (CacheException e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			saveErrors(request, errors);
			LOG.error("get cache system info failed " + e);
			return mapping.findForward("failure");
		}
		request.setAttribute("cacheinfo", sysInfo);
				
		return mapping.findForward("info");
	}
}
