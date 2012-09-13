package net.pusuo.cms.client.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import com.hexun.cms.Global;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.util.*;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.util.BatchNews;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.client.util.UploadEntity;
import com.hexun.cms.client.util.UploadUtil;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;

/**
 * ��ͼ��action
 * 
 * @author denghua
 * 
 */
public class PicBatchAction extends ItemAction {
	/**
	 * Logger for this class
	 */
	private static final Logger log = Logger.getLogger(PicBatchAction.class);

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = null;
		ActionErrors errors = new ActionErrors();
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		BaseForm dForm = (BaseForm) form;

		Map mediaMap = ItemUtil.getMediaMap(auth);
		List mediaList = Collections.list(Collections.enumeration(mediaMap
				.values()));
		
		return mapping.findForward("init");
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
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
		
		List entityList = null;
		
		String uploadModeParam = request.getParameter("uploadmode");
		int uploadMode = -1;
		try {
			uploadMode = Integer.parseInt(uploadModeParam);
		} catch (Exception e) {
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
		
		List result = new ArrayList();

		if (entityList == null) {
			log.warn("nothing to upload.");
			return mapping.findForward("failure");
		}
		
		DynaActionForm fm = (DynaActionForm) form;
		String desc = (String)fm.get("desc");
		String text = (String)fm.get("text");
		String pname = (String)fm.get("pname");
		
		Integer media = (Integer)fm.get("media");
		String mediaName = request.getParameter("medianame");
		if (mediaName == null)
			mediaName = "";
		
		String author = (String)fm.get("author");
		String keyword = (String)fm.get("keyword");
		Integer status = (Integer)fm.get("status");
		
		Integer priority = (Integer)fm.get("priority");
		String addprio = (String)fm.get("addprio");	
		if (addprio == null || !addprio.equalsIgnoreCase("on")) {
			addprio = "no";
		}
		
		for (int i = 0; i < entityList.size(); i++) {
			UploadEntity entity = (UploadEntity)entityList.get(i);
			
			BatchNews news = new BatchNews();
			news.setDesc(desc + " " + entity.getFileName());
			news.setText(text);
			news.setPname(pname);
			news.setMedia(media);
			news.setMediaName(mediaName);
			news.setPriority(priority);
			news.setAddprio(addprio);
			news.setKeyword(keyword);
			news.setAuthor(author);
			news.setStatus(status);
			
			news.setFileAbsolutePath(entity.getFilePath());
			news.setImageShowPath(entity.getUrl());
			news.setThumb(entity.getThumb());
			
			result.add(news);
		}
				
		request.setAttribute("newsListSize", "" + result.size());
		request.setAttribute("newsList", result);
		
		return mapping.findForward("edit");
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String _batchSize = request.getParameter("batchSize");
		ActionErrors errors = new ActionErrors();

		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}

		if (StringUtils.isEmpty(_batchSize)
				|| !StringUtils.isNumeric(_batchSize)) {
			errors.add("errors.parameter", new ActionError("errors.parameter"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		int batchSize = Integer.parseInt(_batchSize); // �õ�Ҫ�������������
		List result = new ArrayList();
		for (int i = 0; i < batchSize; i++) {
			String desc = request.getParameter("desc" + i);
			String pname = request.getParameter("pname" + i);
			
			String mediaName = request.getParameter("mediaName" + i);
			Media media=(Media) ItemManager.getInstance().getItemByName(mediaName, Media.class);
			int mediaId=media==null?-1:media.getId();
			
			int priority = Integer.parseInt(request.getParameter("priority" + i));
			String addprio = request.getParameter("addprio" + i);
			if (addprio != null && addprio.equalsIgnoreCase("on")) {
				priority += 2;
			}
			
			String keyword = request.getParameter("keyword" + i);
			String author = request.getParameter("author" + i);
			String status = request.getParameter("status" + i);
			
			String text = request.getParameter("text" + i);
			
			String imagePath = request.getParameter("imagePath" + i);
			String imageDesc = request.getParameter("imageDesc" + i);
			if (imagePath == null || imagePath.indexOf(".") == -1)
				continue;
			String imageExt = imagePath.substring(imagePath.lastIndexOf(".") + 1);
			
			News news = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
			news.setDesc(desc);
			news.setMedia(mediaId);
			news.setPriority(priority);
			news.setKeyword(keyword);
			news.setStatus(Integer.parseInt(status));
			news.setAuthor(author);
			news.setText(text);
			news.setSubtype(News.SUBTYPE_PICTURE);
			
			Map extend = new HashMap();
			// ������֤��Ϣ
			extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);

			// ���ݸ��������
			extend.put(NewsManager.PROPERTY_NAME_PNAME, pname);

			try {
				news = ManagerFacade.getNewsManager().addNews(news, extend);
				result.add(news);
			} catch (ParentNameException pne) {
				errors.add("errors.item.pnamenotexist" + i,
						new ActionError("errors.item.pnamenotexist", pname));
			} catch (PropertyException pe) {
				errors.add("errors.parameter" + i, new ActionError("errors.parameter", i + ""));
			} catch (UnauthenticatedException ue) {
				errors.add("auth.failure" + i, new ActionError("auth.failure", "" + auth.getUserID()));
			} catch (DaoException de) {
				errors.add("errors.item.save" + i, new ActionError("errors.item.save", desc));
			} catch (Exception e) {
				errors.add("errors.item.save" + i, new ActionError("errors.item.save", desc));
			}
			
			// �ϴ�ͼƬ
			byte[] content = null;
			File file = new File(imagePath);
			if (!file.canRead() || !file.isFile()) {
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
				continue;
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						continue;
					}
				}
			}
			
			// ͼƬ����
			Picture pictureConfig = (Picture) ItemInfo.getItemByType(ItemInfo.PICTURE_TYPE);
			pictureConfig.setPid(news.getId());
			pictureConfig.setExt(imageExt);
			pictureConfig.setComment(imageDesc);
			pictureConfig.setDesc(imageDesc);

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
				continue;
			}
			
			// �������
			String contentPic = ContentGenerator.addTableAroundPicture(picture.getUrl(), news.getDesc(), imageDesc);
			text = contentPic + text;
			news.setText(text);
			news.setPictures("" + picture.getId());
			ItemManager.getInstance().update(news);
		}
		saveErrors(request, errors);

		request.setAttribute("resultList", result);
		return mapping.findForward("succ");
	}
}
