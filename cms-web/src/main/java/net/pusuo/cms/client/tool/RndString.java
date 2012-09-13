package net.pusuo.cms.client.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.hexun.cms.util.Util;

/**
 * @author Wang Chao
 * 
 */
public class RndString {

	public static String TEXT = "����Ѷ�ƾ�ԭ����";

	/**
	 * ���ú�������ԭhtml���ݣ�������ź������
	 * 
	 * @param src
	 * @return
	 */
	public static String encode(String src, String url) {
		String[] classnames = { rndClassName() };
		String style = styleString(classnames);
		String rtn = src;
		String hx = hx(classnames[0], url);
		try {
			rtn = rtn.replaceAll("</p>", "</p>" + hx);
			rtn = rtn.replaceAll("</P>", "</P>" + hx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtn = style + "\n" + rtn;
		return rtn;
	}

	public static void main(String[] args) {
		String a = "<p>a</p> <p>b</p>";
		String url = "http://www.hexun.com";
		a = RndString.encode(a, url);
		System.out.println(a);
	}

	public static String hx(String classname, String url) {
		return "<a href=\"" + url + "\" class=\"" + classname + "\">"
				+ Util.unicodeToGBK(TEXT) + "</a>";
	}

	/**
	 * ����ĸ����ַ�
	 * 
	 * @return
	 */
	private static String genString() {
		StringBuffer bf = new StringBuffer();
		int len = rndInt(10, 15);
		for (int i = 0; i < len; i++) {
			char c = (char) rndInt(65, 122);
			bf.append(c);
		}
		return bf.toString();
	}

	/**
	 * ������ʽ
	 * 
	 * @param classnames
	 * @return
	 */
	private static String styleString(String[] classnames) {
		StringBuffer style = new StringBuffer("<style>");
		for (int i = 0; i < classnames.length; i++) {
			style.append("." + classnames[i] + " { display:none; } ");
		}
		style.append("</style>");
		return style.toString();
	}

	/**
	 * ��������ĸ������ǩ
	 * 
	 * @return
	 */
	private static String rndString(String[] classnames) {
		String tag = rndTagName();
		int c = rndInt(0, classnames.length - 1);
		String classname = classnames[c];
		String rtn = "<" + tag + " class=\"" + classname + "\">" + genString()
				+ "</" + tag + ">";
		return rtn;
	}

	/**
	 * ���������ʽ��
	 * 
	 * @return
	 */
	private static String rndClassName() {
		int s = 0;
		int e = 999;
		int num = rndInt(s, e);
		char ch1 = rndChar();
		char ch2 = rndChar();
		char ch3 = rndChar();
		return String.valueOf(new char[] { ch1, ch2, ch3 }) + num;
	}

	/**
	 * ��������ǩ��
	 * 
	 * @return
	 */
	private static String rndTagName() {
		String[] tags = { "font", "b", "span" };
		int t = rndInt(0, tags.length - 1);
		return tags[t];
	}

	/**
	 * �������Ӣ���ַ�
	 * 
	 * @return
	 */
	private static char rndChar() {
		int asc_A = (int) 'A';
		int asc_Z = (int) 'Z';
		int asc_a = (int) 'a';
		int asc_z = (int) 'z';
		long asc;
		while (true) {
			asc = Math.round(Math.random() * (asc_z - asc_A) + asc_A);
			if (asc <= 90 || asc >= 97)
				break;
		}
		return (char) (int) asc;
	}

	/**
	 * ��start��end֮����������
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private static int rndInt(int start, int end) {
		long asc = Math.round(Math.random() * (end - start) + start);
		return (int) asc;
	}

	public static String getContentFromResource(String path) {
		try {
			InputStream is = RndString.class.getResourceAsStream(path);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer content = new StringBuffer();
			String line;
			while (true) {
				line = br.readLine();
				if (line == null)
					break;
				content.append(line + "\r\n");
			}
			br.close();
			isr.close();
			is.close();
			return content.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
