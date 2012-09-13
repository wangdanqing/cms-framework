package net.pusuo.cms.client.xport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.util.ClientHttpFile;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.util.Util;

/**
 * @author shijinkui 090821
 * 
 */
public class XmlWorker {

	private static Log log = LogFactory.getLog(XmlWorker.class);
	public XmlWorker() {

	}

	public List exportData(SrcXmlBean bean) {
		if(bean.getId()==null || bean.getMediaId()==null || bean.getNewsPriority()==null || bean.getPid()==null || bean.getTemplateId()==null || bean.getSourceUrl()==null)
			return null;
		List result = new ArrayList();
		// parent
		EntityItem entity = (EntityItem) ItemManager.getInstance().get(
				new Integer(bean.getPid().trim()), EntityItem.class);
		if (entity == null || !(entity instanceof Subject)) {
			log.error("错误：XmlWorker类中: type of entity(id=" + bean.getPid() + ") isn`t Subject.");
			return null;
		}
		Subject subject = (Subject) entity;
		int subjectId = subject.getId();
		int subjectChannel = subject.getChannel();

		try {
			
			String rss = ClientHttpFile.wgetIfcString(bean.getSourceUrl(), 3000);
			if(StringUtils.isEmpty(rss))return null;
			
			Document doc = DocumentHelper.parseText(rss.trim());
			Element root = doc.getRootElement();

			List nodeList = root.selectNodes("/rss/channel/item");
			
			SpecXportUtil sxu = new SpecXportUtil();
			String flag = "", tmp = "";
			try {
				flag = sxu.getPropValue(
						"/opt/hexun/xport/xport_rss_lastrecords.properties",
						"id-" + bean.getId() + "-" + bean.getPid());
			} catch (Exception e) {}
			
			for (int j = 0; nodeList != null && j < nodeList.size(); j++) {
				Node parent = (Node) nodeList.get(j);
				String title = ((Node) parent.selectNodes("title").get(0)).getText();
				String link = ((Node) parent.selectNodes("link").get(0)).getText();
				String description = ((Node) parent.selectNodes("description").get(0)).getText();
				
				if(flag.equals(link))
					break;
				if(j == 0 && StringUtils.isNotEmpty(link.trim()))
					tmp = link;//记录last url
				
				if(StringUtils.isEmpty(title)||StringUtils.isEmpty(link))continue;
				String content = fetchContent(bean,link);
				if(StringUtils.isEmpty(content))continue;
				News news = CoreFactory.getInstance().createNews();
				news.setPid(subjectId);
				news.setTemplate("" + bean.getTemplateId());
				news.setEditor(90);
				news.setExt("html");
				news.setTime(new Timestamp(System.currentTimeMillis() + 1000*j));
				news.setMedia(Integer.parseInt(bean.getMediaId()));
				news.setChannel(subjectChannel);
				if(StringUtils.isEmpty(bean.getNewsPriority()))
					news.setPriority(70);
				else
					news.setPriority(Integer.parseInt(bean.getNewsPriority()));
				
				title = translate(bean.getTranslate(), title);
				news.setDesc(Util.RemoveHTML(title));
				news.setText(transCharsToHtmlTag(content));
				if(StringUtils.isNotBlank(description))
				{
					description = translate(bean.getTranslate(), description);
					news.setAbstract(description);
				}
				result.add(news);
			}
			SpecXportUtil sxu2 = new SpecXportUtil();
			if(StringUtils.isNotEmpty(tmp))
				sxu2.setPropKey("/opt/hexun/xport/xport_rss_lastrecords.properties","id-" + bean.getId() + "-" + bean.getPid(), tmp);
			
		} catch (Exception e) {
			log.info("exception  xxxxxxxxxxxx2:" + e.toString());
			e.printStackTrace();
		} finally {
		}

		return result;
	}
	
	/**
	 *	抓取内容 
	 */
	public String fetchContent(SrcXmlBean bean, String link){
		String content = "";
		
		try {
			long t1 = st();
			content = ClientHttpFile.wgetIfcString(link.trim(), 3000);
			if (content.length() < 10)
				return null;
			Pattern pattern = Pattern.compile(bean.getStartTag() + "(.+?)"+ bean.getEndTag(), Pattern.DOTALL+ Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				content = matcher.group();
			}
			content = content.replaceAll(bean.getStartTag(), "").replaceAll(bean.getEndTag(), "");
			content = content.replaceAll("</div>|<div[^>]*?>|</tbody>|<a[^>]*?>|<tbody[^>]*?>|<table[^>]*?>|<tr[^>]*?>|<td[^>]*?>|<A[^>]*?>|</a>|</A>|</td>|</tr>|</table>", " ");
			content = translate(bean.getTranslate(), content);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return content;
	}

	private long st(){
		return System.currentTimeMillis()/1000;
	}
	
	/**
	 * 翻译
	 */
	public String translate(String flag, String text){
		if(StringUtils.isEmpty(flag) || StringUtils.isEmpty(text))
			return null;
		String content = text;
		Translate.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
		switch(Integer.parseInt(flag)){
			case 1:
				try {
					content = Translate.translate(content,Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					content = Translate.translate(content,Language.CHINESE_SIMPLIFIED, Language.ENGLISH);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					content = Translate.translate(content,Language.CHINESE_SIMPLIFIED, Language.CHINESE_TRADITIONAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 4: 
				try {
					content = Translate.translate(content,Language.CHINESE_TRADITIONAL, Language.CHINESE_SIMPLIFIED);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				log.info("no need to translate.");
		}
		return content;
	}
	
	
	// //////////////////////////////////////////////////////////////////////////

	static Map replacementMap = new LinkedHashMap();
	static {
		replacementMap.put(">  ", ">");
		replacementMap.put("> ", ">");
		replacementMap.put(">\r\n\r\n", ">");
		replacementMap.put(">\r\n", ">");
		replacementMap.put("\r\n  ", "\r\n&nbsp;&nbsp;");
		replacementMap.put("\r\n\r\n", "<br><br>");
		replacementMap.put("\r\n\r\n", "<br><br>");
		replacementMap.put("\r\n", "<br><br>");
	}

	public static String transCharsToHtmlTag(String content) {

		String result = content;

		Iterator iter = replacementMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = (String) replacementMap.get(key);

			result = result.replaceAll(key, value);
		}

		return result;
	}
}
