/**
 * 
 */
package net.pusuo.cms.client.biz.event;

import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.core.EntityItem;

/**
 * @author Alfred.Yuan
 * Ϊ���ó�����ͨ��,��Ϊ�¼�ȡ��Ƶ�ʱ��,��Ҫ�������¹���:
 *    �¼����(/ֵ)  = �������
 * ����:String EVENT_BEFORE_ADD = "beforeAdd",
 *     ��ô�����¼���ʱ��,ϵͳ�ͻ���������beforeAdd�ķ���.
 */
public interface CmsEventListener {
	
	public static final String EVENT_BEFORE_ADD = "beforeAdd";
	public static final String EVENT_AFTER_ADD = "afterAdd";

	public static final String EVENT_BEFORE_UPDATE = "beforeUpdate";
	public static final String EVENT_AFTER_UPDATE = "afterUpdate";

	public static final String EVENT_BEFORE_DELETE = "beforeDelete";
	public static final String EVENT_AFTER_DELETE = "afterDelete";
	
	////////////////////////////////////////////////////////////////////////////

	public void beforeAdd(CmsEvent event);
	
	public void afterAdd(CmsEvent event);
	
	public void beforeUpdate(CmsEvent event);
	
	public void afterUpdate(CmsEvent event);
	
	public void beforeDelete(CmsEvent event);
	
	public void afterDelete(CmsEvent event);
	
	////////////////////////////////////////////////////////////////////////////
	
	public void update(CmsManager cmsManager, CmsEvent event);

}
