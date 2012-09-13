package net.pusuo.cms.client.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.core.News;

/**
 * @author shijinkui recompile news accroding ids files.
 */
public class RecompileNews {
	private static Log log = LogFactory.getLog(RecompileNews.class);

	public static void run() {
		String[] files = null ;
		String ids = "";
		ClientFile cf = null;
		try {
			cf = ClientFile.getInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		files = cf.getFileList("/refreshnews/");
		log.info("get file lists: " + files);

		for(int j = 0; files != null && j < files.length; j++){
			String file = cf.read("/refreshnews/"+files[j]);
			log.info("read file: " + files[j]);
			if (file == null || file.equals(""))continue;
			List list = new ArrayList();
			
			String str[] = file.split(";");
			for (int i = 0; i < str.length; i++) {
				News item = (News) ItemManager.getInstance().get(new Integer(str[i].trim()), News.class);
				if (item == null || item.equals(""))
					continue;
				list.add(item);
			}
			
			if(list == null || list.size()<1)continue;
				
			ViewContext context = new ViewContext();
			context.put("newsList", list);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			context.put("formatter", formatter);
			String content = ViewEngine.getViewManager().getContent("syncdata/listcache/sitemap.vm", context);
			if (content != null || content.trim().length() > 0) {
				try {
					ClientFile.getInstance().write(content, "/refreshnews/" + files[j] + ".xml");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

	}
}
