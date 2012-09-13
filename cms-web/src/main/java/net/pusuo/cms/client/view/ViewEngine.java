/**
 * 
 */
package net.pusuo.cms.client.view;

import com.hexun.cms.client.view.vtl.ViewManagerImpl;

/**
 * @author Alfred.Yuan
 *
 */
public class ViewEngine {

	private static ViewManager viewManager = null;
	private static final Object lock4ViewManager = new Object();
	
	public static ViewManager getViewManager() {
		
		try {
			if (viewManager == null) {
				synchronized (lock4ViewManager) {
					if (viewManager == null) {
						viewManager = new ViewManagerImpl();
					}
				}
			}
			return viewManager;
		}
		catch (Exception e) {
			throw new IllegalStateException();
		}
	}
}
