/*
 * 
 * @author chenqj
 * Created on 2004-8-20
 *
 */
package net.pusuo.cms.server.image;

import java.io.Serializable;

/**
 * @author chenqj
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public final class TextWatermarkInfo implements Serializable {

    private String srcFilePath = "";

    private String markText = "WWW.CAIJING.COM.CN";

    private int offsetX = 5;

    private int offsetY = 2;

    private int placement = Placement.SouthEastGravity;

    private String fontName = "fixed";

    private int fontSize = 18;

    private String fillColor = "white";

    /**
     * @param srcFilePath
     * @param markText
     */
    public TextWatermarkInfo(String srcFilePath, String markText) {
        this.srcFilePath = srcFilePath;
        this.markText = markText;
    }

    /**
     * @param srcFilePath
     * @param markText
     * @param fontName
     * @param fontSize
     * @param fillColor
     */
    public TextWatermarkInfo(String srcFilePath, String markText,
                             String fontName, int fontSize, String fillColor) {
        super();
        this.srcFilePath = srcFilePath;
        this.markText = markText;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.fillColor = fillColor;
    }

    /**
     * @param srcFilePath
     * @param markText
     * @param offsetX
     * @param offsetY
     * @param placement
     * @param fontName
     * @param fontSize
     * @param fillColor
     */
    public TextWatermarkInfo(String srcFilePath, String markText,
                             int offsetX, int offsetY, int placement,
                             String fontName, int fontSize, String fillColor) {
        this.srcFilePath = srcFilePath;
        this.markText = markText;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.placement = placement;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.fillColor = fillColor;
    }

    /**
     * @return Returns the fillColor.
     */
    public String getFillColor() {
        return fillColor;
    }

    /**
     * @param fillColor The fillColor to set.
     */
    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @return Returns the fontName.
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * @param fontName The fontName to set.
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    /**
     * @return Returns the fontSize.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize The fontSize to set.
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return Returns the markText.
     */
    public String getMarkText() {
        return markText;
    }

    /**
     * @param markText The markText to set.
     */
    public void setMarkText(String markText) {
        this.markText = markText;
    }

    /**
     * @return Returns the offsetX.
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * @param offsetX The offsetX to set.
     */
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * @return Returns the offsetY.
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * @param offsetY The offsetY to set.
     */
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * @return Returns the placement.
     */
    public int getPlacement() {
        return placement;
    }

    /**
     * @param placement The placement to set.
     */
    public void setPlacement(int placement) {
        this.placement = placement;
    }

    /**
     * @return Returns the srcFilePath.
     */
    public String getSrcFilePath() {
        return srcFilePath;
    }

    /**
     * @param srcFilePath The srcFilePath to set.
     */
    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }
}
