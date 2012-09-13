package net.pusuo.cms.client.action;

import java.io.*;

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

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;

import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.Permission;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;

import com.hexun.cms.core.Template;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.client.ItemManager;

import org.jdom.*;
import org.jdom.input.*;        
import org.jdom.output.*;       
import com.hexun.cms.client.compile.XMLProperties;

public class FragPropAction extends BaseAction
{
	private static final Log log = LogFactory.getLog( FragPropAction.class );

	//private static final String filename = "/opt/itc/resin/webapps/cms4/WEB-INF/classes/compileconfig.xml";

	public ActionForward view(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try{
			auth = AuthenticationFactory.getAuthentication(request, response);
		}catch(UnauthenticatedException e){
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors( request,errors );
			return mapping.findForward("failure");
		}

		InputStream in = null;
		try
		{
			// ģ�����ý�ɫ��һ������������ԴȨ��,��������ȡ������Դ�б�
			//List permlist = auth.getUserPermission().getResources();
			List permlist = Permission.getResources();
			request.setAttribute("permission", permlist);

			// time type
                        //XMLProperties prop = new XMLProperties( filename );
			in = FragPropAction.class.getResourceAsStream( "/compileconfig.xml" );
                        XMLProperties prop = new XMLProperties( in );

                        List list = new ArrayList();
                        Element[] elements = prop.getProperties("skin.timetype");
                        for(int i=0; i<elements.length; i++)
                        {
                                Element e = elements[i];
                                String value = e.getChild("pattern").getText();
                                String label = e.getChild("pattern").getText();
                                list.add( label);
                        }
			request.setAttribute( "timestyle", list );


			// get tfmaps
			String templateid = request.getParameter("templateid");


			Template template = (Template)ItemManager.getInstance().get(new Integer(templateid),Template.class);
			if( template.getTFMaps()==null )
			{
				// new template , not in cache
				template.setTFMaps(new HashSet());
			}
			Object[] tfmaps = template.getTFMaps().toArray();
			
			request.setAttribute("tfmaps",tfmaps);

		} catch(Exception e){
			log.error("view fragprop exception.", e);
			errors.add("errors.fragprop", new ActionError("errors.fragprop.view", e.toString()));
			saveErrors( request, errors );
			return mapping.findForward("failure");
		} finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e) {

				log.error("view fragprop exception.", e);
				errors.add("errors.fragprop", new ActionError("errors.fragprop.view", e.toString()));
				saveErrors( request, errors );
				return mapping.findForward("failure");
			}
		}

		return mapping.findForward("success");
	
	}
}
