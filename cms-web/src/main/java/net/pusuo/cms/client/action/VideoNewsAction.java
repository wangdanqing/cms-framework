package net.pusuo.cms.client.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Collections;
import java.sql.Timestamp;
import java.util.regex.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import com.hexun.cms.Configuration;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.image.ClientImage;
import com.hexun.cms.client.util.ParamUtils;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;
import com.hexun.cms.core.Video;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.image.TextWatermarkInfo;

import com.hexun.cms.util.Util;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoNewsAction extends EntityAction {
	/**
	 * ͼƬ�?ǰ׺
	 */
	public static final String prefix_picture = "picfile";
	/**
	 * ��Ƶ�?ǰ׺
	 */
	public static final String prefix_video = "video";

	private static final Log log = LogFactory.getLog(VideoNewsAction.class);

	private static final Pattern p = Pattern.compile("(jpg|gif|jpeg|png|bmp)",
			Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	public static Pattern video_p = null;
	
	protected static String VIDEO_ROOT = null;
	protected static String VIDEO_SYNC= null;
	protected static String VIDEO_CSYNC = null;
	protected static String MEDIA_PATTERN=null;
	private static final Object sync_lock = new Object();

	static {
		VIDEO_ROOT = Configuration.getInstance().get("cms4.video.root");
		VIDEO_SYNC = Configuration.getInstance().get("cms4.video.sync");
		VIDEO_CSYNC = Configuration.getInstance().get("cms4.video.csync");
		MEDIA_PATTERN = Configuration.getInstance().get("cms4.video.pattern");
		video_p = Pattern.compile(MEDIA_PATTERN, Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
		if(log.isInfoEnabled()){
			log.info("cms4.video.root:"+VIDEO_ROOT);
			log.info("cms4.video.sync:"+VIDEO_SYNC);
			log.info("cms4.video.csync:"+VIDEO_CSYNC);
			log.info("cms4.video.pattern:"+MEDIA_PATTERN);
		}
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward ret = null;
		ActionErrors errors = new ActionErrors();
		/*
		 * ������26���ϴ���Ƶ
		 */
		if (VIDEO_ROOT == null || VIDEO_SYNC == null) {
			errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("errors.video.here", "Oppos,is video here?"));
			saveErrors(request, errors);	
			return mapping.findForward("failure");
		}
		Authentication auth = null;
		try {
			auth = AuthenticationFactory.getAuthentication(request, response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			return mapping.findForward("failure");
		}

		try {
			Item item = null;
			//������JSPҳ���еò��������
			BaseForm dForm = (BaseForm) form;
			dForm.set("type", new Integer(ItemInfo.NEWS_TYPE));

			Integer _id = (Integer) dForm.get("id");
			int id = _id.intValue();
			Integer _pid = (Integer) dForm.get("pid");
			int pid = _pid.intValue();

			//������޸��������Ȱ�����ID��������
			News oItem = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
			boolean isNewNews = false;
			if (id > 0) {//�޸�
				News oldItem = (News) ItemManager.getInstance().get(
						new Integer(id), News.class);
				org.apache.commons.beanutils.PropertyUtils.copyProperties(
						oItem, oldItem);
				dForm.set("editor", new Integer(oldItem.getEditor()));
			} else {
				dForm.set("editor", new Integer(auth.getUserID()));
				isNewNews = true;
			}

			Object[] obj = saveProcesser(dForm, "html");			
			item = (Item) obj[0];
			errors = (ActionErrors) obj[1];
			if (!errors.isEmpty()) {
				if (item != null) {
					org.apache.commons.beanutils.PropertyUtils.copyProperties(
							item, oItem);
				}
				saveErrors(request, errors);
				return mapping.findForward("failure");
			}
			
			if (item != null && item.getId() > 0) {//���³ɹ�
				boolean needUpdate = false;
				if(log.isInfoEnabled()){
					log.info("upload item ,id:"+item.getId());
				}
				String vp[] = upload(item, form, request, response);
				if (vp != null && vp.length == 2) {
					News nItem = (News)item;
					if(vp[0].trim().length()>0){
						String oldPics = nItem.getPictures();
						if(oldPics == null){
							oldPics = "";
						}
						nItem.setPictures(oldPics+vp[0]);
						needUpdate = true;
					}
					if(vp[1].trim().length()>0){
						String oldVids = nItem.getVideos();
						if(oldVids == null){
							oldVids = "";
						}
						nItem.setVideos(vp[1]);
						needUpdate = true;
					}	
				}
				
				//��������
				boolean newspush = ("auto".equals(request
						.getParameter("newspush")));
				if (newspush) {
					Object[] obj1 = newsPush((News) item, auth.getUserID(),
							request);
					errors = null;
					if (obj1[0] == null) {
						errors = null;
					} else {
						errors = (ActionErrors) obj1[0];
					}
					String history = (String) obj1[1];
					if (errors != null && !errors.isEmpty()) {
						saveErrors(request, errors);
						return mapping.findForward("failure");
					} else {
						if (!history.equals("")) {
							((News) item).setPushrecord((String) obj1[1]);
							needUpdate = true;
						}
					}
				}

				if (needUpdate) {
					item = ItemManager.getInstance().update(item);
				}

				//�����Ͳ�������ŵ�״̬Ҳ����Ϊ�������ŵ�һ��
				String pushRecord = ((News) item).getPushrecord();
				if (pushRecord != null && !pushRecord.equals("")) {//�����ͼ�¼
					Integer status = (Integer) dForm.get("status");
					String[] pra = pushRecord.split(Global.CMSSEP);
					News pnItem = null;
					for (int i = 0; i < pra.length; i++) {
						pnItem = (News) ItemManager.getInstance().get(
								new Integer(pra[i]), News.class);
						if (pnItem != null) {
							pnItem.setStatus(status.intValue());
							ItemManager.getInstance().update(pnItem);
						}
					}
				}
			} else {//item.getId()<0
				errors.add("errors.item.save", new ActionError(
						"errors.item.save"));
			}

			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			} else {				
				//response.sendRedirect("http://cms.hexun.com/videonews.do?method=view&id=" + item.getId());
				response.sendRedirect("http://cms.hexun.com/news.do?refresh=yes&method=view&id=" + item.getId());
			}

		} catch (Exception e) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
			log.error("NewsAction save error . ", e);
			ret = mapping.findForward("failure");
		}
		return ret;
	}

	/**
	 * ����ͼƬ����Ƶ��Ϣ
	 */
	private String[] upload(Item item, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String newsPictures = "";
		String videos = "";

		try {
			if (item == null || item.getId() <= 0) {
				return null;
			}
			String contentType = request.getContentType();
			log.warn("video upload contentType:"+contentType);
			if (contentType == null
					|| contentType.indexOf("multipart/form-data") == -1) {
				return null;
			}

			DynaActionForm fm = (DynaActionForm) form;
			MultipartRequestHandler mrh = (MultipartRequestHandler) fm
					.getMultipartRequestHandler();
			Hashtable ht = (Hashtable) mrh.getFileElements();
			Enumeration keys = null;
			if (ht == null || ht.size() <= 0) {
				return null;
			}

			List photo = new ArrayList();
			List video = new ArrayList();

			//����ֶε�ǰ׺�ּ��ͼƬ����Ƶ
			keys = ht.keys();
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				if (name.startsWith(prefix_picture)) {
					photo.add(name);
				} else if (name.startsWith(prefix_video)) {
					video.add(name);
				}
			}

			//�����Ŷ�ͼƬ��������
			Collections.sort(photo, new Comparator() {
				public int compare(Object picture1, Object picture2) {
					String pic1 = (String) picture1;
					int num1 = new Integer(pic1.substring(prefix_picture
							.length(), pic1.length())).intValue();

					String pic2 = (String) picture2;
					int num2 = new Integer(pic2.substring(prefix_picture
							.length(), pic2.length())).intValue();

					return num1 - num2;
				}
			});

			//�����Ŷ���Ƶ��������
			Collections.sort(video, new Comparator() {
				public int compare(Object video1, Object video2) {
					String v1 = (String) video1;
					int num1 = new Integer(v1.substring(prefix_video.length(),
							v1.length())).intValue();

					String v2 = (String) video2;
					int num2 = new Integer(v2.substring(prefix_video.length(),
							v2.length())).intValue();

					return num1 - num2;
				}
			});
			newsPictures = processPic(item, request, ht, photo);
			videos = processVideo(item, request, ht, video);

		} catch (Exception e) {
			log.error("NewsAction.upload error. " , e);
		}

		return new String[] { newsPictures, videos };
	}

	/**
	 * �ϴ�������ͼƬ��Ϣ
	 * 
	 * @param item
	 * @param request
	 * @param ht
	 * @param photo
	 * @return
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 * @throws Exception
	 */
	private String processPic(Item item, HttpServletRequest request,
			Hashtable ht, List photo) throws FileNotFoundException,
			IOException, Exception {
		String newsPictures = "";
		boolean genThumbnail = ("auto"
				.equals(request.getParameter("thumbnail")));
		boolean genWatermark = ("auto"
				.equals(request.getParameter("watermark")));

		for (int i = 0; i < photo.size(); i++) {
			String keyStr = (String) photo.get(i);
			FormFile temp = (FormFile) ht.get(keyStr);
			String fileName = temp.getFileName();
			if (fileName == null || fileName.equals("")) {
				continue;
			}

			//�ж��ļ���׺���Ƿ���Ҫ��
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!p.matcher(ext).matches()) {
				if (log.isWarnEnabled()) {
					log.warn(fileName + " does not with jpg,gif,bmp,png");
				}
				continue;
			}
			if (temp.getFileSize() <= 0) {
				continue;
			}

			//����pictureʵ���д�ļ�
			byte[] b_content = temp.getFileData();

			String picIdx = keyStr.substring(prefix_picture.length());
			String comment = ((String) request.getParameter("piccomment"
					+ picIdx)).trim();
			int width = ParamUtils.getIntParameter(request,
					"picwidth" + picIdx, 0);
			int height = ParamUtils.getIntParameter(request, "picheight"
					+ picIdx, 0);

			//����pictureʵ��
			Picture pictureItem = (Picture) ItemInfo
					.getItemByType(ItemInfo.PICTURE_TYPE);
			pictureItem.setType(ItemInfo.PICTURE_TYPE);
			pictureItem.setPid(item.getId());
			pictureItem.setPriority(70);
			pictureItem.setStatus(EntityItem.ENABLE_STATUS);
			pictureItem.setChannel(((EntityItem) item).getChannel());
			pictureItem.setEditor(((EntityItem) item).getEditor());
			pictureItem.setExt(ext);
			pictureItem.setComment(comment);
			pictureItem.setDesc(comment);
			pictureItem.setTime(new Timestamp(System
					.currentTimeMillis()));
			pictureItem.setWidth(width);
			pictureItem.setHeight(height);

			pictureItem = (Picture) ItemManager.getInstance().update(
					pictureItem);

			if (pictureItem != null && pictureItem.getId() > 0) {
				//�洢���
				String storeFileName = PageManager
						.getTStorePath((EntityItem) pictureItem);
				ClientFile.getInstance().write(b_content, storeFileName, true);
				newsPictures += pictureItem.getId() + Global.CMSSEP;

				boolean thumbResult = false;
				boolean markResult = false;
				if (genThumbnail) {
					thumbResult = ClientImage.getInstance().genThumbnail(
							storeFileName);
					if (!thumbResult) {
						log.error("Generate Thumbnail Failed! srcImage="
								+ storeFileName);
					} else {
						pictureItem.addExflag(Picture.THUMB_FLAG);
					}
				}

				if (genWatermark) {
					String markText = "CMS.HEXUN.COM";
					TextWatermarkInfo info = new TextWatermarkInfo(
							storeFileName, markText);
					markResult = ClientImage.getInstance().genWatermarkImageT(
							info);
					if (!markResult) {
						log.error("Generate Wartermark Failed! srcImage="
								+ storeFileName);
					} else {
						pictureItem.addExflag(Picture.MARK_FLAG);
					}
				}

				Dimension dimension = ClientImage.getInstance().getDimension(
						"/opt" + storeFileName);
				if (pictureItem.getWidth() <= 0) {
					pictureItem.setWidth((int) dimension.getWidth());
				}
				if (pictureItem.getHeight() <= 0) {
					pictureItem.setHeight((int) dimension.getHeight());
				}

				pictureItem = (Picture) ItemManager.getInstance().update(
						pictureItem);
			}
		}
		return newsPictures;
	}

	private String processVideo(Item item, HttpServletRequest request,
			Hashtable ht, List videos) throws FileNotFoundException,
			IOException, Exception {
		String newsVideos = "";
		if(log.isInfoEnabled()){
			log.warn("process video:"+videos);
		}	
		for (int i = 0; i < videos.size(); i++) {
			String keyStr = (String) videos.get(i);
			FormFile temp = (FormFile) ht.get(keyStr);
			String fileName = temp.getFileName();
			if (fileName == null || fileName.equals("")) {
				continue;
			}

			//�ж��ļ���׺���Ƿ���Ҫ��
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!video_p.matcher(ext).matches()) {
				if (log.isWarnEnabled()) {
					log.warn(fileName + " does not with wmv,avi");
				}
				continue;
			}

			if (temp.getFileSize() <= 0) {
				continue;
			}

			String picIdx = keyStr.substring(prefix_video.length());
			int length = ParamUtils.getIntParameter(request, "videolen"
					+ picIdx, 0);
			int encrpt = ParamUtils.getIntParameter(request, "videoencrypt"
					+ picIdx, 0);

			//����videoʵ��
			Video video = (Video) ItemInfo.getItemByType(ItemInfo.VIDEO_TYPE);
			video.setType(ItemInfo.VIDEO_TYPE);
			video.setPid(item.getId());
			video.setPriority(70);
			video.setStatus(EntityItem.ENABLE_STATUS);
			video.setChannel(((EntityItem) item).getChannel());
			video.setEditor(((EntityItem) item).getEditor());
			video.setExt(ext);
			video.setTime(new Timestamp(System.currentTimeMillis()));
			video.setEncrypt(encrpt);
			video.setLength(length);
			

			video = (Video) ItemManager.getInstance().update(video);

			newsVideos = writeVideos(newsVideos, temp, video);
		}
		return newsVideos;
	}

	/**
	 * @param newsVideos
	 * @param temp
	 * @param video
	 * @return
	 * @throws Exception
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	protected String writeVideos(String newsVideos, FormFile temp, Video video) throws Exception, FileNotFoundException, IOException {
		if (video != null && video.getId() > 0) {
			//������Ƶ����:����ͼƬ����,��Ƶ�Ǳ����ڱ��ص�Ӳ���ϵ�,��MUME_ROOTΪ��
			String storeFileName = VIDEO_ROOT+ PageManager.getTStorePath((EntityItem) video);
			String ppDir = LocalFile.getPath(storeFileName);
			File ppDirF = new File(ppDir);
			if (!ppDirF.exists()) {
				//�ļ����ڵĸ�Ŀ¼������ʱ,��Ҫ�����ļ��ĸ�Ŀ¼
				ppDirF.mkdirs();
			}
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			int count = 0;
			try {
				in = new BufferedInputStream(temp.getInputStream());
				out = new BufferedOutputStream(new FileOutputStream(
						storeFileName));
				count = ClientFile.copy(in, out);
			} finally {
				ClientFile.closeStream(in);
				ClientFile.closeStream(out);
			}
			
			if(count == temp.getFileSize() ){
				//if only count > 0
				syncAdd(storeFileName,count);
				newsVideos += video.getId() + Global.CMSSEP;
			}
		}
		return newsVideos;
	}

	protected void syncAdd(String storeFileName,int count) throws Exception{
		String syncData = storeFileName+"#"+count+"\r\n";
		StringBuffer syncCData= new StringBuffer();
		syncCData.append((long)(System.currentTimeMillis()/1000) );
		syncCData.append("\t");
		syncCData.append("+");
		syncCData.append("\t");
		syncCData.append(storeFileName);
		syncCData.append("\r\n");
		synchronized(sync_lock){
			LocalFile.write(syncData,VIDEO_SYNC,true);
			LocalFile.write(syncCData.toString(),VIDEO_CSYNC,true);
		}	
	}
	
	protected void syncDel(Video video) throws Exception{
		if(video == null){
			return;
		}
		String storeFileName = VIDEO_ROOT+ PageManager.getTStorePath((EntityItem) video);
		StringBuffer syncCData= new StringBuffer();
		syncCData.append((long)(System.currentTimeMillis()/1000) );
		syncCData.append("\t");
		syncCData.append("-");
		syncCData.append("\t");
		syncCData.append(storeFileName);
		syncCData.append("\r\n");
		synchronized(sync_lock){
			LocalFile.write(syncCData.toString(),VIDEO_CSYNC,true);
		}	
	}

	private Object[] newsPush(News news, int user, HttpServletRequest request) {

		Object[] obj = new Object[2];
		ActionErrors errors = new ActionErrors();
		String history = "";

		String pushNames = request.getParameter("pushnames");
		String pushDesc = request.getParameter("pushdesc");
		String pushPriority = request.getParameter("pushpriority");
		String pushTime = request.getParameter("pushtime");

		//EntityItem item = null;

		if (pushNames == null || pushNames.equals("")) {
			obj[0] = errors;
			obj[1] = history;
			return obj;
		}

		//���pushRecord�ҳ����͵�names
		String pushRecord = news.getPushrecord();
		pushRecord = (pushRecord == null ? "" : pushRecord);
		StringBuffer pushedNames = new StringBuffer();
		int count = 0;
		if (!pushRecord.equals("")) {
			String[] pushRecordArray = pushRecord.split(Global.CMSSEP);
			for (int i = 0; i < pushRecordArray.length; i++) {
				EntityItem item = (EntityItem) ItemManager.getInstance().get(
						new Integer(pushRecordArray[i]), EntityItem.class);
				if (item != null && item.getId() > 0) {
					EntityItem pitem = (EntityItem) ItemManager.getInstance()
							.get(new Integer(item.getPid()), EntityItem.class);
					if (pitem != null && pitem.getId() > 0) {
						if (count == 0) {
							pushedNames.append(pitem.getName());
						} else {
							pushedNames.append(";" + pitem.getName());
						}
						++count;
					}
				}
			}
		}
		String[] pushedNamesArray = pushedNames.toString().split(Global.CMSSEP);

		//����������ظ���ר������
		String[] filterPush = exclude(pushNames.split(Global.CMSSEP));
		for (int i = 0; i < filterPush.length; i++) {
			boolean repeat = false;
			for (int j = 0; j < pushedNamesArray.length; j++) {
				if (pushedNamesArray[j].equals(filterPush[i])) {
					repeat = true;
					break;
				}
			}
			if (repeat) {//�Ѿ����͹�
				errors.add("errors.push.pushed", new ActionError(
						"errors.detail", filterPush[i]
								+ Util.unicodeToGBK(" �Ѿ����͹�")));
				obj[0] = errors;
				obj[1] = history;
				return obj;
			} else {//�жϽ�Ҫ���͵�Ŀ���Ƿ���Ҫ��
				EntityItem item = (EntityItem) ItemManager.getInstance()
						.getItemByName(filterPush[i], EntityItem.class);
				if (item != null && item.getId() > 0) {
					if (item.getType() != ItemInfo.SUBJECT_TYPE) {
						errors
								.add(
										"errors.push.pid.notsubject",
										new ActionError(
												"errors.detail",
												filterPush[i]
														+ Util
																.unicodeToGBK(" ����ר�⡢��ר�⡢��Ŀ������Ŀ�����ܽ������͡�")));
						obj[0] = errors;
						obj[1] = history;
						return obj;
					}
					if (item.getId() == news.getPid()) {
						errors.add("errors.push.pid.self", new ActionError(
								"errors.push.pid.self"));
						obj[0] = errors;
						obj[1] = history;
						return obj;
					}
				} else {
					errors.add("errors.push.pid.notfound", new ActionError(
							"errors.detail", filterPush[i]
									+ Util.unicodeToGBK(" �����ڡ�")));
					obj[0] = errors;
					obj[1] = history;
					return obj;
				}
			}
		}

		//�������
		for (int i = 0; i < filterPush.length; i++) {
			int pid = -1;
			EntityItem item = (EntityItem) ItemManager.getInstance()
					.getItemByName(filterPush[i], EntityItem.class);
			if (null != item && item.getId() > 0) {
				pid = item.getId();
			} else {
				continue;
			}

			int channel = item.getChannel();
			String template = news.getTemplate();
			template = template.substring(0, template.indexOf(Global.CMSCOMMA));

			News pushNews = (News) ItemInfo.getItemByType(ItemInfo.NEWS_TYPE);
			pushNews.setType(ItemInfo.NEWS_TYPE);
			pushNews.setChannel(channel);
			pushNews.setPid(pid);
			pushNews.setDesc(pushDesc);
			pushNews.setPriority((new Integer(pushPriority)).intValue());
			//pushNews.setReferid( news.getId() );
			pushNews.setReurl(news.getUrl());
			pushNews.setEditor(user);
			pushNews.setTemplate(template);
			pushNews.setAuthor(news.getAuthor());

			if (pushTime != null && pushTime.equals("1")) {
				pushNews.setTime(news.getTime());
			} else {
				pushNews.setTime(new Timestamp(System.currentTimeMillis()));
			}
			pushNews = (News) ItemManager.getInstance().update(pushNews);

			history += ";" + pushNews.getId();
		}

		//��¼����
		if (pushRecord.equals("")) {
			history = history.substring(1, history.length());
		} else {
			history = pushRecord + history;
		}

		obj[0] = errors;
		obj[1] = history;
		return obj;
	}

	String[] exclude(String[] valArray) {

		for (int i = 0; i < valArray.length; i++) {
			String iv = valArray[i];
			for (int j = i + 1; j < valArray.length; j++) {
				String jv = valArray[j];
				if (iv.equals(jv)) {
					valArray[j] = "-1";
				}
			}
		}

		int count = 0;
		for (int i = 0; i < valArray.length; i++) {
			if (!valArray[i].equals("-1")) {
				count++;
			}
		}

		String[] ret = new String[count];
		for (int i = valArray.length - 1; i >= 0; i--) {
			if (!valArray[i].equals("-1")) {
				ret[--count] = valArray[i];
			}
		}

		return ret;
	}
}
