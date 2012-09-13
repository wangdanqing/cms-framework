package net.pusuo.cms.client.newshoo.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.book.ContentProcess;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.newshoo.ClientService;
import com.hexun.cms.core.News;

public class ClientServiceImpl implements ClientService {
	/**
	 * Logger for this class
	 */
	private static final Log log = LogFactory.getLog(ClientServiceImpl.class);
	/**
	 * 增加新闻
	 */
	public News addNews(News news, String pname ) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("addNews(News, String) - start addNews - pname=" + pname);
			log.debug("addNews(News, String) - start addNews - news=" + news);
			log.debug(news.getDesc());
		}
		
		log.info("This news("+news+") comes from Newshoo, pname:" + pname  + "||editor: " + news.getEditor());
		Map extend = new HashMap(); // 扩展属性
		extend.put(NewsManager.PROPERTY_NAME_PNAME, pname);
		extend.put(NewsManager.PROPERTY_NAME_TIME, new Boolean(true));

		try {
			if (!(news.getEditor() > 0))
				throw new UnauthenticatedException();
			User user = (User) ItemManager.getInstance().get(new Integer(news.getEditor()), User.class);
			Authentication auth = new Authentication(user);
			extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);
			NewsManager newsManager = ManagerFacade.getNewsManager();
			//log.info("====newshoo==="+ news.getMedia());
			//process news from newshoo
			//if(news.getMedia()!=4015){
			//	news.setText(ContentProcess.process(news.getText()));
			//}
			News result = newsManager.addNews(news, extend);

			if (log.isDebugEnabled()) {
				log.debug("addNews(News, String) - save success - result=" + result);
			}
			return result;
		} catch (PropertyException e) {
			log.error(e);
			throw new Exception(e);
		} catch (UnauthenticatedException e) {
			log.error(e);
			throw new Exception(e);
		} catch (DaoException e) {
			log.error(e);
			throw new Exception(e);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}
	/**
	 * 推送新闻
	 */
	public News pushNews(News newsConfig, String parentName, Map extend) throws Exception {
		NewsManager newsManager = ManagerFacade.getNewsManager();
//		 推送新闻
		try {
			if (!(newsConfig.getEditor() > 0))
				throw new UnauthenticatedException();
			User user = (User) ItemManager.getInstance().get(new Integer(newsConfig.getEditor()), User.class);
			Authentication auth = new Authentication(user);
			extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);
		News pushNews = newsManager.pushNews(newsConfig, parentName, extend);
		return pushNews;
		} catch (PropertyException e) {
			log.error(e);
			throw new Exception(e);
		} catch (UnauthenticatedException e) {
			log.error(e);
			throw new Exception(e);
		} catch (DaoException e) {
			log.error(e);
			throw new Exception(e);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}
	public News addNewsAndPush(News news, String pname) throws Exception {
		//News alreadyNews=addNews(news, pname);
		//AutoPush.getInstance().pushIt(alreadyNews);
		//return alreadyNews;
		return null;
	}

	public String touchMe(String pname){
		
		return "hello," + pname + ", u can connect me !";
	}
}
