package net.pusuo.cms.client.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.hexun.cms.core.News;

public class NewsDiffUtil {

	private static final Log log = LogFactory.getLog(NewsDiffUtil.class);

	public static Map diffMap(News oldNews, News newNews) {
		Map rtMap = new HashMap();
		String regTime = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{1,3}$";
		Pattern p = Pattern.compile(regTime);
		try {
			Map oldMap = BeanUtils.describe(oldNews);
			Map newMap = BeanUtils.describe(newNews);

			for (Iterator it = oldMap.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				// �Թ�ıȽ���
				if(key.equalsIgnoreCase("content"))
					continue;
				Object oldValue = oldMap.get(key);
				Object newValue = newMap.get(key);
				if (oldValue == null)
					oldValue = "";
				if (newValue == null)
					newValue = "";
				oldValue = oldValue.toString().trim();
				newValue = newValue.toString().trim();
				Matcher m = p.matcher(oldValue.toString());
				boolean find = m.find();
				Matcher m2 = p.matcher(newValue.toString());
				boolean find2 = m2.find();
				if (find && find2) {
					oldValue = oldValue.toString().substring(0, 19);
					newValue = newValue.toString().substring(0, 19);
				}

				if (!oldValue.equals(newValue))
					rtMap.put(key, key);
			}
		} catch (Exception e) {
			log.error("ERROR - Diff Properties: " + e);
		}
		return rtMap;
	}

	public static String getXMl(News news) {
		try {
			Element root = DocumentFactory.getInstance().createElement("news");
			Document doc = DocumentFactory.getInstance().createDocument(root);
		//	doc.setXMLEncoding("UTF-8");

			Map ref = BeanUtils.describe(news);

			for (Iterator it = ref.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (ref.get(key) != null)
					root.addElement(key).addCDATA((String) ref.get(key));
				else
					root.addElement(key).addCDATA("");
			}

			return doc.asXML();
		} catch (Exception e) {
			log.error("ERROR - create Dom4j Document: " + e);
			return "";
		}
	}
}
