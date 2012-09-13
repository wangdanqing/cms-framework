package net.pusuo.cms.client.compile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class TaskFactory
{
	private static final Log log = LogFactory.getLog(TaskFactory.class);

	protected CompileMain main;

	protected TaskFactory( CompileMain main )
	{
		this.main = main;
	}
	protected void runTask( Task task )
	{
		main.execute( task );
	}
	public abstract void getTask();
}

