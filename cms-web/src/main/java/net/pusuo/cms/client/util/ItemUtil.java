package net.pusuo.cms.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.LabelValueBean;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.search.SearchClient;
import com.hexun.cms.search.entry.CmsEntry;
import com.hexun.cms.search.entry.Entry;
import com.hexun.cms.search.util.SearchUtils;
import com.hexun.cms.util.Util;

public final class ItemUtil {
	private static final Log log = LogFactory.getLog(ItemUtil.class);

	private static Map LVBCache = new HashMap(20);

	public static void setItemValues(DynaActionForm dForm, Item item) {
		
		if (dForm == null || item == null)
			return;
		
		String name = null;
		Object value = null;		
		
		try {
			Map map = dForm.getMap();
			Iterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				name = (String) keys.next();
				if (PropertyUtils.isWriteable(item, name)) {
					value = map.get(name);

					if (value instanceof String[]) {
						/*
						 * dirty code . modified by Mark 2004.10.18 Item
						 * collectionItem =
						 * (Item)PropertyUtils.getProperty(item,name+"Instance");
						 * PropertyUtils.setProperty(item,name,CollectionConverter(value,collectionItem.getClass()));
						 */
						// dirty too , because 1.4 can't load data from java
						// metadata :(
						Item collectionItem = (Item) PropertyUtils.getProperty(item, name + "Instance");
						Collection items = (Collection) PropertyUtils.getProperty(item, name);
						setCollection(items, value, collectionItem.getClass());
					} else {
						PropertyUtils.setProperty(item, name, value);
					}
				}
			}
		} catch (Exception e) {
			log.error("setItemValues error. (itemId=" + item.getId() 
					+ ")(itemName=" + item.getName() + ")(itemDesc=" + item.getDesc()
					+ ")(itemClass=" + item + ")(dForm4Id=" + dForm.get("id")
					+ ")(name=" + name + ")(value=" + value + ")" + e.toString());
		}
	}

	public static void putItemValues(DynaActionForm dForm, Item item) {
		try {
			String name = null;
			Map map = dForm.getMap();
			Iterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				name = (String) keys.next();
				if (PropertyUtils.isReadable(item, name)) {
					Object value = PropertyUtils.getProperty(item, name);
					if (value instanceof Collection) {
						dForm.set(name, StringArrayConverter(value));
					}
					/*
					 * maybe ... else if ( value instanceof Map ) { }
					 */
					else {
						dForm.set(name, value);
					}
				} else {
					// set items list here . eg. "List6" -- GROUP_TYPE
					if (name.startsWith("List")) {
						try {
							int entityType = Integer
									.parseInt(name.substring(4));
							List list = ItemManager.getInstance().getList(
									ItemInfo.getItemClass(entityType));

							// added by wangzhigang 2005.9.15
							// sort by item.desc
							Collections.sort(list, new DescComparator());

							if (list != null) {
								List key_value = new ArrayList();
								ListToLVB(list, key_value);
								dForm.set(name, key_value);
							}

							// log.debug("list value: "+list);
						} catch (NumberFormatException nfe) {
							log.error("putItemValues: put list error . " + nfe);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("putItemValues error. " + e.toString());
		}
	}

	public static List ListToLVB(Collection src, List dst) {
		try {
			Item item = null;
			Iterator i = src.iterator();
			while (i.hasNext()) {
				item = (Item) i.next();
				if (item != null)
					dst.add(new LabelValueBean(item.getDesc(), String
							.valueOf(item.getId())));
			}
		} catch (Exception e) {
			log.error("ListToMap error . " + e);
		}
		return dst;
	}

	public static List ListToLVB(Collection src) {
		List dst = new ArrayList();
		try {
			Item item = null;
			Iterator i = src.iterator();
			while (i.hasNext()) {
				item = (Item) i.next();
				if (item != null)
					dst.add(new LabelValueBean(item.getDesc(), String
							.valueOf(item.getId())));
			}
		} catch (Exception e) {
			log.error("ListToMap error . " + e);
		}
		return dst;
	}

	public static List ListToLVBByName(Collection src) {
		List dst = new ArrayList();
		try {
			Item item = null;
			Iterator i = src.iterator();
			while (i.hasNext()) {
				item = (Item) i.next();
				if (item != null)
					dst.add(new LabelValueBean(item.getName(), String
							.valueOf(item.getId())));
			}
		} catch (Exception e) {
			log.error("ListToMap error . " + e);
		}
		return dst;
	}

	public static Object StringArrayConverter(Object value) {
		String[] ret = null;
		if (value instanceof Collection) {
			Collection list = (Collection) value;
			Object[] items = list.toArray();
			ret = new String[items.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = String.valueOf(((Item) items[i]).getId());
			}
		}
		return ret;
	}

	public static void setCollection(Collection items, Object value,
			Class itemClass) {

		if (value instanceof String[]) {
			String[] list = (String[]) value;
			Set newSet = new HashSet();
			Set oldSet = new HashSet();
			Object[] itemlist = items.toArray();
			for (int i = 0; i < list.length; i++) {
				try {
					int itemID = Integer.parseInt(list[i]);
					Object mapItem = null;
					for (int j = 0; j < itemlist.length; j++) {
						if (((Item) itemlist[j]).getId() == itemID) {
							log.debug("========= old " + itemID);
							mapItem = itemlist[j];
							oldSet.add(mapItem);
						}
					}
					if (mapItem == null) {
						mapItem = ItemManager.getInstance().get(
								new Integer(itemID), itemClass);
						newSet.add(mapItem);
						log.debug("========= new " + itemID);
					}
				} catch (Exception e) {
					log.error("setCollection [" + list[i] + "] error . " + e);
					continue;
				}
			}
			items.retainAll(oldSet);
			Iterator it = newSet.iterator();
			while (it.hasNext()) {
				items.add(it.next());
			}

			/*
			 * for ( int i=0;i<list.length;i++ ) { try {
			 * ret.add(ItemManager.getInstance().get(Integer.valueOf(list[i]),itemClass)); }
			 * catch ( NumberFormatException nfe ) { log.error("collection
			 * convert number["+list[i]+"] error . "+nfe); continue; } }
			 */
		}
	}

	/**
	 * �ж�������Ƶ�����Ƿ�������ʵ��
	 */
	public static boolean isPExistChannel(Integer channel, Integer id) {
		boolean ret = false;
		try {
			EntityItem item = (EntityItem) ItemManager.getInstance().get(id,
					EntityItem.class);
			if (channel.intValue() == item.getChannel()) {
				ret = true;
			}
		} catch (Exception e) {
			log.error("ItemUtil.isPExistChannel error . " + e.toString());
			ret = false;
		}
		return ret;
	}

	public static String getPSubjectID(EntityItem item) {
		String ids = "0000";

		try {
			if (item == null)
				return ids;

			ids = String.valueOf(item.getId());
			while (item != null && item.getId() > 0) {
				if (item.getType() == EntityItem.SUBJECT_TYPE) {// ����Ϊר��
					if (((Subject) item).getSubtype() == 1) {// ������Ϊר��
						ids = item.getId() + "";
						break;
					}
				}
				item = (EntityItem) ItemManager.getInstance().get(
						new Integer(item.getPid()), EntityItem.class);
			}
			ids = ((ids != null && ids.length() >= 4) ? ids : "0000");
		} catch (Exception e) {
			log.error("ItemUtil.getPSubjectID error. " + e.toString());
		}

		return ids;
	}

	public static List getEntityParents(EntityItem item) {
		int maxloop = 0;
		ArrayList parents = new ArrayList();
		try {
			while (item != null && item.getPid() > 0) {
				item = (EntityItem) ItemManager.getInstance().get(
						new Integer(item.getPid()), EntityItem.class);
				maxloop++;
				if (maxloop > 20)
					break;
				parents.add(item);
			}
		} catch (Exception e) {
			log.error("ItemUtil.getEntityParents error. " + e.toString());
		}
		return parents;
	}

	public static List getEntityChildren(int pid, int entityType) {
		// never use this api to list news or pics
		if (entityType > 0 && entityType != ItemInfo.SUBJECT_TYPE && entityType != ItemInfo.HOMEPAGE_TYPE) {
			return null;
		}
		String hql = "select item.id from " + ItemInfo.getEntityClass().getName()
				+ " item where item.pid=? and item.type=? order by item.priority desc,item.time asc";
		List ids = null;
		List ret = new ArrayList();
		try {
			Collection values = new ArrayList();
			values.add(new Integer(pid));
			values.add(new Integer(entityType));
			ids = ItemManager.getInstance().getList(hql, values, -1, -1);
			if (ids != null) {
				Iterator itor = ids.iterator();
				while (itor.hasNext()) {
					try {
						ret.add(ItemManager.getInstance().get(((Integer) itor.next()),
								ItemInfo.getEntityClass()));
					} catch (NumberFormatException nfe) {
						log.error("ItemUtil getEntityChildren parse item id error . "
								+ nfe.toString());
						continue;
					}
				}
			}
		} catch (Exception e) {
			log.error("ItemUtil.getEntityChildren error. " + e.toString());
		}
		return ret;
	}

	public static List getEntityChildrenByPriority(int pid, int entityType,
			int minpriority, int maxpriority) {
		List list = getEntityChildren(pid, entityType);
		List ret = new ArrayList();
		for (int i = 0; list != null && i < list.size(); i++) {
			EntityItem eItem = (EntityItem) list.get(i);
			int priority = eItem.getPriority();
			if (priority < minpriority || priority > maxpriority)
				continue;
			if (eItem.getStatus() != 2)
				continue;
			ret.add(list.get(i));
		}
		return ret;
	}

	public static List getChannelList(Set set) {

		List channels = new ArrayList();// �û�����Ƶ��
		Iterator it = set.iterator();
		String channelName = "";

		Channel channelItem = null;
		while (it.hasNext()) {
			channelName = (String) it.next();
			if (channelName != null && !channelName.equals("")) {
				channelItem = (Channel) ItemManager.getInstance()
						.getItemByName(channelName, Channel.class);
				if (channelItem != null && channelItem.getId() > 0) {
					channels.add(channelItem);
				}
			}
		}
		return channels;
	}

	public static void refreshLVBCache(Object key) {
		LVBCache.remove(key);
	}

	public static String toHref(String idStr) throws Exception {
		StringBuffer retHref = new StringBuffer();
		News item = null;
		if (idStr != null && !"".equals(idStr)) {
			StringTokenizer st = new StringTokenizer(idStr, ";");
			while (st.hasMoreElements()) {
				try {
					item = (News) ItemManager.getInstance().get(
							new Integer(st.nextToken().trim()),
							ItemInfo.getItemClass(ItemInfo.NEWS_TYPE));
					if (item != null)
						retHref.append("<li class=relationNews><A href=")
								.append(item.getUrl()).append(
										" ::" + item.getId()).append(
										"|| target=_blank>").append(
										item.getDesc()).append("</A></li>");
				} catch (Exception e) {
					continue;
				}
			}
		}
		return retHref.toString();
	}

	static public Object deepCopy(Object oldObj) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			// serialize and pass the object
			oos.writeObject(oldObj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos
					.toByteArray()); // E
			ois = new ObjectInputStream(bin);
			// return the new object
			return ois.readObject();
		} catch (Exception e) {
			log.error("deepCopy error " + e);
			throw (e);
		} finally {
			oos.close();
			ois.close();
		}
	}

	/**
	 * added by wangzhigang 2005.03.23 get homepage entityitem
	 */
	public static EntityItem getHpEntity(int entityId) {
		try {
			EntityItem ret = (EntityItem) ItemManager.getInstance().get(
					new Integer(entityId), EntityItem.class);
			if (ret == null)
				return null;

			while (true) {
				if (ret == null || ret.getPid() == 0) {
					// ��ʵ���޸�����,�����˳�,����null
					log.warn("getHpEntity -- NULL ENTITY @entity " + entityId);
					ret = null;
					break;
				}

				// �ҵ���ҳʵ��,���˳�
				if (ret.getType() == 5 || ret.getPid() == -1)
					break;
				ret = (EntityItem) ItemManager.getInstance().get(
						new Integer(ret.getPid()), EntityItem.class);
			}
			return ret;
		} catch (Exception e) {
			log.error("getHpEntity exception -- ", e);
			return null;
		}
	}

	public static EntityItem getHpEntity(EntityItem entity) {
		try {
			EntityItem ret = entity;
			if (ret == null)
				return null;

			while (true) {
				if (ret == null || ret.getPid() == 0) {
					// ��ʵ���޸�����,�����˳�,����null
					log.warn("getHpEntity -- NULL ENTITY @entity "
							+ entity.getId());
					ret = null;
					break;
				}

				// �ҵ���ҳʵ��,���˳�
				if (ret.getType() == 5 || ret.getPid() == -1)
					break;

				ret = (EntityItem) ItemManager.getInstance().get(
						new Integer(ret.getPid()), EntityItem.class);
			}
			return ret;
		} catch (Exception e) {
			log.error("getHpEntity exception -- ", e);
			return null;
		}
	}

	private static class DescComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null)
				return 0;
			if (!(o1 instanceof Item) || !(o2 instanceof Item))
				return 0;

			Item obj1 = (Item) o1;
			Item obj2 = (Item) o2;

			return String.valueOf(obj1.getDesc()).compareTo(
					String.valueOf(obj2.getDesc()));
		}
	}

	/**
	 * �ж϶�����(�������)�Ƿ��ظ�
	 */
	public static List getShortnames(String shortName, Integer channel) {
		String hql = "select item.id from "
				+ ItemInfo.getEntityClass().getName()
				+ " item where item.channel=? and item.shortname=?";
		List ret = null;
		try {
			Collection values = new ArrayList();
			values.add(channel);
			values.add(shortName);
			ret = ItemManager.getInstance().getList(hql, values, -1, -1);
		} catch (Exception e) {
			log.error("ItemUtil.getEntityChildren error. " + e.toString());
		}
		return ret;
	}

	/**
	 * 
	 */
	private static final String LIST_ENTITY_TYPE_PREFIX = "cmsET:";

	/**
	 * �б����ʵ��������
	 */
	private static final String LIST_NEWS_TYPE_PREFIX = LIST_ENTITY_TYPE_PREFIX
			+ "news";

	/**
	 * �б����ʵ����ͼƬ
	 */
	private static final String LIST_PICTURE_TYPE_PREFIX = LIST_ENTITY_TYPE_PREFIX
			+ "picture";

	/**
	 * ��ݶ�̬��Ƭ�������������б����ʵ�������,���desc��@link{LIST_PICTURE_TYPE_PREFIX}��ͷ,��ô
	 * ���ص��б�������IntemInfo.PICTURE_TYPE;û��ƥ�䷵��IntemInfo.NEWS_TYPE
	 * 
	 * @param desc
	 *            ��̬��Ƭ������
	 * @return
	 */
	public static int getListEntityTypeByDesc(String desc) {
		int type = ItemInfo.NEWS_TYPE;
		if (desc != null) {
			if (desc.startsWith(LIST_PICTURE_TYPE_PREFIX)) {
				type = ItemInfo.PICTURE_TYPE;
			}
		}
		return type;
	}

	/**
	 * ���Item��id���ַ�,�õ���Ӧ��name�ַ�
	 * 
	 * @param ids
	 *            ��ʽΪxxx;xxx;xxx
	 * @return ��ʽΪyyy;yyy;yyy
	 */
	public static String getItemNamesByIds(String ids) {
		Set temp = new HashSet();
		if (StringUtils.isNotEmpty(ids)) {
			String[] idArray = ids.split(Global.CMSSEP);
			for (int i = 0; i < idArray.length; i++) {
				EntityItem item = (EntityItem) ItemManager.getInstance().get(
						new Integer(idArray[i]), EntityItem.class);
				if (item != null && item.getId() > 0) {
					temp.add(item.getName());

				}
			}
		}
		return StringUtils.join(temp.iterator(), Global.CMSSEP);
	}

	/**
	 * ���Item��name���ַ�,�õ���Ӧ��id�ַ�
	 * 
	 * @param names
	 *            ��ʽΪxxx;xxx;xxx
	 * @return ��ʽΪyyy;yyy;yyy
	 */
	public static String getItemIdsByNames(String names) {
		Set temp = new HashSet();
		if (StringUtils.isNotEmpty(names)) {
			String[] namesArray = names.split(Global.CMSSEP);
			for (int i = 0; i < namesArray.length; i++) {
				EntityItem item = (EntityItem) ItemManager.getInstance()
						.getItemByName(namesArray[i], EntityItem.class);
				if (item != null && item.getId() > 0) {
					temp.add("" + item.getId());
				}
			}

		}
		return StringUtils.join(temp.iterator(), Global.CMSSEP);
	}

	/**
	 * ���Item��id,�õ��������name
	 * 
	 * @param ids
	 * @return
	 */
	public static String getPnameById(String ids) {
		Set temp = new HashSet();
		if (StringUtils.isNotEmpty(ids)) {
			String[] idArray = ids.split(Global.CMSSEP);
			for (int i = 0; i < idArray.length; i++) {
				if(StringUtils.isBlank(idArray[i])||!StringUtils.isNumeric(idArray[i]))continue;
				EntityItem item = (EntityItem) ItemManager.getInstance().get(
						new Integer(idArray[i]), EntityItem.class);
				if (item != null && item.getId() > 0) {
					int pid = item.getPid();
					EntityItem pitem = (EntityItem) ItemManager.getInstance()
							.get(new Integer(pid), EntityItem.class);
					temp.add(pitem.getName());
				}
			}
		}
		return StringUtils.join(temp.iterator(), Global.CMSSEP);
	}

	/**
	 * �滻�����еı�ǩ
	 */
	public static StringBuffer replaceTag(StringBuffer content, String tagOld, String tagNew) {
		
		int idx = 0;
		while (idx >= 0) {
			idx = content.indexOf(tagOld, idx);
			if (idx < 0)
				break;

			content.delete(idx, idx + tagOld.length());
			content.insert(idx, tagNew);
			idx += tagNew.length();
		}
		return content;
	}
	
		/**
	 * ����û�Ȩ��,�õ���Ӧ��ý���б�
	 * ���淽ʽ:  key=id, value=Media����
	 * @param auth
	 * @return
	 */
	public static Map getMediaMap(Authentication auth) {
		Map mediaMap = new HashMap();
		List channels = auth.getChannelList();
		for (int i = 0; i < channels.size(); i++) {
			Channel channel = (Channel) channels.get(i);
			List channelMedias = ChannelMediaUtil
					.getChannelMedia(channel.getId());
			if (channelMedias == null)
				channelMedias = ItemManager.getInstance().getList(
						Media.class);
			for (int j = 0; j < channelMedias.size(); j++) {
				Media media = (Media) channelMedias.get(j);
				mediaMap.put(new Integer(media.getId()), media);
			}
		}
		return mediaMap;
	}
	
	/**
	 * �����������
	 */
	public static String searchRelativeNews(String keyword, int channel, int count) {

		String relativenews = "";

		try {
			if (keyword != null && keyword.trim().length() > 0
					&& !keyword.trim().startsWith("*") && channel > 0
					&& count > 0 && count <= 100) {

				keyword = keyword.trim();

				Term termChannel = new Term(CmsEntry.FIELD_NAME_CHANNEL, ""
						+ channel);
				TermQuery queryChannel = new TermQuery(termChannel);

				Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, "2");
				TermQuery queryType = new TermQuery(termType);

				Term termStatus = new Term(CmsEntry.FIELD_NAME_STATUS, "2");
				TermQuery queryStatus = new TermQuery(termStatus);

				QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_DESC, new StandardAnalyzer());
				queryParser.setOperator(QueryParser.AND);
				Query queryUser = queryParser.parse(keyword);

				BooleanQuery query = new BooleanQuery();
				if (queryUser != null)
					query.add(queryUser, true, false);

				query.add(queryChannel, true, false);
				query.add(queryType, true, false);
				query.add(queryStatus, true, false);

				Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_ID,SortField.INT, true));

				long timeStart = System.currentTimeMillis();

				Map result = SearchClient.getInstance().getSearchManager().search(query, sort, 0, count * 4);

				long timeEnd = System.currentTimeMillis();
				log.info("Search relative news.Cost is: "+ (timeEnd - timeStart)+ " ms.And the query expression is: " + query);

				List list = null;

				if (result != null && result.containsKey(SearchUtils.SEARCH_RESULT_LIST)) {
					list = (List) result.get(SearchUtils.SEARCH_RESULT_LIST);
				}

				int n = 0;
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						Entry entry = (Entry) list.get(i);
						if (entry != null) {
							News item = (News) ItemManager.getInstance().get(new Integer(entry.getId()), News.class);
							if (item == null)
								continue;
							if (relativenews.indexOf(String.valueOf(entry.getId())) < 0&& (item.getReurl() == null || item.getReurl().trim().length() == 0)) {
								relativenews += entry.getId() + ";";
								n++;
								if (n == 8) {
									break;
								}
							}
						}
					}
				}
			}
		} 
		catch (Exception e) {
			log.error("ItemUtil.searchRelativeNews error. " + e.toString());
		}

		return relativenews;
	}
	
	public static final String CONTENT_LINK = "content.link";
	public static final String CONTENT_TEXT = "content.text";
	
	/**
	 * �����ı�������
	 */
	public static Map separateTextAndLink(String linkText) {
		
		if (linkText == null || linkText.trim().length() == 0)
			return null;		
		linkText = linkText.trim();
		
		Map result = new HashMap();
		
		String href = "href='";
		int index = linkText.indexOf(href);
		
		// ֻ���ı�,û������
		if (index == -1) {
			result.put(CONTENT_TEXT, linkText);
			return result;
		}
		
		// ��������
		linkText = linkText.substring(index + href.length());
		index = linkText.indexOf("'");
		result.put(CONTENT_LINK, linkText.substring(0, index));
		
		// �����ı�
		index = linkText.lastIndexOf("</a>");
		if (index > -1) {
			linkText = linkText.substring(0, index);
			index = linkText.lastIndexOf(">");
			if (index > -1)
				result.put(CONTENT_TEXT, linkText.substring(index + 1));
		}
		
		return result;
	}
	
	/**
	 * ƴ���ı�������
	 */
	public static String combineTextAndLink(String text, String link) {
		
		if (text == null || text.trim().length() == 0)
			return null;
		
		if (link == null || link.trim().length() == 0)
			return text;
		
		return "<a href='" + link + "' target='_blank'>" + text + "</a>";
	}
	
	/**
	 *������Ŵ���
	 *@param keyword �ؼ��� channel ���ĸ�Ƶ������������ count ��������
	 *@return ���������غ��ID��
	 */
	public static String searchRelativenewsC(String keyword, int channel, int count) {

		String relativenews = "";

		try {
			if (keyword != null && keyword.trim().length() > 0 && !keyword.trim().startsWith("*") && channel > 0 && count > 0 && count <= 100) {

				keyword = keyword.trim();

				Term termChannel = new Term(CmsEntry.FIELD_NAME_CHANNEL, ""+ channel);
				TermQuery queryChannel = new TermQuery(termChannel);

				Term termType = new Term(CmsEntry.FIELD_NAME_TYPE, "2");
				TermQuery queryType = new TermQuery(termType);

				Term termStatus = new Term(CmsEntry.FIELD_NAME_STATUS, "2");
				TermQuery queryStatus = new TermQuery(termStatus);

				QueryParser queryParser = new QueryParser(CmsEntry.FIELD_NAME_DESC, new StandardAnalyzer());
				//queryParser.setOperator(QueryParser.AND); //��������
				Query queryUser = queryParser.parse(keyword);
				
				BooleanQuery query = new BooleanQuery();
				if (queryUser != null)
					query.add(queryUser, true, false);

				query.add(queryChannel, true, false);
				query.add(queryType, true, false);
				query.add(queryStatus, true, false);

				Sort sort = new Sort(new SortField(CmsEntry.FIELD_NAME_ID,SortField.INT, true));

				long timeStart = System.currentTimeMillis();

				Map result = SearchClient.getInstance().getSearchManager().search(query, sort, 0, count * 2);

				long timeEnd = System.currentTimeMillis();
				log.info("Search relative news.Cost is: " + (timeEnd - timeStart));

				List list = null;

				if (result != null && result.containsKey(SearchUtils.SEARCH_RESULT_LIST)) {
					list = (List) result.get(SearchUtils.SEARCH_RESULT_LIST);
				}

				int n = 0;
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						Entry entry = (Entry) list.get(i);
						if (entry != null) {
							News item = (News) ItemManager.getInstance().get( new Integer(entry.getId()), News.class);
							if (item == null)
								continue;
							if (relativenews.indexOf(String.valueOf(entry.getId())) < 0 && (item.getReurl() == null || item.getReurl().trim().length() == 0)) {
								relativenews += entry.getId() + ";";
								n++;
								if (n == 8) {
									break;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("ItemUtil.searchRelativenewsC error. " + e.toString());
		}

		if( !relativenews.equals("") ){
			return relaContent(relativenews,count);
		}else{
			return relativenews;
		}
	}
	
	/**
	 *������ŵ����ش���
	 *
	 *@param relaStr �������ID��
	 *@return ���������غ��ID��
	 */
	private static String relaContent(String relaStr, int count){
	
		String[] relativeNews = relaStr.split(Global.CMSSEP);
		int curi = 0;//��ǰ��relaArray��λ��
		String returnString = "";
		
		if( relativeNews != null && relativeNews.length >0 ){
			//count = count>=relativeNews.length?relativeNews.length:count;
			String[][] relaArray = new String[relativeNews.length][2];
			for(int i=0; relativeNews!=null && i<relativeNews.length; i++){
				int i_id = -1;
				try{
					i_id = Integer.parseInt( relativeNews[i].trim() );

					EntityItem eItem = (EntityItem)ItemManager.getInstance().get(new Integer(i_id), EntityItem.class);

					if( eItem != null && curi < relaArray.length ){
						relaArray[curi][0] = eItem.getId()+"";
						relaArray[curi][1] = eItem.getDesc();
						curi ++ ;
					}

				} catch(Exception e) {
					continue;
				}
			}

			String[][] tmpArray = new String[curi][2];
			for( int i=0;i<curi;i++ ){
				tmpArray[i][0] = relaArray[i][0];
				tmpArray[i][1] = relaArray[i][1];
			}
			
			String restr =  exclude(tmpArray);//ȥ����
			
			//ֻ������Ҫ������count
			String[] restrArray = restr.split(Global.CMSSEP);
			count = count>=restrArray.length?restrArray.length:count;
			for( int j=0;j<count;j++ ){
				returnString += restrArray[j] + ";";
			}
		}
		
		return returnString;
	}
	 
	/*
	 *���ش���
	 *@param valArray
	 *@return ����֮����Էֺ�Ϊ�ָ��id��
	*/
	private static String exclude( String[][] valArray ){
    
    		String retstr = "";
    		
		for( int i=0;i<valArray.length;i++ ){
			String iv = valArray[i][1];
			for( int j=i+1;j<valArray.length;j++ ){
				String jv = valArray[j][1];
				if( iv.equals(jv) ){
					valArray[j][1] = "-1";
				}
			}
		}
		int count = 0;
		for( int i=0;i<valArray.length;i++ ){
			if( !valArray[i][1].equals("-1") ){
				count ++;
			}
		}

		for( int i=0;i<valArray.length;i++ ){
			if( !valArray[i][1].equals("-1") ){
				retstr += valArray[i][0] +";";
			}
		}
		
		return retstr;
    }
	
}
