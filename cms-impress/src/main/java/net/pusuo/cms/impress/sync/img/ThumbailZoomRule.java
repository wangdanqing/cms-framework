package net.pusuo.cms.impress.sync.img;

public class ThumbailZoomRule extends AbstractThumbailRule {

	boolean keepProportion = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.slideshow.sync.img.IThumbailRule#process(magick.MagickImage)
	 */
	public boolean process(ImageToolkit mi) {
		int wh[] = scale(mi.getSrcWidth(), mi.getSrcHeight());
		return mi.scaleTo(wh[0], wh[1], false);
	}

	public int[] scale(final int width, final int height) {
		int[] widthAndhight = new int[2];
		if (width < this.thumbWidth && height < this.thumbHeight) {
			// 如果原图的长宽均比期望值小，则不再压缩
			widthAndhight[0] = width;
			widthAndhight[1] = height;
			return widthAndhight;
		}

		if (keepProportion) {
			int srcWidth = width;
			int srcHeight = height;
			double xRatio = ((double) thumbWidth) / srcWidth;
			double yRatio = ((double) thumbHeight) / srcHeight;
			double factor = Math.min(xRatio, yRatio);
			widthAndhight[0] = (int) (factor * srcWidth);
			widthAndhight[1] = (int) (factor * srcHeight);
		}
		return widthAndhight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.slideshow.sync.img.IThumbailRule#isKeepProportion()
	 */
	public boolean isKeepProportion() {
		return keepProportion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.slideshow.sync.img.IThumbailRule#setKeepProportion(boolean)
	 */
	public void setKeepProportion(boolean keepProportion) {
		this.keepProportion = keepProportion;
	}

}
