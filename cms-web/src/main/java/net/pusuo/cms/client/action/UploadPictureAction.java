package net.pusuo.cms.client.action;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Enumeration;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import org.apache.struts.action.ActionError;

import org.apache.struts.action.ActionErrors;

import org.apache.struts.action.ActionForm;

import org.apache.struts.action.ActionForward;

import org.apache.struts.action.ActionMapping;

import org.apache.struts.upload.FormFile;

import org.apache.struts.upload.MultipartRequestHandler;

import com.hexun.cms.Configuration;
import com.hexun.cms.client.auth.Authentication;

import com.hexun.cms.client.auth.AuthenticationFactory;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;

import com.hexun.cms.client.file.ClientFile;

/**
 * 
 * 
 * 
 * @author agilewang �ϴ�ͼƬ��img��
 * 
 * 
 * 
 */

public class UploadPictureAction extends BaseAction {

	private static int sequence = 0;

	private static final String DATE_SHORT = "yyyyMMdd";

	private static final String DATE_LONG = "HHmmssSSS";

	private static final String FOCUS_IMG_PRE = "/focusUp/";

	private static final String PHOTO_DOMAIN = "http://img.pusuo.net";

	private static final Log LOG = LogFactory.getLog(UploadPictureAction.class);

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ActionForward ret = null;

		Authentication auth = null;

		ActionErrors errors = new ActionErrors();

		try {

			auth = AuthenticationFactory.getAuthentication(request, response);

		} catch (UnauthenticatedException ue) {

			errors.add("auth.failure", new ActionError("auth.failure"));

			saveErrors(request, errors);

			return mapping.findForward("failure");

		}

		try {

			BaseForm dForm = (BaseForm) form;

			String parentId = (String) dForm.get("parentId");

			FormFile formFile = parseUploadRequest(request, dForm);

			if (formFile != null) {

				String fileName = formFile.getFileName();

				if (fileName != null && fileName.trim().length() > 0) {

					String fileExt = fileName.substring(fileName

					.lastIndexOf("."));

					byte[] content = formFile.getFileData();

					if (content != null && content.length > 0) {

						SimpleDateFormat sdf = new SimpleDateFormat(DATE_SHORT);

						SimpleDateFormat ldf = new SimpleDateFormat(DATE_LONG);

						Date now = new Date();

						String dayDir = sdf.format(now); // ÿ�����һ��Ŀ¼

						/* ���ʱ�����һ��ʱ��µ��ļ��� ��ʽ:ʱ��+��ǰ�̵߳�hashCode */

						String dayFile = ldf.format(now)

						+ System.identityHashCode(Thread

						.currentThread());

						String httpLocation = FOCUS_IMG_PRE + dayDir + "/"

						+ dayFile + fileExt;

						// String realLocation = "/photocdn" + httpLocation;
						String realLocation = Configuration.getInstance().get(
								"cms4.file.picture.root")
								+ httpLocation;

						if (LOG.isInfoEnabled()) {

							LOG.info("realLocal:" + realLocation);

							LOG.info("httpLocal:" + httpLocation);

						}

						try {

							if (ClientFile.getInstance().write(content,

							realLocation, true)) {

								request.setAttribute("imgUrl", PHOTO_DOMAIN

								+ httpLocation);

								request.setAttribute("parentId", parentId);

							} else {

								throw new RuntimeException("remote call error");

							}

						} catch (Exception e) {

							LOG.error("remote call error", e);

							throw new RuntimeException("remote call error", e);

						}

					}

				}

			}

		} catch (Throwable e) {

			errors.add("errors.item.save", new ActionError("errors.item.save"));

		}

		try {

			if (!errors.isEmpty()) {

				saveErrors(request, errors);

				ret = mapping.findForward("failure");

			} else {

				ret = mapping.findForward("success");

			}

		} catch (Exception e) {

		}

		return ret;

	}

	private FormFile parseUploadRequest(HttpServletRequest request,

	ActionForm form) {

		FormFile formFile = null;

		String contentType = request.getContentType();

		if (contentType == null

		|| contentType.indexOf("multipart/form-data") == -1)

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

	public ActionForward preProcess(ActionMapping mapping, ActionForm form,

	HttpServletRequest request, HttpServletResponse response) {

		/*
		 * 
		 * if (!validatePermissions(request, response)) {
		 * 
		 * return mapping.findForward("failure"); }
		 */

		return null;

	}

}
