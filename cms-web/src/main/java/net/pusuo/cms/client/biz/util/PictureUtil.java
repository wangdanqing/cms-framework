/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.regex.Pattern;

import com.hexun.cms.client.biz.PictureManager;

/**
 * @author Alfred.Yuan
 *
 */
public class PictureUtil {
	
	public static final Pattern patternExt = Pattern.compile(
			PictureManager.HEXUN_PICTURE_REGEX_EXT, 
			Pattern.CASE_INSENSITIVE+Pattern.DOTALL );

	public static boolean validateExt(String ext) {
		
		if (ext == null || ext.trim().length() == 0)
			return false;
		
		return patternExt.matcher(ext).matches();
	}
}
