/**
 * 
 */
package net.pusuo.cms.client.biz;

import java.util.Map;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.FileException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PictureFormatException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.core.Picture;

/**
 * @author Alfred.Yuan
 *
 */
public interface PictureManager extends CmsManager {

	public static final String HEXUN_PICTURE_REGEX_URL = "(http://[^<>\"\']*\\.)(jpg|gif|jpeg|png|bmp|pdf|doc|txt)";
	
	public static final String HEXUN_PICTURE_REGEX_EXT = "(jpg|gif|jpeg|png|bmp|pdf|doc|txt)";
	
	public static final int HEXUN_PICTURE_MINIMIZE_PRIORITY = 50;
	public static final int HEXUN_PICTURE_DEFAULT_PRIORITY = 50;
	
	public static final int NEWS_PICTURES_MAXIMIZE_COUNT = 40; // һƪ�����е�ͼƬ���������
	public static final int NEWS_PICTURE_MAXIMIZE_SIZE = 1024 * 150; // ����ͼƬ������ֽ���
	
	public static final String PROPERTY_NAME_CONTENT = "PictureManager.content";
	
	public static final String PROPERTY_NAME_THUMB_GEN_DEFAULT = "PictureManager.genthumbnail.default";
	public static final String PROPERTY_NAME_THUMB_GEN_CUSTOMIZE = "PictureManager.genthumbnail.customize";
	public static final String PROPERTY_NAME_THUMB_WIDTH = "PictureManager.thumbwidth";
	public static final String PROPERTY_NAME_THUMB_HEIGHT = "PictureManager.thumbheight";
	
	public static final String PROPERTY_NAME_WATERMARK_GEN_DEFAULT = "PictureManager.genwatermark.default";
	public static final String PROPERTY_NAME_WATERMARK_GEN_CUSTOMIZE = "PictureManager.genwatermark.customize";
	public static final String PROPERTY_NAME_WATERMARK_TYPE = "PictureManager.marktype";
	public static final String PROPERTY_NAME_WATERMARK_PLACEMENT = "PictureManager.placement";
	public static final String PROPERTY_NAME_WATERMARK_OFFSETX = "PictureManager.markoffsetx";
	public static final String PROPERTY_NAME_WATERMARK_OFFSETY = "PictureManager.markoffsety";
	public static final String PROPERTY_NAME_WATERMARK_TEXT = "PictureManager.marktext";
	public static final String PROPERTY_NAME_WATERMARK_FILLCOLOR = "PictureManager.markfc";
	public static final String PROPERTY_NAME_WATERMARK_FONTNAME = "PictureManager.markfn";
	public static final String PROPERTY_NAME_WATERMARK_FONTSIZE = "PictureManager.markfs";
	public static final String PROPERTY_NAME_WATERMARK_IMAGE = "PictureManager.markimage";

	////////////////////////////////////////////////////////////////////////////
	
	public Picture getPicture(int pictureId) throws DaoException;

	public Picture addPicture(Picture picture, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, 
				DaoException, PictureFormatException, FileException;
	
	public Picture updatePicture(Picture picture, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, 
				DaoException, FileException, PictureFormatException;
	
	public boolean deletePicture(Picture picture) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * �������������е�ͼƬ
	 * Ŀ��:�Զ�����༭ת�������,����"�ⲿ��ͼ->����->�ϴ�"
	 */
	public String handleExternalPicturesFromContent(String content, int parentId, Map extend);

}
