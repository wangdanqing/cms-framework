package net.pusuo.cms.client.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.search.util.SearchUtils;

public class SearchAction extends BaseAction {

	private static final Log log = LogFactory.getLog(SearchAction.class);

	private static final int LIST_MAX_SIZE = 200;

	public String retrievePermission() {
		return "news";
	}

	public ActionForward byID(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;
		String keyword = ((String) dForm.get("keyword")).trim();

		EntityItem item = null;

		try {
			int id = Integer.parseInt(keyword);
			item = (EntityItem) ItemManager.getInstance().get(new Integer(id),
					EntityItem.class);
		} catch (Exception ee) {
			errors.add("errors.item.notfound", new ActionError(
					"errors.item.notfound"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		if (item == null || (item != null && item.getId() < 0)) {// not found
			errors.add("errors.item.notfound", new ActionError(
					"errors.item.notfound"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		/*
		 * if(!isCtl(item,auth)){//ʵ�岻���Լ���Ͻ��
		 * errors.add("errors.item.notcontrol", new
		 * ActionError("errors.item.notcontrol")); saveErrors(request, errors);
		 * return mapping.findForward("failure"); }
		 */

		try {
			response.sendRedirect(forwardTarget(item));
		} catch (Exception e) {
			log.error("SearchAction byID error . " + e.toString());
		}

		return mapping.findForward("failure");

	}

	public ActionForward byName(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;
		String keyword = ((String) dForm.get("keyword")).trim();

		EntityItem item = null;

		try {
			item = (EntityItem) ItemManager.getInstance().getItemByName(
					keyword, EntityItem.class);
		} catch (Exception ee) {
			errors.add("errors.item.notfound", new ActionError(
					"errors.item.notfound"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		if (item == null || (item != null && item.getId() < 0)) {// not found
			errors.add("errors.item.notfound", new ActionError(
					"errors.item.notfound"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		if (!isCtl(item, auth)) {// ʵ�岻���Լ���Ͻ��
			errors.add("errors.item.notcontrol", new ActionError(
					"errors.item.notcontrol"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		try {
			response.sendRedirect(forwardTarget(item));
		} catch (Exception e) {
			log.error("SearchAction byName error . " + e.toString());
		}

		return mapping.findForward("failure");

	}

	/**
	 * �ж��Ƿ����Լ���Ͻ��Ƶ����ʵ��
	 */
	private boolean isCtl(EntityItem item, Authentication auth) {

		List channelList = auth.getChannelList();

		Iterator ics = channelList.iterator();
		Channel channel = null;
		boolean flag = false;

		while (ics.hasNext()) {
			channel = (Channel) ics.next();
			if (channel != null && channel.getId() > 0) {
				if (item.getChannel() == channel.getId()) {// ʵ���Ƶ�����û���Ȩ��Ƶ����ƥ��
					flag = true;
					break;
				}
			}
		}

		return flag;
	}

	/**
	 * forward target
	 */

	private String forwardTarget(EntityItem item) {
		String target = "";
		if (item.getType() == ItemInfo.HOMEPAGE_TYPE) {
			target = "homepage.do?method=view&id=" + item.getId();
		} else if (item.getType() == ItemInfo.SUBJECT_TYPE) {
			target = "subject.do?method=view&id=" + item.getId();
		} else if (item.getType() == ItemInfo.NEWS_TYPE) {
			target = "news.do?method=view&id=" + item.getId();
		} else if (item.getType() == ItemInfo.PICTURE_TYPE) {
			target = "picture.do?method=view&id=" + item.getId();
		} else if (item.getType() == ItemInfo.VIDEO_TYPE) {
			target = "video.do?method=view&id=" + item.getId();
		} else {
			target = "failure";
		}

		return target;
	}

	public ActionForward bySearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		BaseForm dForm = (BaseForm) form;
		String keyword = ((String) dForm.get("keyword")).trim();
		int channel = ((Integer) dForm.get("channel")).intValue();
		int type = ((Integer) dForm.get("entity")).intValue();
		int page = ((Integer) dForm.get("page")).intValue();
		int titleqt = ((Integer) dForm.get("titleqt")).intValue();

		Long cost = new Long(0);
		Integer count = new Integer(0);
		List list = new ArrayList();

		try {
			if (keyword != null && keyword.trim().length() > 0
					&& !keyword.trim().startsWith("*")) {
				keyword = keyword.trim();

				TermQuery queryChannel = null;
				if (channel != -1) {
					Term termChannel = new Term(CmsEntry.FIELD_NAME_CHANNEL,
							channel + "");
					queryChannel = new TermQuery(termChannel);
				}

				TermQuery queryType = null;
				if (type != -1) {
					Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, type
							+ "");
					queryType = new TermQuery(termType);
				}

				QueryParser queryParser = new QueryParser(
						CmsEntry.FIELD_NAME_DESC, new StandardAnalyzer());
				if (type == 2 && titleqt != -1) {
					if (titleqt == 2)
						queryParser = new QueryParser(
								CmsEntry.FIELD_NAME_AUTHOR,
								new StandardAnalyzer());
					else if (titleqt == 3) {
						queryParser = new QueryParser(
						CmsEntry.FIELD_NAME_MEDIANAME,
						new StandardAnalyzer());
						//Media media = (Media) ItemManager.getInstance()
						//		.getItemByName(keyword, Media.class);
						//if (media != null)
						//	keyword = String.valueOf(media.getId());

						//queryParser = new QueryParser(
						//		CmsEntry.FIELD_NAME_MEDIA,
						//		new StandardAnalyzer());
					} else if (titleqt == 4)
						queryParser = new QueryParser(CmsEntry.FIELD_NAME_ORG,
								new StandardAnalyzer());
					else if (titleqt == 5) {
						User user = (User) ItemManager.getInstance()
								.getItemByName(keyword, User.class);
						if (user != null)
							keyword = String.valueOf(user.getId());
						queryParser = new QueryParser(
								CmsEntry.FIELD_NAME_EDITOR,
								new StandardAnalyzer());
					}
				}
				queryParser.setOperator(QueryParser.AND);
				Query queryUser = queryParser.parse(keyword);

				BooleanQuery query = new BooleanQuery();
				query.add(queryUser, true, false);
				if (queryChannel != null)
					query.add(queryChannel, true, false);
				if (queryType != null)
					query.add(queryType, true, false);

				// Sort sort = new Sort(new
				// SortField(CmsEntry.FIELD_NAME_TIME,SortField.FLOAT, true));
				Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_ID,
						SortField.INT, true));

				int range = 20;
				int fromId = (page - 1) * range;

				Map result = SearchClient.getInstance().getSearchManager()
						.search(query, sort, fromId, range);

				if (result != null) {
					if (result.containsKey(SearchUtils.SEARCH_RESULT_COST))
						cost = (Long) result
								.get(SearchUtils.SEARCH_RESULT_COST);

					if (result.containsKey(SearchUtils.SEARCH_RESULT_COUNT))
						count = (Integer) result
								.get(SearchUtils.SEARCH_RESULT_COUNT);

					if (result.containsKey(SearchUtils.SEARCH_RESULT_LIST))
						list = (List) result
								.get(SearchUtils.SEARCH_RESULT_LIST);
				}
			}
		} catch (Exception e) {
			errors.add("errors.item.notfound", new ActionError(
					"errors.item.notfound"));
			log.error("SearchAction bySearch error. " + e.toString());
		}

		request.setAttribute("cost", cost);
		request.setAttribute("count", count);
		request.setAttribute("list", list);

		request.setAttribute("methodName", "bySearch");
		request.setAttribute("keyword", keyword);
		request.setAttribute("channel", new Integer(channel));
		request.setAttribute("entity", new Integer(type));
		request.setAttribute("page", new Integer(page));
		request.setAttribute("titleqt", new Integer(titleqt));

		// Report any errors we have discovered back to the failure page
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("list");
		}

		return ret;

	}

	/**
	 * ��� ���� ����(db) ������(db) ר�� 1 #1 $1 ��ר�� 2 #1 $2 ��Ŀ 3 #1 $3 ����Ŀ 4 #1 $4 ���� 5
	 * #2 ͼƬ 6 #3 ���⣬��������Ϊ0ʱ��������Ϊ��1��4�е��κ�һ�֡�Ҳ����˵�� �κ������͵�������Ӧ�ð���ΪֵΪ0�������
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

		BaseForm dForm = (BaseForm) form;

		int entity = ((Integer) dForm.get("entity")).intValue();
		int type = -1;
		int subtype = -1;
		switch (entity) {
		case 1:
		case 2:
		case 3:
		case 4:
			subtype = entity;
			type = 1;
			break;
		case 5:
			type = 2;
			break;
		case 6:
			type = 3;
			break;
		}

		int pid = ((Integer) dForm.get("pid")).intValue();
		int page = ((Integer) dForm.get("page")).intValue();
		boolean deep = new Boolean((String) dForm.get("deep")).booleanValue();

		// /////////////////////////////////////////////////////////////////////

		Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, type + "");
		TermQuery queryType = new TermQuery(termType);

		BooleanQuery querySubtype = null;
		if (subtype > 0) {
			Term termSubtype = new Term(CmsEntry.FIELD_NAME_SUBTYPE, subtype
					+ "");
			TermQuery query1 = new TermQuery(termSubtype);

			Term termDefaultSubtype = new Term(CmsEntry.FIELD_NAME_SUBTYPE, "0");
			TermQuery query2 = new TermQuery(termDefaultSubtype);

			querySubtype = new BooleanQuery();
			querySubtype.add(query1, false, false);
			querySubtype.add(query2, false, false);
		}

		Query queryCategoryOrId = null;
		if (deep) {
			EntityItem parent = (EntityItem) ItemManager.getInstance().get(
					new Integer(pid), EntityItem.class);
			String category = parent.getCategory();
			category = category.replace(';', ' ');

			try {
				QueryParser queryParser = new QueryParser(
						CmsEntry.FIELD_NAME_CATEGORY, new StandardAnalyzer());
				queryParser.setOperator(QueryParser.AND);
				queryCategoryOrId = queryParser.parse(category);
			} catch (Exception e) {
			}
		} else {
			Term termPid = new Term(CmsEntry.FIELD_NAME_PID, pid + "");
			queryCategoryOrId = new TermQuery(termPid);
		}

		BooleanQuery query = new BooleanQuery();
		query.add(queryType, true, false);
		if (querySubtype != null)
			query.add(querySubtype, true, false);
		if (queryCategoryOrId != null)
			query.add(queryCategoryOrId, true, false);

		// Sort sort = new Sort(new
		// SortField(CmsEntry.FIELD_NAME_TIME,SortField.FLOAT, true));
		Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_ID,
				SortField.INT, true));

		Long cost = new Long(0);
		Integer count = new Integer(0);
		List list = new ArrayList();

		try {
			int range = 20;
			int fromId = (page - 1) * range;

			Map result = SearchClient.getInstance().getSearchManager().search(
					query, sort, fromId, range);

			if (result != null) {
				if (result.containsKey(SearchUtils.SEARCH_RESULT_COST))
					cost = (Long) result.get(SearchUtils.SEARCH_RESULT_COST);

				if (result.containsKey(SearchUtils.SEARCH_RESULT_COUNT))
					count = (Integer) result
							.get(SearchUtils.SEARCH_RESULT_COUNT);

				if (result.containsKey(SearchUtils.SEARCH_RESULT_LIST))
					list = (List) result.get(SearchUtils.SEARCH_RESULT_LIST);
			}
		} catch (Exception e) {
			log.error(e);
		}

		request.setAttribute("cost", cost);
		request.setAttribute("count", count);
		request.setAttribute("list", list);

		request.setAttribute("methodName", "list");
		request.setAttribute("entity", new Integer(entity));
		request.setAttribute("pid", new Integer(pid));
		request.setAttribute("page", new Integer(page));
		request.setAttribute("deep", new Boolean(deep));

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("list");
		}

		return ret;
	}

	private String parseKeyword(String queryText) {
		if (queryText != null) {
			queryText = queryText.replaceAll("\\+", " + ");
			queryText = queryText.replaceAll("\\/", " / ");
			queryText = queryText.replaceAll("\\-", " - ");
		}
		return queryText;
	}

}
