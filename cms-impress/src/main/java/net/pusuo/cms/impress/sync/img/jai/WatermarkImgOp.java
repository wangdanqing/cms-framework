package net.pusuo.cms.impress.sync.img.jai;

import java.awt.Dimension;

/**
 * 增加图片水印的接口
 *
 * @author agilewang
 */
public interface WatermarkImgOp {
    Dimension calImagePostition(int markW, int markH, int imgW, int imgH);

    float getAlapha();

    byte[] getMarkImage();
}
