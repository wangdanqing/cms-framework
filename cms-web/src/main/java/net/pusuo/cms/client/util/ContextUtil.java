/**
 * 
 */
package net.pusuo.cms.client.util;

/**
 * @author Alfred.Yuan
 *
 */
public class ContextUtil {
	
	private static String rootPath = null;
	private static boolean hasInit = false;

	public static String getRootPath() {
		return rootPath;
	}
	
	public static void setRootPath(String value) {
		if (!hasInit) {
			rootPath = value;
			hasInit = true;
		}
	}
	
	public static boolean hasInit() {
		return hasInit;
	}
}
