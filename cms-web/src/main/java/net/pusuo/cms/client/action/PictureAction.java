package net.pusuo.cms.client.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
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

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.FileException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PictureFormatException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Picture;
import com.hexun.cms.image.ImageRule;

public class PictureAction extends EntityAction {

	private static final Log LOG = LogFactory.getLog(PictureAction.class);
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;
		Authentication auth = null;
		ActionErrors errors = new ActionErrors();

		try {
			auth = AuthenticationFactory.getAuthentication(request,response);
		} catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
			saveErrors(request, errors);
			return mapping.findForward("failure");
		}
		
		Picture picture = null;
		
		Picture pictureConfig = (Picture)ItemInfo.getItemByType(ItemInfo.PICTURE_TYPE);
		Map extend = new HashMap();

		try {
			BaseForm dForm = (BaseForm) form;
			
			// ����ԭ�ȵĲ���
			int pictureId = ((Integer)dForm.get("id")).intValue();
			if (pictureId > 0) {
				Picture pictureOrigin = ManagerFacade.getPictureManager().getPicture(pictureId);
				if (pictureOrigin == null)
					throw new DaoException();
				PropertyUtils.copyProperties(pictureConfig, pictureOrigin);
			}
			
			// ��ȡ���û��޸ĵĲ���
			ItemUtil.setItemValues(dForm, pictureConfig);
			
			// ������֤��Ϣ
			extend.put(PictureManager.PROPERTY_NAME_AUTH, auth);
			
			// ���ݸ��������
			extend.put(PictureManager.PROPERTY_NAME_PNAME, ((String)dForm.get("pname")).trim());
			
			// �ϴ�ͼƬ
			FormFile formFile = parseUploadRequest(request, dForm);
			if (formFile != null) {
				String fileName = formFile.getFileName();
				if (fileName != null && fileName.trim().length() > 0) {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);	
					if (fileExt != null && fileExt.trim().length() > 0)
						pictureConfig.setExt(fileExt);
					
					byte[] content = formFile.getFileData();
					if (content != null && content.length > 0)
						extend.put(PictureManager.PROPERTY_NAME_CONTENT, content);
				}
			}
			
			// ����ͼ
			if ("yes".equals(dForm.get("genthumb"))) {
				extend.put(PictureManager.PROPERTY_NAME_THUMB_GEN_CUSTOMIZE, new Boolean(true));
				extend.put(PictureManager.PROPERTY_NAME_THUMB_WIDTH, (Integer)dForm.get("thumbwidth"));
				extend.put(PictureManager.PROPERTY_NAME_THUMB_HEIGHT, (Integer)dForm.get("thumbheight"));
			}
			 
			// ˮӡ
			int markType = ((Integer) dForm.get("marktype")).intValue();
			if (markType != -1) {
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_GEN_CUSTOMIZE, new Boolean(true));
				
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_TYPE, new Integer(markType));
				
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_PLACEMENT, (Integer)dForm.get("placement"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_OFFSETX, (Integer)dForm.get("markoffsetx"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_OFFSETY, (Integer)dForm.get("markoffsety"));
				
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_TEXT, (String)dForm.get("markstr"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_FILLCOLOR, (String)dForm.get("markfc"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_FONTNAME, (String)dForm.get("markfn"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_FONTSIZE, (Integer)dForm.get("markfs"));
				extend.put(PictureManager.PROPERTY_NAME_WATERMARK_IMAGE, (String)dForm.get("markimage"));
			}
			
			if (pictureConfig.getId() < 0) { // ����ͼƬ
				picture = ManagerFacade.getPictureManager().addPicture(pictureConfig, extend);
			}
			else { // ����ͼƬ
				picture = ManagerFacade.getPictureManager().updatePicture(pictureConfig, extend);
			}
			if (picture == null || picture.getId() < 0) {
				throw new DaoException();
			}
		}
		catch (PictureFormatException pfe) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		}
		catch (FileException fe) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		}
		catch (ParentNameException pne) {
			errors.add("errors.item.pnamenotexist", new ActionError("errors.item.pnamenotexist"));
		}
		catch (PropertyException pe) {
			errors.add("errors.parameter", new ActionError("errors.parameter"));
		}
		catch (UnauthenticatedException ue) {
			errors.add("auth.failure", new ActionError("auth.failure"));
		}
		catch (DaoException de) {
			errors.add("errors.item.save", new ActionError("errors.item.save"));
		}
 		catch (Exception e) {
 			errors.add("errors.item.save", new ActionError("errors.item.save"));
 		}
		
		try {
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				ret = mapping.findForward("failure");
			}
			else {
				response.sendRedirect("picture.do?method=view&id=" + picture.getId());
			}
		}
		catch (Exception e) {
		}

		return ret;
	}
	
	private FormFile parseUploadRequest(HttpServletRequest request,	ActionForm form) {

		FormFile formFile = null;
		String contentType = request.getContentType();

		if (contentType == null	|| contentType.indexOf("multipart/form-data") == -1)
			return null;

		MultipartRequestHandler mrh = form.getMultipartRequestHandler();
		Hashtable ht = (Hashtable) mrh.getFileElements();
		Enumeration ema = null;
		if (ht != null && ht.size() > 0) {
			ema = ht.keys();
			if (ema.hasMoreElements()) {
				String keys = (String) ema.nextElement();
				formFile = (FormFile) ht.get(keys);
			}
		}
		return formFile;
	}

	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward af = super.view(mapping, form, request, response);

		DynaActionForm fm = (DynaActionForm) form;

		Integer id = (Integer) fm.get("id");
		Picture pictureItem = (Picture) ItemManager.getInstance().get(id,
				ItemInfo.getItemClass(ItemInfo.PICTURE_TYPE));

		if(pictureItem != null){
			String picUrl = pictureItem.getUrl();
			ImageRule rule = new ImageRule();

			if (pictureItem.hasExflag(Picture.THUMB_FLAG)) {
				String thumbUrl = rule.getThumbnailUrl(picUrl);
				fm.set("thumburl", thumbUrl);
			}

			if (pictureItem.hasExflag(Picture.MARK_FLAG)) {
				String markUrl = rule.getWatermarkUrl(picUrl);
				fm.set("markurl", markUrl);
			}
		}
		return af;
	}

}
