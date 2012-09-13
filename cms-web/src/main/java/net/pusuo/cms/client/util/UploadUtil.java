/**
 * 
 */
package net.pusuo.cms.client.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import com.agile.zip.ZipEntry;
import com.agile.zip.ZipInputStream;
import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.exception.PictureSizeException;
import com.hexun.cms.client.biz.exception.PictureUploadException;
import com.hexun.cms.client.biz.util.PictureUtil;
import com.hexun.cms.core.News;
import com.hexun.cms.util.Util;

/**
 * @author Alfred.Yuan
 *
 */
public class UploadUtil {
	
	private static final Log log = LogFactory.getLog(UploadUtil.class);

	public static final int maxFileSize = Integer.parseInt(
			Configuration.getInstance().get("cms4.picbatch.upload.filesize"));

	// ��ʽ:ext1;ext2;...
	public static final String allowFileExt = Configuration.getInstance()
			.get("cms4.picbatch.upload.allowFileExt");

	// ��ʽ:dir1/dir2/.../
	public static final String defaultFileDir = Configuration.getInstance()
			.get("cms4.picbatch.upload.tempPicturePath");
	
	public static SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyyMMdd");
	
	public static int THUMB_WIDTH = 130;
	public static int THUMB_HEIGHT = 130;
	
	public static boolean validateUpload(ActionForm form, 
			HttpServletRequest request, HttpServletResponse response, int subtype)
		throws PictureUploadException, PictureSizeException {
		
		boolean result = true;
		
		// ��ͼ����
		if (subtype == News.SUBTYPE_ZUTU) {			
			// �ϴ�ģʽ
			String uploadModeParam = request.getParameter("uploadmode");
			int uploadMode = -1;
			try {
				uploadMode = Integer.parseInt(uploadModeParam);
			} catch (Exception e) {
			}
			
			DynaActionForm fm = (DynaActionForm) form;
			MultipartRequestHandler mrh = (MultipartRequestHandler)fm.getMultipartRequestHandler();
			Hashtable ht = (Hashtable) mrh.getFileElements();
			if (ht.size() <= 0)
				return false;
			
			// ����ϴ�
			if (uploadMode == 0) { 
				String key = "piczip";
				FormFile formFile = (FormFile) ht.get(key);
				if (formFile == null)
					return false;
				
				String fileType = formFile.getContentType();
				if (fileType == null || !fileType.equalsIgnoreCase("application/x-zip-compressed")) {
					throw new PictureUploadException();
				}
				
				int pictureCount = 0;
				
				ZipInputStream zis = null;
				try {
					zis = new ZipInputStream(formFile.getInputStream());
					ZipEntry zipEntry = null;
					while ((zipEntry = zis.getNextEntry()) != null) {
						if (zipEntry.isDirectory())
							continue;
						
						String fileName = zipEntry.getName();
						if (fileName.lastIndexOf("/") > -1) {
							fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
						}
						int indexLast = fileName.lastIndexOf(".");
						if (indexLast == -1) {
							throw new PictureUploadException();
						}
						String fileExt = fileName.substring(indexLast + 1); // ��չ��
						if (!PictureUtil.validateExt(fileExt)){
							continue;
						}
								
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						int code = -1;
						while ((code = zis.read()) != -1) {
							baos.write(code);
						}
						byte[] content = baos.toByteArray();
						baos.close();
						
						zis.closeEntry();
						
						if (content == null || content.length > PictureManager.NEWS_PICTURE_MAXIMIZE_SIZE) {
							throw new PictureSizeException();
						}
						
						pictureCount++;
						if (pictureCount > PictureManager.NEWS_PICTURES_MAXIMIZE_COUNT) {
							throw new PictureSizeException();
						}
					}
				}
				catch (IOException ioe) {
					result = false;
				} finally {
					try {
						if (zis != null)
							zis.close();
					} catch (Exception e) {
					}
				}
			}
			
			// �����ϴ�
			if (uploadMode == 1) { 
				String idsParam = request.getParameter("picids");
				if (idsParam == null || idsParam.trim().length() == 0)
					return false;				
				String[] picIds = idsParam.split(Global.CMSSEP);

				for (int i = 0; i < picIds.length; i++) {
					String key = "picfile" + picIds[i];
					FormFile formFile = (FormFile) ht.get(key);

					String fileName = formFile.getFileName();
					if (fileName == null || fileName.trim().equals("") || fileName.indexOf(".") == -1) {
						throw new PictureUploadException();
					}
					int indexLast = fileName.lastIndexOf(".");
					String fileExt = fileName.substring(indexLast + 1);
					if (!PictureUtil.validateExt(fileExt)){
						throw new PictureUploadException();
					}

					byte[] content = null;
					try { content = formFile.getFileData(); } catch (Exception e) {}
					if (content == null || content.length <= 0 || 
							content.length > PictureManager.NEWS_PICTURE_MAXIMIZE_SIZE) {
						throw new PictureSizeException();
					}
				}
			}
		}
		
		return result;
	}

	public static List uploadTempPictureByFiles(Authentication auth, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, List keyList, boolean compress) {
		
		List resultList = new ArrayList();
		
		FileOutputStream out = null;
		try {
			String contentType = request.getContentType();
			if (contentType == null	|| contentType.indexOf("multipart/form-data") == -1)
				return null;

			DynaActionForm fm = (DynaActionForm) form;
			MultipartRequestHandler mrh = (MultipartRequestHandler)fm.getMultipartRequestHandler();
			Hashtable ht = (Hashtable) mrh.getFileElements();
			if (ht.size() <= 0)
				return null;

			for(int i = 0; i < keyList.size(); i++) {
				String key = (String)keyList.get(i);
				FormFile formFile = (FormFile) ht.get(key);
	
				String fileName = formFile.getFileName();
				if (fileName == null || fileName.trim().equals("") || fileName.indexOf(".") == -1)
					continue;
				if (fileName.lastIndexOf("/") > -1) {
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				}
				int indexLast = fileName.lastIndexOf(".");
				String fileExt = fileName.substring(indexLast + 1); // ��չ��
				fileName = fileName.substring(0, indexLast);		// �ļ���
				if (!allowExt(fileExt))
					continue;
				
				byte[] content = formFile.getFileData();
				if (content == null || content.length <= 0 || 
						content.length > PictureManager.NEWS_PICTURE_MAXIMIZE_SIZE)
					continue;
				
				File userDir = createUserDir(auth.getUserID());
				String diskFileName = RandomUtils.nextInt(999999) + "." + fileExt;
				File diskFile = new File(userDir, diskFileName);
				diskFile.createNewFile();
				out = new FileOutputStream(diskFile);
				out.write(content);
				
				StringBuffer result = new StringBuffer();
				result.append("http://");
				result.append(request.getServerName());
				result.append(request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
				result.append("/");
				result.append(defaultFileDir); // ������·�������� :)
				result.append(String.valueOf(auth.getUserID()));
				result.append("/");
				result.append(dayFormatter.format(Calendar.getInstance().getTime()));
				result.append("/");
				result.append(diskFileName);
				
				UploadEntity entity = new UploadEntity();
				entity.setUrl(result.toString());
				entity.setFileName(fileName);
				entity.setFileExt(fileExt);
				entity.setFileSize(content.length);
				entity.setFilePath(diskFile.getAbsolutePath());
				
				resultList.add(entity);
				
				out.close();
				
				if (compress) {
					String diskPath = diskFile.getAbsolutePath();
					if(JAIServerImageUtil.genThumbnail(diskPath, THUMB_WIDTH, THUMB_HEIGHT, true)) {
						int index = entity.getUrl().lastIndexOf("/");
						String preUrl = entity.getUrl().substring(0, index + 1);
						String postUrl = entity.getUrl().substring(index + 1);
						postUrl = postUrl.substring(0, postUrl.lastIndexOf("."));
						entity.setThumb(preUrl + ImageRuleUtil.THUMB_PREFIX 
								+ postUrl + ImageRuleUtil.HEXUN_PICTURE_THUM_WATER_SUFF);
					}
				}
			}
		} catch (Exception e) {
			log.error("NewsAction.upload error. " + e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
		
		return resultList;
	}
	
	public static List uploadTempPictureByZip(Authentication auth, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String zipKey, boolean compress) {
		
		List resultList = new ArrayList();
		
		ZipInputStream zis = null;
		FileOutputStream out = null;
		try {
			String contentType = request.getContentType();
			if (contentType == null	|| contentType.indexOf("multipart/form-data") == -1)
				return null;

			DynaActionForm fm = (DynaActionForm) form;
			MultipartRequestHandler mrh = (MultipartRequestHandler)fm.getMultipartRequestHandler();
			Hashtable ht = (Hashtable) mrh.getFileElements();
			if (ht.size() <= 0)
				return null; 
			
			FormFile formFile = (FormFile) ht.get(zipKey);
			if (formFile == null)
				return null;
			
			String fileType = formFile.getContentType();
			if (fileType == null || !fileType.equalsIgnoreCase("application/x-zip-compressed")) 
				return null;
			
			Map sortedPictures = new TreeMap();			// ����
			
			zis = new ZipInputStream(formFile.getInputStream());
			ZipEntry zipEntry = null;
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (zipEntry.isDirectory())
					continue;
				
				String fileName = zipEntry.getName();
				if (fileName.lastIndexOf("/") > -1) {
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				}
				int indexLast = fileName.lastIndexOf(".");
				if (indexLast == -1)
					continue;
				String fileExt = fileName.substring(indexLast + 1); // ��չ��
				fileName = fileName.substring(0, indexLast);		// �ļ���
				if (!PictureUtil.validateExt(fileExt)){
					continue;
				}
						
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int code = -1;
				while ((code = zis.read()) != -1) {
					baos.write(code);
				}
				byte[] content = baos.toByteArray();
				baos.close();
				
				zis.closeEntry();
				
				if (content == null || content.length <= 0 || 
						content.length > PictureManager.NEWS_PICTURE_MAXIMIZE_SIZE) {
					continue;
				}
								
				File userDir = createUserDir(auth.getUserID());
				String diskFileName = RandomUtils.nextInt(999999) + "." + fileExt;
				File diskFile = new File(userDir, diskFileName);
				diskFile.createNewFile();
				out = new FileOutputStream(diskFile);
				out.write(content);
				
				StringBuffer result = new StringBuffer();
				result.append("http://");
				result.append(request.getServerName());
				//result.append(request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
				//����80��
				result.append("/");
				result.append(defaultFileDir); // ������·�������� :)
				result.append(String.valueOf(auth.getUserID()));
				result.append("/");
				result.append(dayFormatter.format(Calendar.getInstance().getTime()));
				result.append("/");
				result.append(diskFileName);
				
				out.close();
				
				UploadEntity entity = new UploadEntity();
				entity.setUrl(result.toString());
				entity.setFileName(fileName);
				entity.setFileExt(fileExt);
				entity.setFileSize(content.length);
				entity.setFilePath(diskFile.getAbsolutePath());
				
				sortedPictures.put(fileName, entity);
				
				if (compress) {
					String diskPath = diskFile.getAbsolutePath();
					if(JAIServerImageUtil.genThumbnail(diskPath, THUMB_WIDTH, THUMB_HEIGHT, true)) {
						int index = entity.getUrl().lastIndexOf("/");
						String preUrl = entity.getUrl().substring(0, index + 1);
						String postUrl = entity.getUrl().substring(index + 1);
						postUrl = postUrl.substring(0, postUrl.lastIndexOf("."));
						entity.setThumb(preUrl + ImageRuleUtil.THUMB_PREFIX 
								+ postUrl + ImageRuleUtil.HEXUN_PICTURE_THUM_WATER_SUFF);
					}
				}
			}
			
			// ΪͼƬ����
			if (sortedPictures.size() > 0) {
				Iterator iter = sortedPictures.keySet().iterator();
				while (iter.hasNext()) {
					Object sortedIndex = iter.next();
					Object entity = sortedPictures.get(sortedIndex);
					resultList.add(entity);
				}
			}
		} catch (Exception e) {
			log.error("NewsAction.upload error. " + e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			try {
				if (zis != null)
					zis.close();
			} catch (Exception e) {
			}
		}
		
		return resultList;
	}
	
	public static UploadEntity uploadTempPictureByUrl(Authentication auth, 
			HttpServletRequest request, String pictureUrl, boolean compress) {
		
		if (auth == null || pictureUrl == null || 
				pictureUrl.indexOf("/") == -1 || pictureUrl.indexOf(".") == -1)
			return null;
		
		UploadEntity entity = null;
		
		FileOutputStream out = null;
		try {
			String fileName = pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);
			if (fileName == null || fileName.trim().equals("") || fileName.indexOf(".") == -1)
				return null;
			int indexLast = fileName.lastIndexOf(".");
			String fileExt = fileName.substring(indexLast + 1); // ��չ��
			fileName = fileName.substring(0, indexLast);		// �ļ���
			if (!allowExt(fileExt))
				return null;
			
			byte[] content = Util.getContentByHttp(pictureUrl);
			if (content == null || content.length <= 0 || 
					content.length > PictureManager.NEWS_PICTURE_MAXIMIZE_SIZE)
				return null;
			
			File userDir = createUserDir(auth.getUserID());
			String diskFileName = RandomUtils.nextInt(999999) + "." + fileExt;
			File diskFile = new File(userDir, diskFileName);
			diskFile.createNewFile();
			out = new FileOutputStream(diskFile);
			out.write(content);
			
			StringBuffer result = new StringBuffer();
			result.append("http://");
			result.append(request.getServerName());
			result.append(request.getServerPort() == 80 ? "" : ":" + request.getServerPort());
			result.append("/");
			result.append(defaultFileDir); // ������·�������� :)
			result.append(String.valueOf(auth.getUserID()));
			result.append("/");
			result.append(dayFormatter.format(Calendar.getInstance().getTime()));
			result.append("/");
			result.append(diskFileName);
			
			entity = new UploadEntity();
			entity.setUrl(result.toString());
			entity.setFileName(fileName);
			entity.setFileExt(fileExt);
			entity.setFileSize(content.length);
			entity.setFilePath(diskFile.getAbsolutePath());
			
			if (compress) {
				String diskPath = diskFile.getAbsolutePath();
				if(JAIServerImageUtil.genThumbnail(diskPath, THUMB_WIDTH, THUMB_HEIGHT, true)) {
					int index = entity.getUrl().lastIndexOf("/");
					String preUrl = entity.getUrl().substring(0, index + 1);
					String postUrl = entity.getUrl().substring(index + 1);
					postUrl = postUrl.substring(0, postUrl.lastIndexOf("."));
					entity.setThumb(preUrl + ImageRuleUtil.THUMB_PREFIX 
							+ postUrl + ImageRuleUtil.HEXUN_PICTURE_THUM_WATER_SUFF);
				}
			}
		} catch (Exception e) {
			log.error("NewsAction.upload error. " + e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
		
		return entity;
	}
	
	/**
	 * Ϊ�û�������ʱͼƬĿ¼
	 */
	private static File createUserDir(int userId) {
		
		File userDir = null;	
		try {
			// ��Ŀ¼
			String parentDir = ContextUtil.getRootPath() + defaultFileDir + userId;
			
			// ɾ������ǰ�����
			Calendar twoDaysAgo = Calendar.getInstance();
			twoDaysAgo.add(Calendar.DAY_OF_MONTH, -2);
			File oldDir = new File(parentDir, dayFormatter.format(twoDaysAgo.getTime()));
			if (oldDir.exists()) {
				FileUtils.deleteDirectory(oldDir);
			}
			
			// ���ӵ�������
			userDir = new File(parentDir, dayFormatter.format(Calendar.getInstance().getTime()));
			if (!userDir.exists()) {
				userDir.mkdirs();
			}
		} catch (IOException e) {
			log.error(e);
		}
		return userDir;
	}
	
	/**
	 * �ж�ͼƬ�����Ƿ�Ϸ�
	 */
	private static boolean allowExt(String fileName) {
		String tempFileName = fileName.toLowerCase();
		String[] allowExt = allowFileExt.split(Global.CMSSEP);
		for (int i = 0; i < allowExt.length; i++) {
			if (tempFileName.endsWith(allowExt[i])) {
				return true;
			}
		}
		return false;
	}
	
}
