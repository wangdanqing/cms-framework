package net.pusuo.cms.client.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.Permission;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PictureSizeException;
import com.hexun.cms.client.biz.exception.PictureUploadException;
import com.hexun.cms.client.biz.exception.PictureUploadValidateException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.util.ContentGenerator;
import com.hexun.cms.client.biz.util.ContentParser;
import com.hexun.cms.client.biz.util.MultiPageContent;
import com.hexun.cms.client.biz.util.MultiPageItem;
import com.hexun.cms.client.biz.util.NewsUtil;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.taglib.FragTag;
import com.hexun.cms.client.tool.AutoPush;
import com.hexun.cms.client.util.BatchNews;
import com.hexun.cms.client.util.EntityParamUtil;
import com.hexun.cms.client.util.History;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.UploadEntity;
import com.hexun.cms.client.util.UploadUtil;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Magazine;
import com.hexun.cms.core.MagazineSheet;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;
import com.hexun.cms.core.Template;
import com.hexun.cms.core.Video;
import com.hexun.cms.util.Util;

public class NewsAction extends EntityAction {

	private static final Log LOG = LogFactory.getLog(NewsAction.class);
	
	private static final String DEFAULT_LINK = "http://";
	
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;
		Integer id = (Integer) dForm.get("id");
		int _id = id.intValue();
		
		int preId = -1;
		String saveAndNew = request.getParameter("saveandnew");
		if (saveAndNew != null && saveAndNew.trim().equalsIgnoreCase("true") && _id == -1) {
			String preIdParam = request.getParameter("preid");
			try {
				preId = Integer.parseInt(preIdParam);
			} catch (Exception e) {
				preId = -1;
			}
		}

		try {
			News news = null;
			if (_id == -1) {
				news = (News)ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
				ItemUtil.setItemValues(dForm, news);
			} else {
				news = (News)ItemManager.getInstance().get(id, News.class);
			}
			if (news == null) {
				errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
				saveErrors(request, errors);
				return mapping.findForward("failure");				
			}
			
			// mode: mem
			if (_id == -1) {
				// newsMode:init
				request.setAttribute("newsMode", "0");
				//time
				dForm.set("time",new java.sql.Timestamp(System.currentTimeMillis()));
				// from "new" link of subject page
				int pid = ((Integer)dForm.get("pid")).intValue();	
				if (pid > 0) {
					dForm.set("pname", getName(pid));
				}
				
				// from "save and new" button of news page
				if (preId > -1) {
					News oldNews = (News)ItemManager.getInstance().get(new Integer(preId), News.class);
					if (oldNews == null) {
						errors.add("errors.item.notfound", new ActionError("errors.item.notfound"));
						saveErrors(request, errors);
						return mapping.findForward("failure");				
					}
					
					////////////////////////////////////////////////////////////
					// code mess(it will be refactored some time)
					
					// parent
					dForm.set("pid", new Integer(oldNews.getPid()));
					dForm.set("pname", getName(oldNews.getPid()));
					//author
					dForm.set("author", oldNews.getAuthor());					
					// media
					int mediaId = oldNews.getMedia();
					if (mediaId > -1) {
						Media media = (Media)ItemManager.getInstance().get(new Integer(mediaId), Media.class);
						if (media != null) {
							dForm.set("media", new Integer(mediaId));
							dForm.set("medianame", media.getName());
						}
					}
					
					// priority
					int priority = oldNews.getPriority();
					int remaining = priority % 10;
					if (remaining > 0) {
						dForm.set("addprio", "on");
						priority -= remaining;
					}
					dForm.set("priority", new Integer(priority));
					
					// status
					dForm.set("status", new Integer(oldNews.getStatus()));
					
					// subtype
					dForm.set("subtype", new Integer(oldNews.getSubtype()));
					
					// style
					if (oldNews.getSubtype() == News.SUBTYPE_ZUTU) {
						MultiPageContent content = ContentParser.parse(oldNews);
						
						int style = ContentParser.getNewsStyle(content);
						dForm.set("newsstyle", new Integer(style));
					}
				}
			}
			
			// mode: update
			if (_id > -1) {
				// newsMode:update
				request.setAttribute("newsMode", "2");

				// general properties
				ItemUtil.putItemValues(dForm, news);
				
				// parentName
				dForm.set("pname", getName(news.getPid()));
				
				// mediaName
				int mediaId = news.getMedia();
				if (mediaId > -1) {
					Media media = (Media)ItemManager.getInstance().get(new Integer(mediaId), Media.class);
					if (media != null)
						dForm.set("medianame", media.getName());
				}
				
				// reurl
				String reurl = news.getReurl();
				if (reurl == null || reurl.trim().length() == 0) {
					dForm.set("reurl", DEFAULT_LINK);
				}
				
				// authorLink
				String author = news.getAuthor();
				if (author != null && author.trim().length() > 0) {
					String href = "href='";
					int index = author.indexOf(href);
					if (index > -1) {
						author = author.substring(index + href.length());
						index = author.indexOf("'");
						dForm.set("authorlink", author.substring(0, index));
					}
					index = author.lastIndexOf("</a>");
					if (index > -1) {
						author = author.substring(0, index);
						index = author.lastIndexOf(">");
						dForm.set("author", author.substring(index + 1));
					}
				}
						
				// priority
				int priority = news.getPriority();
				int remaining = priority % 10;
				if (remaining > 0) {
					dForm.set("addprio", "on");
					priority -= remaining;
				}
				dForm.set("priority", new Integer(priority));
				
				// stockCode
				String param = news.getParam();
				if (param != null && param.trim().length() > 0) {
					String stockCode = (String)EntityParamUtil.getEntityParamItem(
							param, EntityParamUtil.ENTITY_PARAM_STOCK_CODE);
					dForm.set("stockCode", stockCode);
				}
				
				// style,pageList,selectbox
				if (news.getSubtype() == News.SUBTYPE_ZUTU) {
					MultiPageContent content = ContentParser.parse(news);
					
					int style = ContentParser.getNewsStyle(content);
					dForm.set("newsstyle", new Integer(style));
					
					List pageList = ContentParser.getPageList(content);
					if (pageList != null)
						request.setAttribute("pageList", content.getItems());
					
					boolean select = false;
					String bannerTop = content.getBannerTop();
					if (bannerTop == null || bannerTop.trim().length() == 0 || !bannerTop.equalsIgnoreCase("2"))
						select = true;
					request.setAttribute("selectbox_multi", new Boolean(select));
				} else if (news.getSubtype() == News.SUBTYPE_PICTURE) {
					dForm.set("newsstyle", new Integer(ContentGenerator.NEWS_STYLE_ONE_PAGE));
				}
				
				// pushRecord
				String pushRecord = news.getPushrecord();
				if (pushRecord != null && pushRecord.trim().length() > 0) {
					request.setAttribute("pushrecord", pushRecord.trim());
				}
				
				// template id
				Template template = null;
				String templateParam = news.getTemplate().trim();
				if (templateParam != null && templateParam.indexOf(Global.CMSCOMMA) > -1) {
					int index = templateParam.indexOf(Global.CMSCOMMA);
					String templateId = templateParam.substring(0, index);
					template = (Template)ItemManager.getInstance().get(Integer.valueOf(templateId.trim()), Template.class);
					request.setAttribute("templateId", templateId);
				}
				
				// url
				if (template != null) {
					String templateUrl = PageManager.FTWebPath(template, false);
					String urlFrag = templateUrl + "?ENTITYID=" + id + "&view=" + FragTag.FRAG_VIEW;
					String urlFragWithoutAd = templateUrl + "?ENTITYID=" + id + "&view=" + FragTag.FRAG_VIEW + "&filter=noad";
					String urlFragWithoutSd = templateUrl + "?ENTITYID=" + id + "&view=" + FragTag.FRAG_VIEW + "&filter=nosd";

					request.setAttribute("urlNews", news.getUrl());
					request.setAttribute("urlFrag", urlFrag);
					request.setAttribute("urlFragWithoutAd", urlFragWithoutAd);
					request.setAttribute("urlFragWithoutSd", urlFragWithoutSd);
				}
				//magazine 
				if(news.getMagazineId() > 0)
				{
					Magazine maga = (Magazine) ItemManager.getInstance().get(new Integer(news.getMagazineId()), Magazine.class);
					//set sth
					if(maga.getMzid()!=null && !maga.getMzid().equals("")){
						dForm.set("magavideo", maga.getVideo()==null?"":maga.getVideo());
						dForm.set("magapics", maga.getPics() == null?"":maga.getPics());
						dForm.set("magaaudio", maga.getAudio()==null?"":maga.getAudio());
						dForm.set("magareleatenews", maga.getReleatenews()==null?"":maga.getReleatenews());
						dForm.set("itemorder", maga.getItemorder());
						dForm.set("showPicInPic", String.valueOf(maga.getShowPicInPic()));
					}
				}
				//magazine sheet
				if(news.getMagazineSheetId() > -1)
				{
					String mzdesc = "";
					MagazineSheet ms = (MagazineSheet) ItemManager.getInstance().get(new Integer(news.getMagazineSheetId()), MagazineSheet.class);
					if(ms!=null)mzdesc = ms.getDesc();
						dForm.set("mzdesc", mzdesc);
				}
				
				// video
				String videoParam = news.getVideos();
				if (videoParam != null && videoParam.trim().length() > 0) {
					int index = videoParam.indexOf(Global.CMSSEP);
					if (index > -1)
						videoParam = videoParam.substring(0, index);
					if (StringUtils.isNumeric(videoParam)) {
						Integer videoId = Integer.valueOf(videoParam);
						Video video = (Video)ItemManager.getInstance().get(videoId, Video.class);
						if (video != null) {
							String urlVideo = video.getUrl();
							urlVideo = StringUtils.trimToEmpty(urlVideo);
							String smallPic = video.getSmallpic();
							smallPic = StringUtils.trimToEmpty(smallPic);
							String bigPic = video.getBigpic();
							bigPic = StringUtils.trimToEmpty(bigPic);
							
							request.setAttribute("urlVideo", urlVideo);
							request.setAttribute("smallPic", smallPic);
							request.setAttribute("bigPic", bigPic);							
						}
					}
				}
				
				// Submit Permission
				boolean showSubmit = true;
				int channelId = news.getChannel();
				boolean hasChannelPermission = auth.hasChannel(channelId);
				if (auth.isProduct() || !hasChannelPermission) {
					showSubmit = false;
				}
				if (!auth.isProduct() && channelId == 103 && 
						reurl != null && reurl.trim().length() > 0) {
					showSubmit = true;
				}
				request.setAttribute("showSubmit", new Boolean(showSubmit));
				
				// CS Permission
				boolean hasCSPermission = auth.getUserPermission().get(
						Permission.RESOURCE + Permission.DEPARTMENT + "cs");
				request.setAttribute("hasCSPermission", new Boolean(hasCSPermission));
				
				// navigator
				List navigator = new ArrayList();
				String categoryParam = news.getCategory();
				int index = categoryParam.lastIndexOf(Global.CMSSEP);
				categoryParam = categoryParam.substring(0, index);
				String[] categories = categoryParam.split(Global.CMSSEP);
				for (int i = 0; i < categories.length; i++) {
					String entityId = categories[i];
					EntityItem entity = (EntityItem)ItemManager.getInstance()
						.get(Integer.valueOf(entityId), EntityItem.class);
					if (entity == null) 
						continue;
					navigator.add(entity);
				}
				request.setAttribute("navigator", navigator);
				
				// visit history
				History.addRecord(request, response, auth.getUserName(), _id);
			}
			
			// all mode
			boolean isBusinessEditor = false;
			if (auth.hasChannel(EntityParamUtil.CHANNEL_NAME_BUSINESS)) {
				isBusinessEditor = true;
			}
			request.setAttribute("isBusinessEditor", new Boolean(isBusinessEditor));
		} catch (Exception e) {
			errors.add("errors.item.view", new ActionError("errors.item.view"));
			LOG.error("NewsAction view error.", e);
		}
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			ret = mapping.findForward("failure");
		} else {
			ret = mapping.findForward("item");
		}
		
		return ret;
	}

	public ActionForward uploadTemp(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		
		List tempPicUrlList = new ArrayList();
		try {
			List keyList = new ArrayList();
			keyList.add("temppic1");
			keyList.add("temppic2");
			keyList.add("temppic3");
			
			List entityList = UploadUtil.uploadTempPictureByFiles(auth, form, request, response, keyList, false);
			if (entityList != null) {
				for (int i = 0; i < entityList.size(); i++) {
					UploadEntity entity = (UploadEntity)entityList.get(i);
					tempPicUrlList.add(entity.getUrl());
				}
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		
		if (tempPicUrlList != null && tempPicUrlList.size() > 0)
			request.setAttribute("tempPicUrl", tempPicUrlList);
		
		return mapping.findForward("upload");
	}
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		
		// 处理来自聚合新闻工具的请求
		String fromConv = request.getParameter("fromConv");
		if (fromConv != null && fromConv.equalsIgnoreCase("true")) {
			return editFromConv(auth, mapping, form, request, response);
		}
		
		try {
			String subtypeParam = request.getParameter("subtype");
			int subtype = -1;
			try {
				subtype = Integer.parseInt(subtypeParam);
			} catch (Exception e) {
			}
			if (subtype != News.SUBTYPE_ZUTU) {
				return mapping.findForward("failure");
			}
			
			// 验证图片合法性
			if (!UploadUtil.validateUpload(form, request, response, subtype))
				throw new PictureUploadValidateException();			
			
			List entityList = new ArrayList();
			
			int uploadMode = 1; // 上传模式(0:打包上传;1:逐张上传)
			String uploadModeParam = request.getParameter("uploadmode");
			if (uploadModeParam != null && uploadModeParam.trim().equalsIgnoreCase("0")) {
				uploadMode = 0;
			}
			
			if (uploadMode == 0) { 
				String zipKey = "piczip";
				entityList = UploadUtil.uploadTempPictureByZip(auth, form, request, response, zipKey, true);
			}
			else if (uploadMode == 1) {
				String idsParam = request.getParameter("picids");
				if (idsParam != null || idsParam.trim().length() > 0) {
					List keyList = new ArrayList();
					String[] picIds = idsParam.split(Global.CMSSEP);
					for (int i = 0; i < picIds.length; i++) {
						String key = "picfile" + picIds[i];
						keyList.add(key);
					}	
					entityList = UploadUtil.uploadTempPictureByFiles(auth, form, request, response, keyList, true);
				}
			}
			
			if (entityList == null) {
				LOG.warn("nothing to upload.");
				return mapping.findForward("failure");
			}
			
			List newsList = new ArrayList();
	
			DynaActionForm fm = (DynaActionForm) form;
			String text = (String)fm.get("text");
			
			for (int i = 0; i < entityList.size(); i++) {
				UploadEntity entity = (UploadEntity)entityList.get(i);
				
				BatchNews news = new BatchNews();
				news.setDesc(entity.getFileName());
				news.setText(text);
				news.setFileAbsolutePath(entity.getFilePath());
				news.setImageShowPath(entity.getUrl());
				news.setThumb(entity.getThumb());
				
				newsList.add(news);
			}
			
			// 下拉框
			boolean select = false;
			String addSelectBoxParam = request.getParameter("selectbox_multi");
			if (addSelectBoxParam != null && addSelectBoxParam.equalsIgnoreCase("on")) {
				select = true;
			}	
			request.setAttribute("selectbox_multi", new Boolean(select));
			
			request.setAttribute("newsList", newsList);
			request.setAttribute("newsMode", "1"); // 0:初始化;1:编辑;2:修改
		} catch (PictureUploadValidateException puve) {
			errors.add("errors.picture.upload.validate", new ActionError("errors.picture.upload.validate"));
		} catch (PictureSizeException pse) {
			errors.add("errors.picture.upload.size", new ActionError("errors.picture.upload.size"));
		} catch (PictureUploadException pue) {
			errors.add("errors.picture.upload.general", new ActionError("errors.picture.upload.general"));
		}
		
		try {
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			} else {
				ret = mapping.findForward("edit");
			}
		} catch (Exception e) {
		}

		return ret;
	}
	
	public ActionForward editFromConv(Authentication auth, ActionMapping mapping, 
			ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		String[] idList = request.getParameterValues("idsConv");
		if (idList == null || idList.length == 0) {
			return mapping.findForward("failure");
		}
		
		List newsList = new ArrayList();

		for (int i = 0; i < idList.length; i++) {
			String idParam = idList[i];
			EntityItem item = (EntityItem)ItemManager.getInstance().get(
					new Integer(idParam), EntityItem.class);
			if (item == null)
				continue;
			
			BatchNews batchNews = new BatchNews();
			
			int type = item.getType();
			if (type == ItemInfo.PICTURE_TYPE) {
				Picture picture = (Picture)item;
				UploadEntity entity = UploadUtil.uploadTempPictureByUrl(auth, request, picture.getUrl(), true);
				
				batchNews.setDesc(picture.getDesc());
				batchNews.setText("");
				batchNews.setFileAbsolutePath(entity.getFilePath());
				batchNews.setImageShowPath(entity.getUrl());
				batchNews.setThumb(entity.getThumb());
			} else if (type == ItemInfo.NEWS_TYPE) {
				News news = (News)item;
				if (news.getSubtype() != News.SUBTYPE_PICTURE)
					continue;
				String picId = news.getPictures();
				if (picId == null || picId.trim().length() == 0 || picId.indexOf(Global.CMSSEP) > -1)
					continue;
				Picture picture = (Picture)ItemManager.getInstance().get(new Integer(picId), Picture.class);
				if (picture == null)
					continue;
				UploadEntity entity = UploadUtil.uploadTempPictureByUrl(auth, request, picture.getUrl(), true);

				batchNews.setDesc(news.getDesc());
				batchNews.setText(NewsUtil.removeTableFromText(news.getText()));  
				batchNews.setFileAbsolutePath(entity.getFilePath());
				batchNews.setImageShowPath(entity.getUrl());
				batchNews.setThumb(entity.getThumb());
			} else {
				continue;
			}
			
			newsList.add(batchNews);
		}
		
		request.setAttribute("newsList", newsList);
		request.setAttribute("selectbox_multi", new Boolean(true)); // 下拉框
		request.setAttribute("newsMode", "1"); // 0:初始化;1:编辑;2:修改
		
		try {
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			} else {
				ret = mapping.findForward("edit");
			}
		} catch (Exception e) {
		}
		return ret;
	}
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}
		
		News news = null;

		News newsConfig = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE); // 普通属性
		Map extend = new HashMap(); // 扩展属性

		try {
			BaseForm dForm = (BaseForm) form;
			
			////////////////////////////////////////////////////////////////////
			// 第一步:发布新闻
			////////////////////////////////////////////////////////////////////

			// 保留原先的参数
			int newsId = ((Integer) dForm.get("id")).intValue();
			if (newsId > 0) {
				News newsOrigin = ManagerFacade.getNewsManager().getNews(newsId);

				if(newsOrigin.getSubtype() == News.SUBTYPE_ZUTU)
					extend.put(NewsManager.PROPERTY_NAME_OLDTEXT, newsOrigin.getText());

				if (newsOrigin == null)
					throw new DaoException();
				PropertyUtils.copyProperties(newsConfig, newsOrigin);	
			}

			// 获取被用户修改的参数
			ItemUtil.setItemValues(dForm, newsConfig);
			
			// 修改状态下:合并组图内容
			if (newsId > 0 && newsConfig.getSubtype() == News.SUBTYPE_ZUTU) {
				mergeMutilPageContent(mapping, form, request, response, newsConfig);				
			}
			
			// reurl
			String reurl = newsConfig.getReurl();
			if (reurl != null && reurl.trim().equalsIgnoreCase(DEFAULT_LINK)) {
				newsConfig.setReurl(null);
			}
			
			// author
			String authorLink = (String)dForm.get("authorlink");
			if (authorLink != null && !authorLink.trim().equalsIgnoreCase(DEFAULT_LINK)) {
				String author = newsConfig.getAuthor();
				if (author != null && author.trim().length() > 0) {
					author = "<a href='" + authorLink + "' target='_blank'>" + author + "</a>";
					newsConfig.setAuthor(author);
				}
			}

			// priority
			Integer priority = (Integer) dForm.get("priority");
			String addprio = (String) dForm.get("addprio");
			if (addprio != null && addprio.equalsIgnoreCase("on")) {
				priority = new Integer(priority.intValue() + 2);
				newsConfig.setPriority(priority.intValue());
			}

			// 特别处理:财经频道   modify by shijinkui 2008-03-12
			/*if (auth.hasChannel(EntityParamUtil.CHANNEL_NAME_BUSINESS)) {
				String stockCode = (String) dForm.get("stockCode");
				if ((stockCode != null && stockCode.trim().length() != 0)) {
					String param = newsConfig.getParam();
					param = EntityParamUtil.forceEntityParamItem(param,
							EntityParamUtil.ENTITY_PARAM_INDEX_STOCK_CODE,
							stockCode);
					newsConfig.setParam(param);
				}
				
				String needCanonical = request.getParameter("needCanonical");
				if (needCanonical != null && "true".equals(needCanonical)) {
					extend.put(NewsManager.PROPERTY_NAME_CANONICAL, new Boolean(true));
				}
			}
			*/
			// 传递认证信息
			extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);

			// 传递父对象名称
			extend.put(NewsManager.PROPERTY_NAME_PNAME, ((String) dForm.get("pname")).trim());
			
			if (newsConfig.getId() < 0) { // 发布新闻
				news = ManagerFacade.getNewsManager().addNews(newsConfig, extend);
			} else { // 更新新闻
				news = ManagerFacade.getNewsManager().updateNews(newsConfig, extend);
			}
			if (news == null || news.getId() < 0) {
				throw new DaoException();
			}
			
			// 访问历史
			History.addRecord(request, response, auth.getUserName(), news.getId());
			
			////////////////////////////////////////////////////////////////////
			// 第二步:处理图片
			////////////////////////////////////////////////////////////////////
			
			int subtype = news.getSubtype(); 	// 子类型
			boolean needUpdate = false;			// 更新标志位
			
			if (subtype == News.SUBTYPE_ZUTU) {
				// 上传图片
				List entityList = uploadPictures(news, extend, form, request, response);
				
				// 处理新闻内容
				if (entityList != null && entityList.size() > 0) {
					handleNewsContent(news, extend, entityList, form, request, response);
					needUpdate = true;
				}
			}
			
			////////////////////////////////////////////////////////////////////
			// 第三步:处理视频
			////////////////////////////////////////////////////////////////////
			
			// 上传视频
			if (news.getSubtype() == News.SUBTYPE_VIDEO) {
				List videoList = updateVideo(news, extend, form, request, response);
				if (videoList != null && videoList.size() > 0) {
					needUpdate = true;
				}
			}
			
			////////////////////////////////////////////////////////////////////
			// 第四步:处理杂志
			////////////////////////////////////////////////////////////////////
			if (news.getSubtype() == News.SUBTYPE_MAGAZINE || news.getSubtype() == News.SUBTYPE_TEXT) {
				//生成杂志实体
				Integer msid = (Integer) dForm.get("magazineSheetId");
				String magavideo = (String) dForm.get("magavideo");
				String magapics = (String) dForm.get("magapics");
				String magaaudio = (String) dForm.get("magaaudio");
				String magareleatenews = (String) dForm.get("magareleatenews");
				String magaitemorder = (String) dForm.get("itemorder");
				String maga_ispip = (String)dForm.get("showPicInPic");

				Magazine ma = null;
				if(news.getMagazineId()!=-1){
					//update
					ma = (Magazine)ItemManager.getInstance().get(new Integer(news.getMagazineId()), Magazine.class);
				}else		
				{	//new one
					ma = (Magazine)ItemInfo.getItemByType(ItemInfo.MAGAZINE_TYPE);
					ma.setNewsId(news.getId());
				}
				if(msid!=null && !msid.equals(""))
					ma.setMzid(msid.toString());
				
				ma.setVideo(magavideo);
				ma.setPics(magapics);
				ma.setAudio(magaaudio);
				ma.setReleatenews(magareleatenews);
				if(StringUtils.isNotEmpty(magaitemorder))
					ma.setItemorder(magaitemorder);
				ma.setShowPicInPic(Integer.parseInt(maga_ispip));
				Item m2 = ItemManager.getInstance().update(ma);
				news.setMagazineId(m2.getId());
				needUpdate = true;
				
				if(msid!=null && msid.intValue() > 1)
				{
					news.setMagazineSheetId(msid.intValue());
					needUpdate = true;
				}
			}

			// 更新新闻
			if (needUpdate)
				news = (News) ItemManager.getInstance().update(news);
			
			////////////////////////////////////////////////////////////////////
			// 第四步:推送新闻
			////////////////////////////////////////////////////////////////////
			handlePushNews(mapping, form, request, response, news, extend);
			
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
		} catch (ParentNameException pne) {
			errors.add("errors.item.pnamenotexist", new ActionError("errors.item.pnamenotexist"));
		} catch (PropertyException pe) {
			errors.add("errors.parameter", new ActionError("errors.parameter"));
		} catch (DaoException de) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		}
		
		try {
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			} else {
				// save and new
				String saveAndNew = request.getParameter("saveandnew");
				if (saveAndNew != null && saveAndNew.trim().equalsIgnoreCase("true")) {
					response.sendRedirect("news.do?method=view&saveandnew=true&preid=" + news.getId());
				}
				// save
				else {
					response.sendRedirect("news.do?method=view&id=" + news.getId());
				}
			}
		} catch (Exception e) {
		}

		return ret;
	}
	
	private void mergeMutilPageContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, News newsConfig) {
		
		// 组图类型的修改状态
		if (newsConfig.getSubtype() != News.SUBTYPE_ZUTU)
			return;
		
		// 分页样式
		int style = ContentGenerator.NEWS_STYLE_ONE_PAGE;
		String styleParam = request.getParameter("newsstyle");
		if (styleParam != null) {
			try {
				style = Integer.parseInt(styleParam);
			} catch (Exception e) {}
		}
		if (style != ContentGenerator.NEWS_STYLE_MULTI_PAGE && 
				style != ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX) 
			return;
		
		// 是否有索引页
		boolean hasIndex = false;
		if (style == ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX)
			hasIndex = true;
		
		// 分页内容
		String pageIdsParam = request.getParameter("subids");
		if (pageIdsParam == null || pageIdsParam.trim().length() == 0)
			return;
		
		List pageList = new ArrayList();
		String[] pageIds = pageIdsParam.split(Global.CMSSEP);
		for (int i = 0; i < pageIds.length; i++) {
			String pageId = pageIds[i];
			String subhead = request.getParameter("subhead" + pageId);
			String subtext = request.getParameter("subtext" + pageId);
			pageList.add(new MultiPageItem(subhead, subtext));
		}
		
		// 下拉框
		boolean addSelectBox = false;
		String addSelectBoxParam = request.getParameter("selectbox_multi");
		if (addSelectBoxParam != null && addSelectBoxParam.equalsIgnoreCase("on")) {
			addSelectBox = true;
		}		
		
		// 内容合并
		String content = ContentGenerator.genContentFromPageList(pageList, hasIndex, addSelectBox);
		newsConfig.setText(content);
	}
		
	////////////////////////////////////////////////////////////////////////////
	// 处理图片上传
	////////////////////////////////////////////////////////////////////////////

	private List uploadPictures(News news, Map extend, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		if (news == null || news.getId() <= 0 || news.getSubtype() != News.SUBTYPE_ZUTU)
			return null;
		
		// 图片数量
		int imageCount = 0;
		String imageCountParam = request.getParameter("imagecount");
		try {
			imageCount = Integer.parseInt(imageCountParam);
		} catch (Exception e) {}
		
		// 实体信息
		TreeMap sortedMap = new TreeMap();
		for (int i = 0; i < imageCount; i++) {
			String head = request.getParameter("subhead" + i);
			String text = request.getParameter("subtext" + i);
			String imageDesc = request.getParameter("subimagedesc" + i);
			String imagePath = request.getParameter("subimagepath" + i);
			if (imagePath == null || imagePath.indexOf(".") == -1)
				continue;
			String imageExt = imagePath.substring(imagePath.lastIndexOf(".") + 1);
			
			int index = -1;
			String indexParam = request.getParameter("subsort" + i);
			try {
				index = Integer.parseInt(indexParam);
			} catch (Exception e) {
				index = i;
			}
			
			UploadEntity entity = new UploadEntity();
			entity.setDesc(head);
			entity.setText(text);
			if(StringUtils.isNotEmpty(head))
				entity.setImageAlt(head+"_"+news.getDesc());
			else{
				if(StringUtils.isNotEmpty(imageDesc))
					entity.setImageAlt(Util.RemoveHTML(text +"_"+ news.getDesc()).replaceAll("\"|“|”|'", ""));
				else
					entity.setImageAlt(head);
			}
			entity.setImageDesc(imageDesc);
			entity.setFilePath(imagePath);
			entity.setFileExt(imageExt);
			
			sortedMap.put(new Integer(index), entity);
 		}
		
		// 排序
		List errorList = new ArrayList();
		Iterator iter = sortedMap.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			UploadEntity entity = (UploadEntity)sortedMap.get(key);
			
			// 图片内容
			byte[] content = null; // 图片内容
			File file = new File(entity.getFilePath());
			if (!file.canRead() || !file.isFile()) {
				errorList.add(key);
				continue;
			}
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int code = -1;
				while ((code = fis.read()) != -1) {
					baos.write(code);
				}
				content = baos.toByteArray();
				baos.close();
			} catch (Exception e) {
				errorList.add(key);
				continue;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						errorList.add(key);
						continue;
					}
				}
			}
			
			// 图片对象
			Picture pictureConfig = (Picture) ItemInfo.getItemByType(ItemInfo.PICTURE_TYPE);
			pictureConfig.setPid(news.getId());
			pictureConfig.setExt(entity.getFileExt());
			pictureConfig.setComment(entity.getImageDesc());
			pictureConfig.setDesc(entity.getImageDesc());

			Map extend4Picture = new HashMap();

			if (extend != null
					&& extend.containsKey(PictureManager.PROPERTY_NAME_AUTH)) {
				extend4Picture.put(PictureManager.PROPERTY_NAME_AUTH,
						extend.get(PictureManager.PROPERTY_NAME_AUTH));
			}
			extend4Picture.put(PictureManager.PROPERTY_NAME_CONTENT, content);

			Picture picture = null;
			try {
				picture = ManagerFacade.getPictureManager().addPicture(pictureConfig, extend4Picture);
			} catch (Exception e) {
				errorList.add(key);
				continue;
			}
			
			// 设置属性
			entity.setPicture(picture);			
		}
		
		// 排错
		for (int i = 0; i < errorList.size(); i++) {
			Object key = errorList.get(i);
			sortedMap.remove(key);
		}
		
		// 引用
		String pictures = news.getPictures();
		pictures = StringUtils.trimToEmpty(pictures);
		if (pictures.endsWith(Global.CMSSEP))
			pictures = pictures.substring(0, pictures.length() - Global.CMSSEP.length());
		
		List entityList = new ArrayList(sortedMap.values());
		
		for (int i = 0; entityList != null && i < entityList.size(); i++) {
			UploadEntity entity = (UploadEntity)entityList.get(i);
			Picture picture = entity.getPicture();
			String pictureId = picture.getId() + "";
			if (pictures.indexOf(pictureId) == -1) {
				if (pictures.trim().length() > 0)
					pictures += Global.CMSSEP;
				pictures += pictureId;
			}
		}
		if (pictures.trim().length() > 0)
			news.setPictures(pictures);
				
		return entityList;
	}

	////////////////////////////////////////////////////////////////////////////
	// 处理图文混排
	////////////////////////////////////////////////////////////////////////////
	
	private boolean handleNewsContent(News news, Map extend, List entityList, 
			ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		if(news.getSubtype() != News.SUBTYPE_ZUTU)
			return false;
			
		int style = -1;
		String styleParam = request.getParameter("newsstyle");
		if (styleParam != null) {
			try {
				style = Integer.parseInt(styleParam);
			} catch (Exception e) {
				return false;
			}
		}
		if (style == -1)
			return false;
		
		boolean result = false;
		
		boolean addSelectBox = false;
		String addSelectBoxParam = request.getParameter("selectbox_multi");
		if (addSelectBoxParam != null && addSelectBoxParam.equalsIgnoreCase("on")) {
			addSelectBox = true;
		}

		//patch zutu by shijinkui 09.06.10
		BaseForm dForm = (BaseForm) form;
		String zutuorder = (String)dForm.get("zutuorder");
		if(StringUtils.isNotEmpty(zutuorder))
			extend.put(NewsManager.PROPERTY_NAME_ZUTUORDER, zutuorder);
	
		LOG.info("000000000000000000000000: " + style);
		if (style == ContentGenerator.NEWS_STYLE_MULTI_PAGE) {
			// 分页新闻
			if (entityList != null && entityList.size() > 0) {
				String content = ContentGenerator.genMultiPageNews(news, entityList, addSelectBox, extend);
				LOG.info("1111111111111111 0000000000");
				news.setText(content);
				result = true;
			}
		}
		else if (style == ContentGenerator.NEWS_STYLE_MULTI_PAGE_AND_INDEX) {
			// 小图点大图
			if (entityList != null && entityList.size() > 0) {
				String content = ContentGenerator.genMultiPageAndIndexNews(news, entityList, addSelectBox);
				news.setText(content);
				result = true;
			}
		}
		
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 处理视频上传
	////////////////////////////////////////////////////////////////////////////

	private List updateVideo(News news, Map extend, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		if (news == null || news.getId() <= 0)
			return null;

		List videoList = new ArrayList();

		try {
			//处理视频
			String video0 = request.getParameter("video0");
			String videolen0 = request.getParameter("videolen0");
			String video_bigpic = request.getParameter("video_bpic0");
			String video_smallpic = request.getParameter("video_spic0");
			String video_desc = request.getParameter("video_desc0");
			if (StringUtils.isNotEmpty(video0)
					&& StringUtils.isNotEmpty(videolen0)
					&& StringUtils.isNotEmpty(video_bigpic)
					&& StringUtils.isNotEmpty(video_smallpic)
					&& StringUtils.isNotEmpty(video_desc)) {
				int video_len = Integer.parseInt(videolen0);
				String oldVideo = news.getVideos();
				Video video = null;
				if (oldVideo == null || StringUtils.isEmpty(oldVideo)) {
					//新建视频,并设置通用的属性
					video = (Video) ItemInfo.getItemByType(ItemInfo.VIDEO_TYPE);
					video.setType(ItemInfo.VIDEO_TYPE);
					video.setPid(news.getId());
					video.setPriority(70);
					video.setStatus(EntityItem.ENABLE_STATUS);
					video.setChannel(news.getChannel());
					video.setExt("");
					video.setEncrypt(0);

				} else {
					String[] vid = oldVideo.split(";");
					if (vid.length >= 1 && StringUtils.isNotEmpty(vid[0])) {
						video = (Video) ItemManager.getInstance().get(
								new Integer(vid[0]), Video.class);
					}
				}
				if (video != null) {
					video.setDesc(video_desc.trim());
					video.setLength(video_len);
					video.setBigpic(video_bigpic);
					video.setSmallpic(video_smallpic);
					video.setUrl(video0);
					video.setTime(new java.sql.Timestamp(System
							.currentTimeMillis()));
					video.setEditor(news.getEditor());
					video = (Video) ItemManager.getInstance().update(video);
					if (video != null && video.getId() > 0) {
						String newsVideoId = String.valueOf(video.getId());
						news.setVideos(newsVideoId);
						videoList.add(video);
					}
				}

			}
		} catch (Throwable te) {
			if (LOG.isErrorEnabled()) {
				LOG.error("处理视频异常", te);
			}
		}
		return videoList;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 处理新闻推送
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 处理推送新闻
	 */
	private void handlePushNews(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, News news, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException {

		BaseForm dForm = (BaseForm) form;
		int newsId = ((Integer) dForm.get("id")).intValue();
		// 自动推送
		if(newsId == -1)
			AutoPush.getInstance().pushIt(news);
		
		// 手工推送
		String pushIdsParam = request.getParameter("pushids");
		pushIdsParam = StringUtils.trimToNull(pushIdsParam);
		if (pushIdsParam == null)
			return;
		String[] pushIds = pushIdsParam.split(Global.CMSSEP);
		
		for (int i = 0; i < pushIds.length; i++) {
			String index = pushIds[i];
			
			// 源参数
			String pushName = request.getParameter("pushpname" + index);
			if (pushName == null || pushName.trim().length() == 0)
				continue;
			
			String pushDesc = request.getParameter("pushdesc" + index);
			String pushPriority = request.getParameter("pushpriority" + index);
			String pushTime = request.getParameter("pushtime" + index);
			String pushMode = request.getParameter("pushmode" + index);
			
			// 参数处理
			if (StringUtils.isEmpty(pushDesc))
				pushDesc = news.getDesc();

			int thepriority = 0;
			if (StringUtils.isEmpty(pushPriority)
					|| !StringUtils.isNumeric(pushPriority))
				thepriority = news.getPriority();
			else
				thepriority = (new Integer(pushPriority)).intValue();

			int thetime = NewsManager.PROPERTY_NAME_PUSH_TIME_NOW;
			if (pushTime != null && pushTime.equals("1")) {
				thetime = NewsManager.PROPERTY_NAME_PUSH_TIME_OLD;
			}

			int themode = NewsManager.PROPERTY_NAME_PUSH_MODE_LINK;
			if ("true".equalsIgnoreCase(pushMode)) {
				themode = NewsManager.PROPERTY_NAME_PUSH_MODE_COPY;
			}
			
			extend.put(NewsManager.PROPERTY_NAME_PUSH_DESC, pushDesc);
			extend.put(NewsManager.PROPERTY_NAME_PUSH_PRIORITY,	new Integer(thepriority));
			extend.put(NewsManager.PROPERTY_NAME_PUSH_TIME,	new Integer(thetime));
			extend.put(NewsManager.PROPERTY_NAME_PUSH_MODE,	new Integer(themode));

			// 推送新闻
			ManagerFacade.getNewsManager().pushNews(news, pushName, extend);
		}
	}
}
