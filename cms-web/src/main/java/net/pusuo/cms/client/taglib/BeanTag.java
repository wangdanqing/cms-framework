package net.pusuo.cms.client.taglib;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.auth.User;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ad.ADManager;
import com.hexun.cms.client.biz.util.ContentGenerator;
import com.hexun.cms.client.biz.util.NewsUtil;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.util.BeanTagUtil;
import com.hexun.cms.client.util.EmbedGroovy;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.CommendReadUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.MagazineSheet;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.TFMap;
import com.hexun.cms.core.Template;
import com.hexun.cms.util.Util;

public class BeanTag extends TagSupport {
	private static final Log log = LogFactory.getLog(BeanTag.class);
	private static final String GROOVY_ROOT = Configuration.getInstance().get(
			"beantag.grooveroot");
	private String name = null;
	private int view = -1;
	private String property = null;
	private String groovy = null;
	private static final String BEGIN_HEXUNMPCODE = "<HEXUNMPCODE>";
	private static final String END_HEXUNMPCODE = "</HEXUNMPCODE>";
	private static final String BEGIN_HEXUNSUBHEAD = "<HEXUNSUBHEAD>";
	private static final String END_HEXUNSUBHEAD = "</HEXUNSUBHEAD>";
	private static final String BEGIN_HEXUNMPCOMMON = "<HEXUNMPCOMMON>";
	private static final String END_HEXUNMPCOMMON = "</HEXUNMPCOMMON>";
	private static final String BEGIN_HEXUNMPBANNER_BOTTOM = "<HEXUNMPBANNER>";
	private static final String END_HEXUNMPBANNER_BOTTOM = "</HEXUNMPBANNER>";
	private static final String BEGIN_HEXUNMPBANNER_TOP = "<HEXUNMPBANNERTOP>";
	private static final String END_HEXUNMPBANNER_TOP = "</HEXUNMPBANNERTOP>";
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat(Util.unicodeToGBK("yyyy年MM月dd日 HH:mm"));

	private Template template = null;

	private EntityItem entity = null;

	public int doStartTag() throws JspException {
		int ret = SKIP_BODY;
		try {
			ServletRequest request = pageContext.getRequest();
			if (view < 0) {
				// default view , display some frag info
				return ret;
			}
			if (view == FragTag.PRECOMPILE_VIEW) {
				// pre compile
				return ret;
			}

			template = (Template) request.getAttribute(FragTag.TEMPLATE_KEY);
			entity = (EntityItem) request.getAttribute(FragTag.ENTITY_KEY);
			if (template == null) {
				log.warn("BeanTag --> template is null.");
				return ret;
			}
			if (entity == null) {
				log.warn("BeanTag --> entity is null.");
				return ret;
			}

			News news = null;
			Subject subject = null;
			if (entity instanceof News) {
				news = (News) entity;
			} else if (entity instanceof Subject) {
				subject = (Subject) entity;
			}

			String response = "";
			do {
				if (property.equals("media")) {
					//媒体名称
					response = getMedia(news);
					if (response == null) {
						response = "";
					}
					break;
				} else if (property.equals("logo")) {
					//媒体logo
					response = getMediaLogo(news);
					if (response == null) {
						response = "";
					}
					break;
				} else if (property.equals("stockOrg")) {
					//股票机构
					response = getStockOrg(news);
					break;
				} else if (property.equals("stockCode")) {
					//股票代码
					response = getStockCode(news);
					break;
				}else if(property.equals("editor")){
					int editor = news.getEditor();
					User user = (User)ItemManager.getInstance().get(new Integer(editor), User.class);
					response = user.getDesc();
					if(editor == 248)
						response = "自动转载";
					
				} else if (property.equals("author")) {
					// 作者
					response = getAuthor(news);
					break;
				} else if (property.equals("navigation")) {
					//导航
					response = BeanTagUtil.getInstance().getNavigation(entity);
					break;
				} else if (property.equals("channel")) {
					// 频道
					response = getChannel(news);
					break;
				} else if (property.equals("time")) {
					// 时间
					response = getTime(entity);
					break;
				}else if (property.equals("purl")) {
					//获取父对象的URL
					EntityItem item = (EntityItem)ItemManager.getInstance().get(new Integer(entity.getPid()), EntityItem.class);
					response = item.getUrl().replace("index.html", "");
					break;
				} else if (property.equals("magazine")) {
					//magazine
					response = BeanTagUtil.getInstance().getMagazineSheet(news);
					break;
				} else if (property.equals("singlepageUrl")) {
					//单页阅读Url
					int mpage = -1;
					String mpageParam = request.getParameter("mpage");
					try {
						if (mpageParam != null)
							mpage = Integer.parseInt(mpageParam);
					} catch (Exception e) {
						mpage = -1;
					}
					response = BeanTagUtil.getInstance().getSinglePageUrl(news, mpage);
					break;
				} else if (property.equals("text")) {
					// 正文内容
					String mpageParam = request.getParameter("mpage");
					int mpage = -1;
					try {
						if (mpageParam != null)
							mpage = Integer.parseInt(mpageParam);
					} catch (Exception e) {
						mpage = -1;
					}
					response = getText(news, mpage);//单页或分页后的单页内容
					//process all the page logic
					//response = BeanTagUtil.getInstance().processPageWraper(response, news, mpage);
					response = CommendReadUtil.getCommendRead(response, news.getCategory());
					
					
					
					break;
				} else if (property.equals("relativeNews")) {
					// 相关新闻
					response = getRelativeNews(news);
					break;
				} else if (property.equals("relativeHint")) {
					// 相关搜索
					response = getRelativeHint(news);
					break;
				} else if (property.equals("relativeBlog")) {
					// 相关博客
					response = getRelativeBlog(news);
					break;
				} else if (property.equals("relativeSaybar")) {
					// 相关说吧
					response = getRelativeSaybar(news);
					break;
				} else if (property.equals("smsurl")) {
					// sms url
					StringBuffer sb = new StringBuffer();
					sb
							.append("http://dynamic.hexun.com/template/system/sendsms.jsp?TITLE=");
					sb.append(entity.getDesc().replaceAll("<[^>]*>", ""));
					sb.append("&CLS=1&URL=" + entity.getUrl());
					response = sb.toString();
					break;
				} else if (property.equals("printurl")) {
					// print url
					response = "http://dynamic.hexun.com/template/news/print.jsp?ENTITYID="
							+ entity.getId() + "&Dynamic=yes";
					break;
				} else if (property.equals("checkurl")) {
					// check url
					response = "http://check.news.hexun.com/article_add_form.php?newstitle="
							+ entity.getDesc().replaceAll("<[^>]*>", "");
					break;
				} else if (property.equals("discussurl")) {
					// comment url
					response = "http://comment2.news.hexun.com/viewcomments.action?id="
							+ entity.getId();
					break;
				} else if (property.equals("promotion")) {
					// 频道推广碎片
					response = getPromotion();
					break;
				}  else if (property.equals("rssurl")) {
					// rss url
					Channel ch = (Channel)ItemManager.getInstance().get(new Integer(entity.getChannel()),Channel.class);
					response = "http://"+ch.getName()+"/rss/"+entity.getShortname()+".xml";
					break;
				} else if(property.length()>4 && property.startsWith("PNN_")){
					//the next and prefix news for current news.
					String cg = property.substring(4,property.length());
					//get prenews and next news
					News pnn[] = NewsUtil.getPreAndNextNews(news);
					if(pnn.length <1)
					{
						response = "";
						break;
					}
					if (cg != null && cg.trim().length() > 0 && GROOVY_ROOT != null) {
						String groovyPath = GROOVY_ROOT + cg.trim() + ".groovy";
						File f = new File(groovyPath);
						if(!f.exists())
						{
							groovyPath =  GROOVY_ROOT + "pre_nextnews_default.groovy";
						}
						try {
							EmbedGroovy embedGroovy = new EmbedGroovy();
							Map params = new HashMap();
							
							params.put("entity", entity);
							params.put("response", response);
							params.put("property", property);
							params.put("prenews", pnn[0]);
							params.put("nextnews", pnn[1]);
							
							embedGroovy.initial(groovyPath);
							embedGroovy.setParameters(params);
							Object result = embedGroovy.run();
							if (result == null) {
								result = embedGroovy.getProperty("result");
							}
							//log.info("Generate prenews and next news!");
							if (result != null) {
								response = result.toString();
							}
						} catch (Exception e) {
							log.error("execute groovy script[" + groovyPath + "] error", e);
						}
					break;
				}
					
				}else {
				
					//其它的属性
					Object obj = RequestUtils.lookup(pageContext, name,
							property, null);
					if (obj != null) {
						response = obj.toString();
						break;
					} else {
						if (view == FragTag.COMPILE_VIEW) {
							ResponseUtils.write(pageContext, "");
						} else {
							ResponseUtils.write(pageContext,
									"not found property[" + property + "]");
						}
						return ret;
					}
				}
			} while (false);
			String gp = this.getGroovy();
			if (gp != null && gp.trim().length() > 0 && GROOVY_ROOT != null) {
				String groovyPath = GROOVY_ROOT + gp.trim() + ".groovy";
				try {
					EmbedGroovy embedGroovy = new EmbedGroovy();
					Map params = new HashMap();
					params.put("entity", entity);
					params.put("response", response);
					params.put("property", property);
					embedGroovy.initial(groovyPath);
					embedGroovy.setParameters(params);
					Object result = embedGroovy.run();
					if (result == null) {
						result = embedGroovy.getProperty("result");
					}
					if (result != null) {
						response = result.toString();
					}
				} catch (Exception e) {
					log.error(
							"execute groovy script[" + groovyPath + "] error",
							e);
				}
			}
			ResponseUtils.write(pageContext, response);
		} catch (Exception e) {
			log.error("BeanTag Exception", e);
			throw new JspTagException("BeanTag Exception : " + e.toString());
		}
		return ret;
	}
	
	/**
	 * 处理推荐阅读 modify by shijinkui 08-09-26
	 * 
	 */
	private String getCommendRead(String content, String channel){ 
		StringBuffer copycontent = new StringBuffer(content.toLowerCase());
		String adContentAll = "<!--#include virtual=\"/commonfrag/"+channel+"/final_commendRead.inc\"-->";
		final String fullstop = Util.unicodeToGBK("閵??闄??");
		int len1 = getCharLength(copycontent.toString());
		if (len1 <= 300) {
			content =  content + adContentAll;
		} else {
			int pos1 = getPosition(copycontent.toString(), 100);
			int p1 = copycontent.indexOf(fullstop, pos1);
			if (p1 == -1) {
				content = content + adContentAll;
			} else {
				content = content.substring(0, p1 + 1) + adContentAll + content.substring(p1 + 1);
			}
		}
		
		return content;		
	}


	/**
	 * 閼惧嘲褰囨０鎴︿壕閹恒劌绠嶇喊搴ｅ閻ㄥ嫬鍞寸????闄??
	 * 
	 * @return
	 */
	private String getPromotion() {
		// define dot
		String dot = ".";
		try {
			dot = new String("璺??".getBytes("ISO_8859_1"), "GBK");
		} catch (Exception e) {
			dot = ".";
		}

		// read file content
		// String file = "/news/interface/frag.xml";
		String file = "/news/interface/pagefrag.xml";
		String fileContent = "";
		try {
			fileContent = ClientFile.getInstance().read(file);
			fileContent = Util.unicodeToGBK(fileContent);
		} catch (Exception e) {
			fileContent = "";
		}

		// parse xml content
		// ListOrderedMap url2descMap = new ListOrderedMap();
		StringBuffer bufferPromotion = new StringBuffer();
		if (!fileContent.trim().equals("")) {
			try {
				Document doc = DocumentHelper.parseText(fileContent);
				Element root = doc.getRootElement();
				List list = root.selectNodes("/hotnews/news");
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					Element element = (Element) iter.next();
					Iterator iteratorSub = element.elementIterator("subnews");
					String subNews = "";
					while (iteratorSub.hasNext()) {
						Element elementSub = (Element) iteratorSub.next();
						Iterator iteratorTitle = elementSub
								.elementIterator("title");
						Iterator iteratorUrl = elementSub
								.elementIterator("url");
						while (iteratorTitle.hasNext() && iteratorUrl.hasNext()) {
							String title = ((Element) iteratorTitle.next())
									.getTextTrim();
							String url = ((Element) iteratorUrl.next())
									.getTextTrim();
							if (title != null && url != null
									&& title.length() > 0 && url.length() > 0) {
								if (subNews.length() > 0) {
									subNews += "&nbsp;";
								}
								subNews += ("<A href=" + url
										+ " target=_blank>" + title + "</A>");
								break;
							}
						}
					}
					if (subNews.length() > 0) {
						bufferPromotion.append(dot);
						bufferPromotion.append(subNews);
						bufferPromotion.append("<BR>\n");
					}
				}
			} catch (Exception e) {
				log.error("getPromotion", e);
				// url2descMap.clear();
			}
		}

		// add wrapped html code
		String content = "";
		content += "<TABLE cellSpacing=0 cellPadding=0 width=156 bgColor=#daf6ff border=0>";
		content += "<TR><TD>";
		content += "	<TABLE cellSpacing=0 cellPadding=0 width=156 background=http://it.hexun.com/upload/20051205-it/rbg1.gif border=0>";
		content += "		<TR><TD class='wz12_ffff pd3 bold' align=middle height=24>"
				+ Util.unicodeToGBK("妫版垿浜剧划鎯у兊閹恒劏宕??") + "</TD></TR>";
		content += "	</TABLE>";
		content += "</TD></TR>";
		content += "<TR><TD vAlign=top align=middle height=130>";
		content += "	<IMG height=5 src=http://images.hexun.com/ccc.gif width=1><BR>";
		content += "	<TABLE cellSpacing=0 cellPadding=0 width=145 border=0>";
		content += "	<TR><TD class=wz12_080C_1 vAlign=top>";
		content += bufferPromotion.toString();
		content += "	</TD></TR>";
		content += "	</TABLE>";
		content += "</TD></TR>";
		content += "</TABLE>";

		return content;
	}

	/**
	 * 获取所属频道
	 */
	private String getChannel(EntityItem entity) {
		int channelId = entity.getChannel();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelId), Channel.class);
		return channel.getName().toUpperCase();
	}

	/**
	 * 获取文章发布时间
	 */
	private String getTime(EntityItem entity) {
//		SimpleDateFormat formatter = null;
//		try {
//formatter = new SimpleDateFormat("yyyy??MM??dd?? HH:mm");
//formatter = new SimpleDateFormat(new String("yyyy??MM??dd?? HH:mm".getBytes("ISO_8859_1"),"UTF-8"));
//
//			//formatter = new SimpleDateFormat(new String("yyyy年MM月dd日 HH:mm".getBytes("ISO_8859_1"),"UTF-8"));
		//} catch (UnsupportedEncodingException e) {
		//	e.printStackTrace();
		//}
		java.sql.Date date = new java.sql.Date(entity.getTime().getTime());
		return formatter.format(date);
	}
	
	/**
	 * 获取杂志文章所属期刊信息 
	 */
	private String getMagazineSheet(News news)
	{
		if(news.getSubtype()!=News.SUBTYPE_MAGAZINE || news.getMagazineSheetId() == -1)
			return "";
		else
		{
			String rs = "";
			MagazineSheet ms = (MagazineSheet)ItemManager.getInstance().get(new Integer(news.getMagazineSheetId()), MagazineSheet.class);
			if(ms!=null && !ms.equals(""))
				rs = Util.formatDatetime(ms.getPublishTime(), Util.unicodeToGBK("yyyy年")) + Util.unicodeToGBK("第" + ms.getStageNum() + "期 出版日期" 
						+ Util.formatDatetime(ms.getPublishTime(), "yyyy年MM月dd日"));
			return rs;
		}
	}

	/**
	 * 获取新闻页媒体
	 */
	private String getMedia(News news) {
		int mediaId = news.getMedia();
		if (mediaId == -1)
			return null;
		if(mediaId == 4006)mediaId = 4003;
		Media media = (Media) ItemManager.getInstance().get(
				new Integer(mediaId), Media.class);
		if (media == null) {
			return null;
		}
		String desc = media.getDesc();
		String url = "";

		//desc = Util.unicodeToGBK("来源:") + desc;
		if (url == null || url.equals(""))
			return String.valueOf(desc);

		StringBuffer sb = new StringBuffer();
		//sb.append(Util.unicodeToGBK("来源:"));
		sb.append("<a href=");
		sb.append(url);
		sb.append(" target=_blank>");
		sb.append(desc);
		sb.append("</a>");
		return sb.toString();
	}

	/**
	 * 获取股票机构
	 */
	private String getStockOrg(News news) {
		String param = news.getParam();
		String stockOrg = (String) EntityParamUtil.getEntityParamItem(param,
				EntityParamUtil.ENTITY_PARAM_STOCK_ORG);

		if (stockOrg == null || stockOrg.trim().length() == 0)
			return "";

		String ret = Util.unicodeToGBK("机构：") + stockOrg;
		return ret;
	}

	/**
	 * 获取股票代码
	 */
	private String getStockCode(News news) {
		/*
		 * String param = news.getParam(); String stockCode = (String)
		 * EntityParamUtil.getEntityParamItem(param,
		 * EntityParamUtil.ENTITY_PARAM_STOCK_CODE);
		 * 
		 * if (stockCode == null || stockCode.trim().length() == 0) return "";
		 * 
		 * String imgHref = "http://stock.business.hexun.com/q/index.php?code=" +
		 * stockCode; String imgAlt = "Stock Code:" + stockCode; String imgSrc =
		 * "http://stock.hexun.com/stock_image/image/kday_small/" + stockCode +
		 * ".png"; String marketHref =
		 * "http://stock.business.hexun.com/q/bc.php?code=" + stockCode; String
		 * forumHref = "http://stock.business.hexun.com/m/mb.php?code=" +
		 * stockCode;
		 * 
		 * String ret = ""; ret += "<table cellSpacing=0 cellPadding=0 border=0
		 * align=center>"; ret += "<tr>"; ret += "<td align='center'>"; ret += "<a
		 * href='" + imgHref + "' target='_blank'>"; ret += "<img border=0
		 * alt='" + imgAlt + "' src='" + imgSrc + "'></a>"; ret += "</td>";
		 * ret += "</tr>"; ret += "<tr>"; ret += "<td>"; ret +=
		 * "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; ret += "(<a href='" + marketHref + "'
		 * target='_blank'>" + Util.unicodeToGBK("鐞涘本鍎??") + "</a>-"; ret += "<a
		 * href='" + forumHref + "' target='_blank'>" + Util.unicodeToGBK("鐠佸搫娼??") + "</a>)<br>";
		 * ret += "</td>"; ret += "</tr>"; ret += "</table>";
		 * 
		 * return ret;
		 */
		String stockCode = news.getStockcode();

		if (stockCode == null || stockCode.trim().length() == 0)
			return "";

		String ret = stockCode;
		return ret;
	}

	/**
	 * 获取作者
	 */
	private String getAuthor(News news) {
		String author = news.getAuthor();
		if (author == null || author.trim().equals(""))
			return "";
		String ret =  author;
//		String ret = Util.unicodeToGBK("娴ｆ粏鍋欓檱7鑱熼敍?闄??) + author;
		return ret;
	}

	/**
	 * 获取文章页媒体logo
	 */
	private String getMediaLogo(News news) {
		int mediaId = news.getMedia();
		if (mediaId == -1)
			return null;
		if(mediaId == 4006)mediaId = 4003;

		Media media = (Media) ItemManager.getInstance().get(
				new Integer(mediaId), Media.class);
		if (media == null) {
			return null;
		}
		String logo = media.getLogo();
		String url = media.getUrl();

		if (logo == null || logo.equals(""))
			return "";

		StringBuffer sb = new StringBuffer();
		if (url != null && !url.equals("")) {
			sb.append("<a href=");
			sb.append(url);
			sb.append(" target=_blank>");
		}
		sb.append("<img src=");
		sb.append(logo);
		sb.append(" border=0>");
		if (url != null && !url.equals("")) {
			sb.append("</a>");
		}
		return sb.toString();
	}

	private String getRelativeNews(News news) {

		if (news == null)
			return "";

		String storePath = PageManager.getRelativeNewsPath(news, false);
		String result = "<!--#include virtual=\"" + storePath + "\" -->";

		return result;
	}

	private String getRelativeHint(News news) {

		if (news == null)
			return "";

		String storePath = PageManager.getRelativeHintPath(news, false);
		String result = "<!--#include virtual=\"" + storePath + "\" -->";

		return result;
	}

	private String getRelativeBlog(News news) {

		if (news == null)
			return "";

		String storePath = PageManager.getRelativeBlogPath(news, false);
		String result = "<!--#include virtual=\"" + storePath + "\" -->";

		return result;
	}

	private String getRelativeSaybar(News news) {

		if (news == null)
			return "";

		String storePath = PageManager.getRelativeSaybarPath(news, false);
		String result = "<!--#include virtual=\"" + storePath + "\" -->";

		return result;
	}

	private String getText(News news, int mpage) {

		if (news == null)
			return "";

		String content = "";

		if (mpage >= 0) {
			content = handleMultiPage(news, mpage);
		} else {
			content = news.getText();

			// 股票特殊处理开始
			boolean isStockChannel = false;
			String category = news.getCategory();

			//if (category.indexOf("100228599") > -1) {// 股票频道
			//	isStockChannel = true;
			//}

			if (isStockChannel) {//股票频道
				StringBuffer stockContent = new StringBuffer(content);
				String org = news.getOrg();
				String stockCode = news.getStockcode();
				String keyword = news.getKeyword();
				int smpos = stockContent
						.indexOf("<TABLE id=bqshengming20070928");
				if (org != null && !org.equals("")) {
					if (smpos > 0) {
						stockContent.insert(smpos,
								"<div style=\"margin-top:-8px;\">(" + org
										+ ")</div>");
					} else {
						stockContent.append("<div style=\"margin-top:-8px;\">("
								+ org + ")</div>");
					}
				}
				if (stockCode != null && !stockCode.equals("")
						&& stockCode.length() > 5) {
					String whichQuote = "SH";
					if (stockCode.startsWith("0")) {
						whichQuote = "SZ";
					}
					stockContent
							.insert(
									0,
									"<div align=\"center\"><a href=\"http://stockdata.stock.hexun.com/"
											+ stockCode
											+ ".shtml\" target=\"_blank\"><img src=\"http://minpic.quote.stock.hexun.com/WebPic/"
											+ whichQuote + "/Min/" + stockCode
											+ "_emb.gif\" alt=\"" + keyword
											+ Util.unicodeToGBK("鐞涘本鍎??")
											+ "\" border=\"0\"></a></div>");
				}
				content = stockContent.toString();
			}
			// 股票特殊处理结束

		}

		// auto add table tag
		// content = handleAutoTable(content);

		// 瀹搞劌绠欓獮鍨啞
		// content = handleBigadfrag(content, news);

		return content;
	}

	/**
	 * 处理分页
	 * 
	 * @param entity
	 *            新闻实体
	 * @param page
	 *            第page+1页
	 */
	private String handleMultiPage(News entity, int page) {
		StringBuffer sb = new StringBuffer();
		try {
			String content = entity.getText();

			// commmon text
			String commonText = getContentByTag(content, BEGIN_HEXUNMPCOMMON,
					END_HEXUNMPCOMMON);
			sb.append(commonText);

			// top banner
			String topBannerFlag = getContentByTag(content,
					BEGIN_HEXUNMPBANNER_TOP, END_HEXUNMPBANNER_TOP);
			if (!topBannerFlag.equals("2")) {
				String banner1 = getBanner1(entity, page);
				if((entity.getAttr() == 4 || entity.getAttr()==5) && page > -1)
					banner1 = "";
				sb.append(banner1);
			}

			// content
			String newsText = getContentByTag(content, page, BEGIN_HEXUNMPCODE,
					END_HEXUNMPCODE);
			int idx = newsText.indexOf(END_HEXUNSUBHEAD);
			if (idx != -1
					&& idx + END_HEXUNSUBHEAD.length() < newsText.length()) {
				newsText = newsText.substring(idx + END_HEXUNSUBHEAD.length());
			}
			sb.append(newsText);

			// 股票特殊处理开始
			boolean isStockChannel = false;
			String category = entity.getCategory();

			//if (category.indexOf("100228599") > -1) {// 股票频道
			//	isStockChannel = true;
			//}

			if (isStockChannel) {// 股票频道
				int totalPage = getTotalPage(content);
				if (page == 0) {// 第一页判断如果有推荐股票加行情图
					// String stockCode = entity.getStockcode();
					String stockCode = "need notice";
					String keyword = entity.getKeyword();

					if (stockCode != null && !stockCode.equals("")) {
						String whichQuote = "SH";
						if (stockCode.startsWith("0")) {
							whichQuote = "SZ";
						}
						newsText = "<div align=\"center\"><a href=\"http://stockdata.stock.hexun.com/urwh/dynamic/default/"
								+ stockCode
								+ ".shtml\" target=\"_blank\"><img src=\"http://minpic.quote.stock.hexun.com/WebPic/"
								+ whichQuote
								+ "/Min/"
								+ stockCode
								+ "_emb.gif\" alt=\""
								+ keyword
								+ Util.unicodeToGBK("鐞涘本鍎??")
								+ "\" border=\"0\"></a></div>" + newsText;
					}
				} else if (page == totalPage - 1) {//最后一页判断是否加机构
					int smpos = newsText
							.indexOf("<TABLE id=bqshengming20070928");
					// String org = entity.getOrg();
					String org = "need notice";
					if (org != null && !org.equals("")) {
						if (smpos > 0) {
							StringBuffer stockContent = new StringBuffer(
									newsText);
							stockContent.insert(smpos,
									"<div style=\"margin-top:-8px;\">(" + org
											+ ")</div>");
							newsText = stockContent.toString();
						} else {
							newsText = newsText
									+ "<div style=\"margin-top:-8px;\">(" + org
									+ ")</div>";
						}
					}
				}

			}

			// 股票特殊处理结束

			// bottom banner
			String bottomBannerFlag = getContentByTag(content,
					BEGIN_HEXUNMPBANNER_BOTTOM, END_HEXUNMPBANNER_BOTTOM);
			if (bottomBannerFlag.equals("2")) {
				String banner2 = getBanner2(entity, page);
				sb.append(banner2);
				if(entity.getSubtype() != News.SUBTYPE_ZUTU)
					sb.append(BeanTagUtil.getInstance().getPageNav(entity, page));
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		return sb.toString();
	}

/**
	 * 返回最大页数
	 * 
	 * @param news
	 *            新闻实体
	 * @return 最大页数
	 */
	// private int getMaxPage(News news) {
	// int count = 0;
	// String content = news.getText();
	// String bTag = BEGIN_HEXUNMPCODE;
	// String eTag = END_HEXUNMPCODE;
	//
	// while (true) {
	// int idx1 = content.indexOf(bTag);
	// int idx2 = content.indexOf(eTag);
	// if (idx1 == -1 || idx2 == -1)
	// break;
	//
	// if (idx2 >= idx1 + bTag.length()) {
	// count++;
	// }
	// content = content.substring(idx2 + eTag.length());
	// }
	// return count;
	// }
	/**
	 * 用于下拉列表形式分页导航, 显示在下拉列表框中的条目 若在分页设置未定义, 则默认为"第N页"
	 * 
	 * @param news
	 *            新闻实体
	 * @param return
	 *            条目数组
	 */
	// private String[] getSubheadList(News news) {
	// String content = news.getText();
	// String bTag = BEGIN_HEXUNSUBHEAD;
	// String eTag = END_HEXUNSUBHEAD;
	//
	// int maxPage = getMaxPage(news);
	// String[] ret = new String[maxPage];
	//
	// for (int i = 0; i < ret.length; i++) {
	// int idx1 = content.indexOf(bTag);
	// int idx2 = content.indexOf(eTag);
	// if (idx1 == -1 || idx2 == -1)
	// break;
	//
	// if (idx2 >= idx1 + bTag.length()) {
	// ret[i] = content.substring(idx1 + bTag.length(), idx2).trim();
	// }
	// content = content.substring(idx2 + eTag.length());
	// }
	// return ret;
	// }
	/**
	 * 下拉列表形式的分页导航
	 * 
	 * @param news
	 *            新闻实体
	 * @param page
	 *            第page+1页
	 */
	private String getBanner1(News news, int page) {

		return ContentGenerator.genTopBanner(news, page);

		// String[] list = getSubheadList(news);
		//
		// StringBuffer sb = new StringBuffer();
		// sb.append("<SELECT
		// onchange=javascript:window.location=(this.options[this.selectedIndex].value);
		// name=gotopage>");
		// for (int i = 0; i < list.length; i++) {
		// sb.append("<OPTION value=");
		// sb.append(getUrlByPage(news, i));
		// if (i == page)
		// sb.append(" selected");
		// sb.append(">");
		// String pageFlag = "第" + (i + 1) + "页";
		// sb.append(Util.unicodeToGBK(pageFlag));
		// String desc = list[i];
		// if (desc != null && !desc.equals("")) {
		// sb.append(":");
		// sb.append(desc);
		// }
		// sb.append("</OPTION>");
		// }
		// sb.append("</SELECT>");
		// return sb.toString();
	}

	/**
	 * 上一页,下一页形式的分页导航
	 * 
	 * @param news
	 *            新闻实体
	 * @param page
	 *            第page+1页
	 */
	private String getBanner2(News news, int page) {
	
	String previousPage = Util.unicodeToGBK("上一页");
	String nextPage = Util.unicodeToGBK("下一页");
	StringBuffer sb = new StringBuffer();
	int maxPage = getMaxPage( news );
	sb.append("<div id='pageNext'>");
	if( page!=0 )
	{
		sb.append( "<div class='page'><a target=\"_self\" href='" );
		sb.append( getUrlByPage(news, page-1) );
		sb.append( "'>" );
		sb.append( previousPage );
		sb.append( "</a></div>" );
	}
	for(int i=0; i<maxPage; i++)
	{
		if( page==i )
		{
			sb.append( "<div class='nored'>" );
			sb.append( (i+1) );
			sb.append( "</div>" );
		} else {
			sb.append( "<div class='num'><a target=\"_self\" href='" );
			sb.append( getUrlByPage(news, i) );
			sb.append( "'>" );
			sb.append( (i+1) );
			sb.append( "</a></div>" );
		}
	}
	if( page!=(maxPage-1) )
	{
		sb.append( "<div class='page'><a target=\"_self\" href='" );
		sb.append( getUrlByPage(news, page+1) );
		sb.append( "'>" );
		sb.append( nextPage );
		sb.append( "</a></div>" );
	}
	sb.append("</div>");
	String str = "";
	/*
	if(news.getSubtype() == News.SUBTYPE_ZUTU && page == maxPage-1 && page>0)
	{
		str += "<script>pausePlay();</script>";
		str += "<div id=\"EOStools\"><div id=\"top\"></div><div id=\"content\" class=\"clearfix\"><div id=\"subColumnA\"><a id=\"replay\" target=\"_self\" href=\""+ news.getUrl() +"\">"+Util.unicodeToGBK("重复播放")+">></a></div><div id=\"subColumnB\"> ";
		News next = NewsUtil.getNextNewsOfZutu(news);
		if(next!=null && !next.equals("")){
			str += Util.unicodeToGBK("进入下一组图:")+"<a target=\"_self\" id=\"related\" href=\""+ next.getUrl() +"\">"+next.getDesc()+"</a> ";
		}else
			str += Util.unicodeToGBK("没有组图了. 点击返回<a href=\"http://www.pusuo.net/\">普索网</a>");
		str+="</div></div></div>";
	}
	*/

	return str + sb.toString();	
	}



	/**
	 *	返回最大页数
	 *	@param news 新闻实体
	 *	@return 最大页数
	 */
	private int getMaxPage( News news )
	{
		int count = 0;
		String content = news.getText();
		String bTag = BEGIN_HEXUNMPCODE;
		String eTag = END_HEXUNMPCODE;

		while( true )
		{
			int idx1 = content.indexOf(bTag);
			int idx2 = content.indexOf(eTag);
			if( idx1==-1 || idx2==-1 ) break;

			if( idx2 >= idx1+bTag.length() )
			{
				count++;
			}
			content = content.substring(idx2+eTag.length());
		}
		return count;
	}
	
	
	/**
	 *	返回分页url
	 *	@param eItem 新闻实体
	 *	@param page 第page+1页. 0代表第一页,依次类推
	 *		第一页 http://astro.sohu.com/20041114/n230982770.shtml
	 *		第二页 http://astro.sohu.com/20041114/n230982770_1.shtml
	 *		第三页 http://astro.sohu.com/20041114/n230982770_2.shtml
	 *	@return 组合后的url
	 */
	private String getUrlByPage( EntityItem eItem, int page )
	{
		String ret = eItem.getUrl();
		if( page>0 )
		{
			int idx = ret.indexOf(".html");
			if( idx!=-1 )
			{
				ret = ret.substring( 0, ret.indexOf(".html") );
				ret += "_"+page+".html";
			}
		}
		return ret;
	}


	/**
	 * 返回分页url
	 * 
	 * @param eItem
	 *            新闻实体
	 * @param page
	 *            第page+1页. 0代表第一页,依次类推 第一页
	 *            http://astro.hexun.com/20041114/n230982770.shtml 第二页
	 *            http://astro.hexun.com/20041114/n230982770_1.shtml 第三页
	 *            http://astro.hexun.com/20041114/n230982770_2.shtml
	 * @return 组合后的url
	 */
	// private String getUrlByPage(EntityItem eItem, int page) {
	// String ret = eItem.getUrl();
	// if (page > 0) {
	// int idx = ret.indexOf(".html");
	// if (idx != -1) {
	// ret = ret.substring(0, ret.indexOf(".html"));
	// ret += "_" + page + ".html";
	// }
	// }
	// return ret;
	// }
	private String getContentByTag(String content, String bTag, String eTag) {
		return getContentByTag(content, 0, bTag, eTag);
	}

	/**
	 * 返回标签中的内容
	 * 
	 * @param content
	 *            新闻全部内容
	 * @param page
	 *            返回第page+1页的内容, 页数范围 0 -- page
	 * @param bTag
	 *            eTag 起始标签和终止标签
	 * @return 若标签不完整,返回空字符串. 否则返回标签中的内容
	 */
	private String getContentByTag(String content, int page, String bTag,
			String eTag) {
		String ret = content;
		for (int i = 0; i <= page; i++) {
			int idx1 = ret.indexOf(bTag);
			int idx2 = ret.indexOf(eTag);
			if (idx2 >= idx1 + bTag.length()) {
				if (i == page) {
					ret = ret.substring(idx1 + bTag.length(), idx2);
					break;
				} else {
					ret = ret.substring(idx2 + eTag.length());
				}
			} else {
				log.debug("handleMultiPage -- tag not full. bTag:" + bTag
						+ " eTag:" + eTag + " @entity " + entity.getId()
						+ "   idx1 " + idx1 + "  idx2 " + idx2);
				ret = "";
				break;
			}
		}
		return ret;
	}

	/**
	 * 处理正文中的画中画广告
	 */
	private String handleBigadfrag(String content, News news) {
		try {
			if (content == null)
				return "";

			final String bigadfrag = "bigadfrag";
			final String bigadfrag2 = "bigadfrag2";
			Iterator it = template.getTFMaps().iterator();
			TFMap tfmap = null;
			TFMap tfmap2 = null;
			while (it.hasNext()) {
				TFMap t = (TFMap) it.next();
				if (t.getName().equals(bigadfrag))
					tfmap = t;
				if (t.getName().equals(bigadfrag2))
					tfmap2 = t;
				if (tfmap != null && tfmap2 != null)
					break;
			}
			if (tfmap == null && tfmap2 == null)
				return content.toString();

			// 获取第一个碎片的内容
			EntityItem mEntity = null;
			if (tfmap != null)
				mEntity = getMappingEntity(tfmap, entity);
			String adContent = null;
			if (mEntity != null)
				adContent = ClientFile.getInstance()
						.read(
								PageManager.getFStorePath(mEntity, tfmap
										.getId(), true));
			if (adContent != null)
				adContent = Util.unicodeToGBK(adContent);

			// 获取第二个碎片的内容
			EntityItem mEntity2 = null;
			if (tfmap2 != null)
				mEntity2 = getMappingEntity(tfmap2, entity);
			String adContent2 = null;
			if (mEntity2 != null)
				adContent2 = ClientFile.getInstance().read(
						PageManager.getFStorePath(mEntity2, tfmap2.getId(),
								true));
			if (adContent2 != null)
				adContent2 = Util.unicodeToGBK(adContent2);

			// 将两个碎片的内容加入到正文中
			content = mergeContent(content, adContent, adContent2, news);

			return content;
		} catch (Exception e) {
			log.error("BeanTag -- handleBigadfrag exception. ", e);
			return "";
		}
	}

	private String mergeContent(String content, String adContent,
			String adContent2, News news) {
		if (adContent == null && adContent2 == null)
			return content;
		if (adContent == null)
			adContent = "";
		if (adContent2 == null)
			adContent2 = "";

		String adContentAll = "";
		adContentAll = "<table border=0 cellspacing=0 cellpadding=0  align=\"left\">";
		adContentAll += "	<tr><td valign=top>";
		adContentAll += adContent;
		adContentAll += "	</td></tr>";
		adContentAll += "	<tr><td>";
		adContentAll += adContent2;
		adContentAll += "	</td></tr>";
		adContentAll += "</table>";

		try {
			// 页面无广告
			if (news.getText().indexOf("<HEXUN_NO_AD>") >= 0) {
				return content;
			}

			// 广告在最后
			if (news.getText().indexOf("<HEXUN_AD_LAST>") >= 0) {
				content = content + adContentAll;
				return content;
			}

			// content is GBK
			StringBuffer copycontent = new StringBuffer(content.toLowerCase());
			final String fullstop = Util.unicodeToGBK("閵??闄??");

			// 1. 计算正文<table>数量
			int tableCount = 0;
			int idx1 = 0, idx2 = 0;
			while (true) {
				idx1 = copycontent.indexOf("<table", idx2);
				idx2 = copycontent.indexOf("</table>", idx1);
				if (idx1 == -1 || idx2 == -1)
					break;
				if (idx1 >= 0)
					tableCount++;
			}

			// 正文中无<table>, 在100个文字后插入广告
			if (tableCount == 0) {
				int len1 = getCharLength(copycontent.toString());
				if (len1 <= 100) {
					// log.debug("正文中无<table>, 文字小于100, 广告前置");
					// 文字小于100, 广告前置
					content = adContentAll + content;
				} else {
					int pos1 = getPosition(copycontent.toString(), 100);
					int p1 = copycontent.indexOf(fullstop, pos1);
					if (p1 == -1) {
						content = content + adContentAll;
					} else {
						content = content.substring(0, p1 + 1) + adContentAll
								+ content.substring(p1 + 1);
					}
				}
				return content;
			}

			// 正文中包含<table>
			// 首先在正文开始与第一个<table>间找位置
			int tidx2 = copycontent.indexOf("<table");
			int len2 = getCharLength(copycontent.substring(0, tidx2));
			if (len2 >= 400) {
				int pos2 = getPosition(copycontent.substring(0, tidx2), 100);
				int p2 = copycontent.indexOf(fullstop, pos2);
				if (p2 != -1) {
					content = content.substring(0, p2 + 1) + adContentAll
							+ content.substring(p2 + 1);
					return content;
				}
			}

			// 在table 之间找地方
			boolean doad = false;
			int bidx = 0, eidx = 0;
			while (true) {
				bidx = copycontent.indexOf("</table>", eidx);
				eidx = copycontent.indexOf("<table", bidx + 8);
				if (bidx == -1 || eidx == -1)
					break;

				int len3 = getCharLength(copycontent.substring(bidx + 8, eidx));
				if (len3 >= 500) {
					int pos3 = getPosition(copycontent
							.substring(bidx + 8, eidx), 200);

					int p3 = copycontent.indexOf(fullstop, (bidx + 8) + pos3);
					if (p3 != -1) {
						content = content.substring(0, p3 + 1) + adContentAll
								+ content.substring(p3 + 1);
						doad = true;
						break;
					}
				}
			}
			if (doad) {
				return content;
			}

			// 在最后一个</table>后面找
			int tidx4 = copycontent.lastIndexOf("</table>") + 8;
			int len4 = getCharLength(copycontent.substring(tidx4));
			if (len4 >= 200) {
				int pos4 = getPosition(copycontent.substring(tidx4), 200);
				int p4 = copycontent.indexOf(fullstop, tidx4 + pos4);
				if (p4 != -1) {
					content = content.substring(0, p4 + 1) + adContentAll
							+ content.substring(p4 + 1);
				} else {
					content = content + adContentAll;
				}
			} else {
				content = content + adContentAll;
			}

			return content;

		} catch (Exception e) {
			log.error("BeanTag -- MergeContent exception. ", e);
			return content + adContentAll;
		} finally {
		}
	}

	private static int getCharLength(String content) {
		StringBuffer cc = new StringBuffer(content);
		int ret = 0;

		boolean t1 = false, t2 = false;
		for (int i = 0; i < cc.length(); i++) {
			if (cc.charAt(i) == '<')
				t1 = true;
			if (cc.charAt(i) == '>')
				t2 = true;
			if (!t1 && !t2) {
				ret++;
			}
			if (t1 && t2) {
				t1 = false;
				t2 = false;
			}
		}
		return ret;
	}

	private int getPosition(String content, int lLimit) {
		StringBuffer cc = new StringBuffer(content);
		int pos = 0;
		int chars = 0;

		boolean t1 = false, t2 = false;
		for (int i = 0; i < cc.length(); i++) {
			pos++;
			if (cc.charAt(i) == '<')
				t1 = true;
			if (cc.charAt(i) == '>')
				t2 = true;
			if (!t1 && !t2)
				chars++;
			if (chars > lLimit)
				break;
			if (t1 && t2) {
				t1 = false;
				t2 = false;
			}
		}
		return pos;
	}

	private EntityItem getHpEntity(EntityItem eItem) {
		String category = eItem.getCategory();
		if (category == null || category.equals("")) {
			return null;
		}
		String hpId = category.split(Global.CMSSEP)[0];
		return (EntityItem) ItemManager.getInstance().get(new Integer(hpId),
				EntityItem.class);
	}

	private EntityItem getMappingEntity(TFMap tfmap, EntityItem eItem) {
		if (eItem.getType() == ItemInfo.HOMEPAGE_TYPE)
			return eItem;

		List list = ItemUtil.getEntityParents(eItem);
		EntityItem ret = null;

		for (int i = 0; list != null && i < list.size(); i++) {
			EntityItem entity = (EntityItem) list.get(i);
			if (ADManager.getInstance().belong(tfmap.getId(), entity.getId())) {
				ret = entity;
				break;
			}
		}
		// 此广告不是分专题投放, 新闻实体的碎片则投在首页实体下面, 专题则投在本专题实体下面
		if (ret == null) {
			if (eItem.getType() == ItemInfo.NEWS_TYPE) {
				return getHpEntity(eItem);
			} else {
				return eItem;
			}
		} else {
			return ret;
		}
	}

	private String handleAutoTable(String content) {
		StringBuffer sb = new StringBuffer(content);
		List aList = new LinkedList();
		try {
			// match <a href=xxx><img src=xxx></a>
			String aReg = "(<a[^>]+?>[^<]*?<img[^>]+?>[^<]*?</a>)";
			Pattern p = Pattern.compile(aReg, Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);
			Matcher m = p.matcher(content);
			while (m.find()) {
				String tag = m.group(1);
				aList.add(tag);
				sb = addTable(sb, tag);
			}

			// match <img src=xxx>
			String imgReg = "(<img[^>]+>)";
			Pattern p1 = Pattern.compile(imgReg, Pattern.CASE_INSENSITIVE);
			Matcher m1 = p1.matcher(content);
			while (m1.find()) {
				boolean find = false;
				String tag = m1.group(1);
				for (int i = 0; aList != null && i < aList.size(); i++) {
					String aTag = (String) aList.get(i);
					if (aTag.indexOf(tag) >= 0) {
						find = true;
					}
				}
				if (!find) {
					sb = addTable(sb, tag);
					aList.add(tag);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			log.error("handleAutoTable exception -- ", e);
			return content;
		}
	}

	private StringBuffer addTable(StringBuffer sb, String tag) {
		String cont = "<table cellspacing=0 cellpadding=0 border=0><tr><td>"
				+ tag + "</td></tr></table>";
		int idx = 0;
		while (idx >= 0) {
			idx = sb.indexOf(tag, idx);
			if (idx < 0)
				break;

			sb.delete(idx, idx + tag.length());
			sb.insert(idx, cont);
			idx += cont.length();
		}
		return sb;
	}

	private int getTotalPage(String content) {
		if (content == null || content.equals("")) {
			return 0;
		}

		int idx = 0;
		int count = 0;
		while (true) {
			idx = content.indexOf("<HEXUNMPCODE>", idx);
			if (idx == -1) {
				break;
			} else {
				count++;
			}
			idx = idx + "<HEXUNMPCODE>".length();
		}
		return count;
	}

	public int doEndTag() throws JspException {
		// clean up
		return (EVAL_PAGE);
	}

	public String getName() {
		return this.name;
	}

	public int getView() {
		return this.view;
	}

	public String getProperty() {
		return this.property;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setView(int view) {
		this.view = view;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	private static final long ct() {
		return System.currentTimeMillis();
	}

	public String getGroovy() {
		return groovy;
	}

	public void setGroovy(String groovy) {
		this.groovy = groovy;
	}
}
