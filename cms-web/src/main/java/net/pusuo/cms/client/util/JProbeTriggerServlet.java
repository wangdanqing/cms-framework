package net.pusuo.cms.client.util;

import java.util.Timer;
import com.hexun.cms.util.JProbeTrigger;

import javax.servlet.http.HttpServlet;

public class JProbeTriggerServlet extends HttpServlet
{

	public void init(){

		String startJProbeTrigger = getInitParameter("jprobe_trigger_start");
		if("true".equals(startJProbeTrigger)){

			String delayStr = getInitParameter("jprobe_trigger_delay");
			String intervalStr = getInitParameter("jprobe_trigger_interval");
			
			int delay = 0; // default 0
			int interval = 3600; // default 1 hour
			
			try{
				delay = Integer.parseInt(delayStr);	
				interval = Integer.parseInt(intervalStr);	
			}catch(Exception e){ }

			Timer timer = new Timer();
			timer.schedule(new JProbeTrigger(), delay * 1000, interval * 1000);
		}
	}

}
