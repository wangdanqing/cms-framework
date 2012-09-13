package net.pusuo.cms.client.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.taglib.BeanTag;
import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Magazine;
import com.hexun.cms.core.MagazineSheet;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;
import com.hexun.cms.client.biz.util.ContentGenerator;
import com.hexun.cms.client.biz.util.MultiPageContent;
import com.hexun.cms.client.biz.util.MultiPageItem;

public class BeanTagUtil {

	private static final Log log = LogFactory.getLog(BeanTag.class);
	private static BeanTagUtil btu = null;
	private static final Object lock = new Object();
	private static final int hit = 350;//500
	private BeanTagUtil() {}

	public static BeanTagUtil getInstance() {
		if (btu == null) {
			synchronized (lock) {
					btu = new BeanTagUtil();
			}
		}
		return btu;
	}

	public String processPageWraper(String content, News news, int mpage)
			throws UnsupportedEncodingException {
//		if (content.length() < hit + 100)
//			return content;
		String result = content;
		// 截字
		switch (news.getAttr()) {
		case 4:
			result = processNewsAttr(result, news, mpage);
			break;
		case 5:
			result = processNewsAttr(result, news, mpage);
			break;
		case 6:
			result = processNewsAttrPic(result, news);
			break;
		default:
			break;
		}
		// 分页文章 合页阅读
	//	if (mpage > -1)
	//		result = processSinglePage(result, news, mpage);
		boolean flag = true;
		// 杂志文章|普通文章画中画处理
		//if ((news.getSubtype() == News.SUBTYPE_MAGAZINE || news.getSubtype() == News.SUBTYPE_TEXT)&& news.getMagazineId() > 0)
		if ((news.getSubtype() == News.SUBTYPE_MAGAZINE || news.getSubtype() == News.SUBTYPE_TEXT))
		{
				result = processMagazinePicInPic(result, news, mpage);
				if(news.getMagazineId() > 0) flag = false;
		}
		
		//生成xml,杂志的画中画除外
		if (((news.getAttr()>3 && news.getAttr() < 7 )||(mpage > -1 && mpage==0)) && flag)
			generateFullContentXML(news, null);
	
		return result;
	}

	/**
	 * 处理杂志文章正文登录前后的画中画 
	 * 1. 在适当位置插入登录前的内容 
	 * 2. 生成登录后的xml
	 */
	private String processMagazinePicInPic(String content, News news, int mpage) {
		StringBuffer copycontent = new StringBuffer(content.toLowerCase());
		int pos = -1;
		try {
			pos = getNomalPositionByCharNum(copycontent.toString(), 150);//150
		} catch (RuntimeException e) {
			pos = -1;
			log.error("getNomalPositionByCharNum ERROR: " + e.getMessage());
		}
		//重新获取位置
		if(pos == -1)
		{
			try {
			pos  = getTokenPosition(copycontent.toString(), 150);//150
			} catch (RuntimeException e) {
				pos = -1;
				log.error("getTokenPosition ERROR: " + e.getMessage());
			}
		}
		//after login
		boolean flag = true;
		
		if (news.getMagazineId() > 0 && pos > 50)
		{
			log.info("pip: pos= " + pos + "||subtype:" + news.getSubtype() +"||mpage:" + mpage);
			Magazine mg = (Magazine) ItemManager.getInstance().get(new Integer(news.getMagazineId()), Magazine.class);
			if( mg.getShowPicInPic() == 0)return content;
			if(news.getSubtype() == News.SUBTYPE_TEXT)
			{
				log.info("common article pip: " + pos);
				content = content.substring(0, pos + 4) + getPicInPicContent((Item) mg, 1) + content.substring(pos);
				flag = false;
			}
			if((mpage > -1 && mpage==0) || news.getSubtype() == News.SUBTYPE_MAGAZINE)
			{
				String aferpip = getPicInPicContent((Item) mg, 2);
				log.info("after login pip length: " + aferpip.length());
				generateFullContentXML(news, aferpip);
			}
			
			//before login
			if (pos >= 50 && news.getMagazineSheetId() > 0 && flag) {
				log.info("before login pip: " + pos);
				MagazineSheet ms = (MagazineSheet) ItemManager.getInstance().get(new Integer(news.getMagazineSheetId()),MagazineSheet.class);				
				content = content.substring(0, pos + 4) + getPicInPicContent((Item) ms, 3) + content.substring(pos);
			}
		}
		return content;
	}

	/**
	 * process news attribute, cut off the news form 500th word ending with
	 * . by shijinkui 2008-11-17
	 */
	private String processNewsAttr(String content, News news, int mpage) throws UnsupportedEncodingException {
		StringBuffer copycontent = new StringBuffer(content);
		String fullUrl = this.getDynamicUrl(news, null);
		int pos = -1;
		try {
			pos = getNomalPositionByCharNum(copycontent.toString().toLowerCase(), hit);
		} catch (RuntimeException e) {
			log.error("getNomalPositionByCharNum pic in pic: " + e.getMessage() +"||"+ pos);
			pos = -1;			
		}
		//log.info("attr pos: " + pos);
		if((pos == -1 || pos > hit + 101) && news.getSubtype() == News.SUBTYPE_MAGAZINE)
		{	
			try{ 
				pos  = getTokenPosition(copycontent.toString(), hit);
			} catch (RuntimeException e){ 
				log.error("getTokenPosition pic in pic: " + e.getMessage() +"||"+ pos);
				pos = -1;	
			}
		}

		String regUrl = "http://service.caijing.com.cn/auth/register";
		if(news.getChannel() == 107 && news.getType() == 2)
			regUrl = "http://service.msn.caijing.com.cn/auth/register";
		String addc =  "<br><div id=\"fullUrl\">"
		+ Util.unicodeToGBK("继续阅读,请")
		+ " <a href=\""
		+ fullUrl
		+ "\" target=\"_self\">"
		+ Util.unicodeToGBK("查看全文")
		+ "</a>, "
		+ Util.unicodeToGBK("新用户请")
		+ " <a href=\""+regUrl+"\">"
		+ Util.unicodeToGBK("注册") + "</a> </div>";
		
		if (pos > 0 && pos >= (hit - 100))
			content = content.substring(0, pos + 4) + addc;
		else if(pos == -1)
			content += addc;
		
		if(mpage > 0)
			content = addc;

		return content;
	}

	/**
	 * process single page link
	 * 
	 * @param content
	 * @param news
	 * @return
	 */
	private String processSinglePage(String content, News news, int mpage) {
		String fullUrl = getDynamicUrl(news, "/UnionNews.jsp");
		String singlepage = "<div class=\"nrgna\" ><a href=\"" + fullUrl + "\"><b>"+Util.unicodeToGBK("单页阅读")+"</b></a></div>";
		if (news.getAttr() == 4 || news.getAttr() == 5) {
			if (mpage == 0)
				content= "<style>#the_content select,#pageNext{display:none}</style>" + content;
			else
				content = singlepage;
		} else
			content = singlepage + content;

		return content;
	}

	/**
	 * get regist,fee reading and pic news's dynamic url
	 */
	private String getDynamicUrl(EntityItem entity, String page) {
		String dyurl = "http://";
		String jsppage = "/chargeFullNews.jsp";
		if (page != null && !page.equals(""))
			jsppage = page;
		if (entity != null) {
			//msn
			int channelid = entity.getChannel();
			if(entity.getType()==2 && channelid == 107)
				channelid = 106;
			Channel channel = (Channel) ItemManager.getInstance().get(new Integer(channelid), Channel.class);
			dyurl += channel.getName() + jsppage + "?id=" + entity.getId()
					+ "&time="
					+ Util.formatTime(entity.getTime(), "yyyy-MM-dd") + "&cl="
					+ entity.getChannel();
			if (page != null && !page.equals(""))
				dyurl+="&page=all";
		}
		return dyurl;
	}

	private String processNewsAttrPic(String content, News news)
			throws UnsupportedEncodingException {
		return "<br><div id=\"fullUrl\">" + Util.unicodeToGBK("继续阅读,请")
				+ " <a href=\"" + getDynamicUrl(news, null)
				+ "\" target=\"_self\">" + Util.unicodeToGBK("查看全文") + "</a>, "
				+ Util.unicodeToGBK("新用户请")
				+ " <a href=\"http://service.caijing.com.cn/auth/register\">"
				+ Util.unicodeToGBK("注册") + "</a> </div>";
	}

	/**
	 * 获取截字的位置
	 * 
	 * @autor: shijinkui
	 */
	private int getNomalPositionByCharNum(String content, int lLimit) {
		if(content.length() < lLimit)return -1;
		StringBuffer cc = new StringBuffer(content);
		int pos = 0;
		int chars = 0;
		String tmp = "", ct = "";

		boolean t1 = false, t2 = false, t3 = false, t4 = false;

		for (int i = 0; i < cc.length(); i++) {
			pos++;
			if (cc.charAt(i) == '<')
				t1 = true;
			if (cc.charAt(i) == '>')
				t2 = true;
			if(pos+10 < content.length())
			{
				ct = content.substring(pos, pos+9);
				if(ct.indexOf("<select o") > -1)
					t3 = true;
				if(ct.indexOf("</select>") > -1)
					t4 = true;
			}
			
			if (!t1 && !t2 && !t3 && !t4)
				chars++;
			if (t1 && t2)
			{	t1 = false;
				t2 = false;
			}
			if(t3 && t4)
			{
				t3 = false;
				t4 = false;
			}

			if (chars > lLimit && (pos + 100) < content.length()
					&& (pos - 100) > 0) {
				tmp = cc.substring(pos - 100, pos + 100).toLowerCase();
				if (tmp.contains("</p>") && tmp.contains("<p>")) {
					if (tmp.contains("<div") || tmp.contains("<img")
							|| tmp.contains("<table") || tmp.contains("<tr")
							|| tmp.contains("<td")
							|| tmp.contains("<o:p></o:p>")
							|| tmp.contains("mso-border-right-alt"))
						continue;

					int jhere = tmp.indexOf("</p>");
					if (tmp.indexOf("<p>", jhere) < jhere + 15) {
						pos = jhere > 100 ? (pos + jhere - 100)
								: (pos - 100 + jhere);
						break;
					}
				}
			}
		}
		// change to zero if don't cover the condition
		pos = pos == cc.length() ? -1 : pos;
		return pos;
	}

	// 登陆后画中画
	private String getPicInPicContent(Item item, int flag) {
		if (item == null)
			return null;
		ViewContext context = null;
		String content = "";
		String schedu = "system/pipAfterLogin.vm";
	//	if(flag == 1)
	//		schedu = "system/pipCommon.vm";
		if (flag < 3) {
			context = new ViewContext();
			Magazine maga = (Magazine) item;
			// pictures
			if (StringUtils.isNotEmpty(maga.getPics())) {// 单个图片
				String pic[] = maga.getPics().split(Global.CMSCOMMA);
				try{
					if (pic.length == 1) {
						EntityItem picture = (EntityItem) ItemManager.getInstance()
								.get(new Integer(pic[0]), EntityItem.class);
						//if (picture != null && picture.getId()<110123602)
						//	context.put("pictureUrl", picture.getUrl());
						//else {
							int pos = picture.getUrl().lastIndexOf(".");
							context.put("pictureUrl", picture.getUrl().substring(0,pos)+"_180x135"+picture.getUrl().substring(pos));
						//}
					} else if (pic.length == 2) {
						EntityItem picture = (EntityItem) ItemManager.getInstance()
								.get(new Integer(pic[0]), EntityItem.class);
						//if (picture != null && picture.getId()<110123602)
						//	context.put("pictureUrl", picture.getUrl());
						//else {
							int pos = picture.getUrl().lastIndexOf(".");
							context.put("pictureUrl", picture.getUrl().substring(0,pos)+"_180x135"+picture.getUrl().substring(pos));
						//}
	
						EntityItem news = (EntityItem) ItemManager.getInstance()
								.get(new Integer(pic[1]), EntityItem.class);
						if (news != null) {
							context.put("pictureNewsDesc", news.getDesc());
							context.put("pictureNewsUrl", news.getUrl());
						}
					}
				}catch(Exception e){
					log.error("magazine pics error: " + e);
					maga.setPics("");
					ItemManager.getInstance().update(maga);//更新magazine
				}
			}

			// video
			if (StringUtils.isNotEmpty(maga.getVideo())) {
				try {
					int vid = Integer.parseInt(maga.getVideo().split(
							Global.CMSSEP)[0]);// 只要第一个视频，多了以后再说
					News video = (News) ItemManager.getInstance().get(
							new Integer(vid), News.class);
					if (StringUtils.isEmpty(video.getReurl())) {// 排除推送视频
						String[] pstr = video.getPictures()
								.split(Global.CMSSEP);
						int picid = Integer.parseInt(pstr[pstr.length - 1]);

						EntityItem aa = (EntityItem) ItemManager.getInstance()
								.get(new Integer(picid), EntityItem.class);
						//if(aa.getId()<110123602)
						//	context.put("vpicurl", aa.getUrl());
						//else
					 		context.put("vpicurl", aa.getUrl().toLowerCase().replace(".jpg", "_180x135.jpg"));
						context.put("vnews", video);

					}

				} catch (Exception e) {
					log.error("magazine video validate error. " + e.toString());
					maga.setVideo("");
					ItemManager.getInstance().update(maga);//更新magazine
				}
			}
			// 相关新闻
			if (StringUtils.isNotEmpty(maga.getReleatenews())) {
				try{
					String rn[] = maga.getReleatenews().split(Global.CMSSEP);
					List list = new ArrayList();
					for (int i = 0; i < rn.length; i++) {
						EntityItem eitem = (EntityItem) ItemManager.getInstance().get(new Integer(Integer.parseInt(rn[i])), EntityItem.class);
						if (eitem != null)
							list.add(eitem);
					}
					context.put("rnewss", list);
				}catch(Exception e)
				{
					log.error("magazine releasenews validate error:" + e);
					maga.setReleatenews("");
					ItemManager.getInstance().update(maga);//更新magazine
				}
			}
			// 音频 || 相关专题
			if (StringUtils.isNotEmpty(maga.getAudio())) {
				try {
					String[] ids = maga.getAudio().split(Global.CMSSEP);
					List list = new ArrayList();
					String resultname = "audionews";
					for(int i = 0; ids!=null && i < ids.length; i++)
					{
						EntityItem item1 = (EntityItem) ItemManager.getInstance().get(new Integer(ids[i]), News.class);
						if(i == 0 && item1.getType() == EntityItem.SUBJECT_TYPE)
							resultname="subjectlist";
						list.add(item1);
					}
					context.put(resultname, list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			content = ViewEngine.getViewManager().getContent(schedu, context);
		} 
		if(flag == 3){
			MagazineSheet ms = (MagazineSheet) item;
			String media = "";
			if(StringUtils.isNotEmpty(ms.getMedia()))
			{
				Media m = (Media)ItemManager.getInstance().get(Integer.valueOf(ms.getMedia()), Media.class);
				media = m.getDesc();
			}
			context = new ViewContext();
			context.put("media", media);
			context.put("pubtime", Util.formatTime(ms.getPublishTime(), Util.unicodeToGBK("yyyy年MM月dd日")));
			context.put("logpic", ms.getIndexLogo());
			context.put("magazineUrl", ms.getIndexUrl());
			context.put("magayear", Util.formatTime(ms.getPublishTime(), "yyyy"));
			context.put("qikan", ms.getStageNum());
			context.put("indexnewsurl", ms.getIndexNewsUrl());
			content = ViewEngine.getViewManager().getContent("system/pipBeforeLogin.vm", context);
		}
		return content;
	}
	
	/**
	 * generate full content xml file into specified category shijinkui
	 */
	private void generateFullContentXML(News news, String picinpic) {
		ViewContext context = new ViewContext();
		context.put("content", news.getText());
		context.put("time", news.getTime());
		context.put("pid", String.valueOf(news.getPid()));
		context.put("channel", String.valueOf(news.getChannel()));
		context.put("title", news.getDesc());
		context.put("keyword", news.getKeyword());
		context.put("author", news.getAuthor());
		context.put("abstract", news.getAbstract());
		context.put("navigation", getNavigation((EntityItem) news));
		context.put("url", news.getUrl());
		context.put("templageid", news.getTemplate());

		context.put("picinpic", picinpic == null?"":picinpic);
		context.put("releasenews", getReleaseNews(news));
		context.put("magzinesheet", getMagazineSheet(news));
		
		if (news.getAttr() == 4) {
			context.put("ischarge", "0");
			context.put("islogin", "1");
		} else if (news.getAttr() == 5) {
			context.put("ischarge", "1");
			context.put("islogin", "1");
		} else if (news.getAttr() == 6) {
			context.put("ischarge", "0");
			context.put("islogin", "1");
		} else {
			context.put("ischarge", "0");
			context.put("islogin", "0");
		}
		if (news.getMedia() > -1) {
			Media media = (Media) ItemManager.getInstance().get(new Integer(news.getMedia()), Media.class);
			context.put("medialog", media.getLogo());
			context.put("mediadesc", media.getDesc());
			context.put("mediaurl", media.getUrl());
		}else{
			context.put("medialog", "");
			context.put("mediadesc", "");
			context.put("mediaurl", "");
		}
		
		String path = "/cmsdata/fullcxml/" + news.getChannel() + "/"
				+ Util.formatTime(news.getTime(), "yyyy-MM-dd") + "/"
				+ news.getId() + ".xml";
		String content = ViewEngine.getViewManager().getContent(
				"system/fullcontentxml.vm", context);
		if (content == null || content.trim().length() == 0) {
			return;
		}
		// ~
		try {
			ClientFile.getInstance().write(content, path);
		} catch (Exception e) {
			log.error("generate full content error. " + news.getId());
			e.printStackTrace();
		}
	}

	/**
	 * specially processing magazine history fee articles
	 * 
	 * @param content
	 * @param lLimit
	 * @return
	 */
	private int getTokenPosition(String content, int lLimit) {
		if(content.length() < lLimit)return -1;
		StringBuffer cc = new StringBuffer(content);
		int pos = 0;
		int chars = 0;
		String tmp = "",ts = "";

		boolean t1 = false, t2 = false;

		for (int i = 0; i < cc.length(); i++) {
			pos++;
			if (cc.charAt(i) == '<')
				t1 = true;
			if (cc.charAt(i) == '>')
				t2 = true;
			if (!t1 && !t2)
				chars++;
			else {
				t1 = false;
				t2 = false;
			}

			if (chars > lLimit && (pos + 100) < content.length()
					&& (pos - 100) > 0) {
				tmp = cc.substring(pos - 100, pos + 100).toLowerCase();
				if (tmp.contains("<br>") || tmp.contains("<br />")
						|| tmp.contains("<br/>")) {
					if (tmp.contains("<div") || tmp.contains("<img")
							|| tmp.contains("<table") || tmp.contains("<tr")
							|| tmp.contains("<td")
							|| tmp.contains("<o:p></o:p>")
							|| tmp.contains("mso-border-right-alt"))
						continue;

					int jhere = tmp.indexOf("<br");
					ts = tmp.substring(jhere, jhere + 10);
					if (ts.contains("<br>") || ts.contains("<br />")
							|| ts.contains("<br/>")) {
						pos = jhere > 100 ? (pos + jhere - 100)
								: (pos - 100 + jhere);
						break;
					}
				}
			}
		}
		// change to zero if don't cover the condition
		pos = pos == cc.length() ? -1 : pos;
		return pos;
	}

	/**
	 * 获取新闻页导航
	 */
	public String getNavigation(EntityItem entity) {
		String category = entity.getCategory();
		if (category == null || category.equals(""))
			return null;

		String[] categorys = category.split(Global.CMSSEP);

		StringBuffer sb = new StringBuffer();

		int backCount = categorys.length;
		// 新闻导航不出现正文标题
		if (entity.getType() == 2) {
			backCount--;
		}
		for (int i = 0; i < backCount; i++) {
			EntityItem eItem = (EntityItem) ItemManager.getInstance().get(
					new Integer(categorys[i]), EntityItem.class);
			String desc = eItem.getDesc();
			String url = eItem.getUrl();

			/* 专题和栏目实体,如果结尾以index.html,就去掉它,以目录的方式显示出来 */
			if ((eItem.getType() == EntityItem.SUBJECT_TYPE || eItem.getType() == EntityItem.HOMEPAGE_TYPE)
					&& url.endsWith("index.html")) {
				url = url.substring(0, url.lastIndexOf("index.html"));
			}

			sb.append("<a href=");
			sb.append(url);
			sb.append(" target=\"_self\">");
			sb.append(desc);
			if (i == backCount - 1) {
				sb.append("</a>");
			} else {
				sb.append("</a> &gt; ");
			}
		}
		return sb.toString();
	}
	
	private String getReleaseNews(News news){
		if(StringUtils.isEmpty(news.getRelativenews()))
			return "";
		StringBuffer sb = new StringBuffer();
		String[] relativeNews = news.getRelativenews().split(Global.CMSSEP);
		for(int i=0; relativeNews!=null && i<relativeNews.length; i++)
		{
			int i_id = -1;
			try
			{
				i_id = Integer.parseInt( relativeNews[i].trim() );
			} catch(Exception e) {
				log.error("invalid relative news.id "+String.valueOf(relativeNews[i].trim()));
				continue;
			}
			EntityItem eItem = (EntityItem)ItemManager.getInstance().get(new Integer(i_id), EntityItem.class);
			if( eItem!=null )
			{
				sb.append( "<li>" );				
				sb.append("<a href=" + eItem.getUrl() + ">" + eItem.getDesc() + "</a>"); 
				sb.append( "</li>" );
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取杂志文章所属期刊信息 
	 */
	public String getMagazineSheet(News news)
	{
		if(news.getSubtype()!=News.SUBTYPE_MAGAZINE || news.getMagazineSheetId() == -1)
			return "";
		else
		{
			String rs = "";
			MagazineSheet ms = (MagazineSheet)ItemManager.getInstance().get(new Integer(news.getMagazineSheetId()), MagazineSheet.class);
			if(ms!=null)
				rs = Util.formatDatetime(ms.getPublishTime(), Util.unicodeToGBK("yyyy年")) + Util.unicodeToGBK("第" + ms.getStageNum() + "期 出版日期" 
						+ Util.formatDatetime(ms.getPublishTime(), "yyyy年MM月dd日"));
			return rs;
		}
	}
	/**
	 * 获取单页阅读url
	 * @return
	 */
	public String getSinglePageUrl(News news,int mpage){
		String url = "";
		if(mpage!=-1)
			url = getDynamicUrl(news, "/UnionNews.jsp");
		else
			url = news.getUrl();
		
		return url;
		
	}

	/**
	 * 获取"本文导航"d的内容
	 * @param news
	 * @param current
	 * @return
	 */
	public String getPageNav(News news, int current){
		MultiPageContent mpc = new MultiPageContent(news);
		List list = mpc.getItems();
		MultiPageItem mpi = null;
		StringBuffer sb = new StringBuffer("\n<!--本文导航 begin-->\n<div class=\"page_nav\"><div class=\"tit\">本文导航</div><div class=\"page_nav_list\"><ul>\n");
		String subhead = null;
		for(int i = 0; list!=null&&i<list.size();i++){
			mpi = (MultiPageItem) list.get(i);
			subhead = mpi.getSubhead();
			subhead = StringUtils.isEmpty(subhead)?news.getDesc()+"("+(i+1)+")":subhead;
			sb.append("<li><span>第")
			  .append(i+1)
			  .append("页</span><h3>");
		
			if(i == current)
				sb.append(subhead);
			else
				sb.append("<a href=\"")
				  .append(ContentGenerator.getSubUrlOfMutilPageNews(news.getUrl(), i))
				  .append("\"  title=\"")
				  .append(subhead.replaceAll("\"|'|“|”|‘|’", ""))
				  .append("\">")
				  .append(subhead)
				  .append("</a>");

			 sb.append("</h3></li>");
		}

		sb.append("\n</ul><div class=\"clear\"></div></div></div>\n<!--本文导航 end-->\n");
		
		return sb.toString();
	}

	private final long ct() {
		return System.currentTimeMillis();
	}
}
