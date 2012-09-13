package net.pusuo.cms.client.action;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.ClientHttpFile;
import com.hexun.cms.core.DelLog;
import com.hexun.cms.core.EntityItem;

public class NewsDelAction extends BaseAction {

	public static final Log log = LogFactory.getLog(NewsDelAction.class);
	public static final String SEPARATOR = "|*|";
	public static final String SEPARATOR_RE = "\\|\\*\\|";
	public static final int NUMBER_PER_PAGE_ITEM = 10;
	public static final long DELAY = 10 * 1000;

	/**
	 * ִ��ɾ����
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		// ����form�е�����ֵ
		BaseForm dForm = (BaseForm) form;
		String dellog_input = (String) dForm.get("dellog_input");
		String dellog_reason = (String) dForm.get("dellog_reason");
		String dellog_initiator = (String) dForm.get("dellog_initiator");
		
		MessageResources resources = MessageResources
		.getMessageResources("resources.application");
		
		String input = resources.getMessage("dellog.input");
		if(dellog_input == null || dellog_input.equalsIgnoreCase("")) {
			errors.add("errors.required", new ActionError(
					"errors.required", input));
		}
		String rea = resources.getMessage("dellog.reason");
		if(dellog_reason == null || dellog_reason.equalsIgnoreCase("")) {
			errors.add("errors.required", new ActionError(
					"errors.required", rea));
		}
		String initiator = resources.getMessage("dellog.initiator");
		if(dellog_initiator == null || dellog_initiator.equalsIgnoreCase("")) {
			errors.add("errors.required", new ActionError(
					"errors.required", initiator));
		}
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.getInputForward();
		}

		String[] urls = new String[0];
		String regUrl = "^http://([a-z|A-Z|0-9|.]{1,15}).hexun.com/([0-9]{4}-[0-9]{2}-[0-9]{2})/([0-9]{9}).html$";
		List ids = new ArrayList();
		Pattern p = Pattern.compile(regUrl);
		if (dellog_input != null || !dellog_input.equalsIgnoreCase("")) {
			urls = dellog_input.split("\\s+");
			MessageResources messageResources = this.getResources(request);
			if (urls.length > 20)
				errors.add("errors.dellog.toomany", new ActionError(
						"errors.dellog.toomany", messageResources
								.getMessage("dellog.input")));
			for (int i = 0; i < urls.length; i++) {
				Matcher m = p.matcher(urls[i]);
				boolean find = m.find();
				if (!find)
					errors.add("errors.news.url", new ActionError(
							"errors.news.url", urls[i]));
				else {
					String id = m.group(3);
					ids.add(id);
				}
			}
		}

		// ����ID��ѯ����
		List titles = new ArrayList();
		List entities = new ArrayList();
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String sid = (String) it.next();
			try {
				Integer id = Integer.valueOf(Integer.parseInt(sid));
				EntityItem entity = (EntityItem) ItemManager.getInstance().get(
						id, EntityItem.class);
				// ����ʵ�岻����
				if (entity == null) {
					errors.add("errors.news.notfound", new ActionError(
							"errors.news.notfound", sid));
					continue;
				}
				// ����״̬�Ѿ�����Ч
				if (entity.getStatus() == EntityItem.DISABLE_STATUS) {
					errors.add("errors.news.disabled", new ActionError(
							"errors.news.disabled", sid));
					continue;
				}
				String title = entity.getDesc();
				titles.add(title);
				entities.add(entity);
			} catch (Exception e) {
				// ��ѯ������Ϣ����
				errors.add("errors.news.exception", new ActionError(
						"errors.news.exception", sid));
			}
		}

		// ����form�е�����
		DelLog dlog = new DelLog();
		try {
			PropertyUtils.copyProperties(dlog, dForm);
		} catch (Exception e) {
			errors.add("", new ActionMessage(""));
			saveErrors(request, errors);
			return mapping.getInputForward();
		}
		
		String tag = resources.getMessage("dellog.reason");
		if (dForm.get("dellog_reason").equals("other")) {
			String reason = (String) dForm.get("dellog_reason_other");
			if (reason == null || reason.equals("")) {
				errors.add("errors.required", new ActionError(
						"errors.required", tag));
			}
			else
				dlog.setDellog_reason(reason);
		}

		// ���������dlog����
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException e) {
			return mapping.findForward("unauthorized");
		}
		dlog.setDellog_time(new Timestamp(System.currentTimeMillis()));
		dlog.setDellog_ids(list2String(ids));
		dlog.setDellog_titles(list2String(titles));
		if (auth != null) {
			dlog.setDellog_operator(auth.getUserName());
			dlog.setDellog_opid(auth.getUserID());
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.getInputForward();
		}

		// �����´���Ч
		Map success = new HashMap();
		Map failure = new HashMap();
		for (Iterator it = entities.iterator(); it.hasNext();) {
			EntityItem entity = (EntityItem) it.next();
			entity.setStatus(EntityItem.DISABLE_STATUS);
			try {
				ItemManager.getInstance().update(entity);
				success.put(entity.getUrl(), entity.getDesc());
			} catch (Exception e) {
				failure.put(entity.getUrl(), entity.getDesc());
			}
		}

		// ����DelLog��Ϣ
		dlog = (DelLog) ItemManager.getInstance().update(dlog);

		// ͬ��ɾ����Ϣ
		String strids = list2String(ids);
		try {
			String url = "http://cms.hexun.com:8080/delete/sync.jsp";
			Map params = new HashMap();
			params.put("ids", strids);
			ClientHttpFile.wgetString(url, params);
		} catch (Exception e) {
			log.error("ERROR - SENT DEL TO /delete/sync.jsp: " + e);
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.getInputForward();
		} else {
			request.setAttribute("success", success);
			request.setAttribute("failure", failure);
			request.setAttribute("did", Integer.valueOf(dlog.getId()));
			ret = mapping.findForward("result");
		}

		return ret;
	}

	/**
	 * ��ѯɾ���¼
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward listAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		// ����form�е�����ֵ
		BaseForm dForm = (BaseForm) form;
		String search_date_start = (String) dForm.get("search_date_start");
		String search_date_end = (String) dForm.get("search_date_end");
		String search_id = (String) dForm.get("search_id");
		String search_reason = (String) dForm.get("search_reason");
		String search_user = (String) dForm.get("search_user");

		Integer page = (Integer) dForm.get("page");

		// ƴװHQL��ѯ���
		StringBuffer hql = new StringBuffer();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List params = new ArrayList();
		if (search_date_start != null
				&& !search_date_start.equalsIgnoreCase("")) {
			try {
				search_date_start += " 00:00:00";
				Date sd = formatter.parse(search_date_start);
				hql.append(" d.dellog_time >= ?");
				params.add(sd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (search_date_end != null && !search_date_end.equalsIgnoreCase("")) {
			try {
				search_date_end += " 23:59:59";
				Date ed = formatter.parse(search_date_end);
				if (hql.length() > 0)
					hql.append(" and");
				hql.append(" d.dellog_time <= ?");
				params.add(ed);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (search_id != null && !search_id.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_ids like ?");
			params.add("%" + search_id + "%");
		}
		if (search_reason != null && !search_reason.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_reason = ?");
			params.add(search_reason);
		}
		if (search_user != null && !search_user.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_operator = ?");
			params.add(search_user);
		}

		if (hql.length() > 0)
			hql.insert(0, "from " + DelLog.class.getName() + " as d where");
		else
			hql.insert(0, "from " + DelLog.class.getName() + " as d");

		String hqlCount = "select count(*) " + hql.toString();

		hql.append(" order by d.dellog_time desc");

		if (page == null || page.intValue() <= 0)
			page = Integer.valueOf(1);

		List cntList = ItemManager.getInstance().getList(hqlCount, params, -1,
				-1);
		Integer total = new Integer(0);
		if (cntList.size() > 0) {
			total = ((Integer) cntList.get(0));
		}

		int first = (page.intValue() - 1) * NUMBER_PER_PAGE_ITEM;
		List dellogs = ItemManager.getInstance().getList(hql.toString(),
				params, first, NUMBER_PER_PAGE_ITEM);
		request.setAttribute("dlogs", dellogs);
		request.setAttribute("total", total);
		request.setAttribute("page", page);
		return mapping.findForward("list");
	}
	
	/**
	 * ��ѯɾ���¼
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		// ����form�е�����ֵ
		BaseForm dForm = (BaseForm) form;
		String search_date_start = (String) dForm.get("search_date_start");
		String search_date_end = (String) dForm.get("search_date_end");
		String search_id = (String) dForm.get("search_id");
		String search_reason = (String) dForm.get("search_reason");
		//String search_user = (String) dForm.get("search_user");
		
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException e) {
			return mapping.findForward("unauthorized");
		}
		String search_user = auth.getUserName();
		Integer page = (Integer) dForm.get("page");

		// ƴװHQL��ѯ���
		StringBuffer hql = new StringBuffer();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List params = new ArrayList();
		if (search_date_start != null
				&& !search_date_start.equalsIgnoreCase("")) {
			try {
				search_date_start += " 00:00:00";
				Date sd = formatter.parse(search_date_start);
				hql.append(" d.dellog_time >= ?");
				params.add(sd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (search_date_end != null && !search_date_end.equalsIgnoreCase("")) {
			try {
				search_date_end += " 23:59:59";
				Date ed = formatter.parse(search_date_end);
				if (hql.length() > 0)
					hql.append(" and");
				hql.append(" d.dellog_time <= ?");
				params.add(ed);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (search_id != null && !search_id.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_ids like ?");
			params.add("%" + search_id + "%");
		}
		if (search_reason != null && !search_reason.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_reason = ?");
			params.add(search_reason);
		}
		if (search_user != null && !search_user.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" d.dellog_operator = ?");
			params.add(search_user);
		}

		if (hql.length() > 0)
			hql.insert(0, "from " + DelLog.class.getName() + " as d where");
		else
			hql.insert(0, "from " + DelLog.class.getName() + " as d");

		String hqlCount = "select count(*) " + hql.toString();

		hql.append(" order by d.dellog_time desc");

		if (page == null || page.intValue() <= 0)
			page = Integer.valueOf(1);

		List cntList = ItemManager.getInstance().getList(hqlCount, params, -1,
				-1);
		Integer total = new Integer(0);
		if (cntList.size() > 0) {
			total = ((Integer) cntList.get(0));
		}

		int first = (page.intValue() - 1) * NUMBER_PER_PAGE_ITEM;
		List dellogs = ItemManager.getInstance().getList(hql.toString(),
				params, first, NUMBER_PER_PAGE_ITEM);
		request.setAttribute("dlogs", dellogs);
		request.setAttribute("total", total);
		request.setAttribute("page", page);
		return mapping.findForward("list");
	}

	/**
	 * �鿴�ļ�״̬
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward status(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer dellog_id = (Integer) dForm.get("dellog_id");
		DelLog dlog = (DelLog) ItemManager.getInstance().get(dellog_id,
				DelLog.class);
		if (dlog == null) {
			errors.add("errors.dellog.notfound", new ActionError(
					"errors.dellog.notfound", dellog_id));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		List ids = string2List(dlog.getDellog_ids());

		List entities = new ArrayList();
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String sid = (String) it.next();
			try {
				Integer id = Integer.valueOf(Integer.parseInt(sid));
				EntityItem entity = (EntityItem) ItemManager.getInstance().get(
						id, EntityItem.class);
				entities.add(entity);
			} catch (Exception e) {
				// ��ѯ������Ϣ����
				errors.add("errors.news.exception", new ActionError(
						"errors.news.exception", sid));
			}
		}

		Map success = new HashMap();
		Map failure = new HashMap();
		// ��ѯ�ļ��Ƿ��ѱ�ɾ��
		for (Iterator it = entities.iterator(); it.hasNext();) {
			EntityItem entity = (EntityItem) it.next();
			String path = null;
			try {
				path = PageManager.getTStorePath(entity);
			} catch (Exception e) {
				log.error("ERROR - getTStorePath: " + entity);
				failure.put(entity.getUrl(), entity.getDesc());
			}
			if (path != null) {
				try {
					String content = ClientFile.getInstance().read(path);
					if (content != null) {
						failure.put(entity.getUrl(), entity.getDesc());
					} else {
						success.put(entity.getUrl(), entity.getDesc());
					}
				} catch (Exception e) {
					log.error("ERROR - getFileList: " + path);
					failure.put(entity.getUrl(), entity.getDesc());
				}
			} else {
				failure.put(entity.getUrl(), entity.getDesc());
			}
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			request.setAttribute("success", success);
			request.setAttribute("failure", failure);
			ret = mapping.findForward("status");
		}
		return ret;
	}

	/**
	 * ��ɾ����
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward redel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		BaseForm dForm = (BaseForm) form;
		Integer dellog_id = (Integer) dForm.get("dellog_id");
		DelLog dlog = (DelLog) ItemManager.getInstance().get(dellog_id,
				DelLog.class);
		if (dlog == null) {
			errors.add("errors.dellog.notfound", new ActionError(
					"errors.dellog.notfound", dellog_id));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		List ids = string2List(dlog.getDellog_ids());

		List entities = new ArrayList();
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String sid = (String) it.next();
			try {
				Integer id = Integer.valueOf(Integer.parseInt(sid));
				EntityItem entity = (EntityItem) ItemManager.getInstance().get(
						id, EntityItem.class);
				entities.add(entity);
			} catch (Exception e) {
				// ��ѯ������Ϣ����
				errors.add("errors.news.exception", new ActionError(
						"errors.news.exception", sid));
			}
		}

		Map success = new HashMap();
		Map failure = new HashMap();
		for (Iterator it = entities.iterator(); it.hasNext();) {
			EntityItem entity = (EntityItem) it.next();
			entity.setStatus(EntityItem.DISABLE_STATUS);
			try {
				ItemManager.getInstance().update(entity);
				success.put(entity.getUrl(), entity.getDesc());
			} catch (Exception e) {
				failure.put(entity.getUrl(), entity.getDesc());
			}
		}

		// ����DelLog��Ϣ
		dlog.setDellog_redo(dlog.getDellog_redo() + 1);
		dlog.setDellog_redotime(new Timestamp(System.currentTimeMillis()));
		ItemManager.getInstance().update(dlog);

		String strids = list2String(ids);
		try {
			String url = "http://cms.hexun.com:8080/delete/sync.jsp";
			Map params = new HashMap();
			params.put("ids", strids);
			ClientHttpFile.wgetString(url, params);
		} catch (Exception e) {
			log.error("ERROR - SENT DEL TO /delete/sync.jsp: " + e);
		}
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.getInputForward();
		} else {
			request.setAttribute("success", success);
			request.setAttribute("failure", failure);
			request.setAttribute("did", Integer.valueOf(dlog.getId()));
			ret = mapping.findForward("result");
		}

		return ret;
	}

	/**
	 * ���б�ת��Ϊ�ָ���ַ�
	 * 
	 * @param list
	 * @return
	 */
	private String list2String(List list) {
		StringBuffer buf = new StringBuffer();
		for (Iterator it = list.iterator(); it.hasNext();) {
			if (buf.length() > 0)
				buf.append(SEPARATOR + (String) it.next());
			else
				buf.append((String) it.next());
		}
		return buf.toString();
	}

	/**
	 * ���ָ���ַ�ת��Ϊ�б�
	 * 
	 * @param str
	 * @return
	 */
	private List string2List(String str) {
		List list = new ArrayList();
		if (str != null) {
			String[] array = str.split(SEPARATOR_RE);
			for (int i = 0; i < array.length; i++)
				list.add(array[i]);
		}
		return list;
	}
}
