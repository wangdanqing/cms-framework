package net.pusuo.cms.client.compile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PublishTaskFactory extends TaskFactory
{
	public PublishTaskFactory( CompileMain main )
	{
		super(main);
	}

	/*
        private static int[] ids =
        { 
             	230583991,
		230584010,
		230584011,
		230584012,
		230584013,
		230584014,
		230584015,
		230584016,
		230584017
        };
	*/
	private static int[] ids =
	{
		230637620,
		230637622,
		230637623,
		230637624,
		230637625,
		230701268,
		230701269,
		230701270,
		230701271,
		230701272
	};

	private static int tasknum = 10;
	public static void setTasknum( int _tasknum )
	{
		tasknum = _tasknum;
	}
	public static int getTasknum()
	{
		return tasknum;
	}

	private int getRandomId()
	{
		int count = ids.length;
		int idx = (int)(Math.random() * ids.length );
		return ids[idx];
	}

	public void getTask()
	{
		try
		{
			Thread.sleep(100);
		}catch(InterruptedException e)
		{}

		Task[] tasks = new Task[tasknum];
		for(int i=0; i<tasknum; i++)
		{
			int entityId = getRandomId();
			QItem qitem = new PublishQItem();
			((PublishQItem)qitem).setPid( entityId );

			tasks[i] = new PublishTask( qitem );
			runTask( new PublishTask(qitem) );
		}
	}
}

