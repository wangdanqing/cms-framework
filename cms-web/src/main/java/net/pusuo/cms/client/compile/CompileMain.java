package net.pusuo.cms.client.compile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import java.io.*;
import java.lang.reflect.*;

public class CompileMain implements Runnable
{
        private static final Log log = LogFactory.getLog(CompileMain.class);

	private static long beginTest = 0;
	private static long endTest = 0;

	private static final Object lock = new Object();
	private static CompileMain instance;

	private PooledExecutor pool;
	private BoundedBuffer buffer;

	public static final int RUNNING=1;
	public static final int STOP=2;
	private static int status = STOP;

	private XMLProperties compileconfig;

	private String className = "com.hexun.cms.client.compile.CompileTaskFactory";
	private TaskFactory taskFactory;

	public static CompileMain getInstance()
	{
		if(instance==null)
		{
			synchronized(lock)
			{
				if(instance==null)
				{
					instance = new CompileMain();
				}
			}
		}
		return instance;
	}

	private CompileMain()
	{
		init();
	}

	private void initConf() throws Exception
	{
		InputStream in = null;
		try
		{
			in = CompileMain.class.getResourceAsStream( "/compileconfig.xml" );
			compileconfig = new XMLProperties( in );
		}catch(Exception e) {
			throw new Exception( "required compileconfig.xml.",e );
		}finally {
			try
			{
				if( in!=null ) in.close();
			}catch(Exception e) {
				throw new Exception( "close inputstream exception -- ",e );
			}
		}
	}
	private void init()
	{
		try
		{
			initConf();

			// ��ʼ���̳߳�
			buffer = new BoundedBuffer( Integer.parseInt(compileconfig.getProperty("pool.buffersize")) );
			pool = new PooledExecutor( buffer );
			pool.setMaximumPoolSize( Integer.parseInt(compileconfig.getProperty("pool.maxpoolsize")) );
			pool.setMinimumPoolSize( Integer.parseInt(compileconfig.getProperty("pool.minpoolsize")) );
			pool.setKeepAliveTime( Long.parseLong(compileconfig.getProperty("pool.keepalivetime")) );

			// �̳߳�����ʱ�ȴ�
			pool.waitWhenBlocked();

			log.info("buffersize:"+ Integer.parseInt(compileconfig.getProperty("pool.buffersize")) );
			log.info("maxpoolsize:"+ Integer.parseInt(compileconfig.getProperty("pool.maxpoolsize")) );
			log.info("minpoolsize:"+ Integer.parseInt(compileconfig.getProperty("pool.minpoolsize")) );
			log.info("keepalivetime:"+ Long.parseLong(compileconfig.getProperty("pool.keepalivetime")) );

			status = STOP;

		} catch(Exception e) {
			log.error("CompileMain init error: "+e.toString());
		}

	}
	private void reloadInit()
	{
		try
		{
			initConf();

			this.setMaxPoolSize( Integer.parseInt(compileconfig.getProperty("pool.maxpoolsize")) );
			this.setMinPoolSize( Integer.parseInt(compileconfig.getProperty("pool.minpoolsize")) );
			this.setKeepAliveTime( Integer.parseInt(compileconfig.getProperty("pool.keepalivetime")) );

		} catch(Exception e) {
			log.error("reloadParam error: "+e.toString());
		}
	}
	public void startTask()
	{
		if(status==STOP)
		{
			reloadInit();
			beginTest = ct();
			status = RUNNING;
			new Thread(this).start();
			log.info("compile start...");
		}
	}
	public void stopTask()
	{
		if( status==RUNNING )
		{
			status = STOP;

			//pool.shutdownAfterProcessingCurrentlyQueuedTasks();
			//pool.shutdownNow();
			log.info("compile shutdown.");

			endTest = ct();
			StringBuffer sb = new StringBuffer();
			sb.append("begin test: " + beginTest+"\t");
			sb.append("end test: " + endTest+"\t");
			sb.append("elapse: " + (endTest-beginTest) + "\n\n");
		}
	}

	public void execute( Runnable task )
	{
		try
		{
			if(log.isInfoEnabled()){				
				log.info("Compile Pool size:"+this.pool.getPoolSize()+" run Task begin:"+task);
			}
			this.pool.execute( task );
			if(log.isInfoEnabled()){				
				log.info("Compile Pool size:"+this.pool.getPoolSize()+" run Task end:"+task);
			}
		} catch(Exception e) {
			log.error("execute task error",e);
		}
	}
	public void run()
	{
		try
		{
			Constructor constructor = Class.forName(className).getConstructor( new Class[] {CompileMain.class} );
			taskFactory = (TaskFactory) constructor.newInstance( new Object[] { this } );

			while( status==RUNNING )
			{
//				if(log.isInfoEnabled()){
//					log.info("Compile run to get task.");
//				}
				taskFactory.getTask();
			}
			taskFactory = null;
		} catch(Exception e) {
			log.error("Compile run break error",e);
		}finally{
			if(log.isInfoEnabled()){
				log.info("Compile end to get task.");
			}
		}
	}

	public int getStatus()
	{
		return status;
	}

	/**
	 *	�̳߳�
	 */
	public int getMaxPoolSize()
	{
		return pool.getMaximumPoolSize();
	}
	public synchronized void setMaxPoolSize(int newsize)
	{
		pool.setMaximumPoolSize(newsize);
	}
	public int getMinPoolSize()
	{
		return pool.getMinimumPoolSize();
	}
	public synchronized void setMinPoolSize(int newsize)
	{
		pool.setMinimumPoolSize(newsize);
	}
	public long getKeepAliveTime()
	{
		return pool.getKeepAliveTime();
	}
	public synchronized void setKeepAliveTime(long newtime)
	{
		pool.setKeepAliveTime(newtime);
	}

	/**
	 *	���ص�ǰ��߳���
	 */
	public int getActiveWorkers()
	{
		return pool.getPoolSize();
	}
	

	/**
	 *	������
	 */
	public int getBufferCapacity()
	{
		return (buffer).capacity();
	}
	public int getBufferSize()
	{
		 return (buffer).size();
	}

	public void setClassName( String _className )
	{
		try
		{
			this.className = _className;
			log.info("set factory --> "+className);
		} catch(Exception e) {
			log.error("setFactory error: "+e.toString());
		}
	}
	public String getClassName()
	{
		return this.className;
	}

	private static long ct()
	{
		return System.currentTimeMillis();
	}

}

