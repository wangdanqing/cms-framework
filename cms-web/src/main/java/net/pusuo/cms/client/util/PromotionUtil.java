/*
 * Created on 2006-3-6
 */
package net.pusuo.cms.client.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.News;
import com.hexun.cms.Global;

/**
 * @author agilewang
 */
public class PromotionUtil {
	private static final Log log = LogFactory.getLog(PromotionUtil.class);

	public static String promotionPath = "/cms4/promotion/promotion.prop";

	public final static Properties current = new Properties();

	public final static Properties currentID2Keyword = new Properties();

	private static Properties readOnlly = new Properties();

	private static final ItemManager im = ItemManager.getInstance();

	public static final Object lock = new Object();

	public static final String RECORD_TOKEN = ";";

	public static final String FIELD_TOKEN = "/";

	private static boolean dirty = false;

	public static final Pattern entityPattern = Pattern.compile("\\d{9}",
			Pattern.CASE_INSENSITIVE);

	static {
		load();
	}

	/**
	 *  
	 */
	public static void load() {
		try {
			String content = ClientFile.getInstance().read(promotionPath);
			PropertyUtil.propertiesFromString(current, content);
			PropertyUtil.propertiesFromString(readOnlly, content);
			id2keyword(current, currentID2Keyword);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("load error", e);
			}
		}
	}

	public static void id2keyword(Properties keymap, Properties idMap) {
		Enumeration keywords = keymap.propertyNames();
		while (keywords.hasMoreElements()) {
			String key = (String) keywords.nextElement();
			String values = keymap.getProperty(key);
			if (values != null) {
				String[] ids = values.split(RECORD_TOKEN);
				for (int i = 0; ids != null && i < ids.length; i++) {
					addKey2ID(currentID2Keyword, key, getKFR(ids[i]));
				}
			}
		}
	}

	public static String getKFR(String record) {
		String[] keyValues = record.split(FIELD_TOKEN);
		return keyValues[0];
	}

	public static String getPFR(String record) {
		String[] keyValues = record.split(FIELD_TOKEN);
		return keyValues[1];
	}

	public static String newKF(int key, int prio) {
		return "" + key + FIELD_TOKEN + prio;
	}

	public static void addKey2ID(Properties prop, String key, String id) {
		dirty = true;
		String id2keys = prop.getProperty(id);
		if (id2keys == null) {
			id2keys = key;
		} else {
			id2keys += RECORD_TOKEN + key;
		}
		prop.setProperty(id, id2keys);
	}

	public static void delKey2ID(Properties prop, String key, String[] ids) {
		dirty = true;
		for (int i = 0; i < ids.length; i++) {
			String id2keys = prop.getProperty(ids[i]);
			if (id2keys != null) {
				String newKeys = null;
				String[] keys = id2keys.split(RECORD_TOKEN);
				for (int j = 0; j < keys.length; j++) {
					String key_r = getKFR(keys[j]);
					if (!key.equals(key_r)) {
						if (newKeys == null) {
							newKeys = keys[j];
						} else {
							newKeys += RECORD_TOKEN + keys[j];
						}
					}
				}
				if (newKeys != null) {
					newKeys.replaceAll(";{2,}", "");
				}
				if (newKeys == null || newKeys.trim().length() == 0) {
					prop.remove(ids[i]);
				} else {
					prop.setProperty(ids[i], newKeys);
				}
			}
		}
	}

	public static String error(String s) {
		return "Error:" + s;
	}

	public static String success(String s) {
		return "Success:" + s;
	}

	public static boolean isSuccess(String s) {
		if (s == null) {
			return false;
		}
		return s.startsWith("Success");
	}

	public static String addKeyword(String keyword, String id, Properties prop,
			String priority) {
		if (keyword == null || id == null || prop == null
				|| keyword.length() == 0 || id.length() == 0
				|| priority == null || priority.length() == 0) {
			return error("parameter is not set");
		}
		dirty = true;
		String values = prop.getProperty(keyword);
		Integer intId = null;
		int priority_int = 0;
		try {
			intId = new Integer(id);
			priority_int = Integer.parseInt(priority);
		} catch (Exception e) {
			return error("Targe id " + id + " is a number?");
		}

		EntityItem subject = getSubject(intId);
		if (subject == null) {
			return error("Not is a subject whose id is " + id);
		}

		String msg = null;
		if (values == null) {
			values = newKF(intId.intValue(), priority_int);
			msg = success("Add target id " + intId + " for " + keyword
					+ " success");
		} else {
			String category = subject.getCategory();
			boolean isInPidTree = false;
			if (category != null) {
				String[] pids = category.split(";");
				for (int i = 0; i < pids.length; i++) {
					if (values.indexOf(pids[i]) >= 0) {
						isInPidTree = true;
						break;
					}
				}
			}

			if (!isInPidTree) {
				values += RECORD_TOKEN + newKF(intId.intValue(), priority_int);
				msg = success("Add target id " + intId + " for " + keyword
						+ " success");
			} else {
				msg = error("Add target id " + intId + " for " + keyword
						+ " fail,it's parent has existed in this item");
			}
		}

		if (isSuccess(msg)) {
			prop.setProperty(keyword, values);
		}
		return msg;
	}

	public static String deleteKeyID(String keyword, String[] ids,
			Properties prop) {
		if (keyword == null || ids == null || keyword.length() == 0
				|| ids.length == 0) {
			return error("parameter is not set");
		}
		dirty = true;
		String values = prop.getProperty(keyword);
		for (int i = 0; i < ids.length; i++) {
			int index = values.indexOf(ids[i]);
			if (index >= 0) {
				values = values.replaceAll(ids[i] + "[^;]*;?", "");
				if (values.endsWith(";")) {
					values = values.substring(0, values.length() - 1);
				}
			}
		}
		if (values == null || values.trim().length() == 0) {
			prop.remove(keyword);
		} else {
			prop.setProperty(keyword, values.trim());
		}
		return success("Delete id");
	}

	public static EntityItem getSubject(Integer id) {
		EntityItem item = (EntityItem) im.get(id, EntityItem.class);
		if (item == null || item.getType() != EntityItem.SUBJECT_TYPE) {
			return null;
		} else {
			return item;
		}
	}

	public static void checkPermission(Authentication auth, Integer pid) {
		EntityItem item = (EntityItem) im.get(pid, EntityItem.class);
		Channel channel = (Channel) im.get(new Integer(item.getChannel()),
				Channel.class);
		if (item != null && !auth.hasChannel(channel.getName())) {
			throw new RuntimeException(
					"You don't have permission whith entity:" + item.getDesc());
		}
	}

	/**
	 * ȡ��title�а�Ĺؼ�ʵ�ר��
	 * 
	 * @param title
	 * @return
	 */
	public static List fineTarget(String title, int newsId) {
		List resultList = new ArrayList();
		News news = (News) im.get(new Integer(newsId), News.class);
		if (news == null) {
			return resultList;
		}
		String me_category = news.getCategory();
		String pushRecord = PromotionUtil.getPushParent(news.getPushrecord());
		Properties newProp = readOnlly;
		if (title != null && title.length() != 0) {
			if (dirty) {
				newProp = new Properties();
				try {
					String content = ClientFile.getInstance().read(
							promotionPath);
					PropertyUtil.propertiesFromString(newProp, content);
					readOnlly = newProp;
					dirty = false;
				} catch (Exception e) {
				}
			}
			Set set = newProp.entrySet();
			Iterator setIr = set.iterator();
			while (setIr.hasNext()) {
				Map.Entry item = (Map.Entry) setIr.next();
				String key = (String) item.getKey();
				String value = (String) item.getValue();
				if (key == null || value == null) {
					continue;
				}
				if (title.indexOf(key) >= 0) {
					String[] iws = value.split(RECORD_TOKEN);
					if (iws != null && iws.length > 0) {
						for (int i = 0; i < iws.length; i++) {
							String target = getKFR(iws[i]);
							String weight = getPFR(iws[i]);
							try {
								int t = Integer.parseInt(target);
								int w = Integer.parseInt(weight);
								EntityItem targetItem = (EntityItem) ItemManager
										.getInstance().get(new Integer(t),
												EntityItem.class);
								if (target != null
										&& !me_category.startsWith(targetItem
												.getCategory())) {
									//news�Ĳ���targetItem������
									if ((pushRecord == null || pushRecord
											.indexOf(String.valueOf(t)) < 0)) {
										//���ͼ�¼��û��targetItem
										PromotionResult result = new PromotionResult(
												t, w);
										if (!resultList.contains(result)) {
											resultList.add(result);
										}
									}
								}
							} catch (Throwable e) {

							}
						}
					}
				}
			}
		}
		return resultList;
	}

	public static String getPushParent(String pushRecord) {
		if (pushRecord == null || pushRecord.trim().length() == 0) {
			return "";
		}
		String[] pushRecordArray = pushRecord.split(Global.CMSSEP);
		StringBuffer pushedNames = new StringBuffer();
		int count = 0;
		for (int i = 0; i < pushRecordArray.length; i++) {
			String id = pushRecordArray[i];
			if (StringUtils.isEmpty(id) || !StringUtils.isNumeric(id))
				continue;
			EntityItem item = (EntityItem) im.get(new Integer(id), EntityItem.class);
			if (item != null && item.getId() > 0) {
				EntityItem pitem = (EntityItem) im.get(new Integer(item
						.getPid()), EntityItem.class);
				if (pitem != null && pitem.getId() > 0) {
					if (count == 0) {
						pushedNames.append(pitem.getId());
					} else {
						pushedNames.append(";" + pitem.getId());
					}
					++count;
				}
			}
		}
		return pushedNames.toString();
	}

	public static class PromotionResult {
		private int id;

		private int weight;

		/**
		 * @param id
		 * @param weight
		 */
		public PromotionResult(int id, int weight) {
			this.id = id;
			this.weight = weight;
		}

		public int getId() {
			return id;
		}

		public int getWeight() {
			return weight;
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (!obj.getClass().equals(this.getClass())) {
				return false;
			}

			return ((PromotionResult) obj).id == this.id;
		}

		public int hashCode() {
			return id;
		}

		public String toString() {
			return super.toString();
		}
	}
}
