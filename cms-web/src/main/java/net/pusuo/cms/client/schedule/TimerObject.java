package net.pusuo.cms.client.schedule;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import org.apache.commons.logging.*;

import java.util.Calendar;

public class TimerObject extends Timer
{
	private static final Log log = LogFactory.getLog(TimerObject.class);

	private TimerTask timertask;

	private String name;
	private String classname;
	private int type;

	private int hour;
	private int minute;
	private int second;
	
	private long period;
	private long delay;

	private boolean isrunning;
	private boolean stoped;

	protected TimerObject( TimerTask timertask )
	{
		this.timertask = timertask;
		this.isrunning = false;
		this.stoped = false;
	}

	public void startup()
	{
		if( !isrunning && !stoped )
		{
			isrunning = true;
			if( this.type==1 )
			{
				// ÿ�춨ʱִ��
				Date executetime = getExecuteTime();
				super.schedule( timertask, executetime, period );
			}

			if( this.type==2 )
			{
				// delay����ms��, ����ִ��, ������
				super.schedule( timertask, delay, period );
			}
		}
	}
	public void stop()
	{
		if( isrunning )
		{
			isrunning = false;
			stoped = true;
			super.cancel();
		}
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}

	public void setClassname(String classname)
	{
		this.classname = classname;
	}
	public String getClassname()
	{
		return this.classname;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	public int getType()
	{
		return this.type;
	}
	public void setDelay( long delay )
	{
		this.delay = delay;
	}
	public long getDelay()
	{
		return this.delay;
	}

	public void setPeriod( long period )
	{
		this.period = period;
	}
	public long getPeriod()
	{
		return this.period;
	}

	public void setHour( int hour )
	{
		this.hour = hour;
	}
	public int getHour()
	{
		return this.hour;
	}
	public void setMinute( int minute )
	{
		this.minute = minute;
	}
	public int getMinute()
	{
		return this.minute;
	}
	public void setSecond( int second )
	{
		this.second = second;
	}
	public int getSecond()
	{
		return this.second;
	}

	public boolean isRunning()
	{
		return this.isrunning;
	}
	public boolean stoped()
	{
		return this.stoped;
	}
	public void setTimertask( TimerTask timertask )
	{
		this.timertask = timertask;
	}
	public TimerTask getTimertask()
	{
		return this.timertask;
	}

	/**
	 *	��ȡ��һ��ִ��ʱ��
	 *	@param hour, minute, second ��, millis second is 0
	 *	@return ��ִ��ʱ����ڵ�ǰʱ��, ���ؽ���������+ʱ��; ���򷵻���������+ʱ��
	 */
	public Date getExecuteTime()
	{
		Calendar rightnow = Calendar.getInstance();
		rightnow.set( Calendar.MILLISECOND, second );

		Calendar executeTime = Calendar.getInstance();
		executeTime.set( Calendar.HOUR_OF_DAY, hour );
		executeTime.set( Calendar.MINUTE, minute );
		executeTime.set( Calendar.SECOND, second );
		executeTime.set( Calendar.MILLISECOND, second );

		if( executeTime.getTimeInMillis() < rightnow.getTimeInMillis() )
		{
			executeTime.add( Calendar.DATE, 1 );
		}
		return executeTime.getTime();
	}
}

