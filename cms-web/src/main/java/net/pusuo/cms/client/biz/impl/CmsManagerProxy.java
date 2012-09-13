/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.CmsEventListener;

/**
 * @author Alfred.Yuan
 *
 */
public class CmsManagerProxy implements CmsManager {
	
	private static final Log log = LogFactory.getLog(CmsManagerProxy.class);
	
	public CmsManagerProxy() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////

	public void addListener(CmsEventListener listener) {
		// nothing.
	}

	public void deleteListener(CmsEventListener listener) {
		// nothing.
	}

	public void notifyListeners(CmsEvent event) {
		// nothing.
	}
	
}
