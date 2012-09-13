/**
 * 
 */
package net.pusuo.cms.client.biz.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Global;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.cache.CmsSortItem;
import com.hexun.cms.cache.Query;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.ListCacheClient;
import com.hexun.cms.core.CoreFactory;
import com.hexun.cms.core.News;

/**
 * @author jackey
 *
 */
public class NewsUtil {
	
	private static final Log log = LogFactory.getLog(NewsUtil.class);
	
	/**
	 * shijinkui
	 * 取上一个/下一个新闻
	 * 对于体育频道,取三级栏目/专题的列表;
	 * 对于其他频道,取二级栏目/专题的列表.
	 */
	public static News[] getPreAndNextNews(News news) {
		News result[] = new News[2];
		if (news == null)
			return null;
		
		String categoryParam = news.getCategory();
		String[] categories = categoryParam.split(Global.CMSSEP);
		if (categories.length <2)
			return null;
		
		long timeStart = System.currentTimeMillis();
		
		try {
			//取当前栏目新闻
		 	Query query = new Query();
			query.setId(news.getPid());
			query.setType(ItemInfo.NEWS_TYPE);
			query.setStart(0);
			query.setCount(100);
			query.setSortType(Query.SORT_TYPE_TIME);
			
			List items = ListCacheClient.getInstance().filter(query);
			//确保有下一个新闻
			if (items != null && items.size() >= 1) {
				// 该组图新闻应该已经存在队列中
				News nullnews = CoreFactory.getInstance().createNews();

//				System.out.println("1. " + items.size() + "||" + nullnews);

				for (int i = 0; i < items.size(); i++) {
					CmsSortItem item = (CmsSortItem)items.get(i);
					if (item != null && item.getId() == news.getId()) {
						//取上一篇组图新闻
						CmsSortItem preItem = (i-1)>-1?(CmsSortItem)items.get(i - 1):null;

						//System.out.println("2. preItem:" + preItem);						

						if(preItem!=null && preItem.getId() > -1)
						{
							News n = (News)ItemManager.getInstance().get(new Integer(preItem.getId()), News.class);
							result[0] = n;
							//重新提交最新的文章的下一文章
							//ItemManager.getInstance().update(n);
						}else
							result[0] = nullnews;
							
						
						//取下一个组图新闻
						//System.out.println("item size:" + items.size() + "|| " + i);
						CmsSortItem nextItem = (i+1)>=items.size()?null:(CmsSortItem)items.get(i+1);
						if (nextItem != null && nextItem.getId()>-1) {
							News nextNews = (News)ItemManager.getInstance().get(new Integer(nextItem.getId()), News.class);
							result[1] = nextNews;
						}else
							result[1] = nullnews;
						
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("news["+ news.getId() +"] Cost getting next/pre news of zutu's time is:" + (timeEnd - timeStart));
		return result;
	}
	
	/**
	 * 获取下一个组图新闻
	 * 对于体育频道,取三级栏目/专题的列表;
	 * 对于其他频道,取二级栏目/专题的列表.
	 */
	public static News getNextNewsOfZutu(News news) {
		
		if (news == null || news.getSubtype() != News.SUBTYPE_ZUTU)
			return null;
		
		News nextNews = null;
		
		int deep = 3;//(news.getChannel() == 102 ? 3 : 2); // 102:体育频道
		
		String categoryParam = news.getCategory();
		String[] categories = categoryParam.split(Global.CMSSEP);
		if (categories.length <= deep)
			return null;
		
		long timeStart = System.currentTimeMillis();
		
		try {
			int parentId = Integer.parseInt(categories[deep - 1]);
			
		 	Query query = new Query();
			query.setId(parentId);
			query.setType(ItemInfo.NEWS_TYPE);
			query.setSubtype(News.SUBTYPE_ZUTU);
			query.setStart(0);
			query.setCount(200);
			query.setSortType(Query.SORT_TYPE_TIME);
			
			List items = ListCacheClient.getInstance().filter(query); 
			if (items != null && items.size() > 0) {
				int nextId = -1;
				
				// 该组图新闻应该已经存在队列中,否则就去查查listcache的bug吧.:)
				for (int i = 0; i < items.size(); i++) {
					CmsSortItem item = (CmsSortItem)items.get(i);
					if (item != null && item.getId() == news.getId() && i < items.size() - 1) {
						if(item.getSubtype() != News.SUBTYPE_ZUTU)continue;
						CmsSortItem nextItem = (CmsSortItem)items.get(i + 1);
						if (nextItem != null) {
							nextId = nextItem.getId();
						}
						break;
					}
				}
				
				// 取下一个组图新闻
				if (nextId > 0) {
					nextNews = (News)ItemManager.getInstance().get(new Integer(nextId), News.class);
				}
			}
		} catch (Exception e) {
			nextNews = null;
		}
		
		long timeEnd = System.currentTimeMillis();
		log.info("Cost of getting next news of zutu is : " + (timeEnd - timeStart) 
				+ " (idCurr=" + news.getId() 
				+ ")(idNext=" + (nextNews == null ? -1 : nextNews.getId()) + ")");
		
		return nextNews;
	}
	
	/**
	 * 删除内容中的table标签
	 */
	public static String removeTableFromText(String text) {
		
		if (text == null || text.trim().length() == 0)
			return text;
		
		String textLower = text.toLowerCase();
		
		int indexBegin = textLower.indexOf("<table");
		int indexEnd = textLower.indexOf("/table>");
		if (indexBegin == -1 || indexEnd == -1 || indexBegin >= indexEnd)
			return text;
		
		String result = text.substring(0, indexBegin) + text.substring(indexEnd + 7);
		result = result.trim();
		
		// 可以同时删除多个table标签
		if (result.toLowerCase().indexOf("<table") != -1) {
			result = removeTableFromText(result);
		}
		return result;
	}
}
