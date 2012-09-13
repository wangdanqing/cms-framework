/*
 * Created on 2005-7-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import com.hexun.cms.Global;
import com.hexun.cms.Item;
import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Subject;
import com.hexun.cms.core.Template;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EntityParamUtil {

	public static final String ENTITY_PARAM_SEPARATOR = "#";
	public static final String ENTITY_PARAM_SUBSEPARATOR = "&";
	
	public static final String ENTITY_PARAM_STOCK_ORG = "stock.org";
	public static final String ENTITY_PARAM_STOCK_CODE = "stock.code";
	
	public static final String CHANNEL_NAME_BUSINESS = "business.hexun.com";
	public static final int CHANNEL_ID_BUSINESS = 104;
	
	public static final int ENTITY_PARAM_INDEX_STOCK_ORG = 0;
	public static final int ENTITY_PARAM_INDEX_STOCK_CODE = 1;
	public static final int ENTITY_PARAM_INDEX_BLOG_AUTHOR = 2;
	public static final int ENTITY_PARAM_INDEX_BLOG_ARTICLE = 3;
	
	/**
	 * Append item after oldParam, and format is: item1SEPitem2SEPitem3SEP...SEP.
	 * If newItem is null or empty, a SEP will be appended too.
	 * If the newItem is assembled, use sub-separator pls.
	 * first param: stock organization,
	 * second param: commended stock code,
	 * third param: sourceBlog,
	 * fourth param: sourceArticle.
	 * @param oldParam
	 * @param newItem
	 * @return
	 */
	public static String appendEntityParamItem(String oldParam, Object newItem)
	{
		if (oldParam == null)
			oldParam = "";
		else
			oldParam = oldParam.trim();
		
		if (newItem == null)
			newItem = "";
		
		String newParam = oldParam + newItem.toString() + ENTITY_PARAM_SEPARATOR;
		
		return newParam;
	}
	
	/**
	 * Decode entity params.See method: appendEntityParamItem(...)
	 * @param params
	 * @return
	 */
	public static String encodeEntityParam4Map(ListOrderedMap params)
	{
		if (params == null || params.size() == 0)
			return null;
		
		String result = null;
		
		OrderedMapIterator iter = params.orderedMapIterator();
		while(iter.hasNext())
		{
			String key = (String)iter.next();
			Object value = params.get(key);
			result = appendEntityParamItem(result, value);
		}
		
		return result;
	}
	
	/**
	 * Decode entity params.See method: appendEntityParamItem(...)
	 * @param params: type of list item must be java.lang.String.
	 * @return
	 */
	public static String encodeEntityParam4List(List params)
	{
		if (params == null || params.size() == 0)
			return null;
		
		String result = null;
		
		for (int i = 0; i < params.size(); i++)
		{
			result = appendEntityParamItem(result, (String)params.get(i));
		}
		
		return result;
	}
	
	/**
	 * Decode entity params.See method: appendEntityParamItem(...)
	 * @param param
	 * @return
	 */
	public static ListOrderedMap decodeEntityParam2Map(String param)
	{
		if (param == null || param.indexOf(ENTITY_PARAM_SEPARATOR) < 0)
			return null;
		
		String[] params = split(param, ENTITY_PARAM_SEPARATOR);
		if (params == null || params.length < 2)
			return null;
		
		ListOrderedMap result = new ListOrderedMap();
		
		try
		{
			String stockOrg = params[0];
			result.put(ENTITY_PARAM_STOCK_ORG, stockOrg);
			
			String stockCode = params[1];
			result.put(ENTITY_PARAM_STOCK_CODE, stockCode);
		}
		catch (NumberFormatException e)
		{
		}
		
		return result;
	}
	
	/**
	 * Decode entity params.See method: appendEntityParamItem(...)
	 * @param param
	 * @return
	 */
	public static List decodeEntityParam2List(String param)
	{
		if (param == null || param.indexOf(ENTITY_PARAM_SEPARATOR) < 0)
			return null;
		
		String[] params = split(param, ENTITY_PARAM_SEPARATOR);
		if (params == null || params.length == 0)
			return null;

		List result = new ArrayList();
		
		for (int i = 0; i < params.length; i++)
		{
			result.add(params[i]);
		}
		
		return result;
	}
	
	/**
	 * Split string into array by seperator.
	 * Method java.lang.String.split() has some problem.
	 * @param param
	 * @return
	 */
	private static String[] split(String param, String delim)
	{
		if (param == null || delim == null)
			return null;
		
		List items = new ArrayList();
		String item = null;
		int index = param.indexOf(delim);
		while (index > -1)
		{
			item = param.substring(0, index);
			items.add(item);
			
			param = param.substring(index + delim.length());
			index = param.indexOf(delim);
		}
		
		String[] result = new String[items.size()];
		for (int i = 0; i < items.size(); i++)
		{
			item = (String)items.get(i);
			result[i] = item;
		}
		
		return result;
	}
	
	/**
	 * Get entity param item
	 * @param param
	 * @param key: It`s strongly recommended that the key is pre-defined constant.
	 * @return
	 */
	public static Object getEntityParamItem(String param, String key)
	{
		if (key == null || key.trim().length() == 0)
			return null;
		else
			key = key.trim();
		
		ListOrderedMap params = decodeEntityParam2Map(param);
		if (params == null)
			return null;
		
		return params.get(key);
	}

	/**
	 * Get entity param item
	 * @param param
	 * @param index: base 0
	 * @return
	 */
	public static String getEntityParamItem(String param, int index)
	{
		if (index < 0)
			return null;
		
		List params = decodeEntityParam2List(param);
		if (params == null)
			return null;
		
		if (params.size() < index + 1)
			return null;
		
		return (String)params.get(index);
	}
	
	/**
	 * Set entity param item value
	 * @param param
	 * @param key
	 * @param value
	 * @return: New entity param.Return is null if parameter is invalid.
	 */
	public static String setEntityParamItem(String param, String key, Object value)
	{
		if (key == null || key.trim().length() == 0)
			return null;
		else
			key = key.trim();
		
		if (value == null)
			value = "";
		
		ListOrderedMap params = decodeEntityParam2Map(param);
		if (params == null)
			return null;
		
		if (!params.containsKey(key))
			return null;
		else
			params.put(key, value);
		
		return encodeEntityParam4Map(params);
	}
	
	/**
	 * Set entity param item value
	 * @param param
	 * @param index: base 0
	 * @param value
	 * @return: New entity param.Return is null if parameter is invalid.
	 */
	public static String setEntityParamItem(String param, int index, String value)
	{
		if (index < 0)
			return null;
		
		if (value == null)
			value = "";
		
		List params = decodeEntityParam2List(param);
		if (params == null || params.size() < index + 1)
			return null;
		
		params.set(index, value);
		
		return encodeEntityParam4List(params);
	}
	
	/**
	 * Force to set entity item value
	 * @param param
	 * @param index: base 0
	 * @param value
	 * @return: New entity param.Return is null if parameter is invalid.
	 */
	public static String forceEntityParamItem(String param, int index, String value)
	{
		if (index < 0)
			return null;
		
		if (value == null)
			value = "";
		
		if (param == null)
			param = "";
		
		List params = decodeEntityParam2List(param);
		
		if (params != null && params.size() > index)
			return setEntityParamItem(param, index, value);
		
		String result = param;
				
		int count = 0;
		if (params == null)
			count = index;
		else
			count = index - params.size();
		
		for (int i = 0; i < count; i++)
		{
			result = appendEntityParamItem(result, "");
		}
		
		result = appendEntityParamItem(result, value);
		
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * ��ȡģ���ID�б�
	 */
	public static List getTemplateIdList(String templateParam) {
		
		if (templateParam == null || templateParam.trim().length() == 0)
			return null;
		templateParam = templateParam.trim();
		
		List templateIdList = new ArrayList();
		
		String[] templates = templateParam.split(Global.CMSSEP);
		for (int i = 0; templates != null && i < templates.length; i++) {
			String template = templates[i];
			if (template == null || template.trim().length() == 0 
					|| template.indexOf(Global.CMSCOMMA) == -1) 
				continue;
			template = template.trim();
			String templateIdParam = template.split(Global.CMSCOMMA)[0];
			Integer templateId = null;
			try {
				templateId = new Integer(templateIdParam);
			}
			catch (Exception e) {
				continue;
			}
			templateIdList.add(templateId);
		}
		
		return templateIdList;
	}
	
	/**
	 * ��ȡ������ģ���б�
	 * @param oldTemplateParam
	 * @param newTemplateParam
	 * @return
	 */
	public static List getAddedTemplateIdList(String oldTemplateParam, String newTemplateParam) {
		
		List oldTemplateIdList = getTemplateIdList(oldTemplateParam);
		List newTemplateIdList = getTemplateIdList(newTemplateParam);
		
		if (oldTemplateIdList == null)
			return newTemplateIdList;
		
		if (newTemplateIdList == null)
			return null;
		
		Collection collection = CollectionUtils.subtract(newTemplateIdList, oldTemplateIdList);
		List addedTemplates = new ArrayList(collection);
		
		return addedTemplates;
	}
	
	/**
	 * ��ȡ��ɾ���ģ���б�
	 * @param oldTemplateParam
	 * @param newTemplateParam
	 * @return
	 */
	public static List getDeletedTemplateIdList(String oldTemplateParam, String newTemplateParam) {
		
		return getAddedTemplateIdList(newTemplateParam, oldTemplateParam);
	}
	
	/**
	 * ��ȡID�б�
	 * @param param
	 * @param regex
	 * @return
	 * @throws Exception
	 */
	public static List getIdList(String param, String regex) {
		
		if (param == null || regex == null)
			return null;
		
		Set idSet = null;
		
		String[] ids = param.split(regex);
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (id != null && id.trim().length() > 0) {
					if (idSet == null) {
						idSet = new HashSet();
					}
					idSet.add(new Integer(id.trim()));
				}
			}
		}
		
		return idSet == null ? null : Collections.list(Collections.enumeration(idSet));
	}
	
	/**
	 * ���ID�б�.ֻ��otherIds��Чʱ,�Ż������ID����.
	 * @param otherIds
	 * @param entityId
	 * @return
	 */
	public static List getAllIdList(String otherIds, int entityId) {
		
		if (otherIds == null || otherIds.trim().length() == 0 || entityId <= 0)
			return null;
		
		List allIdList = null;
		
		try {
			otherIds = otherIds.trim();
			allIdList = EntityParamUtil.getIdList(otherIds, Global.CMSSEP);
			if (allIdList != null && allIdList.size() > 0) {
				allIdList.add(new Integer(entityId)); // �ټ���ʵ������
			}
		}
		catch (Exception e) {
			allIdList = null;
		}
		
		return allIdList;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public static final int REFERENCE_MAXIMIZE_COUNT = 100;
	public static final int WEAKREFERENCE_MAXIMIZE_COUNT = 10;
	
	/**
	 * ����ר����ⲿ����
	 * @param subjectId
	 * @param templateId
	 */
	public static boolean updateSubject4AddWeakReference(int subjectId, int templateId) {
		
		boolean ret = false;
		
		EntityItem entity = (EntityItem)ItemManager.getInstance().get(new Integer(subjectId), EntityItem.class);
		// ֻ��(��)ר��/��Ŀ�����ݲ��ܱ��ⲿ����
		if (entity != null && entity.getType() == ItemInfo.SUBJECT_TYPE && templateId > 0) {
			Subject subject = (Subject)ItemManager.getInstance().get(new Integer(subjectId), Subject.class);
			if (subject != null) {
				String templateParam = "" + templateId;			
				String weakReference = subject.getWeakReference();
				
				if (weakReference == null || weakReference.trim().length() == 0) { // ר��δ���ⲿ���ù�
					weakReference = templateParam;
				}
				else {
					// �Ƿ��ظ�.����ʹ��indexOf
					boolean isRepeat = false;
					String[] templateIds = weakReference.split(Global.CMSSEP);
					for (int i = 0; templateIds != null && i < templateIds.length; i++) {
						String id = templateIds[i];
						if (id != null && id.trim().length() > 0) {
							if (templateParam.equalsIgnoreCase(id)) {
								isRepeat = true;
								break;
							}
						}
					}
					
					if (!isRepeat) {
						if (!weakReference.endsWith(Global.CMSSEP)) {
							weakReference += Global.CMSSEP;
						}
						weakReference += templateParam;
					}
				}
				
				//  ��ֹר�ⱻ�������(weakreference�ֶι��)
				int matchesCount = StringUtils.countMatches(weakReference, Global.CMSSEP);
				if (matchesCount > WEAKREFERENCE_MAXIMIZE_COUNT) {
					weakReference = weakReference.substring(weakReference.indexOf(Global.CMSSEP) + 1);
				}
				
				subject.setWeakReference(weakReference);
				Item item = ItemManager.getInstance().update(subject);
				if (item != null) {
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * ɾ��ר����ⲿ����
	 * @param subject
	 * @param templateId
	 */
	public static boolean updateSubject4DeleteWeakReference(int subjectId, int templateId) {
		
		boolean ret = false;
		
		EntityItem entity = (EntityItem)ItemManager.getInstance().get(new Integer(subjectId), EntityItem.class);
		// ֻ��(��)ר��/��Ŀ�����ݲ��ܱ��ⲿ����
		if (entity != null && entity.getType() == ItemInfo.SUBJECT_TYPE) {
			Subject subject = (Subject)ItemManager.getInstance().get(new Integer(subjectId), Subject.class);
			if (subject != null) {
				String weakReference = subject.getWeakReference();
				String templateParam = "" + templateId;
				
				if (weakReference != null && weakReference.trim().length() > 0 
						&& weakReference.indexOf(templateParam) > -1) {
					StringBuffer weakReferenceNew = new StringBuffer();
					
					String[] templateIds = weakReference.split(Global.CMSSEP);
					for (int i = 0; templateIds != null && i < templateIds.length; i++) {
						String id = templateIds[i];
						if (id != null && id.trim().length() > 0) {
							if (templateParam.equalsIgnoreCase(id)) {
								continue;
							}
							else {
								weakReferenceNew.append(id);
								weakReferenceNew.append(Global.CMSSEP);
							}
						}
					}
					int index = weakReferenceNew.lastIndexOf(Global.CMSSEP);
					if (index > -1) {
						weakReferenceNew.delete(index, weakReferenceNew.length());
					}
				
					subject.setWeakReference(weakReferenceNew.toString());
					Item item = ItemManager.getInstance().update(subject);
					if (item != null) {
						ret = true;
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * ����ģ��ķ�������
	 * @param templateId
	 * @param subjectId
	 */
	public static boolean updateTemplate4AddReference(int templateId, int subjectId) {
		
		boolean ret = false;
		
		Template template = (Template)ItemManager.getInstance().get(new Integer(templateId), Template.class);
		EntityItem entity = (EntityItem)ItemManager.getInstance().get(new Integer(subjectId), EntityItem.class);
		// ר��/��ҳģ��
		if (template != null && (template.getType() == ItemInfo.SUBJECT_TYPE
				|| template.getType() == ItemInfo.HOMEPAGE_TYPE)
				&& entity != null && (entity.getType() == ItemInfo.SUBJECT_TYPE
						|| entity.getType() == ItemInfo.HOMEPAGE_TYPE)) {
			String subjectParam = "" + subjectId;			
			String reference = template.getReference();
			
			if (reference == null || reference.trim().length() == 0) { // δ���ⲿ���ù�
				reference = subjectParam;
			}
			else {
				// �Ƿ��ظ�.����ʹ��indexOf
				boolean isRepeat = false;
				String[] subjectIds = reference.split(Global.CMSSEP);
				for (int i = 0; subjectIds != null && i < subjectIds.length; i++) {
					String id = subjectIds[i];
					if (id != null && id.trim().length() > 0) {
						if (subjectParam.equalsIgnoreCase(id)) {
							isRepeat = true;
							break;
						}
					}
				}
				
				if (!isRepeat) {
					if (!reference.endsWith(Global.CMSSEP)) {
						reference += Global.CMSSEP;
					}
					reference += subjectParam;
				}
			}
			
			// ��ֹͨ��ģ�屻�������(reference�ֶι��)
			int matchesCount = StringUtils.countMatches(reference, Global.CMSSEP);
			if (matchesCount > REFERENCE_MAXIMIZE_COUNT) {
				reference = reference.substring(reference.indexOf(Global.CMSSEP) + 1);
			}
			
			template.setReference(reference);
			Item item = ItemManager.getInstance().update(template);
			if (item != null) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * ɾ��ģ��ķ�������
	 * @param templateId
	 * @param subject
	 */
	public static boolean updateTemplate4DeleteReference(int templateId, int subjectId) {
		
		boolean ret = false;
		
		Template template = (Template)ItemManager.getInstance().get(new Integer(templateId), Template.class);
		EntityItem entity = (EntityItem)ItemManager.getInstance().get(new Integer(subjectId), EntityItem.class);
		// ר��/��ҳģ��
		if (template != null && (template.getType() == ItemInfo.SUBJECT_TYPE
				|| template.getType() == ItemInfo.HOMEPAGE_TYPE)
				&& entity != null && (entity.getType() == ItemInfo.SUBJECT_TYPE
						|| entity.getType() == ItemInfo.HOMEPAGE_TYPE)) {
			String subjectParam = "" + subjectId;			
			String reference = template.getReference();
			
			if (reference != null && reference.trim().length() > 0 
					&& reference.indexOf(subjectParam) > -1) {
				StringBuffer referenceNew = new StringBuffer();
				
				String[] templateIds = reference.split(Global.CMSSEP);
				for (int i = 0; templateIds != null && i < templateIds.length; i++) {
					String id = templateIds[i];
					if (id != null && id.trim().length() > 0) {
						if (subjectParam.equalsIgnoreCase(id)) {
							continue;
						}
						else {
							referenceNew.append(id);
							referenceNew.append(Global.CMSSEP);
						}
					}
				}
				int index = referenceNew.lastIndexOf(Global.CMSSEP);
				if (index > -1) {
					referenceNew.delete(index, referenceNew.length());
				}
			
				template.setReference(referenceNew.toString());
				Item item = ItemManager.getInstance().update(template);
				if (item != null) {
					ret = true;
				}
			}
		}
		
		return ret;
	}

	/**
	 * ����ģ��ķ�������
	 * @param oldTemplate
	 * @param newTempalte
	 */
	public static void updateTemplate4Reference(int subjectId, String oldTemplateParam, String newTemplateParam) {
		
		// �����ӵ�����
		// Ϊ��������:��ǰ��ģ�嶼û�з���������Ϣ,���Ը�Ϊ"��Ч������",����ֻ��"����������"
		//List addedTemplateIdList = EntityParamUtil.getAddedTemplateIdList(oldTemplateParam, newTemplateParam);
		List newTemplateIdList = EntityParamUtil.getTemplateIdList(newTemplateParam);
		if (newTemplateIdList != null) {
			for(int i = 0; i < newTemplateIdList.size(); i++) {
				int templateId = ((Integer)newTemplateIdList.get(i)).intValue();
				EntityParamUtil.updateTemplate4AddReference(templateId, subjectId);
			}
		}
		
		// ��ɾ�������
		List deletedTemplateIdList = EntityParamUtil.getDeletedTemplateIdList(oldTemplateParam, newTemplateParam);
		if (deletedTemplateIdList != null) {
			for(int i = 0; i < deletedTemplateIdList.size(); i++) {
				int templateId = ((Integer)deletedTemplateIdList.get(i)).intValue();
				EntityParamUtil.updateTemplate4DeleteReference(templateId, subjectId);
			}
		}
	}

	public static void main(String[] args) {
		
		String reference = "2489;2343;;24;23;2343";
		
		try {
			
			int matchesCount = StringUtils.countMatches(reference, Global.CMSSEP);
			if (matchesCount > 5) {
				reference = reference.substring(reference.indexOf(Global.CMSSEP) + 1);
			}
			
			System.out.println(reference);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
}
