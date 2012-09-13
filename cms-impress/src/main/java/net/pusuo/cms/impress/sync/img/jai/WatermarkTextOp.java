package net.pusuo.cms.impress.sync.img.jai;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

public interface WatermarkTextOp {
    Dimension calTextPostition(Graphics2D gc);

    Font getFont();

    float getAlapha();

    String getText();

    String getColor();

    void setImageW(int width);

    void setImageH(int height);
}
