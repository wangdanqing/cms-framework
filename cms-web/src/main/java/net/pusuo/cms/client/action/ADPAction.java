/*
 * Created on 2006-04-11
 */
package net.pusuo.cms.client.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.ad.ADManagerInterface;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.ad.ADPManager;
import com.hexun.cms.core.*;
import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.ItemInfo;

/**
 * ���������ӣ�ɾ��鿴�����Ŀ���
 *
 * @author wzg
 */
public class ADPAction extends ItemAction {

	private static final Log LOG = LogFactory.getLog(ADPAction.class);

	//��־λ ����Ϊ1����ʾ��ʵ����������е�����ҳ����include�ĸ�ʽ���������
	//����ʹ�ðѴ���ֱ��д��ҳ��ķ�ʽ
	private static final int AD_INCLUDE = 1;

	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
                ActionErrors errors = new ActionErrors();
                ClientFile cf = null;
                ADManagerInterface ADPManager = null;
                StringBuffer sb = null;
                List coll = new ArrayList();
                EntityItem entity = null;
		try {
			cf = ClientFile.getInstance();
			ADPManager = (ADManagerInterface) ClientUtil.renewRMI("ADPManager");
		} catch (Exception e) {
			LOG.error("Error In Getting ClientFile. "+e.toString());
		}
		try {
			sb = ADPManager.list();
			if(sb!=null && !sb.toString().equals(""))
			{
				String[] slist = sb.toString().split("<br>");
				for(int i=0;i<slist.length;i++) {
					if(slist[i]!=null && !slist[i].equals(""))
					{
						String hpId = slist[i].substring(0,slist[i].indexOf("@"));
						entity = (EntityItem)ItemManager.getInstance().get(new Integer(hpId), EntityItem.class);
						if(entity!=null) coll.add(entity);
						LOG.debug("The File entityId is : "+hpId);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error In Getting StringBuffer. "+e.toString());
			errors.add("errors.adp.list", new ActionError("errors.adp.list"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		request.setAttribute("list", coll);
		ret = mapping.findForward("list");
		return ret;
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		ActionForward ret = null;
		BaseForm dForm = (BaseForm) form;
		Integer entityId = (Integer) dForm.get("entityid");
		if(entityId!=null) request.setAttribute("entityid", entityId);
		ret = mapping.findForward("item");
		return ret;
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		ActionForward ret = null;
                ActionErrors errors = new ActionErrors();
                ClientFile cf = null;
                ADManagerInterface ADPManager = null;
                EntityItem entity = null;
                Integer entityId = (Integer)dForm.get("entityid");
		try {
			cf = ClientFile.getInstance();
			ADPManager = (ADManagerInterface) ClientUtil.renewRMI("ADPManager");
		} catch (Exception e) {
			LOG.error("Error In Getting ClientFile. "+e.toString());
		}
		try {
			entity = (EntityItem)ItemManager.getInstance().get(entityId, EntityItem.class);
			if(entity == null) {
				errors.add("errors.adp.noexist", new ActionError("errors.adp.noexist"));
			}
			else if(entity.getType()!=ItemInfo.SUBJECT_TYPE && entity.getType()!=ItemInfo.HOMEPAGE_TYPE)
			{
				errors.add("errors.adp.nosh", new ActionError("errors.adp.nosh"));
			}
			else if(ADPManager.belong(entityId.intValue(),AD_INCLUDE))
			{
				errors.add("errors.adp.exist", new ActionError("errors.adp.exist"));
			}
			else {
				ADPManager.append(entityId.intValue(),AD_INCLUDE);
			}
		} catch (Exception e) {
			LOG.error("Error In append entityId. "+e.toString());
		}
		// Report any errors we have discovered to the failure page
                if (!errors.isEmpty()) {
                	saveErrors(request, errors);
                	ret = mapping.findForward("failure");
                } else {
			// Go so far, forward to next page
			ret =  mapping.findForward("success");
		}
		return ret;
	}

      public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		ActionForward ret = null;
                ActionErrors errors = new ActionErrors();
                ClientFile cf = null;
                ADManagerInterface ADPManager = null;
                EntityItem entity = null;
                Integer entityId = (Integer)dForm.get("entityid");
		try {
			cf = ClientFile.getInstance();
			ADPManager = (ADManagerInterface) ClientUtil.renewRMI("ADPManager");
			ADPManager.delete(entityId.intValue(),AD_INCLUDE);
		} catch (Exception e) {
			LOG.error("Error In Getting ClientFile. "+e.toString());
			errors.add("errors.adp.faill", new ActionError("errors.adp.faill"));
		}

		// Report any errors we have discovered to the failure page
                if (!errors.isEmpty()) {
                	saveErrors(request, errors);
                	ret = mapping.findForward("failure");
                } else {
			// Go so far, forward to next page
			ret =  mapping.findForward("success");
		}                
		return ret;
	}
}
