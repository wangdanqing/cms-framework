/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.client.biz.Handler;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.NewsEventListener;
import com.hexun.cms.client.biz.event.impl.DefaultNewsEventListener;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.util.TemplateUtil;
import com.hexun.cms.client.compile.CompileTaskFactory;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.Template;
import com.hexun.cms.file.LocalFile;

/**
 * @author Alfred.Yuan
 * 
 */
public class NewsManagerImpl extends CmsManagerImpl implements NewsManager {

	private static final Log log = LogFactory.getLog(NewsManagerImpl.class);

	private Handler contentHandler = null;

	public NewsManagerImpl() {
		contentHandler = new ContentHandler();

		this.addListener(new DefaultNewsEventListener());
	}

	public News getNews(int newsId) throws DaoException {

		if (newsId < 0)
			return null;

		News result = null;
		try {
			Item item = ItemManager.getInstance().get(new Integer(newsId),
					News.class);
			if (item != null)
				result = (News) item;
		} catch (Throwable t) {
			log.error("getNews: get news from server.");
			throw new DaoException();
		}

		return result;
	}

	public News addNews(News newsConfig, Map extend) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {

		if (newsConfig == null)
			return null;

		// �¼�
		CmsEvent event = new CmsEvent(newsConfig,
				NewsEventListener.EVENT_BEFORE_ADD);
		this.notifyListeners(event);

		// ����id
		newsConfig.setId(-1);

		// ���ñ༭��Ϣ
		if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
			Authentication auth = (Authentication) extend
					.get(PROPERTY_NAME_AUTH);
			newsConfig.setEditor(auth.getUserID());
		}

		// ����ʱ��
		if (extend != null && !extend.containsKey(PROPERTY_NAME_TIME))
			newsConfig.setTime(new Timestamp(System.currentTimeMillis()));

		// ������չ��
		newsConfig.setExt(EXTEND_NAME_WEB_PAGE);

		return saveOrUpdateNews(newsConfig, extend);
	}

	public News updateNews(News newsConfig, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (newsConfig == null)
			return null;

		// �¼�
		CmsEvent event = new CmsEvent(newsConfig,
				NewsEventListener.EVENT_BEFORE_UPDATE);
		this.notifyListeners(event);

		int newsId = newsConfig.getId();
		if (newsId < 0)
			throw new PropertyException();

		News newsOld = getNews(newsId);
		if (newsOld == null)
			throw new PropertyException();

		// ����ԭ�ȵı༭��Ϣ
		newsConfig.setEditor(newsOld.getEditor());

		return saveOrUpdateNews(newsConfig, extend);
	}

	public boolean deleteNews(News news) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {

		return false;
	}

	// //////////////////////////////////////////////////////////////////////////

	public News pushNews(News news, String parentName, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (parentName != null && parentName.trim().length() > 0) {
			List parentNameList = new ArrayList();
			parentNameList.add(parentName);
			return pushNews(news, parentNameList, extend);
		}

		return news;
	}

	public News pushNews(News news, int parentId, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (parentId > 0) {
			List parentIdList = new ArrayList();
			parentIdList.add(new Integer(parentId));
			return pushNewsByIds(news, parentIdList, extend);
		}

		return news;
	}

	public News pushNews(News news, List parentNameList, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (parentNameList == null || parentNameList.size() == 0)
			return news;

		// ��������nameת��Ϊ������id
		List parentIdList = nameList2idList(parentNameList);
		if (parentIdList == null)
			return news;

		return pushNewsByIds(news, parentIdList, extend);
	}

	public News pushNewsByIds(News news, List parentIdList, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (news == null || news.getId() < 0 || parentIdList == null)
			throw new PropertyException();

		// ���˵��ظ��ĸ�����
		parentIdList = filterRepeatedParents(news, parentIdList);
		if (parentIdList == null || parentIdList.size() == 0)
			throw new PropertyException();

		// ��ȡ����
		int mode = PROPERTY_NAME_PUSH_MODE_LINK;
		int priority = news.getPriority();
		String desc = news.getDesc();
		Timestamp time = news.getTime();
		if (extend != null) {
			if (extend.containsKey(PROPERTY_NAME_PUSH_MODE)) {
				mode = ((Integer) extend.get(PROPERTY_NAME_PUSH_MODE))
						.intValue();
			}
			if (extend.containsKey(PROPERTY_NAME_PUSH_PRIORITY)) {
				priority = ((Integer) extend.get(PROPERTY_NAME_PUSH_PRIORITY))
						.intValue();
			}
			if (extend.containsKey(PROPERTY_NAME_PUSH_DESC)) {
				desc = (String) extend.get(PROPERTY_NAME_PUSH_DESC);
			}
			if (extend.containsKey(PROPERTY_NAME_PUSH_TIME)) {
				int timeCode = ((Integer) extend.get(PROPERTY_NAME_PUSH_TIME))
						.intValue();
				if (timeCode == PROPERTY_NAME_PUSH_TIME_NOW) {
					time = new Timestamp(System.currentTimeMillis());
				}
			}
		}

		// ��������
		String pushRecordNew = null;
		StringBuffer pushedList = new StringBuffer();
		for (int i = 0; i < parentIdList.size(); i++) {
			try {
				int parentId = ((Integer) parentIdList.get(i)).intValue();

				News pushNews = (News) ItemInfo
						.getItemByType(ItemInfo.NEWS_TYPE);
				Map extend4PushNews = new HashMap();

				pushNews.setPid(parentId);
				pushNews.setPriority(priority);
				pushNews.setDesc(desc);
				pushNews.setTime(time);
				pushNews.setStatus(news.getStatus()); // ״̬ͬ��
				pushNews.setAuthor(news.getAuthor());
				pushNews.setReferid(news.getId());

				if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
					Authentication auth = (Authentication) extend
							.get(PROPERTY_NAME_AUTH);
					pushNews.setEditor(auth.getUserID());

					extend4PushNews.put(PROPERTY_NAME_AUTH, auth);
				}

				switch (mode) {
				case PROPERTY_NAME_PUSH_MODE_COPY:
					pushNews.setSubtype(news.getSubtype());
					pushNews.setText(news.getText());
					pushNews.setMedia(news.getMedia());
					pushNews.setKeyword(news.getKeyword());
					pushNews.setSubhead(news.getSubhead());
					pushNews.setAbstract(news.getAbstract());
					pushNews.setPictures(news.getPictures());
					pushNews.setVideos(news.getVideos());
					break;
				case PROPERTY_NAME_PUSH_MODE_LINK:
				default:
					pushNews.setReurl(news.getUrl());
					pushNews.setMedia(news.getMedia());//������������ý��
					pushNews.setAbstract(news.getAbstract());
					pushNews.setKeyword("  ");
				}

				extend4PushNews.put(PROPERTY_NAME_TIME, new Boolean(true));

				pushNews = ManagerFacade.getNewsManager().addNews(pushNews,
						extend4PushNews);
				// addNews(pushNews, extend4PushNews);
				// (ʹ��ManagerFacade���Դ���ϵͳ�¼�)
				if (pushNews != null) {
					pushedList.append(pushNews.getId() + Global.CMSSEP);

					// ��������(onclick�¼�) News.SUBTYPE_ZUTU modify by shijinkui
					// 2008-04-14
					if (pushNews.getSubtype() == News.NEWS_TYPE) {
						String oldUrl = news.getUrl().substring(0,
								news.getUrl().lastIndexOf("."));
						String newUrl = pushNews.getUrl().substring(0,
								pushNews.getUrl().lastIndexOf("."));

						String content = news.getText();
						if (content != null && content.indexOf(oldUrl) > -1) {
							content = content.replaceAll(oldUrl, newUrl);
							pushNews.setText(content);
							pushNews = (News) ItemManager.getInstance().update(
									pushNews);
						}
					}
				}
			} catch (Exception e) {
			}
		}
		if (pushedList.length() > 0) {
			pushedList.delete(pushedList.length() - Global.CMSSEP.length(),
					pushedList.length());
			pushRecordNew = pushedList.toString();
		}

		// ����Դ����
		if (pushRecordNew != null) {
			String pushRecord = "";

			String pushRecordOld = news.getPushrecord();
			if (pushRecordOld != null && pushRecordOld.trim().length() > 0) {
				if (!pushRecordOld.endsWith(Global.CMSSEP))
					pushRecordOld += Global.CMSSEP;
				pushRecord = pushRecordOld;
			}

			pushRecord += pushRecordNew;

			news.setPushrecord(pushRecord);

			news = (News) ItemManager.getInstance().update(news);
			if (news == null)
				throw new DaoException();
		}

		// ͬ����ǰ���������ŵ�״̬
		// ......

		return news;
	}

	// //////////////////////////////////////////////////////////////////////////

	public News moveNews(News news, int parentId, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (news != null && news.getId() > 0) {
			List newsList = new ArrayList();
			newsList.add(news);
			List newsListNew = moveNews(newsList, parentId, extend);
			if (newsListNew != null && newsListNew.size() == 1) {
				return (News) newsListNew.get(0);
			}
		}

		return news;
	}

	public News moveNews(News news, String parentName, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (news != null && news.getId() > 0) {
			List newsList = new ArrayList();
			newsList.add(news);
			List newsListNew = moveNews(newsList, parentName, extend);
			if (newsListNew != null && newsListNew.size() == 1) {
				return (News) newsListNew.get(0);
			}
		}

		return news;
	}

	public List moveNews(List newsList, String parentName, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (parentName != null && parentName.trim().length() > 0) {
			Item item = ItemManager.getInstance().getItemByName(parentName,
					Subject.class);
			if (item == null)
				throw new PropertyException();
			return moveNews(newsList, item.getId(), extend);
		}

		return newsList;
	}

	public List moveNews(List newsList, int parentId, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		if (newsList == null || parentId < 0)
			throw new PropertyException();

		Item item = ItemManager.getInstance().get(new Integer(parentId),
				Subject.class);
		if (item == null)
			throw new ParentNameException();
		Subject parent = (Subject) item;

		List newsListNew = new ArrayList();

		for (int i = 0; i < newsList.size(); i++) {
			News news = (News) newsList.get(i);
			if (news.getPid() != parent.getId()) {
				news.setPid(parent.getId());
				news = updateNews(news, extend);
				newsListNew.add(news);
			}
		}

		return newsListNew;
	}

	// //////////////////////////////////////////////////////////////////////////

	private News saveOrUpdateNews(News newsConfig, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException {

		boolean isSaveAction = newsConfig.getId() < 0 ? true : false;

		// �������ŵĸ�����:name����,id��֮
		Item parentItem = null;
		if (extend != null && extend.containsKey(PROPERTY_NAME_PNAME)) {
			String parentName = (String) extend.get(PROPERTY_NAME_PNAME);
			if (parentName != null && parentName.trim().length() > 0) {
				parentItem = ItemManager.getInstance().getItemByName(
						parentName, Subject.class);
				if (parentItem != null && parentItem.getId() > 0) {
					newsConfig.setPid(parentItem.getId());
				}
			}
		}
		if (newsConfig.getPid() < 0)
			throw new ParentNameException();

		// ��֤���������Ч��
		if (parentItem == null)
			parentItem = ItemManager.getInstance().get(
					new Integer(newsConfig.getPid()), Subject.class);
		if (parentItem == null)
			throw new PropertyException();
		Subject parent = (Subject) parentItem;

		// ��ݸ���������Ƶ��
		newsConfig.setChannel(parent.getChannel());

		// ��ݸ���������ģ��(ֻ����·�����)
		if (isSaveAction) { // dirty
			int templateId = -1;
			if (newsConfig.getSubtype() == News.SUBTYPE_VIDEO) {
				templateId = TemplateUtil.processVideoTemplate(parent,
						newsConfig.getTemplate());
			} else if(newsConfig.getSubtype() == News.SUBTYPE_ZUTU){
				templateId = TemplateUtil.processZutuTemplate(parent, newsConfig.getTemplate());
			} else {
				templateId = TemplateUtil.processNewsTemplate(parent,
						newsConfig.getTemplate());
			}
			newsConfig.setTemplate("" + templateId);
		}

		// ���ⲻ��Ϊ��
		String desc = newsConfig.getDesc();
		if (desc == null || desc.trim().length() == 0)
			throw new PropertyException();

		// ���������ݽ���Ԥ����
		newsConfig = contentHandler.preHandle(newsConfig, extend);

		// �����ɵ�״ֵ̬:(ֻ��Ը�������)
		// ���EntityItem.setPid(...)�ȵ����,����ε���,�ɵ�״ֵ̬�ͻᱻ�ƻ�
		News newsOrigin = null;
		if (!isSaveAction) {
			newsOrigin = ManagerFacade.getNewsManager().getNews(
					newsConfig.getId());
			if (newsOrigin == null)
				throw new DaoException();
			newsConfig.setOldpid(newsOrigin.getPid());
			newsConfig.setOldpriority(newsOrigin.getPriority());
			newsConfig.setOldstatus(newsOrigin.getStatus());
		}

		// ��������
		News news = null;
		try {
			Item item = ItemManager.getInstance().update(newsConfig);
			if (item != null)
				news = (News) item;
			else
				throw new DaoException();
		} catch (Throwable t) {
			log.error("saveOrUpdateNews: update news to server.");
			throw new DaoException();
		}

		// ���������ݽ��к���
		extend.put(PROPERTY_NAME_IS_SAVE_ACTION, new Boolean(isSaveAction));
		news = contentHandler.postHandle(news, extend);

		// ͬ���������ŵ�״̬
		String pushRecord = news.getPushrecord();
		if (pushRecord != null && pushRecord.trim().length() > 0) {
			List pushNewsList = EntityParamUtil.getIdList(pushRecord,
					Global.CMSSEP);
			if (pushNewsList != null && pushNewsList.size() > 0) {
				for (int i = 0; i < pushNewsList.size(); i++) {
					try {
						Integer pushNewsId = (Integer) pushNewsList.get(i);
						Item item = ItemManager.getInstance().get(pushNewsId,
								News.class);
						if (item != null) {
							News pushNews = (News) item;
							pushNews.setStatus(news.getStatus());
							ItemManager.getInstance().update(pushNews);
						}
					} catch (Exception e) {
					}
				}
			}
		}

		// ������ҳ���ŵı���(ֻ����·�����)
		if (isSaveAction) {
			EntityItem parentEntity = parent;
			while (parentEntity != null
					&& parentEntity.getType() == ItemInfo.SUBJECT_TYPE) {
				putPaginationQueue(parentEntity);
				parentEntity = (EntityItem) ItemManager.getInstance().get(
						new Integer(parentEntity.getPid()), EntityItem.class);
			}
		}

		News oldNews = null, newNews = null;
		try {
			oldNews = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
			newNews = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
			BeanUtils.copyProperties(newNews, newsConfig);
			BeanUtils.copyProperties(oldNews, newsOrigin);
		} catch (Exception e) {
			log.error("Error - Bean Copy for ModLog");
		}
		// �¼�
		CmsEvent event = new CmsEvent(newNews, oldNews, extend
				.get(CmsManager.PROPERTY_NAME_AUTH),
				isSaveAction ? NewsEventListener.EVENT_AFTER_ADD
						: NewsEventListener.EVENT_AFTER_UPDATE);
		this.notifyListeners(event);

		return news;
	}

	/**
	 * ������������б�ת��Ϊ������id�б�.
	 */
	private static List nameList2idList(List parentNameList) {

		if (parentNameList != null) {
			List idList = new ArrayList();

			for (int i = 0; i < parentNameList.size(); i++) {
				String parentName = (String) parentNameList.get(i);
				if (parentName != null && parentName.trim().length() > 0) {
					Item item = ItemManager.getInstance().getItemByName(
							parentName, EntityItem.class);
					if (item != null && item.getId() > 0) {
						idList.add(new Integer(item.getId()));
					}
				}
			}

			return idList;
		}

		return null;
	}

	/**
	 * ���������ŵ�ʱ��,���˵�����Ϲ淶�ĸ�����
	 */
	private static List filterRepeatedParents(News news, List parentIdList) {

		if (news == null || parentIdList == null || parentIdList.size() == 0)
			return null;

		// ���˵�Դ���ŵĸ�����id
		CollectionUtils.filter(parentIdList, new NotPredicate(
				new EqualPredicate(new Integer(news.getPid()))));

		// ���˵��ظ��ĸ�����id
		CollectionUtils.filter(parentIdList, new UniquePredicate());

		// ��ȡ���ͼ�¼�ĸ�����id�б�
		List idList = new ArrayList();
		List pushRecordList = EntityParamUtil.getIdList(news.getPushrecord(),
				Global.CMSSEP);
		for (int i = 0; pushRecordList != null && i < pushRecordList.size(); i++) {
			try {
				int newsId = ((Integer) pushRecordList.get(i)).intValue();
				Item item = ItemManager.getInstance().get(new Integer(newsId),
						News.class);
				if (item != null) {
					News temp = (News) item;
					int parentId = temp.getPid();
					if (parentId > 0)
						idList.add(new Integer(parentId));
				}
			} catch (Exception e) {
			}
		}

		// ���˵������ͼ�¼���Ѿ����ڵĸ�����
		List result = parentIdList;
		if (idList.size() > 0) {
			Collection collection = CollectionUtils.subtract(parentIdList,
					idList);
			result = new ArrayList(collection);
		}

		return result;
	}

	private static void putPaginationQueue(EntityItem entity) {

		if (entity.getType() != ItemInfo.SUBJECT_TYPE)
			return;

		try {
			if (!haveMPageTemplate(entity))
				return;

			String queue = Configuration.getInstance().get(
					"cms4.client.pagination.queue");
			if (queue == null || queue.length() == 0) {
				return;
			}

			synchronized (CompileTaskFactory.PQUEUE_LOCK) {
				String content = LocalFile.read(queue);
				if (content == null)
					content = "";
				if (content.equals("")) {
					content = "" + entity.getId();
					LocalFile.write(content, queue);
				} else {
					if (content.indexOf(entity.getId() + "") >= 0) {
						return;
					} else {
						content += ";" + entity.getId();
						LocalFile.write(content, queue);
					}
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	private static boolean haveMPageTemplate(EntityItem entity)
			throws Exception {

		boolean ret = false;

		String[] templates = entity.getTemplate().split(";");
		for (int i = 0; i < templates.length; i++) {
			int tid = Integer.parseInt(templates[i].split(",")[0]);
			Template template = (Template) ItemManager.getInstance().get(
					new Integer(tid), Template.class);
			if (template.getMpage() == 2) {
				ret = true;
				break;
			}
		}

		return ret;
	}

}
