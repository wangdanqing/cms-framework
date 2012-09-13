package net.pusuo.cms.client.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.Global;
import com.hexun.cms.util.Util;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import com.hexun.cms.client.compile.XMLProperties;

public class CompileConfigAction extends BaseAction
{
	private static final Log log = LogFactory.getLog( CompileConfigAction.class );

	// rmi config file   rmi server: /opt/cms4/compileconfig.xml
	//private static final String filename = "/cms4/compileconfig.xml";

	// local config file
	//private static final String filenametmp = "/opt/itc/resin/webapps/cms4/WEB-INF/classes/compileconfig.xml";

	public ActionForward save(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
	{
		InputStream in = null;

		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm)form;

		try
		{
			in = CompileConfigAction.class.getResourceAsStream( "/compileconfig.xml" );
			XMLProperties prop = new XMLProperties( in );

			prop.setProperty( "pool.buffersize", (String)_form.get("buffersize") );
			prop.setProperty( "pool.maxpoolsize", (String)_form.get("maxpoolsize") );
			prop.setProperty( "pool.minpoolsize", (String)_form.get("minpoolsize") );
			prop.setProperty( "pool.keepalivetime", (String)_form.get("keepalivetime") );

			// ����ʱ����ʽ
			prop.deleteProperties( "skin.timetype" );

			String[] timetypes = (String[])_form.get("timetype");
			for(int i=0; timetypes!=null && i<timetypes.length; i++)
			{
				/*
				if( timetypes[i].indexOf("|")==-1 )
				{
					continue;
				}
				*/
				//String pattern = timetypes[i].substring( timetypes[i].indexOf("|")+1 );
				if( !checkPattern( timetypes[i] ) )
				{
					errors.add("errors.compile.timetype.invalid", new ActionError("errors.invalid","ʱ����ʽ"));
					continue;
				}

				Element e = new Element("type");
				Element e1 = new Element("name").setText( Global.CMSTIMETYPE+(i+1) );
				Element e2 = new Element("pattern").setText( timetypes[i] );
				e.addContent( e1 );
				e.addContent( e2 );
				prop.setProperties( "skin.timetype", e );
			}

			// write rmi file
			// do not save to local file and RMI file
			// modified by wangzhigang
			//String content = LocalFile.read( filenametmp );
			//ClientFile.getInstance().write( content, filename, false );
		} catch(Exception e) {
			errors.add("errors.compileconfig", new ActionError("errors.compileconfig.save",e.toString()));
			log.error("CompileConfigAction save exception -- "+e.toString());
		} finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e) {
				errors.add("errors.compileconfig", new ActionError("errors.compileconfig.save",e.toString()));
				log.error("CompileConfigAction save exception -- "+e.toString());
			}
		}

		if( !errors.isEmpty() )
		{
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
		return mapping.findForward("save");
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
	{
		ActionErrors errors = new ActionErrors();
		BaseForm _form = (BaseForm)form;
		
		InputStream in = null;
		try
		{
			/*
			// get file content from rmi, write local temp file
			String content = ClientFile.getInstance().read( filename );
			if( content==null )
			{
				log.error("rmi compileconfig.xml ==> content is null...");
				errors.add("errors.compileconfig", new ActionError("errors.compileconfig.normifile"));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
			LocalFile.write( content, filenametmp );
			*/

			// get file property in turn, set to FORM
			in = CompileConfigAction.class.getResourceAsStream( "/compileconfig.xml" );
			XMLProperties prop = new XMLProperties( in );

			_form.set( "buffersize", prop.getProperty("pool.buffersize") );
			_form.set( "maxpoolsize", prop.getProperty("pool.maxpoolsize") );
			_form.set( "minpoolsize", prop.getProperty("pool.minpoolsize") );
			_form.set( "keepalivetime", prop.getProperty("pool.keepalivetime") );

			List list = new ArrayList();
			Element[] elements = prop.getProperties("skin.timetype");
			for(int i=0; i<elements.length; i++)
			{
				Element e = elements[i];
				String value = e.getChild("pattern").getText();
				String label = e.getChild("pattern").getText();
				list.add( new LabelValueBean( label, value ) );
			}
			_form.set( "timetypelist", list );
		} catch(Exception e) {
			errors.add("errors.compileconfig", new ActionError("errors.compileconfig.view",e.toString()));
			log.error("CompileConfigAction view exception -- "+e.toString());
		} finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e) {
				errors.add("errors.compileconfig", new ActionError("errors.compileconfig.view",e.toString()));
				log.error("CompileConfigAction view exception -- "+e.toString());
			}
		}


		if( !errors.isEmpty() )
		{
			saveErrors( request, errors );
			return mapping.findForward("failure");
		}
		return mapping.findForward("view");
	}

	private boolean checkPattern( String pattern )
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat( pattern );
			return true;
		}catch( IllegalArgumentException e)
		{
			log.warn("invalid pattern. "+e.toString());
			return false;
		}
	}
}

