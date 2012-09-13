/**
 * 
 */
package net.pusuo.cms.client.xport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.hexun.cms.client.file.ClientFile;

public class XportUtil {

	private static Log log = LogFactory.getLog(XportUtil.class);
	private static XportUtil instance = null;
	private static Object lock = new Object();

	
    public static XportUtil getInstance() {
        
        if(instance == null) {
            synchronized(lock) {
                if(null == instance) {
                    instance = new XportUtil();
                }
            }
        }
        return instance;
    }

	
	public List parseXmlToBean() {
		String xf = null;
		List list = new ArrayList();
		try {
			xf = ClientFile.getInstance().read("/cmsdata/xport_dbconfig.xml");
			//log.info(xf);
			Document doc = DocumentHelper.parseText(xf.trim());
			Element root = doc.getRootElement();

			List nodeList = root.selectNodes("/root/item");
			log.info("xml node: " + nodeList.size());
			for (int j = 0; nodeList != null && j < nodeList.size(); j++) {

				Node parent = (Node) nodeList.get(j);
				String id = ((Node) parent.selectNodes("id").get(0)).getText();
				log.info("id:" + id);
				if (StringUtils.isEmpty(id))
					break;
				
				String pid = ((Node) parent.selectNodes("pid").get(0)).getText();
				String mediaId = ((Node) parent.selectNodes("mediaId").get(0)).getText();
				String newsPrio = ((Node) parent.selectNodes("newsPriority").get(0)).getText();
				String templateId = ((Node) parent.selectNodes("templateId").get(0)).getText();
				String sql = ((Node) parent.selectNodes("sql").get(0)).getText();
				SrcDbBean bean = new SrcDbBean();
				bean.setId(id);
				bean.setMediaId(mediaId);
				bean.setNewsPriority(newsPrio);
				bean.setTemplateId(templateId);
				bean.setPid(pid);
				bean.setSql(sql);
				list.add(bean);
			}

			if (nodeList.size() != list.size())
			{
				list = null;
				throw new Exception();
			}
			 

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 解析源为xml的信息到Bean
	 */
	public List parseRssToXmlBean() {
		String xf = null;
		List list = new ArrayList();
		try {
			xf = ClientFile.getInstance().read("/cmsdata/xport_rss_config.xml");
			Document doc = DocumentHelper.parseText(xf.trim());
			Element root = doc.getRootElement();

			List nodeList = root.selectNodes("/root/item");
			log.info("rss node: " + nodeList.size());
			for (int j = 0; nodeList != null && j < nodeList.size(); j++) {

				Node parent = (Node) nodeList.get(j);
				String id = ((Node) parent.selectNodes("id").get(0)).getText();
				if (StringUtils.isEmpty(id))
					break;
				
				String pid = ((Node) parent.selectNodes("pid").get(0)).getText();
				String mediaId = ((Node) parent.selectNodes("mediaId").get(0)).getText();
				String newsPrio = ((Node) parent.selectNodes("newsPriority").get(0)).getText();
				String templateId = ((Node) parent.selectNodes("templateId").get(0)).getText();
				String sourceUrl = ((Node) parent.selectNodes("sourceUrl").get(0)).getText();
				String startTag = ((Node) parent.selectNodes("startTag").get(0)).getText();
				String endTag = ((Node) parent.selectNodes("endTag").get(0)).getText();
				String translate = ((Node) parent.selectNodes("translate").get(0)).getText();
				
				log.info(sourceUrl);
					
				SrcXmlBean bean = new SrcXmlBean();
				bean.setId(id);
				bean.setMediaId(mediaId);
				bean.setNewsPriority(newsPrio);
				bean.setTemplateId(templateId);
				bean.setTemplateId(templateId);
				bean.setPid(pid);
				bean.setSourceUrl(sourceUrl);
				bean.setStartTag(startTag);
				bean.setEndTag(endTag);
				bean.setTranslate(translate);
				list.add(bean);
			}

			if (nodeList.size() != list.size())
			{
				list = null;
				throw new Exception();
			}
			 

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
