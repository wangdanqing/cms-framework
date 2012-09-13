/*
 * Created on 2005-10-14
 */
package net.pusuo.cms.client.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * ģ��ĳ��ò���
 * 
 * @author agilewang
 */
public class TemplateAction extends ItemAction {
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		if (_id <= 0) {
			//���_idС��0,����Ϊ�����һ���½���ģ��,�ܾ�ִ��
			ActionErrors errors = new ActionErrors();
			errors.add("errors.create.template", new ActionError(
					"errors.create.template"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		} else {
			return super.view(mapping, form, request, response);
		}
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		if (_id <= 0) {
			//���_idС��0,����Ϊ�����һ���½���ģ��,�ܾ�ִ��
			ActionErrors errors = new ActionErrors();
			errors.add("errors.create.template", new ActionError(
					"errors.create.template"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		} else {
			return super.save(mapping, form, request, response);
		}
	}
}
