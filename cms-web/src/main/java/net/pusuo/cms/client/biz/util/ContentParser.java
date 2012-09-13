/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.List;

import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 *
 */
public class ContentParser {
	
	private static final int SUBTYPE_ZUTU = 3; // modified some time ...
	
	public static MultiPageContent parse(News news) {
		
		if (news == null || news.getSubtype() != SUBTYPE_ZUTU)
			return null;
		
		MultiPageContent content = new MultiPageContent(news.getText());
		
		return content;
	}
	
	public static int getNewsStyle(MultiPageContent content) {
		
		if (content == null)
			return ContentGenerator.NEWS_STYLE_UNDEFINED;
		
		if (!content.isMPNews())
			return ContentGenerator.NEWS_STYLE_ONE_PAGE;
		
		return content.getStyle();
	}

	public static int getNewsStyle(News news) {
		
		MultiPageContent content = parse(news);
		return getNewsStyle(content);
	}
	
	public static List getPageList(MultiPageContent content) {
		
		if (content == null)
			return null;
		
		return content.getItems();
	}
	
}
