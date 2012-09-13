/**
 * 
 */
package net.pusuo.cms.client.biz.event.impl;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.CmsEventListener;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.NewsDiffUtil;
import com.hexun.cms.client.util.Statistic;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.ModLog;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

/**
 * @author Alfred.Yuan
 * 
 */
public class DefaultCmsEventListener implements CmsEventListener {

	private static final Log log = LogFactory
			.getLog(DefaultCmsEventListener.class);

	public DefaultCmsEventListener() {

	}

	public void update(CmsManager cmsManager, CmsEvent event) {

		fireCmsEvent(event);
	}

	protected void fireCmsEvent(CmsEvent event) {

		if (event == null || event.getSource() == null
				|| event.getEventCode() == null)
			return;

		String eventCode = event.getEventCode();

		try {
			Class[] parameterTypes = new Class[] { CmsEvent.class };
			Method method = this.getClass()
					.getMethod(eventCode, parameterTypes);
			if (method != null) {
				Object[] args = new Object[] { event };
				method.invoke(this, args);
			}
		} catch (Exception e) {
			log.error("Invoke method (" + eventCode + ") from class ("
					+ this.getClass().getName() + ") err.");
		}
	}

	protected EntityItem getEventSource(CmsEvent event) {

		EntityItem item = null;

		if (event != null) {
			Object object = event.getSource();

			if (object != null && object instanceof EntityItem) {
				item = (EntityItem) object;
			}
		}

		return item;
	}

	protected EntityItem getEventDest(CmsEvent event) {
		EntityItem item = null;
		if (event != null) {
			Object object = event.getDest();
			if (object != null && object instanceof EntityItem) {
				item = (EntityItem) object;
			}
		}
		return item;
	}

	protected Authentication getAuth(CmsEvent event) {
		Authentication auth = null;
		if (event != null) {
			Object object = event.getAuth();
			if (object != null && object instanceof Authentication) {
				auth = (Authentication) object;
			}
		}
		return auth;
	}

	// //////////////////////////////////////////////////////////////////////////

	public void afterAdd(CmsEvent event) {

		EntityItem entity = getEventSource(event);
		if (entity != null) {
			// ͳ����Ϣ
			if (entity instanceof News) {
				News news = (News) entity;
				Statistic.activeParentAndMedia(news);
			}
		}
	}

	public void afterDelete(CmsEvent event) {

	}

	public void afterUpdate(CmsEvent event) {

		EntityItem entity = getEventSource(event);

		// After Update News - By WangChao
		if (entity instanceof News && entity != null) {
			try {
				log.info("After Update News - Add to ModLog");
				News newNews = (News) this.getEventSource(event);
				News oldNews = (News) this.getEventDest(event);
				Authentication auth = this.getAuth(event);
				if (oldNews != null && newNews != null) {
					// �Ա���������Щ�޸�
					Map diffMap = NewsDiffUtil.diffMap(oldNews, newNews);
					// �����޸ļ�¼
					if (diffMap.size() > 0) {
						ModLog mlog = new ModLog();
						mlog.setModlog_nid(oldNews.getId());
						mlog.setModlog_title(oldNews.getDesc());
						if (auth != null) {
							mlog.setModlog_operator(auth.getUserName());
							mlog.setModlog_opid(auth.getUserID());
						}
						mlog.setModlog_time(new Timestamp(System
								.currentTimeMillis()));
						mlog.setModlog_property(diffMap.values().toString());
						mlog.setModlog_url(oldNews.getUrl());

						String xml = NewsDiffUtil.getXMl(oldNews);
						mlog = (ModLog) ItemManager.getInstance().update(mlog);
						String path = PageManager.getModlogPath(oldNews, mlog
								.getId());
						boolean suc = ClientFile.getInstance().write(xml, path,
								false);
						log.info("WRITE FILE - " + path + ": " + suc);
					}
				}
			} catch (Exception e) {
				log.error("Error - Save ModLog: " + e);
			}
		}
	}

	public void beforeAdd(CmsEvent event) {

		EntityItem entity = getEventSource(event);
		if (entity != null) {
			// System.out.println("before adding (" + entity.getId() + ")");
		}
	}

	public void beforeDelete(CmsEvent event) {

	}

	public void beforeUpdate(CmsEvent event) {

		EntityItem entity = getEventSource(event);
		if (entity != null) {
			System.out.println("before updating (" + entity.getId() + ")");
		}
	}

}
