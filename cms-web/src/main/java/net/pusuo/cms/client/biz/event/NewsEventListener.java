/**
 * 
 */
package net.pusuo.cms.client.biz.event;

/**
 * @author Alfred.Yuan
 *
 */
public interface NewsEventListener extends CmsEventListener {
	
	public static final String NEWS_EVENT_BEFORE_PUSH = "beforePushNews";
	public static final String NEWS_EVENT_AFTER_PUSH = "afterPushNews";

	public static final String NEWS_EVENT_BEFORE_MOVE = "beforeMoveNews";
	public static final String NEWS_EVENT_AFTER_MOVE = "afterMoveNews";
	
	////////////////////////////////////////////////////////////////////////////

	public void beforePushNews(CmsEvent event);
	
	public void afterPushNews(CmsEvent event);
	
	public void beforeMoveNews(CmsEvent event);
	
	public void afterMoveNews(CmsEvent event);
}
