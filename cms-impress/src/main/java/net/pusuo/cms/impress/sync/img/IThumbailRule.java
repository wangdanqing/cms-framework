package net.pusuo.cms.impress.sync.img;



public interface IThumbailRule {

	/**
	 * 处理图片
	 * 
	 * @param mi
	 * @return 返回的MagickImage需要由调用者释放
	 * @throws magick.MagickException
	 */
	public abstract boolean process(ImageToolkit mi);

	public abstract String getThumbnailPath(String srcFilePath);

	public abstract String getSuffix();

	public abstract void setSuffix(String suffix);

	public abstract int getThumbHeight();

	public abstract void setThumbHeight(int thumbHeight);

	public abstract int getThumbWidth();

	public abstract void setThumbWidth(int thumbWidth);
}