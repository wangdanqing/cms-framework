package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.core.Template;

public class TemplatelibAction extends DispatchAction {
	private static final Log log = LogFactory.getLog(TemplatelibAction.class);

	public ActionForward list(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest req,
				HttpServletResponse res) {
		ActionErrors errors = new ActionErrors();
		try
		{
			String typeId = req.getParameter("typeid");
//			String templates = req.getParameter("template");
			if( typeId==null )
			{
				log.error("TemplatelibAction.list() --> typeid is null.");
				errors.add( "errors.templatelib", new ActionError("errors.templatelib.invalidtype") );
				saveErrors( req, errors );
				return mapping.findForward("failure");
			}
			int i_typeId = -1;
			try
			{
				i_typeId = Integer.parseInt( typeId );
			} catch(NumberFormatException e) {
				log.error("TemplatelibAction.list() NumberFormatException --> invalid template type.");
				errors.add( "errors.templatelib", new ActionError("errors.templatelib.invalidtype") );
				saveErrors( req, errors );
				return mapping.findForward("failure");
			}

			List categoryList = ItemManager.getInstance().getList( ItemInfo.getItemClass(ItemInfo.CATEGORY_TYPE) );
			List templateList = ItemManager.getInstance().getList( ItemInfo.getItemClass(ItemInfo.TEMPLATE_TYPE) );
			List filterList = new ArrayList();
			for(int i=0; i<templateList.size(); i++)
			{
				Template template = (Template)templateList.get(i);
				if (template == null)
					continue;
				if( template.getType() == i_typeId )
				{
					filterList.add( template );
				}
			}

			req.setAttribute("categorylist",categoryList);
			req.setAttribute("templatelist",filterList);
			req.setAttribute("typeid",typeId);
			return mapping.findForward("list");

		} catch(Exception e) {
			log.error(" list templatelib exception --> ", e);
			errors.add( "errors.templatelib", new ActionError("errors.templatelib.list",e.toString()) );
			saveErrors( req, errors );
			return mapping.findForward("failure");
		}
	}
}
