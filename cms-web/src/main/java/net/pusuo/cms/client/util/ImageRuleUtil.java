/*
 * 
 * @author chenqj
 * Created on 2004-8-27
 *
 */
package net.pusuo.cms.client.util;

import java.util.regex.Pattern;
import java.io.File;

/**
 * @author chenqj
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ImageRuleUtil {

	public static final String HEXUN_PICTURE_REGEX_THUM_WATER = ".+\\.jpe?g";

	public static final String HEXUN_PICTURE_THUM_WATER_SUFF = ".jpg";

	public static final Pattern patternThumWater = Pattern.compile(
			HEXUN_PICTURE_REGEX_THUM_WATER, Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL + Pattern.MULTILINE);

	public static final String THUMB_PREFIX = "s_";

	private boolean jpeg = true;

	public String getThumbnailFileName(String imageFileName) {
		imageFileName = getThumWaterName(imageFileName);
		return addFilePrefix(imageFileName, THUMB_PREFIX);
	}

	private String getThumWaterName(String fileName) {
		if (this.jpeg) {
			if (!patternThumWater.matcher(fileName).matches()) {
				int i = fileName.lastIndexOf(".");
				if (i > 0) {
					fileName = fileName.substring(0, i)	+ HEXUN_PICTURE_THUM_WATER_SUFF;
				}
			}
		}
		return fileName;
	}
	
	private String addFilePrefix(String origin, String prefix) {
		StringBuffer result = new StringBuffer();
		int idx = origin.lastIndexOf(File.separator);
		result.append(origin.substring(0, idx + 1))
			.append(prefix)
			.append(origin.substring(idx + 1));
		return result.toString();
	}

	public boolean isJpeg() {
		return jpeg;
	}

	public void setJpeg(boolean jpeg) {
		this.jpeg = jpeg;
	}
}
