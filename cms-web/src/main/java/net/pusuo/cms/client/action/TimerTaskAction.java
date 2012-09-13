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

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.schedule.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Calendar;

public class TimerTaskAction extends BaseAction
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
		TimerManager.getInstance();
		return mapping.findForward("list");
	}

	public ActionForward reload(ActionMapping mapping, ActionForm form, 
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
		TimerManager.getInstance().load();
		return mapping.findForward("list");
	}


	public ActionForward view(ActionMapping mapping, ActionForm form, 
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

		try
		{
			BaseForm dForm = (BaseForm)form;
			String name = (String)dForm.get("name");
			TimerObject to = TimerManager.getInstance().get( String.valueOf(name) );
			if( to==null )
			{
				errors.add("errors.timertask", new ActionError("errors.timertask.notask", name));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			dForm.set("name", to.getName());
			dForm.set("classname", to.getClassname());

			dForm.set("hour", new Integer(to.getHour()) );
			dForm.set("minute", new Integer(to.getMinute()) );
			dForm.set("second", new Integer(to.getSecond()) );
			dForm.set("period", new Long(to.getPeriod()));

			return mapping.findForward("view");
		} catch(Exception e) {
			errors.add("errors.timertask", new ActionError("errors.timertask.view", e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
	}

	public ActionForward save(ActionMapping mapping, ActionForm form, 
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

		try
		{
			BaseForm dForm = (BaseForm)form;
			String name = (String)dForm.get("name");
			TimerObject to = TimerManager.getInstance().get( name );
			if( to==null )
			{
				errors.add("errors.timertask", new ActionError("errors.timertask.notask"));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			int hour = ((Integer)dForm.get("hour")).intValue();
			int minute = ((Integer)dForm.get("minute")).intValue();
			int second = ((Integer)dForm.get("second")).intValue();
			long period = ((Long)dForm.get("period")).longValue();

			to.setHour( hour );
			to.setMinute( minute );
			to.setSecond( second );
			to.setPeriod( period );

			TimerManager.getInstance().update( to );

			return mapping.findForward("list");
		} catch(Exception e) {
			errors.add("errors.timertask", new ActionError("errors.timertask.save",e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
	}

	public ActionForward startup(ActionMapping mapping, ActionForm form, 
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

		try
		{
			String name = request.getParameter("name");
			TimerManager.getInstance().schedule( name );

			return mapping.findForward("list");
		} catch(Exception e) {
			errors.add("errors.timertask", new ActionError("errors.timertask.startup",e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
	}

	public ActionForward stop(ActionMapping mapping, ActionForm form, 
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

		try
		{
			String name = request.getParameter("name");
			TimerManager.getInstance().cancel( name );
			return mapping.findForward("list");
		} catch(Exception e) {
			errors.add("errors.timertask", new ActionError("errors.timertask.stop", e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
	}

}

