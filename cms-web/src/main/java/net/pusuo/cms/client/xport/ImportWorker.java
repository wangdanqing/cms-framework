package net.pusuo.cms.client.xport;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Item;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.client.util.ClientHttpFile;
import com.hexun.cms.core.News;

public class ImportWorker {
	private static final Log log = LogFactory.getLog(ImportWorker.class);
	public static void importData(List newsList) {

		if (newsList == null || newsList.size() == 0) {
			return;
		}

		for (int i = 0; i < newsList.size(); i++) {
			News news = (News) newsList.get(i);
			boolean flag = true;
			try {
				//约束条件,内容和标题不能为空
				if(StringUtils.isEmpty(news.getDesc()) || StringUtils.isEmpty(news.getText()) || news.getText().length() < 10)
					continue;
				String forbiden = ClientFile.getInstance().read("/cmsdata/forbidenwords.bat");
				String tmp = "", ctmp = "";
				String fb[] = null;
				if(forbiden!=null && forbiden.length() > 0)
					fb = forbiden.split(";");
				
				for (int j = 0; fb != null && j < fb.length; j++) {
					
					tmp = fb[j];
					ctmp = news.getDesc() + news.getText() + news.getAbstract();
					//黑词过滤
					if(ctmp.indexOf(tmp) > -1)
					{
						log.info("黑词过滤不通过: " + news.getDesc() + "||" + news.getPid() + "||" + news.getMedia());
						flag = false;
						break;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(flag)
			{
				Item item = ItemManager.getInstance().update(news);
				//log.info("===> " + news.getDesc() + "||" + news.getText().length());
				log.info("==>" + item.getId());
			}

			if (news.getPid() > 0 && i%4==0) {
				ClientHttpFile.wgetIfcString("http://cms.pusuo.net/patch/pagination.jsp?pids=" + String.valueOf(news.getPid()), 3000);
			}
		}
	}
}
