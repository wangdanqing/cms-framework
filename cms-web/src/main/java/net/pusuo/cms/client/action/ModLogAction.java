package net.pusuo.cms.client.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.ModLog;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

public class ModLogAction extends BaseAction {

	public static final Log log = LogFactory.getLog(ModLogAction.class);
	public static final int NUMBER_PER_PAGE_ITEM = 10;
	public static final long DELAY = 10 * 1000;

	/**
	 * ��ѯ�޸ļ�¼
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
				hql.append(" m.modlog_time >= ?");
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
				hql.append(" m.modlog_time <= ?");
				params.add(ed);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (search_id != null && !search_id.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" m.modlog_nid = ?");
			params.add(search_id);
		}
		if (search_user != null && !search_user.equalsIgnoreCase("")) {
			if (hql.length() > 0)
				hql.append(" and");
			hql.append(" m.modlog_operator = ?");
			params.add(search_user);
		}

		if (hql.length() > 0)
			hql.insert(0, "from " + ModLog.class.getName() + " as m where");
		else
			hql.insert(0, "from " + ModLog.class.getName() + " as m");

		String hqlCount = "select count(*) " + hql.toString();

		hql.append(" order by m.modlog_time desc");

		if (page == null || page.intValue() <= 0)
			page = Integer.valueOf(1);

		List cntList = ItemManager.getInstance().getList(hqlCount, params, -1,
				-1);
		Integer total = new Integer(0);
		if (cntList.size() > 0) {
			total = ((Integer) cntList.get(0));
		}

		int first = (page.intValue() - 1) * NUMBER_PER_PAGE_ITEM;
		List modlogs = ItemManager.getInstance().getList(hql.toString(),
				params, first, NUMBER_PER_PAGE_ITEM);
		request.setAttribute("mlogs", modlogs);
		request.setAttribute("total", total);
		request.setAttribute("page", page);
		return mapping.findForward("list");

	}

	/**
	 * ������ʷ����
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward snapshot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		BaseForm dForm = (BaseForm) form;
		Integer modlog_id = (Integer) dForm.get("modlog_id");
		ModLog mlog = (ModLog) ItemManager.getInstance().get(modlog_id,
				ModLog.class);
		if (mlog == null) {
			errors.add("errors.modlog.notfound", new ActionError(
					"errors.modlog.notfound", modlog_id));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		Integer news_id = Integer.valueOf(mlog.getModlog_nid());
		News news = (News) ItemManager.getInstance().get(news_id, News.class);

		String path = PageManager.getModlogPath(news, modlog_id.intValue());
		try {
			path = path.replaceAll("\\\\", "/");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map map = new HashMap();
		try {
			String xml = ClientFile.getInstance().read(path);
			xml = Util.unicodeToGBK(xml);
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			for (Iterator it = root.elementIterator(); it.hasNext();) {
				Element e = (Element) it.next();
				map.put(e.getName(), e.getText());
			}
		} catch (Exception e) {
			errors.add("errors.modlog.xml.notfound", new ActionError(
					"errors.modlog.xml.notfound", modlog_id));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// ���?����
		String strpid = (String) map.get("pid");
		try {
			Integer pid = Integer.valueOf(strpid);
			EntityItem entity = (EntityItem) ItemManager.getInstance().get(pid,
					EntityItem.class);
			String pname = entity.getDesc();
			map.put("pid", pname);
		} catch (Exception e) {
		}
		// ����ý��
		String strmediaid = (String) map.get("media");
		try {
			Integer mediaid = Integer.valueOf(strmediaid);
			Media media = (Media) ItemManager.getInstance().get(mediaid,
					Media.class);
			String medianame = media.getName();
			map.put("media", medianame);
		} catch (Exception e) {
		}
		// ����Ȩ��
		String strprio = (String) map.get("priority");
		try {
			int priority = Integer.valueOf(strprio).intValue();
			int remaining = priority % 10;
			if (remaining > 0) {
				map.put("addprio", "on");
				priority -= remaining;
				map.put("priority", String.valueOf(priority));
			}
		} catch (Exception e) {
		}

		ret = mapping.findForward("snapshot");
		request.setAttribute("mlog", mlog);
		request.setAttribute("pmap", map);
		return ret;
	}
}
