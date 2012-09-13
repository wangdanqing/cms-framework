/*
 * Created on 2004-11-14
 * Author: sohu
 */
package net.pusuo.cms.client.tool;

/**
 * @author sohu
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DicTree {
		
		static {
			WordSegment.initLib();
		}
		int obj;
		public DicTree() {			
			open();
		}
		
		native void open();
		native void deinit();
		
		public void finalnize() {
			deinit();
		}
		
}
