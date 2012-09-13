/*
 * Created on 2006-2-16
 * 
 */
package net.pusuo.cms.client.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author agilewang
 */
public class PropertyUtil {

	/**
	 * @param prop
	 * @param content
	 * @throws java.io.IOException
	 */
	public static void propertiesFromString(Properties prop, String content)
			throws IOException {
		if (content != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(content
					.getBytes());
			prop.load(bis);
			bis.close();
			bis = null;
		}
	}

	/**
	 * @param metaProp
	 * @param content
	 * @return
	 */
	public static String stringToProperties(Properties metaProp)
			throws IOException {
		String content = null;
		java.io.OutputStream out = new java.io.ByteArrayOutputStream();
		metaProp.store(out, "");
		out.close();
		content = out.toString();
		return content;
	}
}
