package net.pusuo.cms.client.schedule;

import java.util.*;
import java.io.*;
import java.sql.Timestamp;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.file.LocalFile;
import com.hexun.cms.client.file.ClientFile;

public class TimerManager
{
	private static final Log log = LogFactory.getLog(TimerManager.class);

	private static final Object lock = new Object();
	private static TimerManager instance;

	//private static final String RMIFILE = "/cms4/cmstimer.xml";

	private Map pool;

	public static TimerManager getInstance()
	{
		if( instance==null )
		{
			synchronized( lock )
			{
				if( instance==null ) instance = new TimerManager();
			}
		}
		return instance;
	}
	private TimerManager()
	{
		pool = new HashMap();
		load();
	}

	public void load()
	{
		InputStream in = null;
		try
		{
			// do not read from RMI ,  modified by wangzhigang
			/*
			// get file from RMI
			String content = ClientFile.getInstance().read( RMIFILE );
			if( content==null )
			{
				log.info("read "+RMIFILE+" from RMI error. content is null. Do not write to local.");
				return;
			}
			// write file to Local
			boolean suc = LocalFile.write( content, LOCALFILE );
			if( !suc )
			{
				log.error("write to local file "+ LOCALFILE +" error.");
				return;
			}
			*/

			// load local file to mem
			in = TimerManager.class.getResourceAsStream( "/cmstimer.xml" );
			//in = new FileInputStream( LOCALFILE );

			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build( in );
			
			Element rootEle = doc.getRootElement();
			List tasks = rootEle.getChildren();
			for(int i=0; tasks!=null && i<tasks.size(); i++)
			{
				Element task = (Element)tasks.get(i);

				String name = task.getChild("name").getText();
				String className = task.getChild("class").getText();
				int type = Integer.parseInt(task.getChild("type").getText());

				int hour = Integer.parseInt(task.getChild("hour").getText());
				int minute = Integer.parseInt(task.getChild("minute").getText());
				int second = Integer.parseInt(task.getChild("second").getText());
				long period = Long.parseLong(task.getChild("period").getText());
				long delay = Long.parseLong(task.getChild("delay").getText());

				// ����cache�д��ڵ�����,�������½����������
				if( pool.get(name)!=null ) continue;

				Class c = TimerManager.class.getClassLoader().loadClass( className );
				TimerTask tt = (TimerTask)c.newInstance();
				TimerObject to = new TimerObject( tt );

				to.setName( name );
				to.setClassname( className );

				to.setType( type );

				to.setHour( hour );
				to.setMinute( minute );
				to.setSecond( second );
				to.setPeriod( period );
				to.setDelay( delay );

				pool.put( name, to );
			}
		} catch(Exception e) {
			log.error("load timer task error. ", e);
		} finally {
			try
			{
				if( in!=null ) in.close();
			} catch(Exception e) {
				log.error("close FileInputStream error. ", e);
			}
		}
	}

	public Map getPool()
	{
		return this.pool;
	}
	public TimerObject get( String key )
	{
		return (TimerObject)pool.get(key);
	}
	public TimerObject[] list()
	{
		TimerObject[] list = new TimerObject[ pool.size() ];
		Iterator itr = pool.values().iterator();
		int count = 0;
		while(itr.hasNext())
		{
			list[count++] = (TimerObject) itr.next();
		}
		return list;
	}

	public void schedule( String name )
	{
		try
		{
			TimerObject to = (TimerObject)pool.get( name );
			if( to==null )
			{
				log.error("schedule --> Task ["+ name+"] is not exist.");
				return;
			}

			if( to.stoped() )
			{
				Class c = TimerManager.class.getClassLoader().loadClass( to.getClassname() );
				TimerTask tt = (TimerTask)c.newInstance();
			
				TimerObject newTo = new TimerObject( tt );
				newTo.setName( to.getName() );
				newTo.setClassname( to.getClassname() );
				newTo.setType( to.getType() );
				newTo.setHour( to.getHour() );
				newTo.setMinute( to.getMinute() );
				newTo.setSecond( to.getSecond() );
				newTo.setPeriod( to.getPeriod() );
				pool.remove( name );
				to = null;
				pool.put( name, newTo );
				newTo.startup();
			} else {
				to.startup();
			}
		} catch(Exception e) {
			log.error("schedule --> exception.", e);
		}
	}
	public void cancel( String name )
	{
		TimerObject to = (TimerObject)pool.get( name );
		if( to==null )
		{
			log.error("schedule --> Task ["+ name+"] is not exist.");
			return;
		}
		to.stop();
	}

	public synchronized void update( TimerObject updTo )
	{
		if( updTo==null ) return;

		try
		{
			String name = updTo.getName();
			TimerObject to = get( name );
			if( to==null )
			{
				log.error("update error. Task "+name+" is not exist.");
				return;
			}
			to.setHour( updTo.getHour() );
			to.setMinute( updTo.getMinute() );
			to.setSecond( updTo.getSecond() );
			to.setPeriod( updTo.getPeriod() );

			// do not update local conf file, and RMI file
			// modified by wangzhigang
			//updateDocument( to );

		} catch(Exception e) {
			log.error("update error. ", e);
		}
	}

	/**
	 *	�޸Ķ�ʱ����, ����cache, ͬʱ���������ļ�.
	 *	@param TimerObject ��Action�㴫�ݹ����ĸ��¶���
	 */
	private void updateDocument( TimerObject to )
	{
		/*
		FileInputStream fis = null;
		try
		{
			String name = to.getName();

			fis = new FileInputStream( LOCALFILE );
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build( fis );
			Element rootEle = doc.getRootElement();
			Element e = null;
			List list = rootEle.getChildren();
			for(int i=0; list!=null && i<list.size(); i++)
			{
				Element temp = (Element)list.get(i);
				if( temp.getChild("name").getText().equals( name ) )
				{
					e = temp;
					break;
				}
			}
			if( e!=null )
			{
				int hour = to.getHour();
				int minute = to.getMinute();
				int second = to.getSecond();
				e.getChild("hour").setText( String.valueOf(hour) );
				e.getChild("minute").setText( String.valueOf(minute) );
				e.getChild("second").setText( String.valueOf(second) );
				e.getChild("period").setText( String.valueOf(to.getPeriod()) );

				writeFile( doc );
			}
		} catch(Exception e) {
			log.error("updateDocument error. ", e);
		} finally {
			try
			{
				if( fis!=null ) fis.close();
			} catch(Exception e) {
				log.error("close FileInputStream and FileOutputStream error. ", e);
			}
		}
		*/
	}


	/**
	 *	��Documentд�뱾���ļ���RMI
	 *	@param doc �ڴ��е�Document�ĵ�
	 */
	private synchronized void writeFile( Document doc )
	{
		/*
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( LOCALFILE );
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat( Format.getPrettyFormat() );
			outputter.output( doc, fos );

			String content = LocalFile.read( LOCALFILE );
			if( content==null )
			{
				log.error("Local content is null.");
			} else {
				ClientFile.getInstance().write( content, RMIFILE, false );
			}
		} catch(Exception e) {
			log.error("write XML Document to local RMI error.", e);
		} finally {
			try
			{
				if( fos!=null ) fos.close();
			} catch(Exception e) {
				log.error("close FileOutputStream error. ", e);
			}
		}
		*/
	}

	public void reload()
	{
		try
		{
			Iterator itr = pool.entrySet().iterator();
			while( itr.hasNext() )
			{
				Map.Entry entry = (Map.Entry)itr.next();
				String name = (String)entry.getKey();
				cancel(name);
			}
			pool.clear();
			load();
		} catch(Exception e) {

		}
	}
}

