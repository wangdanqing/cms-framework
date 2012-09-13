/*
 * 
 * @author chenqj
 * Created on 2004-8-19
 *
 */
package net.pusuo.cms.client.image;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.client.util.ConfigUtils;
import com.hexun.cms.image.ImageInterface;
import com.hexun.cms.image.TextWatermarkInfo;

import java.awt.Dimension;

/**
 * @author chenqj Image Client
 */
public class ClientImage {

	private static final Log log = LogFactory.getLog(ClientImage.class
			.getName());

	private static ClientImage ci = null;

	private static ImageInterface serverImage = null;

	private static final String serverInterfaceName = "ServerImage";

	private static final String THUMB_WIDTH_CONFIG_NAME = "cms4.image.thumbnail.default.width";

	private static final String THUMB_HEIGHT_CONFIG_NAME = "cms4.image.thumbnail.default.height";

	private static final int THUMB_DEFAULT_WIDTH = 125;

	private static final int THUMB_DEFAULT_HEIGHT = 125;

	/**
	 * Singleton Method
	 * 
	 * @return ClientImage's Singleton Instance
	 */
	public static ClientImage getInstance() {
		if (ci == null) {
			synchronized (ClientImage.class) {
				if (ci == null)
					ci = new ClientImage();
			}
		}
		return ci;
	}

	private ClientImage() {
		serverImage = getServerInterface();
	}

	private static ImageInterface getServerInterface() {
		return (ImageInterface) ClientUtil
				.renewRMI(serverInterfaceName);
	}

	/**
	 * @param srcFilePath
	 * @return
	 */
	public boolean genThumbnail(String srcFilePath) {
		int thumbWidth = ConfigUtils.getIntValue(
				THUMB_WIDTH_CONFIG_NAME, THUMB_DEFAULT_WIDTH);
		int thumbHeight = ConfigUtils.getIntValue(
				THUMB_HEIGHT_CONFIG_NAME, THUMB_DEFAULT_HEIGHT);
		return this.genThumbnail(srcFilePath, thumbWidth, thumbHeight,
				true);
	}

	/**
	 * @param srcFilePath
	 * @param thumbWidth
	 * @param thumbHeight
	 * @return
	 */
	public boolean genThumbnail(String srcFilePath, int thumbWidth,
			int thumbHeight) {
		return this.genThumbnail(srcFilePath, thumbWidth, thumbHeight,
				true);
	}

	/**
	 * @param srcFilePath
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param keepProportion
	 * @return
	 */
	public boolean genThumbnail(String srcFilePath, int thumbWidth,
			int thumbHeight, boolean keepProportion) {
		try {
			return serverImage
					.genThumbnail(srcFilePath, thumbWidth,
							thumbHeight,
							keepProportion);
		} catch (RemoteException e) {
			String error = "Invoke Image RMI interface failed! reason: "
					+ e;
			log.error(error);
			serverImage = getServerInterface();
			return false;
		}
	}

	/**
	 * @param srcFilePath
	 * @param markFilePath
	 * @param placement
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	public boolean genWatermarkImageG(String srcFilePath,
			String markFilePath, int placement, int offsetX,
			int offsetY) {
		try {
			return serverImage.genWatermarkImageG(srcFilePath,
					markFilePath, placement, offsetX,
					offsetY);
		} catch (RemoteException re) {
			String error = "Invoke Image RMI interface failed! reason: "
					+ re;
			log.error(error);
			serverImage = getServerInterface();
			return false;
		}
	}

	/**
	 * @param info
	 * @return
	 */
	public boolean genWatermarkImageT(TextWatermarkInfo info) {
		try {
			return serverImage.genWatermarkImageT(info);
		} catch (RemoteException re) {
			String error = "Invoke Image RMI interface failed! reason: "
					+ re;
			log.error(error);
			serverImage = getServerInterface();
			return false;
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public Dimension getDimension( String file ) {
		try {
			return serverImage.getDimension(file);
		} catch (RemoteException re) {
			String error = "Invoke Image RMI interface failed! reason: "
					+ re;
			log.error(error);
			serverImage = getServerInterface();
			return new Dimension(-1,-1);
		}
	}


}
