package net.pusuo.cms.client.compile;

public class PublishQItem implements QItem
{
	private int pid;

	protected PublishQItem(){}

	public void setPid(int pid)
	{
		this.pid = pid;
	}
	public int getPid()
	{
		return this.pid;
	}

}
