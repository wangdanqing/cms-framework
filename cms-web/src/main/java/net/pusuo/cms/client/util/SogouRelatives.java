/**
 * 
 */
package net.pusuo.cms.client.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.client.view.ViewContext;

/**
 * @author Alfred.Yuan
 *
 */
public class SogouRelatives {

	private static final Log log = LogFactory.getLog(SogouRelatives.class);
	
	public static SimpleDateFormat format4Sogou = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat format4CMS = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public final static int NEWS_COUNT = 10; 
	public final static int HINT_COUNT = 10; 
	public final static int BLOG_COUNT = 10; 
	public final static int SAYBAR_COUNT = 5; 
	
	public final static String SOGOU_INTERFACE_URL = "http://10.10.66.88/result.jsp";
	public final static int SOGOU_INTERFACE_TIMEOUT = 3000;
	
	public static String dot = null;
	static {
		try {
			dot = new String("��".getBytes("ISO_8859_1"), "GBK");
		}
		catch (Exception e) {
		}
	}
	
	public static Relatives getSogouRelatives(News news, int newsCount, 
			int hintCount, int blogCount, int saybarCount) {
		
		if (news == null || news.getId() < 0)
			return null;
		
//		int channelId = news.getChannel();
//		Channel channel = (Channel)ItemManager.getInstance().get(new Integer(channelId), Channel.class);
//		if (channel == null)
//			return null;
		
		Map params = new HashMap();
		params.put("url", news.getUrl());
		params.put("channel", "hexun.com");// + channel.getName());
		
		params.put("news_count", String.valueOf(newsCount));			
		params.put("hint_count", String.valueOf(hintCount));			
		params.put("blog_count", String.valueOf(blogCount));
		params.put("saybar_count", String.valueOf(saybarCount));			
		params.put("hotbar_count", String.valueOf(0));			
		
		params.put("title", Util.GBKToUnicode(StringUtils.trimToEmpty(news.getDesc())));
		params.put("content", Util.GBKToUnicode(StringUtils.trimToEmpty(news.getText())));
		params.put("keyword", Util.GBKToUnicode(StringUtils.trimToEmpty(news.getKeyword())));
		
		long timeStart = System.currentTimeMillis();
		String xmlText = ClientHttpFile.wgetIfcString(SOGOU_INTERFACE_URL, params, SOGOU_INTERFACE_TIMEOUT);
		long timeEnd = System.currentTimeMillis();
		log.info("Cost of ClientHttpFile.wgetIfcString: " 
				+ (timeEnd - timeStart) + ". (id=" + news.getId() + ")");
		
		if (xmlText == null || xmlText.trim().length() == 0) {
			log.info("Return of ClientHttpFile.wgetIfcString is null. (id=" + news.getId() + ")");
			return null;
		}
		
		Relatives relatives = new Relatives();
		
		try {
			Document doc = DocumentHelper.parseText(xmlText);
			Element root = doc.getRootElement();

			List keywordList = root.selectNodes("/relatives/keyword/@value");
			if (keywordList != null && keywordList.size() > 0) {
				List keywordList4Relatives = new ArrayList();
				for (int i = 0; i < keywordList.size(); i++) {
					Attribute attribute = (Attribute)keywordList.get(i);
					String value = attribute.getValue();
					if (value != null && value.trim().length() > 0)
						keywordList4Relatives.add(value);
				}
				relatives.setKeywordList(keywordList4Relatives);
			}
			
			List newsList = root.selectNodes("/relatives/news");
			relatives.setNewsList(parseItems(newsList, relatives));
			
			List hintList = root.selectNodes("/relatives/hint");
			relatives.setHintList(parseItems(hintList, relatives));
		
			List blogList = root.selectNodes("/relatives/blog");
			relatives.setBlogList(parseItems(blogList, relatives));
			
			List saybarList = root.selectNodes("/relatives/saybar");
			relatives.setSaybarList(parseItems(saybarList, relatives));
		}
		catch (Exception e) {
			log.error("Parsing SogouRelatives err.");
			return null;
		}
	
		return relatives;
	}
	
	private static List parseItems(List nodeList, Relatives relatives) {
		
		if (nodeList == null || nodeList.size() == 0)
			return null;
		
		List result = new ArrayList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = (Node)nodeList.get(i);
			Relatives.Item item = relatives.new Item();
			try {
				String title = node.valueOf("@value");
				String href = node.valueOf("@href");
				String time = node.valueOf("@pubtime");
				String picUrl = node.valueOf("@picurl");
				String summary = node.valueOf("@summary");
				String secondTitle = node.valueOf("@author");
				String secondHref = node.valueOf("@site");
				
				if (title != null && title.trim().length() > 0)
					item.setTitle(title);
				if (href != null && href.trim().length() > 0)
					item.setHref(href);
				if (time != null && time.trim().length() > 0)
					item.setTime(new Timestamp(format4Sogou.parse(time).getTime()));
				if (picUrl != null && picUrl.trim().length() > 0)
					item.setPicUrl(picUrl);
				if (summary != null && summary.trim().length() > 0)
					item.setSummary(summary) ;
				if (secondTitle != null && secondTitle.trim().length() > 0)
					item.setSecondTitle(secondTitle);
				if (secondHref != null && secondHref.trim().length() > 0)
					item.setSecondHref(secondHref);
			} catch (Exception e) {
				log.error("SogouRelatives: parsing item error.");
				continue;
			}
			result.add(item);
		}
		
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static String getKeywordContent(Relatives relatives) {
		
		if (relatives == null)
			return null;
		
		List keywordList = relatives.getKeywordList();
		if (keywordList == null || keywordList.size() == 0)
			return null;
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < keywordList.size(); i++) {
			String keyword = (String)keywordList.get(i);
			if (keyword != null && keyword.trim().length() > 0) {
				sb.append(keyword.trim());
				sb.append(" ");
			}
		}
		
		return sb.toString().trim();
	}
	
	public static String getRelativeNewsContent(Relatives relatives) {
		
		if (relatives == null)
			return null;
		
		List relativeList = relatives.getNewsList();
		if (relativeList == null || relativeList.size() == 0)
			return null;
		
//		StringBuffer sb = new StringBuffer();
//		
//		for (int i = 0; i < relativeList.size(); i++) {
//			Relatives.Item item = (Relatives.Item)relativeList.get(i);
//			sb.append("<li>");
//			sb.append(dot);
//			sb.append("<a href=\"");
//			sb.append(			  item.getHref());
//			sb.append(                            "\" onclick=\"return sogouRelateNews(this);\" target=\"_blank\">");
//			sb.append(trimToLength(item.getTitle(), MAX_LENGTH_NEWS, DOT_COUNT_LONG));
//			sb.append("</a><span>(");
//			sb.append(             format4CMS.format(item.getTime()));
//			sb.append(                                         ")</span></li>");
//		}
//		
//		return sb.toString().trim();
		
		ViewContext context = new ViewContext();
		context.put("relatives", relativeList);
		context.put("instance", new SogouRelatives());
		String content = ViewEngine.getViewManager().getContent("biz/relatives/relative-news.vm", context);
		
		return content;
	}

	public static String getRelativeNewsContent4Decorate(Relatives relatives, String cssClass) {
		
		if (relatives == null)
			return null;
		
		if (cssClass == null || cssClass.trim().length() == 0)
			return getRelativeNewsContent(relatives);
		
		List relativeList = relatives.getNewsList();
		if (relativeList == null || relativeList.size() == 0)
			return null;
		
//		StringBuffer sb = new StringBuffer();
//		
//		for (int i = 0; i < relativeList.size(); i++) {
//			Relatives.Item item = (Relatives.Item)relativeList.get(i);
//			sb.append("<li ");
//			sb.append(cssClass.trim());
//			sb.append(">");
//			sb.append("<a href=\"");
//			sb.append(			  item.getHref());
//			sb.append(                            "\" target=\"_blank\">");
//			sb.append(trimToLength(item.getTitle(), MAX_LENGTH_NEWS, DOT_COUNT_LONG));
//			sb.append("</a><span>(");
//			sb.append(             format4CMS.format(item.getTime()));
//			sb.append(                                         ")</span></li>");
//		}
//		
//		return sb.toString().trim();
		
		ViewContext context = new ViewContext();
		context.put("relatives", relativeList);
		context.put("cssClass", cssClass.trim());
		context.put("instance", new SogouRelatives());
		String content = ViewEngine.getViewManager().getContent("biz/relatives/relative-news-decorate.vm", context);
		
		return content;
	}

	public static String readRelativeNewsContent(News news) {
		
		if (news == null)
			return null;
		
		String content = null;
		
		try {
			String storePath = PageManager.getRelativeNewsPath(news, true);
			if (storePath != null && storePath.trim().length() > 0) {
				content = ClientFile.getInstance().read(storePath);
				if (content != null)
					content = Util.unicodeToGBK(content);
			}
		} catch (Exception e) {
			log.error("read err." + e);
			content = null;
		}
		
		return content;
	}
	
	public static void writeRelativeNewsContent(News news, String content) {
		
		if (news == null)
			return;
		
		if (content == null)
			content = "";
		
		try {
			String storePath = PageManager.getRelativeNewsPath(news, true);
			if (storePath != null && storePath.trim().length() > 0)
				ClientFile.getInstance().write(Util.GBKToUnicode(content), storePath);
		} catch (Exception e) {
			log.error("write err." + e);
		}
	}
	
	public static String getRelativeHintContent(Relatives relatives) {
		
		if (relatives == null)
			return null;
		
		List relativeList = relatives.getHintList();
		if (relativeList == null || relativeList.size() == 0)
			return null;
		
//		StringBuffer sb = new StringBuffer();
//		
//		for (int i = 0; i < relativeList.size(); i++) {
//			Relatives.Item item = (Relatives.Item)relativeList.get(i);
//			sb.append("<li><a href=\"");
//			sb.append(               item.getHref());
//			sb.append(                             "\" onclick=\"return sogouRelateWeb(this);\" target=\"_blank\">");
//			sb.append(trimToLength(item.getTitle(), MAX_LENGTH_HINT, DOT_COUNT_LONG));
//			sb.append(                                                                   "</a></li>");
//		}
//		
//		return sb.toString().trim();
		
		ViewContext context = new ViewContext();
		context.put("relatives", relativeList);
		context.put("instance", new SogouRelatives());
		String content = ViewEngine.getViewManager().getContent("biz/relatives/relative-hint.vm", context);

		return content;
	}
	
	public static String readRelativeHintContent(News news) {
		
		if (news == null)
			return null;
		
		String content = null;
		
		try {
			String storePath = PageManager.getRelativeHintPath(news, true);
			if (storePath != null && storePath.trim().length() > 0) {
				content = ClientFile.getInstance().read(storePath);
				if (content != null)
					content = Util.unicodeToGBK(content);
			}
		} catch (Exception e) {
			log.error("read err." + e);
			content = null;
		}
		
		return content;
	}
	
	public static void writeRelativeHintContent(News news, String content) {
		
		if (news == null)
			return;
		
		if (content == null)
			content = "";
		
		try {
			String storePath = PageManager.getRelativeHintPath(news, true);
			if (storePath != null && storePath.trim().length() > 0) {
				ClientFile.getInstance().write(Util.GBKToUnicode(content), storePath);
			}
		} catch (Exception e) {
			log.error("write err." + e);
		}
	}
	
	public static String getRelativeBlogContent(Relatives relatives) {
		
		if (relatives == null)
			return null;
		
		List relativeList = relatives.getBlogList();
		if (relativeList == null || relativeList.size() == 0)
			return null;
		
//		StringBuffer sb = new StringBuffer();
//		
//		for (int i = 0; i < relativeList.size(); i++) {
//			Relatives.Item item = (Relatives.Item)relativeList.get(i);
//			
//			sb.append("<tr><td class=\"a\">");
//			sb.append(						dot);
//			sb.append(						   "<a href=\"");
//			sb.append(                                     item.getSecondHref());
//			sb.append(                                        "\" onclick=\"return sogouRelateBlog(this);\" target=\"_blank\">");
//			sb.append(trimToLength(item.getSecondTitle(), 4, 0));
//			sb.append(						   ":</a>");
//			sb.append(						   "<a href=\"");
//			sb.append(                                     item.getHref());
//			sb.append(                                        "\" onclick=\"return sogouRelateBlog(this);\" target=\"_blank\">");
//			sb.append(trimToLength(item.getTitle(), MAX_LENGTH_BLOG, 0));
//			sb.append(                         "</a></td></tr>");
//		}
//		
//		return sb.toString().trim();
		
		ViewContext context = new ViewContext();
		context.put("relatives", relativeList);
		context.put("instance", new SogouRelatives());
		String content = ViewEngine.getViewManager().getContent("biz/relatives/relative-blog.vm", context);

		return content;
	}
	
	public static String readRelativeBlogContent(News news) {
		
		if (news == null)
			return null;
		
		String content = null;
		
		try {
			String storePath = PageManager.getRelativeBlogPath(news, true);
			if (storePath != null && storePath.trim().length() > 0) {
				content = ClientFile.getInstance().read(storePath);
				if (content != null)
					content = Util.unicodeToGBK(content);
			}
		} catch (Exception e) {
			log.error("read err." + e);
			content = null;
		}
		
		return content;
	}
	
	public static void writeRelativeBlogContent(News news, String content) {
		
		if (news == null)
			return;
		
		if (content == null)
			content = "";
		
		try {
			String storePath = PageManager.getRelativeBlogPath(news, true);
			if (storePath != null && storePath.trim().length() > 0)
				ClientFile.getInstance().write(Util.GBKToUnicode(content), storePath);
		} catch (Exception e) {
			log.error("write err." + e);
		}
	}
	
	public static String getRelativeSaybarContent(Relatives relatives) {
		
		if (relatives == null)
			return null;
		
		List relativeList = relatives.getSaybarList();
		if (relativeList == null || relativeList.size() == 0)
			return null;
		
		// �ж��Ƿ��ȡ��ȡ��ʩ
		boolean needTruncation = false;
		int realLength = 0;
		for (int i = 0; i < relativeList.size(); i++) {
			Relatives.Item item = (Relatives.Item)relativeList.get(i);
			realLength += getRealLength(item.getTitle());
		}
		if (realLength > MAX_LENGTH_SAYBAR * SAYBAR_COUNT * 2)
			needTruncation = true;
		
//		StringBuffer sb = new StringBuffer();
//		
//		for (int i = 0; i < relativeList.size(); i++) {
//			Relatives.Item item = (Relatives.Item)relativeList.get(i);
//			sb.append("<a href=\"");
//			sb.append(           item.getHref());
//			sb.append(                         "\" onclick=\"return sogouRelateSaybar(this);\" target=\"_blank\">");
//			if (needTruncation)
//				sb.append(trimToLength(item.getTitle(), MAX_LENGTH_SAYBAR, DOT_COUNT_SHORT));
//			else
//				sb.append(item.getTitle());
//			sb.append(                                                               "</a> | ");
//		}
//		int index = sb.lastIndexOf("|");
//		sb.delete(index, sb.length());
//		
//		return sb.toString().trim();
		
		ViewContext context = new ViewContext();
		context.put("relatives", relativeList);
		context.put("needTruncation", new Boolean(needTruncation));
		context.put("instance", new SogouRelatives());
		String content = ViewEngine.getViewManager().getContent("biz/relatives/relative-saybar.vm", context);

		return content;
	}
	
	public static String readRelativeSaybarContent(News news) {
		
		if (news == null)
			return null;
		
		String content = null;
		
		try {
			String storePath = PageManager.getRelativeSaybarPath(news, true);
			if (storePath != null && storePath.trim().length() > 0) {
				content = ClientFile.getInstance().read(storePath);
				if (content != null)
					content = Util.unicodeToGBK(content);
			}
		} catch (Exception e) {
			log.error("read err." + e);
			content = null;
		}
		
		return content;
	}
	
	public static void writeRelativeSaybarContent(News news, String content) {
		
		if (news == null)
			return;
		
		if (content == null)
			content = "";
		
		try {
			String storePath = PageManager.getRelativeSaybarPath(news, true);
			if (storePath != null && storePath.trim().length() > 0)
				ClientFile.getInstance().write(Util.GBKToUnicode(content), storePath);
		} catch (Exception e) {
			log.error("write err." + e);
		}
	}
	
	public static SimpleDateFormat getFormatter() {

		return format4CMS;
	}
	
	public final static int MAX_LENGTH_NEWS = 23;
	public final static int MAX_LENGTH_HINT = 10;
	public final static int MAX_LENGTH_BLOG = 12;
	public final static int MAX_LENGTH_AUTH = 6;
	public final static int MAX_LENGTH_SAYBAR = 5;
	
	public final static int DOT_COUNT_LONG = 3;
	public final static int DOT_COUNT_SHORT = 2;
	
	/**
	 * ���ַ��ֹ��һ������
	 * @param content
	 * @param maxLength ˫�ֽ��ַ����󳤶�
	 * @param dotCount ׷�ӵ�"."�ĸ���
	 * @return
	 */
	public static String trimToLength(String content, int maxLength, int dotCount) {
		
		// ɾ������ַ�(char<=32)�Ϳո�,���ҽ�nullת��Ϊ""
		content = StringUtils.trimToEmpty(content);

		if (content.trim().length() == 0)
			return "";
		
		if (maxLength <= 0)
			return "";
		
		if (maxLength >= content.length())
			return content;
		
		if (dotCount >= maxLength)
			return content;
		
		// ����ʵ�ʳ���
		int position = 0;
		int realLength = 0;
		for (int i = 0; i < content.length(); i++ ) {
			int charCode = content.charAt(i);		
			int charLength = 1;
			if (charCode <= 0 || charCode >= 126)
				charLength = 2;
			realLength += charLength;
			
			if (realLength <= (maxLength - Math.round((float)dotCount / 2)) * 2)
				position++;
		}
		
		// �ضϳ�������
		if (realLength > maxLength * 2) {
			StringBuffer sb = new StringBuffer();
			sb.append(content.substring(0, position));
			for (int i = 0; i < dotCount; i++) {
				sb.append(".");
			}
			return sb.toString();
		}
		
		return content;
	}
	
	/**
	 * ��ȡһ���ַ���ֽ���
	 * @param content
	 * @return
	 */
	public static int getRealLength(String content) {
		
		if (content == null || content.length() == 0)
			return 0;
		
		int realLength = 0;
		
		for (int i = 0; i < content.length(); i++ ) {
			int charCode = content.charAt(i);		
			int charLength = 1;
			if (charCode <= 0 || charCode >= 126)
				charLength = 2;
			realLength += charLength;
		}
		
		return realLength;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static long testJava(int count) {
		
		Relatives relativesInstance = new Relatives();
		
		Relatives.Item item1 = relativesInstance.new Item();
		item1.setHref("http://www.sina.com.cn");
		item1.setTitle("�������ϯ� ��ͦ������û�н�����յĻ��� ����������촽 ��������˫˫������̺");
		item1.setTime(new Timestamp(new java.util.Date().getTime()));

		Relatives.Item item2 = relativesInstance.new Item();
		item2.setHref("http://www.hexun.com");
		item2.setTitle("����ֳ�:��������ޱ������ӵ");
		item2.setTime(new Timestamp(new java.util.Date().getTime()));
		
		List relatives = new ArrayList();
		relatives.add(item1);
		relatives.add(item2);
		
		long timeStart = System.currentTimeMillis();
		
		for (int k = 0; k < count; k++) {
			for (int i = 0; i < relatives.size(); i++) {
				StringBuffer sb = new StringBuffer();
				
				Relatives.Item item = (Relatives.Item)relatives.get(i);
				sb.append("<li>");
				sb.append(dot);
				sb.append("<a href=\"");
				sb.append(			  item.getHref());
				sb.append(                            "\" onclick=\"return sogouRelateNews(this);\" target=\"_blank\">");
				sb.append(trimToLength(item.getTitle(), MAX_LENGTH_NEWS, DOT_COUNT_LONG));
				sb.append("</a><span>(");
				sb.append(             format4CMS.format(item.getTime()));
				sb.append(                                         ")</span></li>");
			}
		}
		
		long timeEnd = System.currentTimeMillis();
		
		return (timeEnd - timeStart);
	}
	
	public static long testVelocity(int count) {
		
		Relatives relativesInstance = new Relatives();
		
		Relatives.Item item = relativesInstance.new Item();
		item.setHref("http://www.sina.com.cn");
		item.setTitle("�������ϯ� ��ͦ������û�н�����յĻ��� ����������촽 ��������˫˫������̺");
		item.setTime(new Timestamp(new java.util.Date().getTime()));

		Relatives.Item item2 = relativesInstance.new Item();
		item2.setHref("http://www.hexun.com");
		item2.setTitle("����ֳ�:��������ޱ������ӵ");
		item2.setTime(new Timestamp(new java.util.Date().getTime()));
		
		List relatives = new ArrayList();
		relatives.add(item);
		relatives.add(item2);
		
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < count; i++) {
		
			ViewContext context = new ViewContext();
			context.put("relatives", relatives);
			context.put("instance", new SogouRelatives());
			ViewEngine.getViewManager().getContent("biz/relatives/relative-news.vm", context);
		}
		
		long timeEnd = System.currentTimeMillis();
		
		return (timeEnd - timeStart);
	}
	
	public static void main(String[] args) {
		
	}
	
}
