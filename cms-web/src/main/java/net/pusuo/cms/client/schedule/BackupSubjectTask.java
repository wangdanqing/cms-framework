package net.pusuo.cms.client.schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.hexun.cms.client.tool.CommandBean;
import com.hexun.cms.client.tool.SubjectCommand;
import com.hexun.cms.client.tool.SubjectList;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;

import org.apache.commons.logging.*;

import com.hexun.cms.util.*;
import com.hexun.cms.client.*;

public class BackupSubjectTask extends TimerTask
{
	private static final Log log = LogFactory.getLog(BackupSubjectTask.class);

	public void run()
	{
		try
		{
			// �������������
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add( Calendar.DAY_OF_MONTH, -1 );
			java.util.Date date = calendar.getTime();
			long ct = date.getTime();
			String yesterday = Util.formatDatetime( ct ,"yyyy-MM-dd");
			
			List list = SubjectList.getInstance().getTask();

			PooledExecutor pool = new PooledExecutor( new BoundedBuffer(10) );
			pool.setMinimumPoolSize(5);
			pool.setMaximumPoolSize(10);
			pool.setKeepAliveTime(1000);
			pool.waitWhenBlocked();

			log.debug("backup oldnews task start...   command count: "+(list!=null?list.size():0));
			for(int i=0; list!=null && i<list.size(); i++)
			{
				SubjectCommand sc = (SubjectCommand)list.get(i);
				sc.setBackupdate( yesterday );
				pool.execute( new MyExecutor(sc) );
			}
		} catch(Exception e) {
			log.error("backup oldnews exception -- ", e);
		}
	}

	private class MyExecutor implements Runnable
	{
		CommandBean sb = null;
		MyExecutor(CommandBean sb)
		{
			this.sb = sb;
		}
		public void run()
		{
			try
			{
				sb.execute();
			}catch(Exception e){
				log.error("MyExecutor exception -- ", e);
				e.printStackTrace();
			}
		}
	}
}

