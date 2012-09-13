package net.pusuo.cms.impress.sync.img;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.slideshow.sync.img.AbstractImageUtil;
import com.hexun.slideshow.sync.img.IThumbailRule;
import com.hexun.slideshow.sync.img.SyncThumbnail;

/**
 * ���õ�ѹ��ͼƬ�Ĺ���
 * 
 * @author agilewang
 */
public class CMSImageUtil extends AbstractImageUtil {
	private static final Log log = LogFactory.getLog(CMSImageUtil.class);

	private String imageToolkitClass = null;

	public String getImageToolkitClass() {
		return imageToolkitClass;
	}

	public void setImageToolkitClass(String imageToolkitClass) {
		this.imageToolkitClass = imageToolkitClass;
	}

	protected ImageToolkit getImageToolkit() {
		if (imageToolkitClass == null) {
			throw new IllegalArgumentException("imageToolkitClass is excepted.");
		}
		try {
			return (ImageToolkit) Class.forName(imageToolkitClass)
					.newInstance();
		} catch (Throwable te) {
			throw new RuntimeException("Get getImageToolkit error", te);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.slideshow.sync.img.magic.ImageUtil#genThumbnail(com.hexun.slideshow.sync.img.SyncThumbnail)
	 */
	public boolean genThumbnail(SyncThumbnail tb) {
		if (tb == null) {
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("genThumbnail src=" + tb.srcFilePath);
		}

		ImageToolkit imageToolkit = getImageToolkit();
		imageToolkit.setImageSrc(tb.srcFilePath);
		imageToolkit.setImgMissTimeOut(this.getImgMissTimeOut());

		if (!imageToolkit.readImage()) {
			if (log.isErrorEnabled()) {
				log.error(String.format("Read source image file %s failed",
						tb.srcFilePath));
			}
			return false;
		}

		String thumbFilePath = null;
		try {
			for (IThumbailRule rule : this.rules) {
				if (!rule.process(imageToolkit)) {
					return false;
				}
				if(log.isInfoEnabled()){
					log.info("tb.srcFilePath:"+tb.srcFilePath);
				}
				thumbFilePath = rule.getThumbnailPath(tb.srcFilePath);
				if(log.isInfoEnabled()){
					log.info("thumbFilePath:"+thumbFilePath);
				}

				imageToolkit.writeImageTo(thumbFilePath);
				tb.thumbPaths.add(thumbFilePath);
			}
		} catch (Throwable we) {
			String estr = "Generate thumbnail file " + thumbFilePath
					+ " failed " + we;
			log.error(estr, we);
			return false;
		} finally {
			imageToolkit.release();
		}
		log.info("Generate thumbnail file from " + tb.srcFilePath + " to "
				+ thumbFilePath);
		return true;
	}
}
