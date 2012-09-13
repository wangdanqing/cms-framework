package net.pusuo.cms.server.image;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.slideshow.sync.img.jai.JAIImageToolkit;
import com.hexun.slideshow.sync.img.jai.WatermarkImgOp;
import com.hexun.slideshow.sync.img.jai.WatermarkTextOp;

/**
 * @author agilewang
 * 
 */
public class JAIServerImage extends UnicastRemoteObject implements
		ImageInterface {
	private static final long serialVersionUID = 7384997462006981078L;

	private static final Log log = LogFactory.getLog(JAIServerImage.class
			.getName());

	private static JAIServerImage si = null;

	/**
	 * @throws java.rmi.RemoteException
	 */
	private JAIServerImage() throws RemoteException {
		super();

	}

	public static JAIServerImage getInstance() throws RemoteException {
		if (si == null) {
			synchronized (JAIServerImage.class) {
				if (si == null)
					si = new JAIServerImage();
			}
		}
		return si;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.cms.image.ImageInterface#genThumbnail(com.hexun.cms.image.ThumbnailInfo)
	 */
	public boolean genThumbnail(String srcFilePath, int thumbWidth,
			int thumbHeight, boolean keepProportion) throws RemoteException {
		if (log.isInfoEnabled()) {
			log.info("genThumbnail src=" + srcFilePath + " width=" + thumbWidth
					+ " height=" + thumbHeight + " keep proportion="
					+ keepProportion);
		}

		JAIImageToolkit jt = new JAIImageToolkit();
		jt.setImageSrcBytes(readImage(srcFilePath));
		jt.readImage();
		String thumbFilePath = new ImageRule()
				.getThumbnailFileName(srcFilePath);

		try {
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
		} catch (Exception we) {
			String estr = "Generate thumbnail file " + thumbFilePath
					+ " failed " + we;
			log.error(estr, we);
			return false;
		}
		if (log.isInfoEnabled()) {
			log.info("Generate thumbnail file from " + srcFilePath + " to "
					+ thumbFilePath);
		}
		return true;
	}

	public boolean genWatermarkImageT(TextWatermarkInfo info)
			throws RemoteException {

		String srcFilePath = info.getSrcFilePath();
		WatermarkOpImpl wop = new WatermarkOpImpl();
		wop.jais = this;
		wop.text = info.getMarkText();
		wop.offsetX = info.getOffsetX();
		wop.offsetY = info.getOffsetY();
		wop.placement = info.getPlacement();
		wop.color = info.getFillColor();
		wop.font = new Font(info.getFontName(), Font.BOLD, info.getFontSize());
		try {
			JAIImageToolkit jt = new JAIImageToolkit();
			jt.setImageSrcBytes(readImage(srcFilePath));
			jt.readImage();
			jt.addWatermarkText(wop, false);
			String destFilePath = new ImageRule()
					.getWatermarkFileName(srcFilePath);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jt.writeToStream(out);
			out.close();
			// for test
			// jt.writeImageTo(wop.placement + "_" + destFilePath);
			writeImage(out.toByteArray(), destFilePath);
			jt.release();
		} catch (Throwable te) {
			if (log.isErrorEnabled()) {
				log.error("text water error", te);
			}
			return false;
		}
		return true;
	}

	public boolean genWatermarkImageG(String srcFilePath, String markFilePath,
			int placement, int offsetX, int offsetY) throws RemoteException {
		if (log.isDebugEnabled()) {
			log.debug("genWatermarkImageG src=" + srcFilePath + "markFilePath="
					+ markFilePath + " placement=" + placement + " offsetX="
					+ offsetX + " offsetY=" + offsetY);
		}

		String destFilePath = new ImageRule().getWatermarkFileName(srcFilePath);
		WatermarkImgOpImpl op = new WatermarkImgOpImpl();
		op.offsetX = offsetX;
		op.jais = this;
		op.offsetY = offsetY;
		op.placement = placement;
		op.markImage = readImage(markFilePath);

		JAIImageToolkit jt = new JAIImageToolkit();
		jt.setImageSrcBytes(readImage(srcFilePath));
		try {
			jt.readImage();
			jt.addWatermarkImage(op, false);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jt.writeToStream(out);
			out.close();
			// for test:
			// jt.writeImageTo(op.placement + "_" + destFilePath);
			writeImage(out.toByteArray(), destFilePath);
		} catch (Exception re) {
			String estr = "Add water mark for source image file " + srcFilePath
					+ " failed.";
			log.error(estr, re);
			return false;
		}

		if (log.isInfoEnabled()) {
			log.info("Generate watermark file from " + srcFilePath + " to "
					+ destFilePath);
		}
		return true;
	}

	public Dimension getDimension(String file) throws RemoteException {
		try {
			JAIImageToolkit jt = new JAIImageToolkit();
			jt.setImageSrc(file);
			return new Dimension(jt.getSrcWidth(), jt.getSrcHeight());
		} catch (Exception e) {
			return new Dimension(-1, -1);
		}
	}

	private void writeImage(byte[] data, String imagePath)
			throws RemoteException, Exception {
		ServerFile.getInstance().write(data, imagePath);
	}

	Dimension calImgPostion(int placement, int offsetX, int offsetY,
			int sWidth, int sHeight, int lWidth, int lHeight) {

		int posX = offsetX;
		int posY = offsetY;

		if (placement == Placement.EastGravity
				|| placement == Placement.CenterGravity
				|| placement == Placement.WestGravity)
			posY = lHeight / 2 - sHeight / 2 + offsetY;
		if (placement == Placement.SouthEastGravity
				|| placement == Placement.SouthGravity
				|| placement == Placement.SouthWestGravity)
			posY = lHeight - sHeight - offsetY;

		if (placement == Placement.SouthGravity
				|| placement == Placement.CenterGravity
				|| placement == Placement.NorthGravity)
			posX = lWidth / 2 - sWidth / 2 + offsetX;

		if (placement == Placement.NorthEastGravity
				|| placement == Placement.EastGravity
				|| placement == Placement.SouthEastGravity)
			posX = lWidth - sWidth - offsetX;

		return new Dimension(posX, posY);
	}

	Dimension calTextPostion(int placement, int offsetX, int offsetY,
			int sWidth, int sHeight, int lWidth, int lHeight, int maxAscent) {

		int posX = offsetX;
		int posY = offsetY;

		if (placement == Placement.EastGravity
				|| placement == Placement.CenterGravity
				|| placement == Placement.WestGravity)
			posY = lHeight / 2 - sHeight / 2 + offsetY;

		if (placement == Placement.SouthEastGravity
				|| placement == Placement.SouthGravity
				|| placement == Placement.SouthWestGravity)
			posY = lHeight - sHeight - offsetY + maxAscent;

		if (placement == Placement.NorthEastGravity
				|| placement == Placement.NorthGravity
				|| placement == Placement.NorthWestGravity) {
			posY = offsetY + maxAscent;
		}

		if (placement == Placement.SouthGravity
				|| placement == Placement.CenterGravity
				|| placement == Placement.NorthGravity)
			posX = lWidth / 2 - sWidth / 2 + offsetX;

		if (placement == Placement.NorthEastGravity
				|| placement == Placement.EastGravity
				|| placement == Placement.SouthEastGravity)
			posX = lWidth - sWidth - offsetX;

		return new Dimension(posX, posY);
	}

	private byte[] readImage(String imagePath) {
		byte[] srcData = null;
		try {
			srcData = ServerFile.getInstance().read(imagePath).getBytes();
		} catch (Throwable t) {
			throw new RuntimeException("read image error.", t);
		}
		return srcData;
	}

	/*
	 * Just For Test
	 */
	public static void main(String[] args) throws RemoteException {
		JAIServerImage si = JAIServerImage.getInstance();
		String srcFilePath = "Img242912706.jpg";
		String srcImg = "1104d46c765.jpg";
		String srclogo = "logo.gif";

		TextWatermarkInfo info = new TextWatermarkInfo(srcFilePath,
				"www.pusuo.net");
		info.setFontName("");
		info.setFontSize(20);
		info.setPlacement(Placement.NorthGravity);
		info.setFillColor("gray");
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.EastGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.NorthEastGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.NorthWestGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.SouthEastGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.SouthGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.SouthWestGravity);
		si.genWatermarkImageT(info);

		info.setPlacement(Placement.WestGravity);
		si.genWatermarkImageT(info);

		// add Image mark
		si.genWatermarkImageG(srcImg, srclogo, Placement.NorthGravity, 2, 2);
		si.genWatermarkImageG(srcImg, srclogo, Placement.EastGravity, 2, 2);
		si
				.genWatermarkImageG(srcImg, srclogo,
						Placement.NorthEastGravity, 2, 2);
		si
				.genWatermarkImageG(srcImg, srclogo,
						Placement.NorthWestGravity, 2, 2);
		si
				.genWatermarkImageG(srcImg, srclogo,
						Placement.SouthEastGravity, 2, 2);
		si.genWatermarkImageG(srcImg, srclogo, Placement.SouthGravity, 2, 2);
		si
				.genWatermarkImageG(srcImg, srclogo,
						Placement.SouthWestGravity, 2, 2);
		si.genWatermarkImageG(srcImg, srclogo, Placement.WestGravity, 2, 2);
		System.exit(0);
	}
}

class WatermarkImgOpImpl implements WatermarkImgOp {
	byte[] markImage = null;

	int offsetX;

	int offsetY;

	int placement;

	float alapha = 0.5f;

	JAIServerImage jais = null;

	public Dimension calImagePostition(int markW, int markH, int imgW, int imgH) {
		return jais.calImgPostion(placement, offsetX, offsetY, markW, markH,
				imgW, imgH);

	}

	public float getAlapha() {
		return alapha;
	}

	public byte[] getMarkImage() {
		return markImage;
	}

}

class WatermarkOpImpl implements WatermarkTextOp {
	Font font;

	String text;

	String color;

	int offsetX;

	int offsetY;

	int placement;

	int lWidth;

	int lHeight;

	int sWidth;

	int sHeight;

	JAIServerImage jais = null;

	public Dimension calTextPostition(Graphics2D gc) {
		FontMetrics fm = gc.getFontMetrics();
		Rectangle2D rec = fm.getStringBounds(text, gc);
		sWidth = (int) rec.getWidth();
		sHeight = (int) rec.getHeight();
		return jais.calTextPostion(placement, offsetX, offsetY, sWidth,
				sHeight, lWidth, lHeight, fm.getMaxAscent());
	}

	public Font getFont() {
		return font;
	}

	public float getAlapha() {
		return 0.9f;
	}

	public String getText() {
		return text;
	}

	public void setImageH(int height) {
		this.lHeight = height;

	}

	public void setImageW(int width) {
		this.lWidth = width;

	}

	public String getColor() {
		return color;
	}
}
