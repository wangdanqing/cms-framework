package net.pusuo.cms.client.compile;

import com.hexun.cms.core.TFMap;

public class FragQItem implements QItem
{
	private int entityid;

	protected FragQItem() {}

	public void setEntityid(int entityid)
	{
		this.entityid = entityid;
	}
	public int getEntityid()
	{
		return this.entityid;
	}
}
