package net.pusuo.cms.client.schedule;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.hexun.cms.client.tool.CommandBean;
import com.hexun.cms.client.tool.SubjectCommand;
import com.hexun.cms.client.tool.SubjectList;
import java.sql.*;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;

import org.apache.commons.logging.*;

public class MyTask extends TimerTask
{
	private static final Log log = LogFactory.getLog(MyTask.class);
	public void run()
	{
		try
		{
			String t = new Timestamp(System.currentTimeMillis()).toString();
			log.error("info -- > execute Mytask at "+t);
		} catch(Exception e) {
			log.error(e.toString());
		}
	}
}

