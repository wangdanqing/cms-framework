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

public class BackupSubject
{
	private static final Log log = LogFactory.getLog(BackupSubject.class);

	// main begindate enddate entityid templateid
	public static void main(String[] args)
	{
		if( args.length<2 )
		{
			System.out.println("missing arguments");
			System.out.println("BackupSubject begindate enddate OR ");
			return;
		}
		if( args.length==2 )
		{
			new BackupSubject().run( args[0], args[1] );
		}
	}
	private void run( String begindate, String enddate )
	{
		try
		{
			int _begindate = Integer.parseInt( begindate );
			int _enddate = Integer.parseInt( enddate );

			for(int i=_begindate; i<=_enddate; i++)
			{
				List list = SubjectList.getInstance().getTask();

				PooledExecutor pool = new PooledExecutor( new BoundedBuffer(10) );
				pool.setMinimumPoolSize(5);
				pool.setMaximumPoolSize(10);
				pool.setKeepAliveTime(1000);
				pool.waitWhenBlocked();
				String iStr = i+"";
				String backupdate = iStr.substring(0,4)+"-"+iStr.substring(4,6)+"-"+iStr.substring(6);

				for(int k=0; list!=null && k<list.size(); k++)
				{
					SubjectCommand sc = (SubjectCommand)list.get(k);
					sc.setBackupdate( backupdate );
					pool.execute( new MyExecutor(sc) );
					System.out.println("backup oldnews "+sc.getEid()+"  "+backupdate);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}
	}
}

