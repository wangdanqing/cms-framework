/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.Handler;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.Relatives;
import com.hexun.cms.client.util.SogouRelatives;
import com.hexun.cms.client.util.StockUtil;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

/**
 * @author XuLin
 *
 */
public class ContentHandler implements Handler {
	
	private static final Log log = LogFactory.getLog(ContentHandler.class);
	
	public News preHandle(News newsConfig, Map extend) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {
		
		// �Ƿ�����ת����
		boolean isRedirectNews = false;
		String reurl = newsConfig.getReurl();
		if (reurl != null && reurl.trim().length() > 0)
			isRedirectNews = true;
		
		if (isRedirectNews)
			return newsConfig;
		
		// ����ؼ���
		if(newsConfig.getId()<0){
			String keyword = newsConfig.getKeyword();
			// �����������(ֻ����·�����)
			keyword = newsConfig.getKeyword();
			if (keyword != null && keyword.trim().length() > 0) { // dirty
				String relativeNews = ItemUtil.searchRelativenewsC(keyword, newsConfig.getChannel(), 8);
				if (relativeNews != null) {
					newsConfig.setRelativenews(relativeNews.trim());
				}
			}
		}
		return newsConfig;
	}

	public News postHandle(News news, Map extend) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {
		
		// ���������е�ͼƬ(�Զ��ϴ��ⲿͼƬ)
		PictureManager pictureManager = ManagerFacade.getPictureManager();
		String contentAfterHandle = pictureManager.handleExternalPicturesFromContent(
				news.getText(), news.getId(), extend);
		if (contentAfterHandle != null && contentAfterHandle.trim().length() > 0) {
			news.setText(contentAfterHandle);
			news = (News)ItemManager.getInstance().update(news);
		}
		
		return news;
	}
}
