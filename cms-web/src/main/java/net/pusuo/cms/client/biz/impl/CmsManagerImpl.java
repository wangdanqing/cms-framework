/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.util.ArrayList;
import java.util.List;

import com.hexun.cms.client.biz.CmsManager;
import com.hexun.cms.client.biz.event.CmsEvent;
import com.hexun.cms.client.biz.event.CmsEventListener;

/**
 * @author Alfred.Yuan
 *
 */
public class CmsManagerImpl implements CmsManager {
	
	protected List listeners = new ArrayList();
	
	////////////////////////////////////////////////////////////////////////////

	public void addListener(CmsEventListener listener) {
		
		if (listener == null)
			throw new NullPointerException();
		listeners.add(listener);
	}

	public void deleteListener(CmsEventListener listener) {
		
		// ......
	}

	public void notifyListeners(CmsEvent event) {
		
		for (int i = 0; i < listeners.size(); i++) {
			CmsEventListener listener = (CmsEventListener)listeners.get(i);
			listener.update(this, event);
		}
	}
	
}
