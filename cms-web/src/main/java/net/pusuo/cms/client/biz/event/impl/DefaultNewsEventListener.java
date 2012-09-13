/**
 * 
 */
package net.pusuo.cms.client.biz.event.impl;

import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.NewsEventListener;
import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 *
 */
public class DefaultNewsEventListener extends DefaultCmsEventListener implements NewsEventListener {

	public DefaultNewsEventListener() {
		
	}

	public void afterMoveNews(CmsEvent event) {
				
		News news = (News)getEventSource(event);
		if (news != null) {
			//System.out.println("after moving news(" + news.getId() + ")");
		}
	}

	public void afterPushNews(CmsEvent event) {
				
		News news = (News)getEventSource(event);
		if (news != null) {
			//System.out.println("after pushing news(" + news.getId() + ")");
		}
	}

	public void beforeMoveNews(CmsEvent event) {
				
		//System.out.println("before moving news");
	}

	public void beforePushNews(CmsEvent event) {
			
		//System.out.println("before pushing news");
	}

}
