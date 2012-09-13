/**
 * 
 */
package net.pusuo.cms.client.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Alfred.Yuan
 * 
 */
public class StatisticThread extends Thread {
	
	private static final Log log = LogFactory.getLog(StatisticThread.class);
	
	boolean keepRunning = true;
	//long interval = 1000 * 60 * 60 * 2; 
	long interval = 1000 * 60 * 60 * 6; 
	
	public void run() {
		
		log.info("StatisticThread running ......");
		
		load();
		log.info("StatisticThread loading ......");
		
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
				log.info("StatisticThread saving ......");
			} catch (RuntimeException e) {
				log.error("runtime exception while executing timers", e);
			}
	    }
	    
	    log.info("StatisticThread ending ......");
	}
	
	private void load() {
		Statistic.load();
	}
	
	private void save() {
		Statistic.save();
	}
}
