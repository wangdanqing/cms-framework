package net.pusuo.cms.client.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.view.ViewContext;
import com.hexun.cms.client.view.ViewEngine;
import com.hexun.cms.core.Channel;

/**
 * 直接从server的文件系统中读取分页列表文件
 * 
 * @author shijinkui
 */
public class AllListPageSiteMap {

	private static final Log log = LogFactory.getLog(AllListPageSiteMap.class);

	public static void generateSiteMap(int channelid) {
		if (channelid < 0)
			return;
		long t1 = System.currentTimeMillis();
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		String cdir = channel.getDir();
		// google
		String storepath = "/" + cdir + "/sitemap-glist.xml";
		String scheduler = "syncdata/listcache/google-sitemap-list.vm";
		// baidu
		String storepath2 = "/" + cdir + "/sitemap-blist.xml";
		String scheduler2 = "syncdata/listcache/baidu-sitemap-list.vm";

		try {
			List flist = new ArrayList(), list = new ArrayList();
			flist = getFileListFromDir("/" + cdir + "/", flist);
			log.info(" sitemap list file size:" + flist.size());
			if (flist == null || flist.size() == 0) {
				return;
			}
			String tf = "";
			Object to = new Object();
			for (int i = 0; i < flist.size(); i++) {
				to = flist.get(i);
				if (to != null && !to.equals("")) {
					tf = (String) to;
					tf = tf.replaceFirst("/" + cdir + "/", "");
					list.add("http://" + channel.getName() + tf);
				}
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String time = formatter.format(new Date());
			// 生成碎片内容 ///////////////////////////////////////////////////////////////
			// google start /////////////////////////////////////////////////////////////
			ViewContext context = new ViewContext();
			context.put("newsList", list);
			
			context.put("time", time);
			String content = ViewEngine.getViewManager().getContent(scheduler,
					context);
			if (content != null || content.trim().length() > 0) {
				// 同步碎片内容
				ClientFile.getInstance().write(content, storepath);
				log.info("google list sitemap generated!" + storepath);
			}
			// google end ////////////////////////////////////////////////////////////////
			// baidu start ///////////////////////////////////////////////////////////////
			ViewContext context2 = new ViewContext();
			context2.put("list", list);
			context2.put("channel", channel.getName());
			context2.put("time", time);
			String content2 = ViewEngine.getViewManager().getContent(scheduler2, context2);
			if (content2 != null && content2.trim().length() > 0) { 
				ClientFile.getInstance().write(content2, storepath2);
				log.info("google list sitemap generated!" + storepath2);
			}
			long t2 = (System.currentTimeMillis()-t1)/1000;
			
			log.info("list sitemap 耗时: " + t2);
			
			// baidu end ////////////////////////////////////////////////////////////////
		} catch (Exception e) {
			return;
		}
	}

	
	// 获得文件列表
	private static List getFileListFromDir(String path1, List flist1) {
		if (path1 == null || path1.equals(""))
			return null;
		String path = path1, fn = null, ft = "";
		List flist = flist1;
		try {
			ClientFile cf = ClientFile.getInstance();
			String str[] = cf.getFileList(path);
			for (int i = 0; i < str.length; i++) {
				fn = str[i];
				if (fn.indexOf("index-") > -1 && fn.indexOf(".html") > -1) {
					ft = cf.read(path + "/" + fn);
					if (ft != null && !ft.equals(""))
						flist.add(path + "/" + fn);
				} else {
					String dirs[] = cf.getFileList(path + "/" + fn);
					if (dirs != null && dirs.length > 0)
						flist = getFileListFromDir(path + "/" + fn, flist);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flist;
	}
}
