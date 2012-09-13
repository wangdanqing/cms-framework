package net.pusuo.cms.impress.sync.img;

/**
 * 对常用的图片操作进行封装
 *
 * @author agilewang
 */
public interface ImageToolkit {
    /**
     * 设置图片的来源
     *
     * @param src
     */
    public void setImageSrc(String src);

    public String getImageSrc();

    /**
     * 将图片读到内存中进行处理
     */
    public boolean readImage();

    /**
     * 将处理过的图片写入指定的文件中去
     *
     * @param file
     */
    public boolean writeImageTo(String file);

    /**
     * 缩放图片
     *
     * @return
     */
    public boolean scaleTo(int width, int heigth, boolean isSeq);

    /**
     * 剪裁图片
     *
     * @return
     */
    public boolean crop(int x, int y, int width, int height, boolean isSeq);

    public int getImgMissTimeOut();

    public void setImgMissTimeOut(int imgMissTimeOut);

    public int getSrcWidth();

    public int getSrcHeight();

    public void release();
}
