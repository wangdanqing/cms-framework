package net.pusuo.cms.impress.sync.img;

import java.util.List;

/**
 * 常用的压缩图片的工具
 *
 * @author agilewang
 */
public abstract class AbstractImageUtil implements ImageUtil {
    protected int imgMissTimeOut = 2000;

    protected List<IThumbailRule> rules = null;

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.sync.img.magic.ImageUtil#getImgMissTimeOut()
      */
    public int getImgMissTimeOut() {
        return imgMissTimeOut;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.sync.img.magic.ImageUtil#setImgMissTimeOut(int)
      */
    public void setImgMissTimeOut(int imgMissTimeOut) {
        this.imgMissTimeOut = imgMissTimeOut;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.sync.img.magic.ImageUtil#getRules()
      */
    public List<IThumbailRule> getRules() {
        return rules;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.sync.img.magic.ImageUtil#setRules(java.util.List)
      */
    public void setRules(List<IThumbailRule> rules) {
        this.rules = rules;
    }
}
