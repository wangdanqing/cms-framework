package net.pusuo.cms.client.util;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ʵ�ֵ���sogou�ִʽӿ�
 * 
 * @author agilewang
 */
public class CmsWordSegmentor {

	private static final Log log = LogFactory.getLog(CmsWordSegmentor.class);

	/**
	 * ����ӿ��Ƿ����
	 */
	public static boolean isAvailiable = true;
	static {
		try {
			System.loadLibrary("CmsWord");
		} catch (Throwable te) {
			if (log.isErrorEnabled()) {
				log.error("load CmsWord error.", te);
			}
			isAvailiable = false;
		}
	}

	public static String[] getTag(String content, int count) {
		byte[] result = null;
		byte[] gbkdata = null;
		try {
			gbkdata = content.getBytes("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			gbkdata = content.getBytes();
		}

		result = new byte[gbkdata.length + 2];
		System.arraycopy(gbkdata, 0, result, 0, gbkdata.length);

		long st0 = System.currentTimeMillis();
		byte[] tags = getTag(result, count);
		long et0 = System.currentTimeMillis();
		if (log.isInfoEnabled()) {
			log.info("call native getTag time:" + (et0 - st0) + " ms");
		}
		List tagResult = new ArrayList();
		int start = 0;
		for (int i = 0; i < tags.length; ++i) {
			if (tags[i] == 0) {
				try {
					String tag = new String(tags, start, i - start, "UTF-8");
					tagResult.add(tag);
					start = i + 1;
				} catch (java.io.UnsupportedEncodingException e) {
				}
			}
		}
		String[] results = new String[tagResult.size()];
		Object[] tagResults = tagResult.toArray();
		System.arraycopy(tagResults, 0, results, 0, results.length);
		return results;
	}

	private static native byte[] getTag(byte[] data, int count);

}
