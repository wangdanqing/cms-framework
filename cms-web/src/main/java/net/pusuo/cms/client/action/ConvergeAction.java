package net.pusuo.cms.client.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
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

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.util.ContentGenerator;
import com.hexun.cms.client.util.UploadEntity;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.search.util.SearchUtils;
import com.hexun.cms.util.Util;

/**
 * <p>
 * �ۺ�����
 * </p>
 * <p>
 * ���Ѿ��ϴ���ͼƬ��,��ͼƬ,Ȼ������µ�����
 * </p>
 * 
 * @author denghua
 * 
 */
public class ConvergeAction extends EntityAction {

	private static final Log log = LogFactory.getLog(ConvergeAction.class);

	// ��ʾ��������ʱ��,ÿҳ������
	static int range = 500;

	// ÿҳ���ŷŶ�����ͼƬ
	static int NEWS_IMAGE_RANGE = 1;

	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		return mapping.findForward("list");

	}

	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		BaseForm dForm = (BaseForm) form;

		String keyword = StringUtils.trimToNull(request.getParameter("keyword"));
		if (StringUtils.isNotEmpty(keyword)) {
			try {
				keyword = java.net.URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("decode keyword error", e);
			}
		}
		
		String pname = StringUtils.trimToNull(request.getParameter("pname"));
		if(StringUtils.isNotEmpty(pname)){
			try {
				pname=java.net.URLDecoder.decode(pname, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("decode pname error", e);
			}
		}
		
		int target = 1; // 1:ͼƬ;2:һͼһƪ
		String targetParam = request.getParameter("target");
		if (StringUtils.isNotEmpty(targetParam)) {
			try {
				target = Integer.parseInt(targetParam);
			} catch (Exception e) {
				log.error(e);
			}
		}
		
		int type = 3;
		int subtype = -1;
		if (target == 1) {
			type = 3;    // ͼƬ
		} else {
			type = 2;	 // ����
			subtype = 2; // һͼһƪ
		}
		
		// ��������
		//int type = ((Integer) dForm.get("type")).intValue();
		int page = ((Integer) dForm.get("page")).intValue();
		int entity = ((Integer) dForm.get("entity")).intValue();
		boolean deep = new Boolean((String) dForm.get("deep")).booleanValue();

		// type
		Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, type + "");
		TermQuery queryType = new TermQuery(termType);
		
		TermQuery querySubtype = null;
		if (subtype > -1) {
			Term termSubtype = new Term(CmsEntry.FIELD_NAME_SUBTYPE, subtype + "");
			querySubtype = new TermQuery(termSubtype);
		}

		Query queryCategoryOrId = null;
		TermQuery queryChannel = null;
		if (pname != null) {
			EntityItem parent = (EntityItem) ItemManager.getInstance()
					.getItemByName(new String(pname), EntityItem.class);
			try {
				queryCategoryOrId = processDeep(deep, parent);
			} catch (ParseException e1) {
				errors.add("errors.search.query.parse", new ActionError(
						"errors.search.query.parse", "category"));
			}
			// channel
			if (parent != null) {
				Term termChannel = new Term(CmsEntry.FIELD_NAME_CHANNEL, parent.getChannel() + "");
				queryChannel = new TermQuery(termChannel);
			}
		}

		// keyword
		Query queryKeyword = null;
		if (keyword != null && keyword.indexOf("*") == -1) {
			QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_DESC,
					new StandardAnalyzer());
			queryParser.setOperator(QueryParser.AND);
			try {
				queryKeyword = queryParser.parse(keyword);
			} catch (ParseException e) {
				errors.add("errors.search.query.parse", new ActionError(
						"errors.search.query.parse", keyword));
				log.error("parse keyword error, keyword=" + keyword, e);
			}
		}

		// ��ϸ�������
		BooleanQuery query = new BooleanQuery();
		query.add(queryType, true, false);
		if (querySubtype != null)
			query.add(querySubtype, true, false);
		if (queryCategoryOrId != null)
			query.add(queryCategoryOrId, true, false);
		if (queryChannel != null)
			query.add(queryChannel, true, false);
		if (queryKeyword != null)
			query.add(queryKeyword, true, false);

		Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_TIME, SortField.FLOAT, true));

		// ��ҳ����
		int fromId = (page - 1) * range;

		Map result = null;
		try {
			result = SearchClient.getInstance().getSearchManager().search(query, sort, fromId, range);
		} catch (RemoteException e) {
			log.error("search error", e);
			errors.add("errors.search", new ActionError("errors.search"));
		}

		Long cost = new Long(0);
		Integer count = new Integer(0);
		List list = new ArrayList();

		if (result != null) {
			if (result.containsKey(SearchUtils.SEARCH_RESULT_COST))
				cost = (Long) result.get(SearchUtils.SEARCH_RESULT_COST);
			if (result.containsKey(SearchUtils.SEARCH_RESULT_COUNT))
				count = (Integer) result.get(SearchUtils.SEARCH_RESULT_COUNT);
			if (result.containsKey(SearchUtils.SEARCH_RESULT_LIST))
				list = (List) result.get(SearchUtils.SEARCH_RESULT_LIST);
		}

		request.setAttribute("cost", cost);
		request.setAttribute("count", count);
		request.setAttribute("list", list);

		request.setAttribute("methodName", "bySearch");
		request.setAttribute("keyword", keyword);
		request.setAttribute("type", new Integer(type));
		request.setAttribute("entity", new Integer(entity));
		request.setAttribute("page", new Integer(page));

		// ��ݲ�ѯ�����ϳ�ҳ������Ҫ�����
		List resultList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			CmsEntry entry = (CmsEntry) list.get(i);
			Map temp = new HashMap();
			temp.put("id", new Integer(entry.getId()));
			temp.put("url", entry.getUrl());
			temp.put("desc", StringUtils.isEmpty(entry.getDesc()) ? Util.unicodeToGBK("û������")
					: entry.getDesc());
			temp.put("pid", new Integer(entry.getPid()));
			temp.put("pdesc", ItemManager.getInstance().get(
					new Integer(entry.getPid()), EntityItem.class).getDesc());
			temp.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(entry.getTime()));
			resultList.add(temp);
		}

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			// ��json������顣
			outputJSON(response, resultList);
			ret = null;
			// ret = mapping.findForward("list_content");
		}
		return ret;
	}

	/**
	 * ��resultList����ת��json���ַ���� Ȼ�������response
	 * 
	 * @param response
	 * @param resultList
	 */
	private void outputJSON(HttpServletResponse response, List resultList) {
		JSONArray jsonArray = JSONArray.fromCollection(resultList);
		response.reset();
		response.setContentType("application/x-json;charset=GBK");
		try {
			response.getWriter().print(jsonArray);
		} catch (IOException e) {

		}
	}

	/**
	 * ����deep����. ���deep=true,����������category
	 * 
	 * @param deep
	 * @param parent
	 * @return
	 * @throws ParseException
	 */
	private Query processDeep(boolean deep, EntityItem parent)
			throws ParseException {
		if (parent == null)
			return null;

		Query queryCategoryOrId = null;

		if (deep) { // ����ǳ�������,���category��
			String category = parent.getCategory();
			category = category.replace(';', ' ');

			QueryParser queryParser = new QueryParser(
					CmsEntry.FIELD_NAME_CATEGORY, new StandardAnalyzer());
			queryParser.setOperator(QueryParser.AND);
			try {
				queryCategoryOrId = (Query) queryParser.parse(category);
			} catch (ParseException e) {
				log.error("category parse error,category:" + category, e);
				throw e;
			}
		} else {
			Term termPid = new Term(CmsEntry.FIELD_NAME_PID, parent.getName()
					+ "");
			queryCategoryOrId = new TermQuery(termPid);
		}
		return queryCategoryOrId;
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String[] ids = request.getParameterValues("ids");
		if (ids == null || ids.length == 0) {
			ActionErrors errors = new ActionErrors();
			errors.add("errors.converge.selectnone", new ActionError(
					"errors.converge.selectnone"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		String _entity = request.getParameter("entity");
		request.setAttribute("ids", StringUtils.join(ids, Global.CMSSEP));

		// �����ID�����Ǵ���޸ģ�ȡ�������Ϣ
		if (StringUtils.isNotEmpty(_entity) && StringUtils.isNumeric(_entity)) {
			Integer entity = new Integer(_entity);
			News news = (News) ItemManager.getInstance().get(entity,
					ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
			request.setAttribute("news", news);
			request.setAttribute("ids", news.getPictures());
		}

		return mapping.findForward("create");
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}

		String[] ids = request.getParameterValues("ids");
		if (ids == null || ids.length == 0) {
			errors.add("errors.converge.selectnone", new ActionError(
					"errors.converge.selectnone"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		String desc = request.getParameter("desc");
		String newstext = request.getParameter("text");
		int priority = new Integer(request.getParameter("priority")).intValue();
		String pname = request.getParameter("ppname");
		String _entity = request.getParameter("entity");
		Integer entity = new Integer(-1);
		if (StringUtils.isNotEmpty(_entity) && StringUtils.isNumeric(_entity)) {
			entity = new Integer(_entity);
		}

		News newsConfig = null;
		if (entity.intValue() > 0) {
			newsConfig = (News) ItemManager.getInstance().get(entity,
					ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
		} else {
			newsConfig = CoreFactory.getInstance().createNews();
		}

		if (newsConfig == null) {
			errors.add("errors.object",
					new ActionError("errors.object", "news"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		newsConfig.setSubtype(News.SUBTYPE_ZUTU); // ��ͼ����
		newsConfig.setDesc(desc);
		newsConfig.setText(newstext);
		newsConfig.setPriority(priority);
		newsConfig.setPictures(StringUtils.join(ids, Global.CMSSEP));
		
		Map extend = new HashMap(); // ��չ����
		extend.put(NewsManager.PROPERTY_NAME_AUTH, auth); // ������֤��Ϣ
		extend.put(NewsManager.PROPERTY_NAME_PNAME, pname); // ���ݸ��������
		
		News news = null;
		try {
			if (newsConfig.getId() < 0) { // ��������
				news = ManagerFacade.getNewsManager().addNews(newsConfig, extend);
			}
			else { // ��������
				news = ManagerFacade.getNewsManager().updateNews(newsConfig, extend);
			}
		}
		catch (Exception e) {
			news = null;
		}
		
		// ����ͼƬ����
		List entityList = new ArrayList();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			if (!StringUtils.isNumeric(id))
				continue;
			Picture picture = (Picture) ItemManager.getInstance()
				.get(new Integer(id), ItemInfo.getItemClass(ItemInfo.PICTURE_TYPE));
			if (picture == null)
				continue;
			
			UploadEntity item = new UploadEntity();
			item.setDesc(desc);
			item.setText(newstext);
			item.setImageAlt(picture.getDesc());
			item.setImageDesc(picture.getDesc());
			item.setPicture(picture);
			
			entityList.add(item);
		}
		String content = ContentGenerator.genMultiPageNews(news, entityList, true, null);
		news.setText(content);
		news = (News) ItemManager.getInstance().update(news);
		
		Item news_save_success_item = news;
		
		request.setAttribute("news_save_success_item", news_save_success_item);

		return mapping.findForward("success");
	}

	/**
	 * �޸�����
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward modify(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		BaseForm dForm = (BaseForm) form;
		Integer entity = (Integer) dForm.get("entity");
		ActionErrors errors = new ActionErrors();
		if (entity == null) {
			errors.add("errors.parameter", new ActionError("errors.parameter"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		News newsItem = (News) ItemManager.getInstance().get(entity,
				ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
		if (newsItem == null) {
			errors.add("errors.object",
					new ActionError("errors.object", "news"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		// ��⣬�Ƿ�ۺ�����
		String picString = checkIsConvergeNews(errors, newsItem);
		if(!errors.isEmpty()){
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		//������

		//������������
		filterNewsText(newsItem);

		request.setAttribute("ids", picString);
		request.setAttribute("method", "modify");
		request.setAttribute("news", newsItem);
		return mapping.findForward("create");
	}

	/**
	 * ������Ŷ����Ƿ�ۺ�����
	 * �жϣ� ���Ŷ�ӦͼƬ�ĸ����󣬲��Ǹ����Ŷ���
	 * ���ͼƬ��Ӧ�ĸ������Ǹö������Ǿۺ�����
	 * @param errors
	 * @param newsItem
	 * @return
	 */
	private String checkIsConvergeNews(ActionErrors errors, News newsItem) {
		String picString = newsItem.getPictures(); // ������Ŷ�Ӧ��ͼƬID
		if (StringUtils.isEmpty(picString))
			errors.add("errors.converge.notnews", new ActionError(
					"errors.converge.notnews", "" + newsItem.getId()));
		String[] pics=picString.split(Global.CMSSEP);
		List picList=getImages(pics[0]);//ֻ��Ҫ���һ��image����ķ����Ƿ�����ţ��Ϳ����ж��Ƿ�ۺ����š�
		if(picList.size()>0){
			Picture picture=(Picture) picList.get(0);
			if(newsItem.getId()==picture.getPid()){
				errors.add("errors.converge.notnews", new ActionError(
						"errors.converge.notnews", "" + newsItem.getId()));
			}
		}else{
			errors.add("errors.converge.notnews", new ActionError(
					"errors.converge.notnews", "" + newsItem.getId()));
		}
		return picString;
	}

	/**
	 * �������������У�����з�ҳ���ݣ����������ȡ�������ѷ�ҳ����Ϣ�Լ�ͼƬȥ��
	 * 
	 * @param newsItem
	 */
	static String REGEX_STRING = "</TABLE>(.+?)</HEXUNMPCODE>";

	private void filterNewsText(News newsItem) {
		String text = newsItem.getText();
		Pattern pattern = Pattern.compile(REGEX_STRING, Pattern.DOTALL
				+ Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			newsItem.setText(matcher.group(1));
		}
	}

	/**
	 * ���pic����ϵ�string,�õ������б�, �ָ��Ϊ";" �������ظ��Ķ���
	 * 
	 * @param picString
	 * @return
	 */
	private List getImages(String picString) {
		List pictureList = new ArrayList();
		if (StringUtils.isNotEmpty(picString)) {
			String[] pics = picString.split(Global.CMSSEP);
			Set pics_set = new HashSet();
			Collections.addAll(pics_set, pics);
			Object[] pics_o = pics_set.toArray();
			for (int i = 0; i < pics_o.length; i++) {
				Picture pictureItem = (Picture) ItemManager.getInstance().get(
						new Integer((String) pics_o[i]),
						ItemInfo.getItemClass(ItemInfo.PICTURE_TYPE));
				if (pictureItem != null) {
					pictureList.add(pictureItem);
				}
			}

		}
		return pictureList;
	}

	/**
	 * ͨ��URL�е�ids����õ���Ӧ��image������б�
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward images(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String ids = request.getParameter("ids");
		List images = getImages(ids);
		outputJSON(response, images);
		return null;
	}
}
