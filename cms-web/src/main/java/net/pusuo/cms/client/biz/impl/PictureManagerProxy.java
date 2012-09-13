/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.PictureManager;
import com.hexun.cms.client.biz.event.PictureEventListener;
import com.hexun.cms.client.biz.event.impl.DefaultPictureEventListener;
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
public class PictureManagerProxy extends CmsManagerProxy implements	PictureManager {

	private static final Log log = LogFactory.getLog(PictureManagerProxy.class);
	
	private PictureManager instance = null;
			
	private PictureManagerProxy() {
		super();
	}

	public PictureManagerProxy(PictureManager pictureManager) {
		instance = pictureManager;
	}

	public Picture addPicture(Picture pictureConfig, Map extend)
			throws PropertyException, ParentNameException,
			UnauthenticatedException, DaoException, PictureFormatException,
			FileException {
		
		Picture picture = null;
		if (instance != null) {
			picture = instance.addPicture(pictureConfig, extend);
		}
		
		return picture;
	}

	public Picture updatePicture(Picture pictureConfig, Map extend)
			throws PropertyException, ParentNameException, PictureFormatException,
			UnauthenticatedException, DaoException, FileException {
		
		Picture picture = null;
		if (instance != null) {
			picture = instance.updatePicture(pictureConfig, extend);
		}
		
		return picture;
	}

	public boolean deletePicture(Picture picture) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {
		
		return instance.deletePicture(picture);
	}

	public Picture getPicture(int pictureId) throws DaoException {
		
		return instance.getPicture(pictureId);
	}

	public String handleExternalPicturesFromContent(String content,
			int parentId, Map extend) {
		
		return instance.handleExternalPicturesFromContent(content, parentId, extend);
	}

}
