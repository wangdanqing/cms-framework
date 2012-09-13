/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 *
 */
public class NewsManagerProxy extends CmsManagerProxy implements NewsManager {
	
	private static final Log log = LogFactory.getLog(NewsManagerProxy.class);
	
	private NewsManager instance = null;

	private NewsManagerProxy() {
		super();
	}

	public NewsManagerProxy(NewsManager newsManager) {
		this.instance = newsManager;
	}

	public News addNews(News newsConfig, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.addNews(newsConfig, extend);
		}
		
		return news;
	}

	public News updateNews(News newsConfig, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.updateNews(newsConfig, extend);
		}
		
		return news;
	}

	public boolean deleteNews(News news) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		return instance.deleteNews(news);
	}

	public News getNews(int newsId) throws DaoException {
		
		return instance.getNews(newsId);
	}
	
	////////////////////////////////////////////////////////////////////////////

	public List moveNews(List newsList, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
			
		return instance.moveNews(newsList, parentId, extend);
	}

	public List moveNews(List newsList, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
			
		return instance.moveNews(newsList, parentName, extend);
	}

	public News moveNews(News newsConfig, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.addNews(newsConfig, extend);
		}
		
		return news;
	}

	public News moveNews(News newsConfig, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.addNews(newsConfig, extend);
		}
		
		return news;
	}
	
	////////////////////////////////////////////////////////////////////////////

	public News pushNews(News newsConfig, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.pushNews(newsConfig, parentName, extend);
		}
			
		return news;
	}

	public News pushNews(News newsConfig, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.pushNews(newsConfig, parentId, extend);
		}
			
		return news;
	}

	public News pushNews(News newsConfig, List parentNameList, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.pushNews(newsConfig, parentNameList, extend);
		}
			
		return news;
	}

	public News pushNewsByIds(News newsConfig, List parentIdList, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {
		
		News news = null;
		if (instance != null) {
			news = instance.pushNewsByIds(newsConfig, parentIdList, extend);
		}
			
		return news;
	}

}
