package net.pusuo.cms.server.util;


import java.util.TimerTask;
import java.util.Date;
import java.util.Calendar;

        
public class JProbeTrigger extends TimerTask {
        public void run(){
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour > 7 && hour < 21){
			trigger();
		}
        }       

	public void trigger(){
		System.out.println("JProbe Trigger " + new Date());
	}
}       
