/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.util.UploadEntity;
import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;
import com.hexun.cms.util.Util;

/**
 * @author Alfred.Yuan
 *
 */
public class ContentGenerator {
	
	public static final int NEWS_STYLE_UNDEFINED = -1;				// 未知样式
	public static final int NEWS_STYLE_ONE_PAGE = 1;				// 不分页-多图
	public static final int NEWS_STYLE_MULTI_PAGE = 2;				// 分页-每页n图
	public static final int NEWS_STYLE_MULTI_PAGE_AND_INDEX = 3;	// 分页-每页n图-索引页
	private static final Log log = LogFactory.getLog(ContentGenerator.class);
	
	public static String addTableAroundPicture(String url, String alt, String desc) {
		
		if (url == null || url.trim().length() == 0)
			return "";
		if (alt == null)
			alt = "";
		if (desc == null)
			desc = "";

		ViewContext context = new ViewContext();
		context.put("url", url);
		context.put("alt", alt);
		context.put("desc", desc);
		context.put("instance", new ContentGenerator());
		String content = ViewEngine.getViewManager().getContent("biz/entity/picture-add-table.vm", context);
		
		return content;
	}

	public static String genMultiPageNews(News news, List entityList, boolean addSelectBox, Map extend) {
		
		if (news == null || entityList == null || entityList.size() == 0)
			return "";
		
		News nextNews = NewsUtil.getNextNewsOfZutu(news);
		
		ViewContext context = new ViewContext();
		if (nextNews != null)
			context.put("nextNewsUrl", nextNews.getUrl());

		//patch zutu by shijinkui 09.05.22
		String pics  = news.getPictures();
		if(pics!=null && !pics.equals("") && pics.split(Global.CMSSEP).length>entityList.size()){
			UploadEntity ue = (UploadEntity)entityList.get(0);
			pics = pics.substring(0,pics.indexOf(String.valueOf(ue.getPicture().getId())));
//			context.put("joinnflag", new Integer(pics.split(Global.CMSSEP).length));
		}

		//patch zutu by shijinkui 09.06.10
		List elist = new ArrayList();
		String nc = (String)extend.get(NewsManager.PROPERTY_NAME_OLDTEXT);
		if(news.getSubtype() == News.SUBTYPE_ZUTU && nc!=null && !nc.equals("")){
			String _pics[] = pics.split(Global.CMSSEP);
			UploadEntity ue = null; 
			news.setText(nc);
			MultiPageContent content = ContentParser.parse(news);
			List plist = ContentParser.getPageList(content);
			if (plist != null)
				plist = content.getItems();
			
			Pattern pattern = Pattern.compile("(</td></tr></table>).+", Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
			Matcher matcher = null;
			MultiPageItem mpi = null, _tmp = null;
			for(int i = 0; _pics.length == plist.size() && i < _pics.length; i++){
				for(int j = 0; j < plist.size(); j++){
					_tmp = (MultiPageItem)plist.get(j);
					if(_tmp.getSubtext().indexOf(_pics[i]+".jpg")>-1)
					{
						mpi = (MultiPageItem)plist.get(j);
						break;
					}
				}
				
				if(mpi == null)continue;
				ue = new UploadEntity();
				//副标题
				ue.setDesc(mpi.getSubhead());
				
				//图片alt属性
				/*if(mpi.getSubhead().length() > 0){//新增图片alt加入注释+文章标题,并过滤特殊字符 2010.5.31 shijinkui
					ue.setImageAlt(Util.RemoveHTML(mpi.getSubhead()+"_"+news.getDesc()).replaceAll("\"|“|”|'", ""));
					System.out.println("1: " + mpi.getSubhead());
				}else{
					if(StringUtils.isNotEmpty(mpi.getSubtext())){
						ue.setImageAlt(Util.RemoveHTML(mpi.getSubtext()+"_"+news.getDesc()).replaceAll("\"|“|”|'", ""));
						System.out.println("2:" + mpi.getSubtext());
					}else{
						ue.setImageAlt(news.getDesc());
						System.out.println("333333333333");
					}
				}*/
				//正文
				if(mpi.getSubtext().length() > 0)
				{
					matcher = pattern.matcher(mpi.getSubtext());
					if(matcher.find())
						ue.setText(matcher.group(0).trim());
				}
				//图片
				Picture picture = (Picture)ItemManager.getInstance().get(new Integer(_pics[i]), Picture.class);
				ue.setPicture(picture);
				elist.add(ue);
			}
			String zutuorder = (String) extend.get(NewsManager.PROPERTY_NAME_ZUTUORDER);
			if(zutuorder == null || zutuorder.equals("0"))
			{
				List tmpl = new ArrayList();
				entityList.addAll(0, elist);
				for(int i = entityList.size()-1; i >-1; i--)
					tmpl.add(entityList.get(i));
				entityList = tmpl;
				tmpl = null;elist = null;
			}else{
				entityList.addAll(0, elist);
			}
		}
		context.put("news", news);
		context.put("entities", entityList);
		context.put("addSelectBox", new Boolean(addSelectBox));
		context.put("instance", new ContentGenerator());
		String content = ViewEngine.getViewManager().getContent("biz/entity/multi-page-news.vm", context);
		
		return content;
	}
	
	public static String genMultiPageAndIndexNews(News news, List entityList, boolean addSelectBox) {
		
		if (news == null || entityList == null || entityList.size() == 0)
			return "";
		
		News nextNews = NewsUtil.getNextNewsOfZutu(news);
		
		ViewContext context = new ViewContext();
		if (nextNews != null)
			context.put("nextNewsUrl", nextNews.getUrl());
	
		//patch zutu by shijinkui 09.05.22
		String pics  = news.getPictures();
		if(pics!=null && !pics.equals("") && pics.split(Global.CMSSEP).length>entityList.size()){
			UploadEntity ue = (UploadEntity)entityList.get(0);
			pics = pics.substring(0,pics.indexOf(String.valueOf(ue.getPicture().getId())));
			context.put("joinnflag", new Integer(pics.split(Global.CMSSEP).length));
		}

		context.put("news", news);
		context.put("entities", entityList);
		context.put("addSelectBox", new Boolean(addSelectBox));
		context.put("instance", new ContentGenerator());
		String content = ViewEngine.getViewManager().getContent("biz/entity/multi-page-and-index-news.vm", context);
		
		return content;
	}
	
	public static String genContentFromPageList(List pageList, boolean hasIndex, boolean addSelectBox) {
		
		if (pageList == null || pageList.size() == 0)
			return "";
		
		ViewContext context = new ViewContext();
		context.put("pageList", pageList);
		context.put("hasIndex", new Boolean(hasIndex));
		context.put("addSelectBox", new Boolean(addSelectBox));
		String content = ViewEngine.getViewManager().getContent("biz/entity/merge-page-list.vm", context);
		
		return content;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static String genTopBanner(News news, int currPage) {

		MultiPageContent mpc = new MultiPageContent(news);
		if (mpc == null || mpc.getPageCount() <= 0)
			return "";
		
		boolean isZutu = news.getSubtype() == News.SUBTYPE_ZUTU ? true : false;
		int newsStyle = ContentParser.getNewsStyle(mpc);
		
		String vmPage = "compile/mn-zutu-nav-top.vm";
		if (isZutu && newsStyle == ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX) {
			if (currPage == 0) // 索引页不显示下拉框
				return "";
			vmPage = "compile/mn-zutu-index-nav-top.vm";
		}

		ViewContext context = new ViewContext();
		context.put("newsUrl", news.getUrl());
		context.put("itemList", mpc.getItems());
		context.put("currPage", new Integer(currPage));
		context.put("instance", new ContentGenerator());
		context.put("nchannel", ""+news.getChannel());
		String content = ViewEngine.getViewManager().getContent(vmPage, context);
		
		return content;
	}
	
	public static String genBottomBanner(News news, int currPage) {
		
		MultiPageContent mpc = new MultiPageContent(news);
		if (mpc == null || mpc.getPageCount() <= 0)
			return "";
		
		boolean isZutu = news.getSubtype() == News.SUBTYPE_ZUTU ? true : false;
		int newsStyle = ContentParser.getNewsStyle(mpc);
		
		News nextNews = null;
		String vmPage = "compile/mn-general-nav-bottom.vm";		// 非组图
		if (isZutu) {
			nextNews = NewsUtil.getNextNewsOfZutu(news);

			vmPage = "compile/mn-zutu-nav-bottom.vm";	// 分页
			if (newsStyle == ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX) {
				vmPage = "compile/mn-zutu-index-nav-bottom.vm"; // 带索引页
			}
		}
		
		ViewContext context = new ViewContext();
		if (nextNews != null)
			context.put("nextNewsUrl", nextNews.getUrl());
		context.put("newsUrl", news.getUrl());
		context.put("itemList", mpc.getItems());
		context.put("currPage", new Integer(currPage));
		context.put("instance", new ContentGenerator());
		String content = ViewEngine.getViewManager().getContent(vmPage, context);
		
		return content;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static boolean compareLen(String msg, int len) {
		boolean flag = false;
		if (msg.length() > len)
			return true;
		int size = 0;
		for (int i = 0; i < msg.length(); i++) {
			size++;
			int ii = (int) msg.charAt(i);
			if (ii <= 0 || ii >= 126)
				size++;// 双字节字符
		}
		if (size > len)
			flag = true;
		return flag;
	}
	
	public static String getSmallPictureUrl(String pictureUrl) {
		
		if (pictureUrl == null)
			return "";
		String tempPictureUrl = pictureUrl.trim().toLowerCase();
		
		if (tempPictureUrl.indexOf("http://") == -1 
				|| tempPictureUrl.indexOf(".") == -1)
			return "";
		
		StringBuffer sb = new StringBuffer();
		
		int index = tempPictureUrl.lastIndexOf(".");
		sb.append(pictureUrl.substring(0, index));
		sb.append("_120x90.jpg");
		
		return sb.toString();
	}
	
	public static String getSubUrlOfMutilPageNews(String newsUrl, int page) {
		
		if (newsUrl == null || page == 0)
			return newsUrl;
		
		if (newsUrl.indexOf("http://") == -1 ||
				newsUrl.indexOf(".") == -1)
			return newsUrl;
		
		StringBuffer sb = new StringBuffer();
		
		int index = newsUrl.lastIndexOf(".");
		sb.append(newsUrl.substring(0, index));
		sb.append("_");
		sb.append(String.valueOf(page));
		sb.append(newsUrl.substring(index));
		
		return sb.toString();
	}	
}
