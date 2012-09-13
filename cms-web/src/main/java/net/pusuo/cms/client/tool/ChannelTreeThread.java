/**
 * 
 */
package net.pusuo.cms.client.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChannelTreeThread extends Thread {
	
	private static final Log log = LogFactory.getLog(ChannelTreeThread.class);
	
	boolean keepRunning = true;
	long interval = 1000 * 60 * 60 * 4; 
	
	public void run() {
		
	    while (keepRunning) {
	    	// �ȴ�
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				log.error("waiting for timers got interuppted");
			}
			
			// ���л�
			try {
				save();
				log.info("ChannelTreeThread saving ......");
			} catch (RuntimeException e) {
				log.error("runtime exception while executing timers", e);
			}
	    }
	    log.info("ChannelTreeThread ending ......");
	}
	
	private void save() {
		ChannelTreeManager.getInstance().saveAllTrees();
	}
}
