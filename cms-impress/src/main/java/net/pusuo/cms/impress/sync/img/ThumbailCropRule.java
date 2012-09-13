package net.pusuo.cms.impress.sync.img;

import java.awt.*;

public class ThumbailCropRule extends AbstractThumbailRule {

    public boolean process(ImageToolkit mi) {
        int wh[] = scale(mi.getSrcWidth(), mi.getSrcHeight());
        int zoomW = wh[0];
        int zoomH = wh[1];
        /*
           * 1.先进行缩放 2.缩放完成之后根据缩放的尺寸与目标尺寸进行对比,超出目标尺寸的部分裁掉
           */
        // if (zoomW != mi.getDimension().width|| zoomH !=
        // mi.getDimension().height) {
        mi.scaleTo(zoomW, zoomH, false);
        Rectangle rectangle = null;
        if (zoomW > this.thumbWidth) {
            // 宽图,从图片的中间裁剪
            int zoomX = (zoomW - this.thumbWidth) / 2;
            rectangle = new Rectangle(zoomX, 0, this.thumbWidth, Math.min(
                    zoomH, this.thumbHeight));
        } else if (zoomH > this.thumbHeight) {
            // 高图,从最上方裁剪
            rectangle = new Rectangle(0, 0, Math.min(this.thumbWidth, zoomW),
                    this.thumbHeight);
        }
        if (rectangle != null) {
            return mi.crop(rectangle.x, rectangle.y, rectangle.width,
                    rectangle.height, true);

        }
        return true;
    }

    public int[] scale(final int width, final int height) {
        int[] widthAndhight = new int[2];
        if (width <= this.thumbWidth || height <= this.thumbHeight) {
            // 如果原图的长或宽比对应的期望值小，则不再压缩
            widthAndhight[0] = width;
            widthAndhight[1] = height;
            return widthAndhight;
        }
        int srcWidth = width;
        int srcHeight = height;
        double xRatio = ((double) thumbWidth) / srcWidth;
        double yRatio = ((double) thumbHeight) / srcHeight;
        double factor = Math.max(xRatio, yRatio);
        widthAndhight[0] = (int) (factor * srcWidth);
        widthAndhight[1] = (int) (factor * srcHeight);
        return widthAndhight;
    }

}
