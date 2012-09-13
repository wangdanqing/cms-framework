package net.pusuo.cms.client.action;

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

import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.EntityItem;

public class HomePageAdminAction extends EntityAction {

	private static final Log LOG = LogFactory.getLog(HomePageAction.class);

	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}

		try {
			// ���ϵͳ����������ΪHOMEPAGE��ʵ��
			List hpList = ItemUtil.getEntityChildren(-1, ItemInfo.HOMEPAGE_TYPE);
			request.setAttribute("list", hpList);
		} catch (Exception e) {
			errors.add("errors.item.list", new ActionError("errors.item.list"));
			LOG.error("HomePageAdminAction list error . " + e.toString());
		}
		return mapping.findForward("list");
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}

		Item item = null;

		try {
			// ������JSPҳ���еò��������
			BaseForm dForm = (BaseForm) form;
			dForm.set("type", new Integer(ItemInfo.HOMEPAGE_TYPE));
		//	dForm.set("status", new Integer(1));
			dForm.set("pid", new Integer(-1));
			dForm.set("priority", new Integer(70));
			dForm.set("editor", new Integer(auth.getUserID()));

			// ��ģ���б�
			String oldTemplate = getTemplateParam((Integer)dForm.get("id"));
			
			Object[] obj = saveProcesser(dForm, "html");// save item data
			item = (Item) obj[0];
			errors = (ActionErrors) obj[1];

			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
			
			// ��ģ���б�
			String newTemplate = getTemplateParam(new Integer(item.getId()));
			
			// �������ģ��ķ�������
			EntityParamUtil.updateTemplate4Reference(item.getId(), oldTemplate, newTemplate);
			
			if (item != null && item.getId() > 0) {
				response.sendRedirect("homepage.do?method=view&id="
						+ item.getId());
			} else {
				errors.add("errors.item.save", new ActionError(
						"errors.item.save"));
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			LOG.error("HomePageAction save error . " + e.toString());
		}

		return ret;
	}

	private String getTemplateParam(Integer id) {
		
		String templateParam = "";

		if (id != null && id.intValue() > -1) {
			EntityItem entity = (EntityItem)ItemManager.getInstance().get(id, EntityItem.class);
			if (entity != null) {
				templateParam = entity.getTemplate();
				if (templateParam != null)
					templateParam = templateParam.trim();
			}
		}

		return templateParam;
	}

}
