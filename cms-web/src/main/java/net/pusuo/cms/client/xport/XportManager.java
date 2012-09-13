/**
 * 
 */
package net.pusuo.cms.client.xport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hexun.cms.client.util.AllListPageSiteMap;


/**
 * @author shijinkui  090818
 *
 */
public class XportManager implements Runnable {
	
	private static Log log = LogFactory.getLog(XportManager.class);
	
	private static XportManager instance = null;
	private static Object lock = new Object();
	
	private boolean runnable = false;
	
	private XportManager() {
		
	}
	
    public static XportManager getInstance() {
        
        if(instance == null) {
            synchronized(lock) {
                if(null == instance) {
                    instance = new XportManager();
                }
            }
        }
        return instance;
    }

	public void run() {
		log.info("XportManager: thread begins!" + runnable);
		while (runnable) {
			
			try {

				//===============================================
				//==============抓取数据库的源内容入CMS==========
				//===============================================
				/*
				List list = XportUtil.getInstance().parseXmlToBean();
				ExportWorker ew = new ExportWorker();
				SrcDbBean bean = new SrcDbBean();
				List nlist = new ArrayList();
				log.info(list);
				for(int i = 0; list!=null && i< list.size(); i++)
				{
					bean = (SrcDbBean)list.get(i);
					log.info("Bean info: " + bean.getId());
					nlist = ew.exportData(bean);
					log.info("nlist: " + nlist.size());
				}
				
				ImportWorker.importData(nlist);
				*/
				//===============================================
				//============抓取Rss源入cms=====================
				//===============================================
				/*
				List rsslist = XportUtil.getInstance().parseRssToXmlBean();
				XmlWorker xw = new XmlWorker();
				SrcXmlBean xbean = new SrcXmlBean();
				List xlist = new ArrayList();
				for(int i = 0; rsslist!=null && i< rsslist.size(); i++)
				{
					xbean = (SrcXmlBean)rsslist.get(i);
					xlist = xw.exportData(xbean);
					log.info("x-----rss news list: " + xlist.size());
				}
				ImportWorker.importData(xlist);
*/
				//===============================================

				//重新编译新闻
				//com.hexun.cms.client.tool.RecompileNews.run();

				//生成列表sitemap
				AllListPageSiteMap.generateSiteMap(110);
				AllListPageSiteMap.generateSiteMap(115);
				Thread.sleep(1000 * 60 * 60 * 60 * 24);
			}
			catch (Exception e) {
				log.error("", e);
			}
		}
		
		log.info("XmportManager: thread exits!");
	}
	
	public void startXport() {
		
		if (!runnable) {
			runnable = true;
			
			new Thread(this).start();
		}
	}
	
	public void stopXport() {
		
		if (runnable) {
			runnable = false;
			
			instance = null;
		}
	}
}
