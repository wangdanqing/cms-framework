package net.pusuo.cms.client.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommendReadUtil {

	private static final Log log = LogFactory.getLog(CommendReadUtil.class);

	/**
	 * 处理推荐阅读 modify by shijinkui 08-09-26
	 * 
	 */
	public static String getCommendRead(String content, String channel) {
		String[] _channel = channel.split(";");
		String adContentAll = "<!--#include virtual=\"/commonfrag/" + _channel[0].toString()
				+ "/channel_commend.inc\"-->";
		long t = ct();
	
		StringBuffer copycontent = new StringBuffer(content.toLowerCase());
		// 奥运期间统一推广
		// String adContentAll = " <!--#include
		// virtual=\"/commonfrag/hexun_tmp.inc\" -->";
		int len1 = getRealCharLength(copycontent.toString());
		if (len1 <= 300) {
			// content = content + adContentAll;
		} else {
			int pos = getCommendReadPosition(copycontent.toString(), 150);
			if (pos != -1) {
				content = content.substring(0, pos+1) + adContentAll + content.substring(pos+1);
			}
		}
		t = ct() - t;
		log.info("推荐阅读耗时: " + t);
		return content;
	}

	/**
	 * 
	 * @param content
	 *            文章内容
	 * @param lLimit
	 *            前面空多少个内容
	 * @return
	 */
	private static int getCommendReadPosition(String content, int lLimit) {
		try {
			StringBuffer cc = new StringBuffer(content);
			int pos = 0, chars = 0;

			boolean t1 = false, t2 = false;
			for (pos = 0; pos < cc.length(); pos++) {

				if (cc.charAt(pos) == '<')
					t1 = true;
				if (cc.charAt(pos) == '>')
					t2 = true;
				// 标签内的内容
				if (t1 && !t2) {
					continue;
				}
				// 标签外的内容
				if (!t1 && !t2) {
					chars++;
				}

				// 标签闭合
				if (t1 && t2) {
					t1 = false;
					t2 = false;
				}

				if (pos > cc.length() - 100) {
					pos = -1;
					break;
				}

				// 当非HTML标签的字符大于150时，才处理推荐阅读（针对编辑在新闻最前面添加的推荐内容）
				if (chars > lLimit) { // fixed by shijinkui
					String tag = cc.substring(pos + 1, pos + 3);
				if (tag.contains("<p") ||tag.contains("<br") || tag.contains(" ")) {//modify by shijinkui <br and " 
						String remain = cc.substring(pos + 1);// 截取剩余的内容
						if (getRealCharLength(remain) < 200) {// 如果剩下的字符不够200个,
							pos = -1;
							break;
						}

						// 查找后面有没有table等标签，找到这些标签的位置，并算出间隔的非标签字符数，来界定
						// 查找img,table标签距当前位置是否足够200个非标签字符
						String[] tags = new String[] { "<img", "<table", "<tr", "<td", "<o:p" };

						boolean badloc = false;
						for (int j = 0; j < tags.length; j++) {
							int loc = remain.indexOf(tags[j]);
							if (loc < 0)
								continue;
							String sub = remain.substring(0, loc);
							if (getRealCharLength(sub) < 200) {
								badloc = true;
								break;
							}
						}

						if (badloc)
							continue;

						// 找到啦
						break;
					}
				}
			}

			return pos;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static int getRealCharLength(String content) {
		String result = content.replaceAll("<[^>]*>", "");
		return result.length();
	}

	private static final long ct() {
		return System.currentTimeMillis();
	}
}
