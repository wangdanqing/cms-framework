package net.pusuo.cms.client.util;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.slideshow.sync.img.jai.JAIImageToolkit;

/**
 * @author agilewang
 * 
 */
public class JAIServerImageUtil  {

	private static final Log log = LogFactory.getLog(JAIServerImageUtil.class);
			
	public static boolean genThumbnail(String srcFilePath, int thumbWidth,
			int thumbHeight, boolean keepProportion) {

		String thumbFilePath = null;
		
		try {
			JAIImageToolkit jt = new JAIImageToolkit();
			jt.setImageSrcBytes(readImage(srcFilePath));
			jt.readImage();
			thumbFilePath = new ImageRuleUtil().getThumbnailFileName(srcFilePath);
			
			if (keepProportion) {
				int srcWidth = jt.getSrcWidth();
				int srcHeight = jt.getSrcHeight();
				double xRatio = ((double) thumbWidth) / srcWidth;
				double yRatio = ((double) thumbHeight) / srcHeight;
				double factor = Math.min(xRatio, yRatio);
				thumbWidth = (int) (factor * srcWidth);
				thumbHeight = (int) (factor * srcHeight);
			}
			jt.scaleTo(thumbWidth, thumbHeight, false);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jt.writeToStream(out);
			writeImage(out.toByteArray(), thumbFilePath);
			jt.release();
		} catch (Throwable we) {
			String estr = "Generate thumbnail file " + thumbFilePath + " failed " + we;
			log.error(estr, we);
			we.printStackTrace();
			return false;
		}
		
		if (log.isInfoEnabled()) {
			log.info("Generate thumbnail file from " + srcFilePath + " to "
					+ thumbFilePath);
		}
		
		return true;
	}
	
	private static byte[] readImage(String imagePath) {
		byte[] srcData = null;
		try {
			srcData = FileUtil.read(imagePath);
		} catch (Throwable t) {
			throw new RuntimeException("read image error.", t);
		}
		return srcData;
	}

	private static void writeImage(byte[] data, String imagePath) throws Exception {
		if (!FileUtil.write(data, imagePath)) {
			log.error("Write image error." + imagePath);
		}
	}
	
	public static void main(String[] args) {
		
		String path = "D:\\JavaDir\\Winter.jpg";
		if (genThumbnail(path, 200, 200, true)) {
			System.out.println("");
		}
		else {
			log.error("error");
		}
	}
}
