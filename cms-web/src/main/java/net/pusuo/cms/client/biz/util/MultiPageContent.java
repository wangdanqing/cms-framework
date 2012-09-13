/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.ArrayList;
import java.util.List;

import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 *
 */
public class MultiPageContent {
	
	// common text
	public static final String BEGIN_HEXUNMPCOMMON = "<HEXUNMPCOMMON>";
	public static final String END_HEXUNMPCOMMON = "</HEXUNMPCOMMON>";
	
	// top banner
	public static final String BEGIN_HEXUNMPBANNER_TOP = "<HEXUNMPBANNERTOP>";
	public static final String END_HEXUNMPBANNER_TOP = "</HEXUNMPBANNERTOP>";
	
	// content
	public static final String BEGIN_HEXUNMPCODE = "<HEXUNMPCODE>";
	public static final String END_HEXUNMPCODE = "</HEXUNMPCODE>";

	public static final String BEGIN_HEXUNSUBHEAD = "<HEXUNSUBHEAD>";
	public static final String END_HEXUNSUBHEAD = "</HEXUNSUBHEAD>";

	// bottom banner
	public static final String BEGIN_HEXUNMPBANNER = "<HEXUNMPBANNER>";
	public static final String END_HEXUNMPBANNER = "</HEXUNMPBANNER>";
	
	// ���������ʽ:��ҳ-����ҳ
	public static final String TAG_HEXUNMPANDINDEX = "<HEXUNMPANDINDEX/>";

	private News news = null;
	
	private String text = null;
	private boolean parsed = false;
	
	private String commonText = null;	
	private String bannerTop = null;
	private List items = null;
	private String bannerBottom = null;
	
	private boolean isMPNews = false;
	private int style = ContentGenerator.NEWS_STYLE_MULTI_PAGE;
	private int pageCount = 0;
	
	public MultiPageContent(News news) {
		if (news != null) {
			this.text = news.getText();
			this.news = news;
		}
	}
	
	public MultiPageContent(String text) {
		this.text = text;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private void parseText() {
		
		parsed = true;
		
		if (text == null || text.trim().length() == 0)
			return;
		
		pageCount = getPageNumber();
		if (pageCount == 0)
			return;
		
		markStyle();
		
		parseCommonText();
		
		parseBannerTop();
		
		for (int i = 0; i < pageCount; i++) {
			MultiPageItem item = parseItem(i);
			if (items == null)
				items = new ArrayList();
			if (item != null)
				items.add(item);
		}
		
		parseBannerBottom();
	}
	
	private int getPageNumber() {
		
		int count = 0;
		
		int idx = 0;
		while (true) {
			idx = text.indexOf(BEGIN_HEXUNMPCODE, idx);
			if (idx == -1) {
				break;
			} else {
				count++;
			}
			idx = idx + BEGIN_HEXUNMPCODE.length();
		}
		
		return count;
	}
	
	private void markStyle() {
		isMPNews = true;
		if (text.indexOf(TAG_HEXUNMPANDINDEX) > -1)
			style = ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX;		
	}
	
	private void parseCommonText() {
		
		commonText = getContentByTag(text, 0, BEGIN_HEXUNMPCOMMON, END_HEXUNMPCOMMON);
	}
	
	private void parseBannerTop() {
		
		bannerTop = getContentByTag(text, 0, BEGIN_HEXUNMPBANNER_TOP, END_HEXUNMPBANNER_TOP);
	}
	
	private void parseBannerBottom() {
		
		bannerBottom = getContentByTag(text, 0, BEGIN_HEXUNMPBANNER, END_HEXUNMPBANNER);
	}
	
	private MultiPageItem parseItem(int page) {
		
		String textItem = getContentByTag(text, page, BEGIN_HEXUNMPCODE, END_HEXUNMPCODE);
		
		String subhead = getContentByTag(textItem, 0, BEGIN_HEXUNSUBHEAD, END_HEXUNSUBHEAD);
		
		String subtext = "";
		int idx = textItem.indexOf(END_HEXUNSUBHEAD);
		if (idx != -1 && idx + END_HEXUNSUBHEAD.length() < textItem.length()) {
			subtext = textItem.substring(idx + END_HEXUNSUBHEAD.length());
		}
		
		return new MultiPageItem(subhead, subtext);
	}
	
	public static String getContentByTag(String content, int page, String tagBegin, String tagEnd) {
		
		String ret = content;
		
		for (int i = 0; i <= page; i++) {
			int idx1 = ret.indexOf(tagBegin);
			int idx2 = ret.indexOf(tagEnd);
			if (idx2 >= idx1 + tagBegin.length()) {
				if (i == page) {
					ret = ret.substring(idx1 + tagBegin.length(), idx2);
					break;
				} else {
					ret = ret.substring(idx2 + tagEnd.length());
				}
			} else {
				ret = "";
				break;
			}
		}
		
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////
	
	public String getBannerTop() {
		if (!parsed)
			parseText();
		return bannerTop;
	}
	
	public String getBannerBottom() {
		if (!parsed)
			parseText();
		return bannerBottom;
	}
	
	public String getCommonText() {
		if (!parsed)
			parseText();
		return commonText;
	}
	
	public List getItems() {
		if (!parsed)
			parseText();
		return items;
	}
	
	public boolean isMPNews() {
		if (!parsed)
			parseText();
		return isMPNews;
	}
	
	public int getStyle() {
		if (!parsed)
			parseText();
		return style;
	}
		
	public int getPageCount() {
		if (!parsed)
			parseText();
		return pageCount;
	}
}
