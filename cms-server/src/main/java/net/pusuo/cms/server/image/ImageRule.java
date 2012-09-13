package net.pusuo.cms.server.image;

import net.pusuo.cms.server.util.Util;

import java.util.regex.Pattern;

public class ImageRule {

    /**
     * 定义缩略图和水印图片的格式
     */
    public static final String HEXUN_PICTURE_REGEX_THUM_WATER = ".+\\.jpe?g";

    /**
     * 定义缩略图和水印图片后缀
     */
    public static final String HEXUN_PICTURE_THUM_WATER_SUFF = ".jpg";

    public static final Pattern patternThumWater = Pattern.compile(
            HEXUN_PICTURE_REGEX_THUM_WATER, Pattern.CASE_INSENSITIVE
            + Pattern.DOTALL + Pattern.MULTILINE);

    private static final String THUMB_PREFIX = "s_";

    private static final String MARK_PREFIX = "m_";

    private boolean jpeg = true;

    public String getThumbnailUrl(String imageUrl) {
        imageUrl = getThumWaterName(imageUrl);
        return Util.addFilePrefix(imageUrl, THUMB_PREFIX);
    }

    public String getWatermarkUrl(String imageUrl) {
        imageUrl = getThumWaterName(imageUrl);
        return Util.addFilePrefix(imageUrl, MARK_PREFIX);

    }

    public String getThumbnailFileName(String imageFileName) {
        imageFileName = getThumWaterName(imageFileName);
        return Util.addFilePrefix(imageFileName, THUMB_PREFIX);
    }

    public String getWatermarkFileName(String imageFileName) {
        imageFileName = getThumWaterName(imageFileName);
        return Util.addFilePrefix(imageFileName, MARK_PREFIX);

    }

    /**
     * 取得水印和缩略图片的存储路径
     *
     * @param fileName
     * @return
     */
    protected String getThumWaterName(String fileName) {
        if (this.jpeg) {
            // 使用jpeg格式生成水印和缩略图
            if (!patternThumWater.matcher(fileName).matches()) {
                int i = fileName.lastIndexOf(".");
                if (i > 0) {
                    fileName = fileName.substring(0, i)
                            + HEXUN_PICTURE_THUM_WATER_SUFF;
                }
            }
        }
        return fileName;
    }

    public boolean isJpeg() {
        return jpeg;
    }

    public void setJpeg(boolean jpeg) {
        this.jpeg = jpeg;
    }
}
