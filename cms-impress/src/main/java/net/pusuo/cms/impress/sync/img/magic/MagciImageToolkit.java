package net.pusuo.cms.impress.sync.img.magic;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import net.pusuo.cms.impress.io.FileUtil;
import net.pusuo.cms.impress.sync.img.ImageToolkit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;


/**
 * 基于magic实现的ImageToolkit
 *
 * @author agilewang
 */
public class MagciImageToolkit implements ImageToolkit {
    private static final Log log = LogFactory.getLog(MagciImageToolkit.class);

    private String src = null;

    protected int imgMissTimeOut = 2000;

    protected MagickImage mi = null;

    protected MagickImage destMi = null;

    public boolean readImage() {
        if (this.src == null) {
            throw new IllegalArgumentException(
                    "The image file src is excepted.");
        }
        if (this.mi != null) {
            throw new IllegalStateException("Image already loaded.");
        }

        byte[] srcData = FileUtil.read(src);
        if (srcData == null) {
            if (log.isErrorEnabled()) {
                log.error(String.format(
                        "Read image <%s> not exit,sleep %d ms.. ", src,
                        imgMissTimeOut));
            }
            try {
                Thread.sleep(imgMissTimeOut);
                srcData = FileUtil.read(src);
            } catch (InterruptedException e1) {

            }
        }

        log.info("read file " + src + " " + (srcData == null));
        if (srcData == null) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Read image <%s> is null ", src));
            }
            return false;
        }
        log.info("read file " + src + " success");
        boolean result = false;
        try {
            mi = new MagickImage(new ImageInfo(src), srcData);
            result = true;
        } catch (MagickException e) {
            e.printStackTrace();
            if (log.isErrorEnabled()) {
                log.error("Create MagickImage for image:" + src + " cause"
                        + e.getCause(), e);
            }
        }
        return result;
    }

    public boolean scaleTo(int width, int height, boolean isSeq) {
        optCheck();
        MagickImage src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }

        MagickImage oldDestMi = destMi;
        boolean result = false;
        try {
            destMi = src.scaleImage(width, height);
            result = true;
        } catch (MagickException me) {
            if (log.isErrorEnabled()) {
                log.error("scale error.", me);
            }
            result = false;
        }
        if (oldDestMi != null) {
            oldDestMi.destroyImages();
            oldDestMi = null;
        }
        return result;
    }

    public boolean crop(int x, int y, int width, int height, boolean isSeq) {
        optCheck();
        MagickImage src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }
        boolean result = false;
        Rectangle rect = new Rectangle(x, y, width, height);
        MagickImage oldDestMi = destMi;
        try {
            destMi = src.cropImage(rect);
            result = true;
        } catch (MagickException e) {
            if (log.isErrorEnabled()) {
                log.error("scale crop.", e);
            }
            result = false;
        }
        if (oldDestMi != null) {
            oldDestMi.destroyImages();
            oldDestMi = null;
        }
        return result;
    }

    public boolean writeImageTo(String file) {
        if (this.destMi == null) {
            throw new IllegalStateException("Image not processed.");
        }
        if (file == null) {
            throw new IllegalArgumentException("No file to be writed.");
        }
        try {
            byte[] scaledData = destMi.imageToBlob(new ImageInfo());
            FileUtil.write(scaledData, file);
            return true;
        } catch (Throwable te) {
            if (log.isErrorEnabled()) {
                log.error("Write to file " + file + " error", te);
            }
        }
        return false;
    }

    public String getImageSrc() {
        return this.src;
    }

    public int getImgMissTimeOut() {
        return this.imgMissTimeOut;
    }

    private void optCheck() {
        if (this.mi == null) {
            throw new IllegalStateException("Image not loaded.");
        }
    }

    public void release() {
        if (this.mi != null) {
            this.mi.destroyImages();
            this.mi = null;
        }
        if (this.destMi != null) {
            this.destMi.destroyImages();
            this.destMi = null;
        }
    }

    public void setImageSrc(String src) {
        this.src = src;
    }

    public void setImgMissTimeOut(int imgMissTimeOut) {
        this.imgMissTimeOut = imgMissTimeOut;
    }

    public int getSrcHeight() {
        try {
            return (int) this.mi.getDimension().getHeight();
        } catch (MagickException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSrcWidth() {
        try {
            return (int) this.mi.getDimension().getWidth();
        } catch (MagickException e) {
            throw new RuntimeException(e);
        }
    }

}
