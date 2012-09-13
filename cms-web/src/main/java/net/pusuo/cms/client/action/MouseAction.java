package net.pusuo.cms.client.action;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.validator.DynaValidatorForm;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.hexun.cms.client.action.BaseAction;
import com.hexun.cms.client.action.BaseForm;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;

public class MouseAction extends DispatchAction {
	
	private static final Log LOG = LogFactory.getLog(MouseAction.class);

	public ActionForward frag(ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response) {
					
		ActionForward ret = null;                
		ActionErrors errors = new ActionErrors();                
		
		BaseForm dForm = (BaseForm)form;
		String url = (String)dForm.get("url");
		String hql = "select item.id, item.template from " + ItemInfo.getEntityClass().getName() + " item where item.url =? order by item.id asc";
                List item = null;
		Integer id = null;
                String[] template = null;
                try {
                        Collection values = new ArrayList();
                        values.add(url);
                        item = ItemManager.getInstance().getList(hql, values, -1, -1);
                        if (item != null) {
				//if (item.size() > 1) {
				//	errors.add("errors.mouse.duplicate", new ActionError("errors.mouse.duplicate"));
                        	//	saveErrors(request, errors);
                        	//	return  mapping.findForward("failure");
				//}
				if (item.size() <=0) {
					errors.add("errors.mouse.notfound", new ActionError("errors.mouse.notfound"));
                                        saveErrors(request, errors);
                                        return  mapping.findForward("failure");
				} else {
	                                Iterator itor = item.iterator();
        	                        if (itor.hasNext()) {
						Object[] value = (Object[])itor.next();
						id = (Integer)value[0];
						template = ((String)value[1]).split(";");
                                	}
				}
                        } else {
				errors.add("errors.mouse.notfound", new ActionError("errors.mouse.notfound"));
                                saveErrors(request, errors);
                                return  mapping.findForward("failure");
			}
			
                } catch (Exception e) {
                        LOG.error("MouseAction error. " + e.toString());
                }
	
		if (!errors.isEmpty()) {                        
			saveErrors(request, errors);                        
			ret = mapping.findForward("failure");                
		} else {
			if (template.length > 1) {
				request.setAttribute("entityid",id);
				request.setAttribute("list",template);
				ret = mapping.findForward("list");
			} else {
				String[] templates = template[0].split(",");
				String fwdUrl = "http://cms.pusuo.net/cms_page/template/jsp/template" + templates[0] + ".jsp?ENTITYID=" + id.intValue() + "&view=3";
				request.setAttribute("fwdurl",fwdUrl);
				ret = mapping.findForward("redirect");
			}             
		}                
	
		return ret;
		
	}

	public ActionForward entity(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {

                ActionForward ret = null;
                ActionErrors errors = new ActionErrors();
		
		BaseForm dForm = (BaseForm)form;
                String url = (String)dForm.get("url");
                String hql = "select item.id, item.template from " + ItemInfo.getEntityClass().getName() + " item where item.url =? order by item.id";
                List item = null;
                Integer id = null;
                try {
                        Collection values = new ArrayList();
                        values.add(url);
                        item = ItemManager.getInstance().getList(hql, values, -1, -1);
                        if (item != null) {
                                //if (item.size() > 1) {
                                        //errors.add("errors.mouse.duplicate", new ActionError("errors.mouse.duplicate"));
                                        //saveErrors(request, errors);
                                        //return  mapping.findForward("failure");
		
                                //} if (item.size() <=0) {
                                if (item.size() <=0) {
                                        errors.add("errors.mouse.notfound", new ActionError("errors.mouse.notfound"));
                                        saveErrors(request, errors);
                                        return  mapping.findForward("failure");
                                } else {
					//  �ظ���url,˵�������͵ļ�¼,��һ����¼�϶���Դ
	                                Iterator itor = item.iterator();
        	                        if (itor.hasNext()) {
                	                        Object[] value = (Object[])itor.next();
                        	                id = (Integer)value[0];
                                	}
				}
                        } else {
                                errors.add("errors.mouse.notfound", new ActionError("errors.mouse.notfound"));
                                saveErrors(request, errors);
                                return  mapping.findForward("failure");
                        }

                } catch (Exception e) {
                        LOG.error("MouseAction error. " + e.toString());
                }

                if (!errors.isEmpty()) {
                        saveErrors(request, errors);
                        ret = mapping.findForward("failure");
                } else {
			String fwdUrl = "";
			EntityItem entity = (EntityItem)ItemManager.getInstance().get(id,EntityItem.class);
			switch (entity.getType()) {
				
				case 1: fwdUrl = "/subject.do?id="+id.intValue()+"&method=view"; break;
				
				case 2: fwdUrl = "/news.do?id="+id.intValue()+"&method=view"; break;
				
				case 3: fwdUrl = "/picture.do?id="+id.intValue()+"&method=view"; break;
				
				case 5: fwdUrl = "/homepage.do?id="+id.intValue()+"&method=view"; break;
			}
			
                        ret = new ActionForward(fwdUrl);
                }

                return ret;

        }

	public ActionForward download(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
		
		return new ActionForward("/tool/reg.html");
	}

}
