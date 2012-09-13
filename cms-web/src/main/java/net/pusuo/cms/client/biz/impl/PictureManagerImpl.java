/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.event.impl.DefaultPictureEventListener;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.FileException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PictureFormatException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.util.PictureUtil;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.file.PageManager;
import com.hexun.cms.client.image.ClientImage;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Picture;
import com.hexun.cms.image.Placement;
import com.hexun.cms.image.TextWatermarkInfo;
import com.hexun.cms.util.Util;

/**
 * @author Alfred.Yuan
 *
 */
public class PictureManagerImpl extends CmsManagerImpl implements PictureManager {

	private static final Log log = LogFactory.getLog(PictureManagerImpl.class);
	
	public static final Pattern patternUrl = Pattern.compile(HEXUN_PICTURE_REGEX_URL,	
			Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	public PictureManagerImpl() {
		
		this.addListener(new DefaultPictureEventListener());
	}
	
	public Picture getPicture(int pictureId) throws DaoException {
		
		if (pictureId < 0)
			return null;
		
		Picture result = null;
		try {
			Item item = ItemManager.getInstance().get(new Integer(pictureId), Picture.class);
			if (item != null)
				result = (Picture)item;
		}
		catch (Throwable t) {
			log.error("getPicture: get picture from server.");
			throw new DaoException();
		}
		
		return result;
	}
	
	public Picture addPicture(Picture pictureConfig, Map extend)
			throws PropertyException, ParentNameException, UnauthenticatedException, 
			DaoException, PictureFormatException, FileException {
		
		if (pictureConfig == null)
			return null;
		
		// ����id
		pictureConfig.setId(-1);
		
		// ���ñ༭��Ϣ
		if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
			Authentication auth = (Authentication)extend.get(PROPERTY_NAME_AUTH);
			pictureConfig.setEditor(auth.getUserID());
		}
		
		// ����ʱ��
		if (pictureConfig.getTime() == null) {
			pictureConfig.setTime(new Timestamp(System.currentTimeMillis()));
		}
		
		return saveOrUpdatePicture(pictureConfig, extend);
	}

	public Picture updatePicture(Picture pictureConfig, Map extend)
			throws PropertyException, ParentNameException, PictureFormatException,
			UnauthenticatedException, DaoException, FileException {
		
		if (pictureConfig == null)
			return null;
		
		int pictureId = pictureConfig.getId();
		if (pictureId < 0) 
			throw new PropertyException();
		
		Picture pictureOld = getPicture(pictureId);
		if (pictureOld == null)
			throw new PropertyException();

		// ����ԭ�ȵı༭��Ϣ
		pictureConfig.setEditor(pictureOld.getEditor());
		
		return saveOrUpdatePicture(pictureConfig, extend);
	}

	public boolean deletePicture(Picture picture) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {
		
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private Picture saveOrUpdatePicture(Picture pictureConfig, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, 
		DaoException, FileException, PictureFormatException {
		
		boolean isSaveAction = pictureConfig.getId() < 0 ? true : false;
		
		// ����ͼƬ�ĸ�����:name����,id��֮
		Item parentItem = null;
		// �û�ѡ��ĸ�����
		if (extend != null && extend.containsKey(PROPERTY_NAME_PNAME)) {
			String parentName = (String)extend.get(PROPERTY_NAME_PNAME);
			if (parentName != null && parentName.trim().length() > 0) {
				parentItem = ItemManager.getInstance().getItemByName(parentName, EntityItem.class);
				if (parentItem != null && parentItem.getId() > 0) {
					pictureConfig.setPid(parentItem.getId());
				}
			}
		}
		// ϵͳĬ�ϵĸ�����
		if (pictureConfig.getPid() < 0) {
			if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
				Authentication auth = (Authentication)extend.get(PROPERTY_NAME_AUTH);
				List channels = auth.getChannelList();
				if (channels != null && channels.size() > 0) {
					String defaultParentName = ((Channel)channels.get(0)).getDir();
					defaultParentName += "picture";
					if (defaultParentName != null && defaultParentName.trim().length() > 0) {
						parentItem = ItemManager.getInstance().getItemByName(defaultParentName, EntityItem.class);
						if (parentItem != null && parentItem.getId() > 0) {
							pictureConfig.setPid(parentItem.getId());
						}
					}
				}
				else {
					throw new UnauthenticatedException();
				}
			}
		}
		if (pictureConfig.getPid() < 0) {
			throw new ParentNameException();
		}
		
		// ��֤���������Ч��
		if (parentItem == null)
			parentItem = ItemManager.getInstance().get(new Integer(pictureConfig.getPid()), EntityItem.class);
		if (parentItem == null)
			throw new PropertyException();
		EntityItem parent = (EntityItem)parentItem;
		
		// ��ݸ���������Ƶ��
		pictureConfig.setChannel(parent.getChannel());
		
		// ��ݸ���������Ȩ��
		if (pictureConfig.getPriority() < HEXUN_PICTURE_MINIMIZE_PRIORITY) {
			pictureConfig.setPriority(parent.getPriority());
			if (pictureConfig.getPriority() < HEXUN_PICTURE_MINIMIZE_PRIORITY) {
				pictureConfig.setPriority(HEXUN_PICTURE_DEFAULT_PRIORITY);
			}
		}
		
		// ����Ĭ�ϵ�����
		String desc = pictureConfig.getDesc();
		if (desc == null || desc.trim().length() == 0) {
			pictureConfig.setDesc("picture");
		}
		
		// �����ɵ�״ֵ̬:(ֻ��Ը���ͼƬ)
		//    ���EntityItem.setPid(...)�ȵ����,����ε���,�ɵ�״ֵ̬�ͻᱻ�ƻ�
		if (!isSaveAction) {
			Picture pictureOrigin = ManagerFacade.getPictureManager().getPicture(pictureConfig.getId());
			if (pictureOrigin == null)
				throw new DaoException();
			pictureConfig.setOldpid(pictureOrigin.getPid());
			pictureConfig.setOldpriority(pictureOrigin.getPriority());
			pictureConfig.setOldstatus(pictureOrigin.getStatus());
		}
		
		// ����DBServer
		Picture picture = null;
		try {
			// ����DBServer
			Item item = ItemManager.getInstance().update(pictureConfig);
			if (item != null)
				picture = (Picture)item;
			else 
				throw new DaoException();
		}
		catch (Throwable t) {
			log.error("saveOrUpdatePicture: update picture to server.");
			throw new DaoException();
		}
		
		// �û��Ƿ��ϴ����µ�ͼƬ
		boolean hasUploadNewPirctureFile = false;
		
		// д��FileServer
		String storeFileName = null;
		try {
			storeFileName = PageManager.getTStorePath(picture);
			if (extend != null && extend.containsKey(PROPERTY_NAME_CONTENT)) {
				byte[] contentPictrue = (byte[])extend.get(PROPERTY_NAME_CONTENT);
				boolean success = ClientFile.getInstance().write(contentPictrue, storeFileName,true);
				if (!success)
					throw new FileException();
				hasUploadNewPirctureFile = true;
			}
		}
		catch (Exception e) {
			throw new FileException();
		}
		
		// �Ƿ�:����ͼƬ,����û���ϴ���¼
		if (isSaveAction && !hasUploadNewPirctureFile) {
			throw new PropertyException();
		}
		
		boolean needUpdated = false;
		
		if (hasUploadNewPirctureFile) {
			// ��֤ͼƬ����չ��
			String pictureExt = pictureConfig.getExt();
			if (!PictureUtil.validateExt(pictureExt)) {
				throw new PictureFormatException();
			}
			
			// ��ȡͼƬ��ʵ�Ŀ�͸�
			Dimension dimension = ClientImage.getInstance().getDimension(FILE_SERVER_ROOT_PATH + storeFileName );
			if (picture.getWidth() <= 0) {
				picture.setWidth( (int)dimension.getWidth() );
				needUpdated = true;
			}
			if (picture.getHeight() <= 0) {
				picture.setHeight( (int)dimension.getHeight() );
				needUpdated = true;
			}
		}
		
		// ��������ͼ��ˮӡ
		if (handleThumbnailAndWatermark(picture, extend)) {
			needUpdated = true;
		}
					
		// ����ͼƬ
		if (needUpdated) {
			picture = (Picture)ItemManager.getInstance().update(picture);
		}
		
		// ���������ͼƬ,���Ҹ�����������,��Ҫ���¸����ŵ�ͼƬ��Ϣ
		if (isSaveAction && parent.getType() == ItemInfo.NEWS_TYPE) {
			News parentNews = (News)parent;
			String pictureIds = parentNews.getPictures();
			if (pictureIds != null && pictureIds.trim().length() > 0) {
				if (!pictureIds.endsWith(Global.CMSSEP))
					pictureIds += Global.CMSSEP;
				pictureIds += String.valueOf(picture.getId());
			}
			else {
				pictureIds = String.valueOf(picture.getId());
			}
			// �ж����ŵ�ͼƬ�����Ƿ񳬹�������(pictures�ֶι�)
			int matchesCount = StringUtils.countMatches(pictureIds, Global.CMSSEP);
			if (matchesCount > NEWS_PICTURES_MAXIMIZE_COUNT) {
				pictureIds = pictureIds.substring(pictureIds.indexOf(Global.CMSSEP) + 1);
			}
			
			parentNews.setPictures(pictureIds);
			ItemManager.getInstance().update(parentNews);

		}
		
		return picture;
	}
	
	////////////////////////////////////////////////////////////////////////////

	public String handleExternalPicturesFromContent(String content, int parentId, Map extend) {
		
		if (content == null || content.trim().length() == 0)
			return null;

		StringBuffer sb = new StringBuffer(content);
		
		try {
			// ��֤������
			Item parentItem = ItemManager.getInstance().get(new Integer(parentId), EntityItem.class);
			if (parentItem == null)
				throw new Exception();
			EntityItem parent = (EntityItem)parentItem;
			if (parent.getType() != ItemInfo.HOMEPAGE_TYPE 
					&& parent.getType() != ItemInfo.SUBJECT_TYPE
					&& parent.getType() != ItemInfo.NEWS_TYPE)
				throw new Exception();
			
			Matcher m = patternUrl.matcher(content);
			while (m.find()) {
				String pictureUrl = m.group(1) + m.group(2);
				
				// ������hexun�Լ���ͼƬ
				boolean handleIt = true;
				String tempUrl = pictureUrl.toUpperCase();
				//log.info("=====tmp pic=====" + tempUrl);
				if (tempUrl.indexOf("PUSUO.NET") > -1 && tempUrl.indexOf("CMS.PUSUO.NET") == -1) {
					handleIt = false;
				}
				if (!handleIt)
					continue;
				
				// ���url����ͼƬ����
				Picture picture = createPictureFromUrl(pictureUrl, parent, extend); 
				if (picture == null)
					continue;
				
				// �ⲿͼƬ�Ѿ������hexun��ͼƬ
				String pictureUrlOfHexun = picture.getUrl();
				if (pictureUrlOfHexun == null || pictureUrlOfHexun.trim().length() == 0)
					continue;
				
				// �����滻
				sb = ItemUtil.replaceTag(sb, pictureUrl, pictureUrlOfHexun);
			}
		}
		catch (Throwable t) {
			log.error("handling external pictures from content err.");
		}
		
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * ���url����ͼƬ����
	 */
	private Picture createPictureFromUrl(String url, EntityItem parent, Map extend) {
		
		Picture result = null;
		
		try {
			if (url == null || url.trim().length() == 0 || parent == null)
				return result;
			
			// ��ȡ��չ��
			String ext = "";
			int idx = url.lastIndexOf(".");
			if (idx > 0)
				ext = url.substring(idx + 1);
			if (ext.trim().length() == 0)
				return result;

			// ��ȡͼƬ����
			byte[] contentPicture = Util.getContentByHttp(url);
			if (contentPicture == null || contentPicture.length == 0)
				return result;

			Picture picture = (Picture)ItemInfo.getItemByType(ItemInfo.PICTURE_TYPE);
			
			// ��������
			picture.setPid(parent.getId());
			picture.setExt(ext);

			// ����ͼƬ
			Map extendPicture = new HashMap();
			extendPicture.put(PROPERTY_NAME_CONTENT, contentPicture);
			if (extend != null && extend.containsKey(PROPERTY_NAME_AUTH)) {
				extendPicture.put(PROPERTY_NAME_AUTH, extend.get(PROPERTY_NAME_AUTH));
			}
			result = addPicture(picture, extendPicture);
		}
		catch (Exception e) {
			result = null;
		}
		
		return result;
	}
	
	private boolean handleThumbnailAndWatermark(Picture picture, Map extend) {
		
		if (extend == null)
			return false;
		
		boolean needUpdated = false;
		
		try {
			String storeFileName = PageManager.getTStorePath(picture);
			
			// ����ͼ
			
			// ���Ʋ���
			if (extend.containsKey(PROPERTY_NAME_THUMB_GEN_CUSTOMIZE)) {
				boolean genCustomize = ((Boolean)extend.get(PROPERTY_NAME_THUMB_GEN_CUSTOMIZE)).booleanValue();
				if (genCustomize) {
					boolean thumbResult = createThumbnail(picture, extend, storeFileName);
					if (thumbResult) {
						picture.addExflag(Picture.THUMB_FLAG);
						needUpdated = true;
					}
				}
			}
			// Ĭ�ϲ���
			else if (extend.containsKey(PROPERTY_NAME_THUMB_GEN_DEFAULT)) {
				boolean genDefault = ((Boolean)extend.get(PROPERTY_NAME_THUMB_GEN_DEFAULT)).booleanValue();
				if (genDefault) {
					boolean thumbResult = ClientImage.getInstance().genThumbnail(storeFileName);
					if (thumbResult) {
						picture.addExflag(Picture.THUMB_FLAG);
						needUpdated = true;
					}
				}				
			}
		
			// ˮӡ
			
			// ���Ʋ���
			if (extend.containsKey(PROPERTY_NAME_WATERMARK_GEN_CUSTOMIZE)) {
				boolean genCustomize = ((Boolean)extend.get(PROPERTY_NAME_WATERMARK_GEN_CUSTOMIZE)).booleanValue();
				if (genCustomize) {
					boolean markResult = createWatermark(picture, extend, storeFileName);
					if (markResult) {
						picture.addExflag(Picture.MARK_FLAG);
						needUpdated = true;
					}
				}
			}
			// Ĭ�ϲ���
			else if (extend.containsKey(PROPERTY_NAME_WATERMARK_GEN_DEFAULT)) {
				boolean genDefault = ((Boolean)extend.get(PROPERTY_NAME_WATERMARK_GEN_DEFAULT)).booleanValue();
				if (genDefault) {
					String markText = "CMS.PUSUO.NET";
					TextWatermarkInfo info = new TextWatermarkInfo(storeFileName, markText);
					boolean markResult = ClientImage.getInstance().genWatermarkImageT(info);
					if (markResult) {
						picture.addExflag(Picture.MARK_FLAG);
						needUpdated = true;
					}
				}
			}
			//������120x90���������  added by shijinkui 091215
			else{
				Channel channel = (Channel)ItemManager.getInstance().get(new Integer(picture.getChannel()), Channel.class);
				String markText = channel.getName().toUpperCase();
				TextWatermarkInfo info = new TextWatermarkInfo(storeFileName, markText);
				info.setPlacement(Placement.NorthEastGravity);		
				info.setFontSize(20);
				info.setFillColor("red");
				info.setFontName("");
				info.setOffsetX(2);
				info.setOffsetY(2);
				boolean markResult = ClientImage.getInstance().genWatermarkImageT(info);
				if (markResult) {
					picture.addExflag(Picture.MARK_FLAG);
					needUpdated = true;
				}
				log.info("markText: " + markText + "|| markResult: " + markResult);
				System.out.println("markText: " + markText + "|| markResult: " + markResult);
			}
		}
		catch (Exception e) {
			needUpdated = false;
		}
		
		return needUpdated;
	}

	private boolean createThumbnail(Picture picture, Map extend, String storeFileName) {
		
		boolean result = false;
		
		if (extend != null) {
			int thumbWidth = ((Integer) extend.get(PROPERTY_NAME_THUMB_WIDTH)).intValue();
			int thumbHeight = ((Integer) extend.get(PROPERTY_NAME_THUMB_HEIGHT)).intValue();
			result = ClientImage.getInstance().genThumbnail(storeFileName, thumbWidth, thumbHeight);
		}
		
		return result;
	}

	private boolean createWatermark(Picture picture, Map extend, String storeFileName) {
		
		if (extend == null)
			return false;
		
		boolean result = false;
		
		int placement = ((Integer)extend.get(PROPERTY_NAME_WATERMARK_PLACEMENT)).intValue();
		int offsetX = ((Integer)extend.get(PROPERTY_NAME_WATERMARK_OFFSETX)).intValue();
		int offsetY = ((Integer)extend.get(PROPERTY_NAME_WATERMARK_OFFSETY)).intValue();
		
		int markType = ((Integer)extend.get(PROPERTY_NAME_WATERMARK_TYPE)).intValue();
		if (markType == 0) {
			String markText = (String)extend.get(PROPERTY_NAME_WATERMARK_TEXT);
			if(markText == null || markText.trim().equals(""))
				return false;
			TextWatermarkInfo info = new TextWatermarkInfo(storeFileName, markText);
			info.setFillColor((String)extend.get(PROPERTY_NAME_WATERMARK_FILLCOLOR));
			info.setFontName((String)extend.get(PROPERTY_NAME_WATERMARK_FONTNAME));
			info.setFontSize(((Integer)extend.get(PROPERTY_NAME_WATERMARK_FONTSIZE)).intValue());
			info.setPlacement(placement);
			info.setOffsetX(offsetX);
			info.setOffsetY(offsetY);
			result = ClientImage.getInstance().genWatermarkImageT(info);
		}
		else {
			String markImage = (String)extend.get(PROPERTY_NAME_WATERMARK_IMAGE);
			result = ClientImage.getInstance().genWatermarkImageG(
					storeFileName, markImage, placement, offsetX, offsetY);
		}
		
		return result;
	}

}
