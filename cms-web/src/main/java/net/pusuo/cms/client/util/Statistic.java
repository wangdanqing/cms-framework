package net.pusuo.cms.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;

/**
 * ͳ�Ƹ������ý�������ʹ�ø���
 * @author Alfred.Yuan
 */
public class Statistic {
	
	private static final Log log = LogFactory.getLog(Statistic.class);
	
	public static final int STATISTIC_TYPE_PARENT = 1;
	public static final int STATISTIC_TYPE_MEDIA = 2;
	
	public static final int ACTIVE_PARENT_COUNT = 200;
	public static final int ACTIVE_PARENT_MANUAL = 338;
	public static final int ACTIVE_MEDIA_COUNT = 200;
	
	// active parents
	private static LRUMap activeParents = new LRUMap(3000);
	
	// userId->LRUMap(parentId->Entity)
	private static Map userMap4Parent = new HashMap();
	
	private static Object lock4Parent = new Object();
	
	// userId->LRUMap(mediaId->Entity)
	private static Map userMap4Media = new HashMap();
	
	private static Object lock4Media = new Object();
	
	// initable
	private static Set initableSet = new HashSet();
	
	// directory to store
	private static String FILE_XML_DIR = null;
	private static final String FILE_XML_EXT = ".xml";
	public static final String FILE_XML_ROOT_NAME = "stat";
	public static final int FILE_XML_RECORD_COUNT = 20;
	static {
		FILE_XML_DIR = Configuration.getInstance().get("cms4.statistic.dir");
	}
	
	public static List getTopParentList(int top, int userId) {
		return getTopList(STATISTIC_TYPE_PARENT, top, userId);
	}
	
	public static List getTopMediaList(int top, int userId) {
		return getTopList(STATISTIC_TYPE_MEDIA, top, userId);
	}
	
	public static List getTopList(int type, int top, int userId) {
		
		Integer user = new Integer(userId);
		
		// ��ʼ��
		if (!initableSet.contains(user)) {
			loadParentsAndMedias(userId);
			initableSet.add(user);
		}
		
		LRUMap map = null;
		if (type == STATISTIC_TYPE_PARENT) {
			if (!userMap4Parent.containsKey(user))
				return null;
			map = (LRUMap)userMap4Parent.get(user);
		}
		else if (type == STATISTIC_TYPE_MEDIA) {
			if (!userMap4Media.containsKey(user))
				return null;
			map = (LRUMap)userMap4Media.get(user);
		}
		
		List result = new ArrayList();
		
		// ��������
		int count = 0;
		Object[] entryArray = map.entrySet().toArray();
		for (int i = entryArray.length - 1; i > -1 && count < top; i--) {
			Map.Entry entry = (Map.Entry)entryArray[i];
			result.add(entry.getValue());
			
			count++;
		}
			
		return result;
	}
	
	public static void activeParentAndMedia(News news) {
		
		// ֻͳ�Ʊ༭�ֹ���������,���˵�newshoo�Զ�����
		if (news == null || news.getEditor() == ACTIVE_PARENT_MANUAL || news.getEditor() <= 0)
			return;
		
		// �û�id
		Integer userId = new Integer(news.getEditor());

		// ������
		Integer parentId = new Integer(news.getPid());
		Subject parent = (Subject)ItemManager.getInstance().get(parentId, Subject.class);
		if (parent == null)
			return;
		
		// ��������:�ض�����ר��
		Subject pparent = null;
		if (parent.getSubtype() == Subject.SUBTYPE_SUBSUBJECT) {
			String category = news.getCategory();
			int index = category.indexOf(Global.CMSSEP + String.valueOf(parentId));
			category = category.substring(0, index);
			index = category.lastIndexOf(Global.CMSSEP);
			String pparentIdParam = category.substring(index + 1);
			Integer pparentId = new Integer(-1);
			try {
				pparentId = new Integer(pparentIdParam);
			}
			catch (Exception e) {
				return;
			}
			
			pparent = (Subject)ItemManager.getInstance().get(pparentId, EntityItem.class);
			// ������������ר������:�ṹ����,������
			if (pparent == null /*|| pparent.getSubtype() == Subject.SUBTYPE_SUBSUBJECT*/)
				return;
		}
		
		// ͬ��:������
		synchronized (lock4Parent) {	
			// һ��:�û�
			LRUMap parentMap = null;
			if (!userMap4Parent.containsKey(userId)) {
				parentMap = new LRUMap(ACTIVE_PARENT_COUNT);
				userMap4Parent.put(userId, parentMap);
			}
			parentMap = (LRUMap)userMap4Parent.get(userId);
			
			// ����:������
			if (!parentMap.containsKey(parentId) || !activeParents.containsKey(parentId)) {
				
				// ������
				String nameParent = parent.getName();
				if (parentId.toString().equals(parent.getName())) {
					nameParent = parent.getDesc();
				}
				
				String namePParent = null;
				String displayParent = null;
				
				if (pparent != null) {
					// ��������
					namePParent = pparent.getName();
					if ((pparent.getId() + "").equals(pparent.getName())) {
						namePParent = pparent.getDesc();
					}
					
					String nameParentTemp = nameParent;
					if (SogouRelatives.getRealLength(nameParent) > 16) {
						nameParentTemp = SogouRelatives.trimToLength(nameParent, 2, 0);
						nameParentTemp += "..";
						
						String reverseContent = StringUtils.reverse(nameParent);
						reverseContent = SogouRelatives.trimToLength(reverseContent, 5, 0);
						reverseContent = StringUtils.reverse(reverseContent);
						
						nameParentTemp += reverseContent;
					}
					
					displayParent = nameParentTemp + "(";
					displayParent += SogouRelatives.trimToLength(namePParent, 8, 0);
					displayParent += ")";
				}
				else {
					displayParent = SogouRelatives.trimToLength(nameParent, 16, 0);
				}
				StatisticItem parentItem = new StatisticItem(
						parent.getId(), 
						nameParent, 
						namePParent, 
						parent.getSubtype(),
						parent.getChannel(),
						displayParent);
				
				if (!parentMap.containsKey(parentId)) {
					parentMap.put(parentId, parentItem);
				}
				
				if (!activeParents.containsKey(parentId)) {
					activeParents.put(parentId, parentItem);
				}
			}
			
			// �������
			parentMap.get(parentId);
			activeParents.get(parentId);
		}
		
		// ý��id
		Integer mediaId = new Integer(news.getMedia());
		
		// ʵ��
		Media media = null;
		if (mediaId.intValue() > 0) {
			media = (Media)ItemManager.getInstance().get(mediaId, Media.class);
		}
		
		// ͬ��:ý��
		if (media != null) {
			synchronized (lock4Media) {
				if (media != null) {
					// һ��:Ƶ��->ý��
					LRUMap mediaMap = null;
					if(!userMap4Media.containsKey(userId)) {
						mediaMap = new LRUMap(ACTIVE_MEDIA_COUNT);
						userMap4Media.put(userId, mediaMap);
					}
					mediaMap = (LRUMap)userMap4Media.get(userId);
					
					// ����:
					if (!mediaMap.containsKey(mediaId)) {
						StatisticItem mediaItem = new StatisticItem(media.getId(), media.getName());
						mediaMap.put(mediaId, mediaItem);
					}
					mediaMap.get(mediaId); // �������
				}
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static void load() {
		loadActiveParents();
	}
	
	private static void loadActiveParents() {
		
		String fileName = getActiveParentsFileName();
		File file = new File(fileName);
		if (!file.exists() || !file.canRead() || !file.isFile())
			return;

		FileInputStream fiStream = null;
		InputStreamReader isReader = null;
		try {
			SAXReader reader = new SAXReader();

			fiStream = new FileInputStream(file);
			isReader = new InputStreamReader(fiStream, "GBK");
			Document document = reader.read(isReader);
			
			Element root = document.getRootElement();
			Element parentsElement = root.element("parents");
			
			Iterator iterParent = parentsElement.elementIterator("parent");
			while (iterParent.hasNext()) {
				Element parentElement = (Element)iterParent.next();
				
				int parentId = getAttrIntValue(parentElement, "id", -1);
				String parentName = getAttrStringValue(parentElement, "name", null);
				String parentParent = getAttrStringValue(parentElement, "parent", null);
				int parentType = getAttrIntValue(parentElement, "type", -1);
				int parentChannel = getAttrIntValue(parentElement, "channel", -1);
				String parentDisplay = getAttrStringValue(parentElement, "display", null);
				if (parentId == -1 || parentName == null || 
						parentDisplay == null || parentType == -1 || parentChannel == -1)
					continue;
				StatisticItem parent = new StatisticItem(parentId, 
						parentName, parentParent, parentType, parentChannel, parentDisplay);
				
				activeParents.put(new Integer(parentId), parent);
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (isReader != null) {
					isReader.close();
					isReader = null;
				}
				if (fiStream != null) {
					fiStream.close();
					fiStream = null;
				}
				if (file != null)
					file = null;
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	private static void loadParentsAndMedias(int editorId) {
		
		String fileName = getUserFileName(editorId);
		File file = new File(fileName);
		if (!file.exists() || !file.canRead() || !file.isFile())
			return;

		FileInputStream fiStream = null;
		InputStreamReader isReader = null;
		try {
			SAXReader reader = new SAXReader();

			fiStream = new FileInputStream(file);
			isReader = new InputStreamReader(fiStream, "GBK");
			Document document = reader.read(isReader);
			
			Element root = document.getRootElement();

			// ͬ��:������
			synchronized (lock4Parent) {
				
				LRUMap parentMap = new LRUMap(ACTIVE_PARENT_COUNT);
				userMap4Parent.put(new Integer(editorId), parentMap);
				
				Element parentsElement = root.element("parents");
				
				Iterator iterParent = parentsElement.elementIterator("parent");
				while (iterParent.hasNext()) {
					Element parentElement = (Element)iterParent.next();
					
					int parentId = getAttrIntValue(parentElement, "id", -1);
					String parentName = getAttrStringValue(parentElement, "name", null);
					String parentParent = getAttrStringValue(parentElement, "parent", null);
					int parentType = getAttrIntValue(parentElement, "type", -1);
					int parentChannel = getAttrIntValue(parentElement, "channel", -1);
					String parentDisplay = getAttrStringValue(parentElement, "display", null);
					if (parentId == -1 || parentName == null || 
							parentDisplay == null || parentType == -1 || parentChannel == -1)
						continue;
					StatisticItem parent = new StatisticItem(parentId, 
							parentName, parentParent, parentType, parentChannel, parentDisplay);
					
					parentMap.put(new Integer(parentId), parent);
				}
			}
			
			// ͬ��:ý��
			synchronized (lock4Media) {
				
				LRUMap mediaMap = new LRUMap(ACTIVE_PARENT_COUNT);
				userMap4Media.put(new Integer(editorId), mediaMap);
				
				Element mediasElement = root.element("medias");
				
				Iterator iterMedia = mediasElement.elementIterator("media");
				while (iterMedia.hasNext()) {
					Element mediaElement = (Element)iterMedia.next();
					
					int mediaId = getAttrIntValue(mediaElement, "id", -1);
					String mediaName = getAttrStringValue(mediaElement, "name", null);
					if (mediaId == -1 || mediaName == null)
						continue;
					StatisticItem media = new StatisticItem(mediaId, mediaName);
					
					mediaMap.put(new Integer(mediaId), media);
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (isReader != null) {
					isReader.close();
					isReader = null;
				}
				if (fiStream != null) {
					fiStream.close();
					fiStream = null;
				}
				if (file != null)
					file = null;
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	public static void save() {
		
		Iterator iterParent = userMap4Parent.keySet().iterator();
		while (iterParent.hasNext()) {
			int editorId = ((Integer)iterParent.next()).intValue();
			String fileName = getUserFileName(editorId);
			
			List parentList = getTopParentList(FILE_XML_RECORD_COUNT, editorId);
			List mediaList = getTopMediaList(FILE_XML_RECORD_COUNT, editorId);
			
			saveParentsAndMedias(fileName, parentList, mediaList);
		}
		
		String activeParentsFileName = getActiveParentsFileName();
		List activeParentsList = Collections.list(Collections.enumeration(activeParents.values()));
		saveParentsAndMedias(activeParentsFileName, activeParentsList, null);
	}
	
	private static void saveParentsAndMedias(String fileName, List parentList, List mediaList) {
		
		FileOutputStream foStream = null;
		OutputStreamWriter osWriter = null;
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement(FILE_XML_ROOT_NAME);

			if (parentList != null && parentList.size() > 0) {
				Element parentsElement = root.addElement("parents");
				
				for (int i = 0; i < parentList.size(); i++) {
					StatisticItem parent = (StatisticItem)parentList.get(i);
					
					Element parentElement = parentsElement.addElement("parent");
					parentElement.addAttribute("id", parent.getId() + "");
					parentElement.addAttribute("name", parent.getName());
					parentElement.addAttribute("parent", parent.getParent());
					parentElement.addAttribute("type", parent.getType() + "");
					parentElement.addAttribute("channel", parent.getChannel() + "");
					parentElement.addAttribute("display", parent.getDisplay());
				}
			}

			if (mediaList != null && mediaList.size() > 0) {
				Element mediasElement = root.addElement("medias");
				
				for (int i = 0; mediaList != null && i < mediaList.size(); i++) {
					StatisticItem media = (StatisticItem)mediaList.get(i);
					
					Element mediaElement = mediasElement.addElement("media");
					mediaElement.addAttribute("id", media.getId() + "");
					mediaElement.addAttribute("name", media.getName());
				}
			}

			// ����
			foStream = new FileOutputStream(fileName);
			osWriter = new OutputStreamWriter(foStream, "GBK");

			// ��ʽ
			OutputFormat format = OutputFormat.createPrettyPrint();

			// ���
			XMLWriter writer = new XMLWriter(osWriter, format);
			writer.write(root);
			writer.close();
		} catch (Exception e) {
			log.error("save stat-data to file error." + e.getMessage());
		}
	}
	
	private static String getUserFileName(int userId) {
		
		if (userId <= 0)
			return null;
		
		User user = (User)ItemManager.getInstance().get(new Integer(userId), User.class);
		if (user == null)
			return null;
		
		return FILE_XML_DIR + user.getName() + FILE_XML_EXT;
	}
	
	private static String getActiveParentsFileName() {
		
		return FILE_XML_DIR + "activeParents" + FILE_XML_EXT;
	}
		
	private static int getAttrIntValue(Element element, String attrName, int defaultValue) {

		Attribute attr = element.attribute(attrName);

		if (attr == null)
			return defaultValue;

		if (attr.getValue() != null) {
			try {
				return Integer.parseInt(attr.getValue());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}

		return defaultValue;
	}

	private static String getAttrStringValue(Element element, String attrName, String defaultValue) {

		Attribute attr = element.attribute(attrName);

		if (attr == null)
			return defaultValue;

		if (attr.getValue() != null) {
			return attr.getValue();
		}

		return defaultValue;
	}

	/////////////////////////////////////////////////////////////////////////////
	
	public static List searchMedias(Authentication auth, String keyword, int maxCount) {
		
		if (auth == null || keyword == null || keyword.trim().length() == 0)
			return null;
		if (maxCount <= 0)
			maxCount = 30;
		keyword = keyword.trim().toLowerCase();
		
//		Map mediaMap = ItemUtil.getMediaMap(auth);
//		List mediaList = Collections.list(Collections.enumeration(mediaMap.values()));
		List mediaList = ItemManager.getInstance().getList(Media.class);
		if (mediaList == null)
			return null;
		
		List result = new ArrayList();
		
		int mediaCount = 0;
		for (int i = 0; mediaCount < maxCount && i < mediaList.size(); i++) {
			Media media = (Media)mediaList.get(i);
			String name = media.getName();
			if (name == null || name.trim().length() == 0)
				continue;
			name = name.toLowerCase();
			
			if (name.indexOf(keyword) > -1) {
				StatisticItem item = new StatisticItem(media.getId(), media.getName());
				result.add(item);
				mediaCount++;
			}
		}
		
		return result;
	}
	
	public static List searchParents(Authentication auth, String keyword, int maxCount) {
		
		if (auth == null || keyword == null || keyword.trim().length() == 0) 
			return null;
		
		if (maxCount <= 0)
			maxCount = 30;
		
		List channelList = auth.getChannelList();
		if (channelList == null || channelList.size() == 0)
			return null;
		
		List parentList = Collections.list(Collections.enumeration(activeParents.values()));
		if (parentList == null)
			return null;
		
		List result = new ArrayList();
		
		int parentCount = 0;
		for (int i = 0; parentCount < maxCount && i < parentList.size(); i++) {
			StatisticItem item = (StatisticItem)parentList.get(i);
			int channelId = item.getChannel();
			
			boolean hasPerm = false;
			for (int j = 0; j < channelList.size(); j++) {
				int tempId = ((Channel)channelList.get(j)).getId();
				if (tempId == channelId) {
					hasPerm = true;
					break;
				}
			}
			
			if (hasPerm) {
				String name = item.getName();
				String parent = item.getParent();
				if ((name != null && name.indexOf(keyword) > -1) || 
						(parent != null && parent.indexOf(keyword) > -1)) {
					result.add(item);
					parentCount++;
				}
			}
		}
		
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		
	}
}


