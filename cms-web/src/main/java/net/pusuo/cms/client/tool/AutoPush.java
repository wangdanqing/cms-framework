package net.pusuo.cms.client.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.hexun.cms.Configuration;
import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.auth.User;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.auth.Authentication;
import com.hexun.cms.client.auth.AuthenticationFactory;
import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.ManagerFacade;
import com.hexun.cms.client.biz.NewsManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.util.ItemUtil;
import com.hexun.cms.core.Media;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Subject;
import com.hexun.cms.util.Util;

/**
 * �Զ�����������<br />
 * ��ݶ���õ���������ĳ������ID��Ӧ������һ��������ID,����֧���б�<br />
 * �Ժ���Ҫ���Ը�ݹؼ��ֽ����Զ�����������<br />
 * 
 * <p>
 * �����xml��ʽ <br />
 * &lt;push&gt;<br />
 * &lt;name from=&quot;1123&quot; to=&quot;332&quot; /&gt;<br />
 * &lt;name from=&quot;1124&quot; to=&quot;332,31231&quot; /&gt;<br />
 * &lt;/push&gt;
 * </p>
 * <p>
 * ����λ����/opt/autopush/
 * </p>
 * 
 * @author denghua
 * 
 */
public class AutoPush {
	public final static String FILE_NAME = Configuration.getInstance().get("cms4.client.autopush.file");

	private static AutoPush instance = new AutoPush();

	private Map namesMap = new HashMap();

	private List mediaList = new ArrayList();

	private static final Log log = LogFactory.getLog(AutoPush.class);

	public static final String PNAME_TYPE = "pname";

	public static final String MEDIA_TYPE = "media";

	private AutoPush() {
		try {
			loadXml();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
	}

	public Map getNamesMap() {
		return namesMap;
	}

	public List getMediaList() {
		return mediaList;
	}

	/**
	 * �жϸ����ţ��Լ��û��Ƿ���Ҫ��������
	 * 
	 * @param request
	 * @param news
	 * @return
	 */
	public boolean isNeedPush(HttpServletRequest request, News news) {
		if ("auto".equals(request.getParameter("newspush")))
			return true;
		/*
		 * ֻά��ҳ����ж�, �������ԭ����ʵ��,���ڷϳ����.
		 * 
		 * int pid = news.getPid(); String pname =
		 * ItemManager.getInstance().get(new Integer(pid),
		 * EntityItem.class).getName();
		 * 
		 * log.debug("isneedPush pname:" + pname); // ����map������û�ж�Ӧ��pname Set a =
		 * findByPname(pname); a.addAll(findRuleInCategory(news)); if (a.size() >
		 * 0) return true;
		 */
		return false;

	}

	private Set findRuleInCategory(News news) {
		Set result = new HashSet();
		// ���û��,��category������û��.
		String category = news.getCategory();

		String[] cates = category.split(";");
		for (int i = 0; i < cates.length; i++) {
			if (!StringUtils.isNumeric(cates[i]))
				continue;
			String name = ItemUtil.getItemNamesByIds(cates[i]);
			if (namesMap.containsKey(name)) {
				PushRule rule = (PushRule) namesMap.get(name);
				if (rule.isDeepSearch()) {
					result.add(rule);
					return result;
				}
				break;
			}
		}

		return result;
	}

	/**
	 * �������͸�����
	 * 
	 * @param from
	 *            from���һ�����֣���type���
	 * @param to
	 *            to ��?���򸸶�������֣���;�Ÿ���
	 * @param deepSearch
	 *            �Ƿ��Բ��ң����Ϊtrue,����category, false����pname
	 * @param changePrio
	 *            �������ŵ�Ȩ��,���Ϊ-1���Ǻ�ԭ������ͬ
	 * @param isCopy
	 *            �Ƿ񿽱����š� ���Ϊtrue����Ϊ������ falseΪ���ӷ�ʽ
	 * @param type
	 *            ���Ͳ���, newsΪ��������, mediaΪý������
	 */
	public void add(String from, String to, boolean deepSearch,int fromPriority, int changePrio, boolean isCopy, String type,
			boolean isSpanChannel) {
		PushRule rule = new PushRule();
		rule.setFromName(from);
		rule.setToName(to);
		rule.setDeepSearch(deepSearch);
		rule.setFromPriority(fromPriority);
		rule.setChangePriority(changePrio);
		rule.setCopyNews(isCopy ? "true" : "false");
		rule.setType(type);
		rule.setSpanChannel(isSpanChannel);

		dispatchRuleToCache(rule);

		saveRules();
	}

	/**
	 * @param from
	 * @param rule
	 * @param type
	 */
	private void dispatchRuleToCache(PushRule rule) {
		if (PNAME_TYPE.equals(rule.getType())) {
			namesMap.put(rule.getFromName(), rule);
		} else if (MEDIA_TYPE.equals(rule.getType())) {
			mediaList.add(rule);
		}
	}

	/**
	 * ɾ��
	 * 
	 * @param fromName
	 */
	public void delete(String fromName) {
		namesMap.remove(fromName);
		saveRules();
	}
	
	public void deleteMedia(int id){
		mediaList.remove(id);
		saveRules();
	}

	private void saveRules() {
		Element pushElement = new Element("push");
		Document doc = new Document(pushElement);

		setupPushElement(pushElement);

		File xmlFile = new File(FILE_NAME);
		if (!xmlFile.exists())
			try {
				xmlFile.createNewFile();
			} catch (IOException e1) {
				log.error("create file error file=" + xmlFile.getPath());
			}
		Format format = Format.getPrettyFormat();
		XMLOutputter outputter = new XMLOutputter(format.setEncoding("ISO_8859_1"));
		// XMLOutputter outputter = new XMLOutputter();
		FileWriter writer;
		try {
			writer = new FileWriter(xmlFile);
			outputter.output(doc, writer);
			writer.close();
		} catch (IOException e) {
			log.error("write file error : " + e);
		}

	}

	/**
	 * ���ñ����pushElement
	 * 
	 * @param pushElement
	 */
	private void setupPushElement(Element pushElement) {

		setupEveryMapPushElement(pushElement, namesMap);

		setupEveryMapPushElement(pushElement, mediaList);
	}

	private void setupEveryMapPushElement(Element pushElement, List list) {
		for (int i = 0; i < mediaList.size(); i++) {
			PushRule rule = (PushRule) mediaList.get(i);
			if (rule == null)
				continue;
			pushElement.addContent(new Element("name").setAttribute("from", Util.GBKToUnicode(rule.getFromName()))
					.setAttribute("to", Util.GBKToUnicode(rule.getToName())).setAttribute("deepsearch",
							rule.isDeepSearch() ? "true" : "false").setAttribute("fromPriority",""+rule.getFromPriority()).setAttribute("changeprio",
							"" + rule.getChangePriority()).setAttribute("copynews", rule.getCopyNews()).setAttribute(
							"type", rule.getType() == null ? PNAME_TYPE : rule.getType()).setAttribute("spanChannel",
							rule.isSpanChannel() ? "true" : "false"));
		}
	}

	/**
	 * ����xml������pushElement��ÿ������
	 * 
	 * @param pushElement
	 * @param map
	 */
	private void setupEveryMapPushElement(Element pushElement, Map map) {
		Iterator key = map.keySet().iterator();

		while (key.hasNext()) {
			String from = (String) key.next();
			PushRule rule = (PushRule) map.get(from);
			if (rule == null)
				continue;
			pushElement.addContent(new Element("name").setAttribute("from", Util.GBKToUnicode(rule.getFromName()))
					.setAttribute("to", Util.GBKToUnicode(rule.getToName())).setAttribute("deepsearch",
							rule.isDeepSearch() ? "true" : "false").setAttribute("fromPriority",""+rule.getFromPriority()).setAttribute("changeprio",
							"" + rule.getChangePriority()).setAttribute("copynews", rule.getCopyNews()).setAttribute(
							"type", rule.getType() == null ? PNAME_TYPE : rule.getType()).setAttribute("spanChannel",
							rule.isSpanChannel() ? "true" : "false"));
		}
	}

	/**
	 * ���PNAME����PushRule����
	 * 
	 * @param pname
	 * @return
	 */
	private Set findRuleByPname(String pname) {
		Set result = new HashSet();
		Iterator namesIterator = namesMap.keySet().iterator();
		while (namesIterator.hasNext()) {
			String names = (String) namesIterator.next();
			String[] nameArray = names.split(Global.CMSSEP);
			for (int i = 0; i < nameArray.length; i++) {
				String name = nameArray[i];
				if (name.equals(pname)) {
					result.add(namesMap.get(names));
				}
			}
		}
		return result;
	}

	/**
	 * ��news��,�ҵ������͵Ĺ���
	 * 
	 * @param news
	 * @return
	 */
	private Set findRules(News news) {
		String pname = ItemUtil.getItemNamesByIds("" + news.getPid());
		Set rules = findRuleByPname(pname);// �ȸ��pname��

		// ���pnameû���ҵ�,��õ�category,�ٴ�category����.
		rules.addAll(findRuleInCategory(news));

		rules.addAll(findRuleInMedia(news));
		return rules;

	}

	private Set findRuleInMedia(News news) {
		Set result = new HashSet();
		int mediaId = news.getMedia();
		if (mediaId == -1)
			return result;
		Item item = ItemManager.getInstance().get(new Integer(mediaId), Media.class);
		if (item == null)
			return result;
		String mediaName = item.getName();
		for (int i = 0; i < mediaList.size(); i++) {
			PushRule rule = (PushRule) mediaList.get(i);

			if (rule.getFromName().equals(mediaName)) {
				if (rule.isSpanChannel()) { // ����ǿ�Ƶ����,�����ֱ����ӵ������.
					result.add(rule);
				} else { // ����ǿ�Ƶ����,��Ҫ�ж����ŵ�subjectƵ��,�ͱ����͵�Ƶ���Ƿ�һ��.
					if (canSpanChannelPush(news, rule)) {
						result.add(rule);
					}
				}
			}

		}

		return result;
	}

	/**
	 * �жϿ�Ƶ����PUSH
	 * 
	 * @param news
	 * @param rule
	 * @return
	 */
	private boolean canSpanChannelPush(News news, PushRule rule) {
		String toName = rule.getToName();
		String ids = ItemUtil.getItemIdsByNames(toName);
		String[] _ids = ids.split(";");
		int newsChannelId = news.getChannel();
		boolean canPush = false;
		for (int i = 0; i < _ids.length; i++) {
			if (StringUtils.isNotBlank(_ids[i]) && StringUtils.isNumeric(_ids[i])) {
				Subject subject = (Subject) ItemManager.getInstance().get(new Integer(_ids[i]), Subject.class);
				if (newsChannelId != subject.getChannel()) {
					canPush = false;
					break; // �����һ������push,�����еĶ�����PUSH
				} else {
					canPush = true;
				}
			}
		}
		return canPush;
	}

	public static AutoPush getInstance() {
		return instance;
	}

	private synchronized void loadXml() throws Exception {
		clearCache();

		File xmlFile = new File(FILE_NAME);
		if (!xmlFile.exists())
			return;

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(xmlFile);
		} catch (JDOMException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}

		Element root = doc.getRootElement();
		List namesElement = root.getChildren("name");
		for (int i = 0; i < namesElement.size(); i++) {
			Element nameElement = (Element) namesElement.get(i);
			PushRule rule = new PushRule();

			String from = Util.unicodeToGBK((nameElement.getAttributeValue("from")));
			rule.setFromName(from);
			rule.setToName(Util.unicodeToGBK(nameElement.getAttributeValue("to")));
			rule.setDeepSearch("true".equalsIgnoreCase(nameElement.getAttributeValue("deepsearch")));
			String changePriority = nameElement.getAttributeValue("changeprio");
			String fromPriority = nameElement.getAttributeValue("fromPriority");
			if (StringUtils.isEmpty(changePriority) || !StringUtils.isNumeric(changePriority))
				changePriority = "70";
			if (StringUtils.isEmpty(fromPriority) || !StringUtils.isNumeric(fromPriority))
				fromPriority = "70";
			rule.setFromPriority(new Integer(fromPriority).intValue());
			rule.setChangePriority(new Integer(changePriority).intValue());
			rule.setCopyNews(nameElement.getAttributeValue("copynews"));
			rule.setType(nameElement.getAttributeValue("type"));
			rule.setSpanChannel("true".equalsIgnoreCase(nameElement.getAttributeValue("spanChannel")));
			dispatchRuleToCache(rule);

		}
	}

	/**
	 * 
	 */
	private void clearCache() {
		namesMap.clear();
		mediaList.clear();
	}

	/**
	 * �Զ��������� �ڳ������ж�������,ֻ���ͷ��������,���׳��쳣��.
	 * 
	 * @param news
	 * @return ���ص����Ѿ����ͳɹ���ר�����,��;�Ÿ���
	 */
	public String pushIt(News news) {
		String pushedPNames = ItemUtil.getPnameById(news.getPushrecord());
		String[] pushedPNamesArray = pushedPNames.toString().split(Global.CMSSEP);

		Set rules = findRules(news);// �õ���Ҫ�Զ����͵Ĺ������.
		Iterator rulesIterator = rules.iterator();
		Set alreadyPush = new HashSet(Arrays.asList(pushedPNamesArray)); // ������Ҫ�������pushrule����,
		News tmp=null;
		// ÿ�������б����͵ĸ������ж��,���ܲ����ظ�.
		// �����һ��SET,�����б���NAME������
		while (rulesIterator.hasNext()) {

			PushRule rule = (PushRule) rulesIterator.next();
			String toNames = rule.getToName();

			
			String[] toNameArray = toNames.split(Global.CMSSEP);
			for (int i = 0; i < toNameArray.length; i++) {
				String toName = toNameArray[i];
				//�Զ����������ж�
				if (alreadyPush.contains(toName))
					continue;
				//Ȩ���Ƿ���ڹ�������ԴȨ��
				if(news.getPriority()!=rule.getFromPriority())
					continue;
				
				Map extend=new HashMap();
				//int changepriority=rule.getChangePriority();
				
				extend.put(NewsManager.PROPERTY_NAME_PUSH_PRIORITY,
						new Integer(rule.getChangePriority()));
				
				extend.put(NewsManager.PROPERTY_NAME_PUSH_TIME,
						new Integer( NewsManager.PROPERTY_NAME_PUSH_TIME_NOW));
				int themode = NewsManager.PROPERTY_NAME_PUSH_MODE_LINK;
				if ("true".equalsIgnoreCase(rule.getCopyNews())) {
					themode = NewsManager.PROPERTY_NAME_PUSH_MODE_COPY;
				}
				extend.put(NewsManager.PROPERTY_NAME_PUSH_MODE,
						new Integer(themode));
				
				ItemManager itemManager = ItemManager.getInstance();
				User user = (User)itemManager.get(new Integer(news.getEditor()),User.class);
				Authentication auth = null;
				try {
				            auth = AuthenticationFactory.getAuthentication(user.getName(), user.getPasswd());
				} catch (UnauthenticatedException ue) {
				            auth = null;
				}
				
				extend.put(NewsManager.PROPERTY_NAME_AUTH,auth);
				
				try {
					tmp=ManagerFacade.getNewsManager().pushNews(news, toName, extend);
					alreadyPush.add(toName);
					
				} catch (ParentNameException e) {
					log.error(e);
				} catch (PropertyException e) {
					log.error(e);
				} catch (UnauthenticatedException e) {
					log.error(e);
				} catch (DaoException e) {
					log.error(e);
				}
				
			}
		}
		// ��news��pushrecord�ֶ����¸�ֵ
		if(tmp==null)return "";
		return tmp.getPushrecord();
	}

	String[] exclude(String[] valArray) {
		for (int i = 0; i < valArray.length; i++) {
			String iv = valArray[i];
			for (int j = i + 1; j < valArray.length; j++) {
				String jv = valArray[j];
				if (iv.equals(jv)) {
					valArray[j] = "-1";
				}
			}
		}

		int count = 0;
		for (int i = 0; i < valArray.length; i++) {
			if (!valArray[i].equals("-1")) {
				count++;
			}
		}

		String[] ret = new String[count];
		for (int i = valArray.length - 1; i >= 0; i--) {
			if (!valArray[i].equals("-1")) {
				ret[--count] = valArray[i];
			}
		}

		return ret;
	}

	public void editMedia(int seq,String from, String to, boolean deepSearch, int fromPriority,int changePrio, boolean isCopy, String type,
			boolean isSpanChannel){
		mediaList.remove(seq);
		add(from, to, deepSearch,fromPriority, changePrio, isCopy, type, isSpanChannel);
	}
}
