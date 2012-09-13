package net.pusuo.cms.client.book;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentProcess {

	public static void main(String[] args) throws SQLException, IOException {
//		ContentProcess  cp = new ContentProcess();
//		cp.process("");
	}
	
	/**
	 * 切分正文,处理正文的图片和文字
	 * @param text
	 * @return
	 */
	public static String process(String text){
		if(text == null || text.equals(""))return "";
		return processContent2(text);
	}
	
	/**
	 * 处理方法二：
	 * 1. 硬切,字数在200-1500以内，分两段；大于2000，每段1000字
	 * 2. 段落索引和每段后面进入下一段的链接
	 * 3. 段落随机排列
	 * 时金魁  2009-12-12 
	 * egt_shijinkui@hotmail.com
	 */
	private static String processContent2(String text) {
		
		StringBuffer result = new StringBuffer();
		if(text.indexOf("<DIV id=contentIndex>")>-1)return text;
		List picList = SeparateImg(text);
		
		String cleanTxt = RemoveHTML(text);
		int txtlen = cleanTxt.length();
		int factor = 1500;
		//0. 200 - 2000字分两段，图片放到最后面
		if (txtlen < 2000 && txtlen>200)
		{
			result.append("<p id=\"content2\">　　")
				  .append(cleanTxt.substring(txtlen/2).trim())
				  .append("  ==><a href=\"#content1\">进入第1段</a><==</p>")
				  .append("<p id=\"content1\">　　")
				  .append(cleanTxt.substring(0, txtlen/2))
				  .append("  ==><a href=\"#content2\">进入第2段</a><==</p>");
			
			//图文混排 放在正文最后
			for(int k = 0; picList!=null && k < picList.size();k++)
				result.append("<div class=\"contentImg\">"+picList.get(k)+"</div>");
			return result.toString();
		}

		String genindex = "", tmp1 = "";
		result = new StringBuffer("");
		List list = new ArrayList();
		//1. 切
		for (int i = 1; i <= txtlen / factor; i++) {
			tmp1 = "<p id=\"content" + i + "\">　　" + cleanTxt.substring((i - 1) * factor, factor * i).trim();
			if (i == txtlen / factor) {
				tmp1 += "  <a href=\"#content1\">点击进入第1段</a></p>\r\n";
				genindex = "<li><a href=\"#content1\">第1段</a></li>" + genindex;
			} else {
				tmp1 += "  <a href=\"#content" + (i + 1) + "\">点击进入第" + (i + 1) + "段</a></p>\r\n";
				genindex += "<li><a href=\"#content" + (i + 1) + "\">第" + (i + 1) + "段</a></li>";
			}
			list.add(tmp1);
			tmp1 = null;
		}
		//2. 随机段落和图片
		Collections.shuffle(list);//随机排序,挺好用的方法
		for (int j = 0; j < list.size(); j++) {
			result.append(list.get(j));
			if(picList.size()>j)
				result.append("<div class=\"contentImg\">"+picList.get(j)+"</div>   ");
		}
		//3. 取出余下的图片
		if(list.size()<picList.size()){
			for(int l = picList.size()-list.size(); l < picList.size(); l++){
				result.append("<div class=\"contentImg\">"+picList.get(l)+"</div>   ");
			}
		}
		//4.
		genindex = "<div id=\"contentIndex\"><ul>" + genindex + " </ul></div>";
		result.insert(0, genindex+"   ");

		genindex = null;
		return result.toString();
	}
	
	/**
	 * 方法一：
	 * 1. 硬切,每500字为一段
	 * 2. 段落索引和每段后面进入下一段的链接
	 * 3. 段落随机排列
	 * @param bookName
	 * 时金魁  2009-12-9  
	 * egt_shijinkui@hotmail.com
	 */
	private static String processContent1(String text){
		
		StringBuffer result = new StringBuffer();
		if(text == null || text.equals(""))return "";
		if(text.indexOf("<DIV id=contentIndex>")>-1)return text;
		List picList = SeparateImg(text);
		System.out.println("pcilists: " + picList.size());
		
		String cleanTxt = RemoveHTML(text);
		int txtlen = cleanTxt.length();
		if (txtlen < 500)
		{
			result.append("<p id=\"txt11\">　　").append(cleanTxt.trim()).append("</p>");
			//图文混排 放在正文最后
			for(int k = 0; picList!=null && k < picList.size();k++)
				result.append("<div class=\"contentImg\">"+picList.get(k)+"</div>");
			return result.toString();
		}

		String genindex = "", tmp1 = "";
		result = new StringBuffer("");
		List list = new ArrayList();
		//1.
		for (int i = 1; i <= txtlen / 500; i++) {
			tmp1 = "<p id=\"content" + i + "\">　　"
					+ cleanTxt.substring((i - 1) * 500, 500 * i).trim();
			if (i == txtlen / 500) {
				tmp1 += "  <a href=\"#content1\">点击进入第1段</a></p>\r\n";
				genindex = "<li><a href=\"#content1\">第1段</a></li>" + genindex;
			} else {
				tmp1 += "  <a href=\"#content" + (i + 1) + "\">点击进入第" + (i + 1)
						+ "段</a></p>\r\n";
				genindex += "<li><a href=\"#content" + (i + 1) + "\">第"
						+ (i + 1) + "段</a></li>";
			}
			list.add(tmp1);
			tmp1 = null;
		}
		//2.
		Collections.shuffle(list);//随机排序,挺好用的方法
		for (int j = 0; j < list.size(); j++) {
			result.append(list.get(j));
		}
		//3.图文混排，第一段前一个图，其余都在正文最后
		for(int k = 0; picList!=null && k < picList.size();k++)
		{
			if(k < 3 && (txtlen / 500)>0)
				result.insert(result.indexOf("<p id=\"content1"),"<div class=\"contentImg\">"+picList.get(k)+"</div>");
			else
				result.append("<div class=\"contentImg\">"+picList.get(k)+"</div>");
		}
		//4.
		genindex = "<div id=\"contentIndex\"><ul>" + genindex + "</ul></div>";
		result.insert(0, genindex + "\r\n");

		genindex = null;
		return result.toString();
		
	}
	
	/**
	 * 提取内容中的图片链接,清除所有img是相对路径的
	 * @return
	 */
	public static List SeparateImg(String str) {
		java.util.regex.Pattern p = null; // 定义正则表达式规则
		java.util.regex.Matcher m = null; // 定义匹配对象
		
		List list = new ArrayList();
		String tmp = "";
		if (str != null && !"".equals(str)) {
			p = java.util.regex.Pattern.compile("(\\<img.*?[\\>])",java.util.regex.Pattern.CASE_INSENSITIVE);//忽略大小写
			m = p.matcher(str);
			while(m.find()){
				tmp = m.group();
				if(tmp.contains("src=") && tmp.contains("http://")){
					System.out.println(tmp);
					list.add(tmp);
					tmp = null;
				}
			}
		}
		return list;
	}
	
	
	/**
	 * RemoveHTML
	 * 
	 * @param tmpContentFirst
	 *            String
	 * @return String
	 */
	public static String RemoveHTML(String str) {
		java.util.regex.Pattern p = null; // 定义正则表达式规则
		java.util.regex.Matcher m = null; // 定义匹配对象

		String s = ""; // 定义返回字符串
		if (str != null && !"".equals(str)) {
			// 替换掉换行符，打乱代码
			p = java.util.regex.Pattern.compile("\r\n");
			m = p.matcher(str);
			s = m.replaceAll("  "); // 替换所有换行符

			// 替换掉SCRIPT，防止网页出错
			//p = java.util.regex.Pattern.compile("(\\<.*?[\\>])", java.util.regex.Pattern.CASE_INSENSITIVE);
			p = java.util.regex.Pattern.compile("(\\<(script|a|table|td|tr|span|div|font|tbody|hr|li|ul|form|style|img).*?[\\>])", java.util.regex.Pattern.CASE_INSENSITIVE);
			m = p.matcher(s);
			s = m.replaceAll(" "); // 替换所有字符和数字以外的其它字符为空

		} else {
			return "";
		}
		return s;
	}
}
