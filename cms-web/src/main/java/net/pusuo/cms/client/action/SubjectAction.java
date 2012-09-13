package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.SubjectManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.exception.RelationException;
import com.hexun.cms.client.biz.exception.ShortnameException;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.SEOUtil;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.Template;

public class SubjectAction extends EntityAction {

	private static final Log LOG = LogFactory.getLog(SubjectAction.class);

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

		Subject subject = null;

		Subject subjectConfig = (Subject) ItemInfo
				.getItemByType(ItemInfo.SUBJECT_TYPE); // ��ͨ����
		Map extend = new HashMap(); // ��չ����

		try {
			BaseForm dForm = (BaseForm) form;

			// ��֤SEO���
			String metaKey = (String) dForm.get(SEOUtil.META_KEYWORD);
			String metaDesc = (String) dForm.get(SEOUtil.META_DESCRIPTION);
			metaKey = (metaKey != null) ? metaKey.trim() : null;
			metaDesc = (metaDesc != null) ? metaDesc.trim() : null;
			if (metaKey == null || metaDesc == null || metaKey.length() == 0
					|| metaDesc.length() == 0) {
				errors.add("keyword.description.empty", new ActionError(
						"keyword.description.empty"));
				return mapping.findForward("failure");
			}

			// ����ԭ�ȵĲ���
			int subjectId = ((Integer) dForm.get("id")).intValue();
			if (subjectId > 0) {
				Subject subjectOrigin = ManagerFacade.getSubjectManager()
						.getSubject(subjectId);
				if (subjectOrigin == null)
					throw new DaoException();
				PropertyUtils.copyProperties(subjectConfig, subjectOrigin);
			}
			/* add by zhu at 080624,��Ŀ����ר���������ֻ�ܽ�5������ҳ��һ�� */
			Integer ppid = (Integer) dForm.get("pid");
			int comPID = ppid.intValue();

			int depth = 0;
			if(comPID>-1){
				EntityItem pItem = (EntityItem) ItemManager.getInstance().get(
						new Integer(comPID), EntityItem.class);
				int pItemTypeID = pItem.getType();
				while (pItemTypeID != ItemInfo.HOMEPAGE_TYPE && depth < 6) {
					depth++;// 1
					EntityItem item = (EntityItem) ItemManager.getInstance().get(
							new Integer(comPID), EntityItem.class);
					comPID = item.getPid();
					pItemTypeID = item.getType();
				}
				if (depth > 4) {
					errors.add("errors.item.depth", new ActionError(
							"errors.item.depth"));// ����Ŀ��ר����ȳ���5��
						saveErrors(request, errors);
						ret = mapping.findForward("failure");
						return ret;
				}
			}
		

			// if comPID>-1 end

			// ��ȡ���û��޸ĵĲ���
			ItemUtil.setItemValues(dForm, subjectConfig);

			// ������֤��Ϣ
			extend.put(CmsManager.PROPERTY_NAME_AUTH, auth);

			// ���ݸ��������
			//extend.put(CmsManager.PROPERTY_NAME_PNAME, (String) dForm.get("pname"));
			extend.put(CmsManager.PROPERTY_NAME_PNAME, ((String) dForm.get("pname")).trim());

			// ����ģ������Ϣ
			extend.put(SubjectManager.PROPERTY_NAME_NEED_HANDLED_TEMPLATE,
					dForm.get("needProcessTemplate"));

			if (subjectConfig.getId() < 0) { // ��������
				subject = ManagerFacade.getSubjectManager().addSubject(
						subjectConfig, extend);
			} else { // ��������
				subject = ManagerFacade.getSubjectManager().updateSubject(
						subjectConfig, extend);
			}
			if (subject == null || subject.getId() < 0) {
				throw new DaoException();
			}

			// ����SEO���
			SEOUtil.SEOMeta meta = new SEOUtil.SEOMeta();
			meta.keyword = metaKey;
			meta.description = metaDesc;
			SEOUtil.saveKeyDescritpion(subject.getId(), meta);
		} catch (ParentNameException pne) {
			errors.add("errors.item.pnamenotexist", new ActionError(
					"errors.item.pnamenotexist"));
		} catch (PropertyException pe) {
			errors.add("errors.parameter", new ActionError("errors.parameter"));
		} catch (RelationException re) {
			errors.add("subject.relative.homepage", new ActionError(
					"subject.relative.homepage"));
			errors.add("subject.relative.subject", new ActionError(
					"subject.relative.subject"));
			errors.add("subject.relative.subsubject", new ActionError(
					"subject.relative.subsubjectr"));
			errors.add("subject.relative.column", new ActionError(
					"subject.relative.column"));
			errors.add("subject.relative.subcolumn", new ActionError(
					"subject.relative.subcolumn"));
		} catch (ShortnameException se) {
			errors.add("subject.relative.shortnameexist", new ActionError(
					"subject.relative.shortnameexist"));
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
		} catch (DaoException de) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		}

		try {
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			} else {
				response.sendRedirect("subject.do?method=view&id="
						+ subject.getId());
			}
		} catch (Exception e) {
		}

		return ret;
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = super.view(mapping, form, request, response);

		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		if (_id > 0) {
			SEOUtil.SEOMeta meta = SEOUtil.getKeyDescription(_id);
			if (meta != null) {
				/* ȡ��seo meta������ */
				dForm.set(SEOUtil.META_DESCRIPTION, meta.description);
				dForm.set(SEOUtil.META_KEYWORD, meta.keyword);
			}
		}
		return ret;
	}

	/**
	 * ר���ģ��ظ�
	 */
	public ActionForward templateRecover(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String tid = request.getParameter("addtid");
		if (tid == null)
			return mapping.findForward("templaterecover");

		String[] tid_array = tid.split(Global.CMSSEP);

		// �õ�ģ������б�,��ϳ��ʺϱ��ֲ�����
		List result = new ArrayList();
		for (int i = 0; i < tid_array.length; i++) {
			Template template = (Template) ItemManager.getInstance().get(
					new Integer(tid_array[i]), Template.class);
			if (template == null)
				continue;
			Map templateMap = new HashMap();
			templateMap.put("id", "" + template.getId());
			templateMap.put("name", template.getName());
			templateMap.put("desc", template.getDesc());

			// �õ�ģ�����õ�ʵ��ID�б�
			List entityList = new ArrayList();
			List entityIdList = EntityParamUtil.getIdList(template
					.getReference(), Global.CMSSEP);
			if (entityIdList != null) {
				for (int j = 0; j < entityIdList.size(); j++) {
					Map entityMap = new HashMap();
					Item item = ItemManager.getInstance().get(
							(Integer) entityIdList.get(j), EntityItem.class);
					entityMap.put("id", "" + item.getId());
					entityMap.put("name", item.getDesc());
					// ��̬��ַ
					String staticUrl;
					try {
						staticUrl = PageManager.getURL((EntityItem) item,
								template.getId(), true);
						if (staticUrl.endsWith("index.html")) {
							staticUrl = staticUrl.substring(0, staticUrl
									.lastIndexOf("index.html"));
						}
						entityMap.put("staticUrl", staticUrl);
					} catch (Exception e) {
						LOG.error("get staticUrl error", e);
					}

					entityList.add(entityMap);
				}
			}
			templateMap.put("entityList", entityList);
			result.add(templateMap);
		}
		JSONArray jsonResult = JSONArray.fromCollection(result);
		request.setAttribute("jsonstring", jsonResult.toString());

		return mapping.findForward("templaterecover");
	}

}
