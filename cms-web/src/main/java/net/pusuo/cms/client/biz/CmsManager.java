/**
 * 
 */
package net.pusuo.cms.client.biz;

import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.CmsEventListener;

/**
 * @author Alfred.Yuan
 *
 */
public interface CmsManager {
	
	public static final String PROPERTY_NAME_AUTH = "CmsManager.auth";

	public static final String PROPERTY_NAME_PNAME = "CmsManager.pname";

	public static final String PROPERTY_NAME_OLDTEXT = "CmsManager.oldtext";//�����޸�ǰ������

	public static final String PROPERTY_NAME_ZUTUORDER = "CmsManager.zutuorder";

	public static final String PROPERTY_NAME_TIME = "CmsManager.time";

	public static final String EXTEND_NAME_WEB_PAGE = "html";
	
	public static final String FILE_SERVER_ROOT_PATH = "/opt";
	
	////////////////////////////////////////////////////////////////////////////
	// �¼�����
	////////////////////////////////////////////////////////////////////////////

	public void addListener(CmsEventListener listener);
	
	public void deleteListener(CmsEventListener listener);
	
	public void notifyListeners(CmsEvent event);
	
}
