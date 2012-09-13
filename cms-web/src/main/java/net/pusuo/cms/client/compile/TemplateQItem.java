package net.pusuo.cms.client.compile;

public class TemplateQItem implements QItem
{
	private int entityid;
	private int action;
	/**
	 * �Ƿ��Ƿ�ҳ����,����Ƿ�ҳ����,��ô�ڱ����ʱ���ֻ�����ҳģ��,�����ģ�岻�ᱻ���� 
	 */
	private boolean isMutil = false;

	public boolean isMutil() {
		return isMutil;
	}
	
	public void setMutil(boolean isMutil) {
		this.isMutil = isMutil;
	}
	protected TemplateQItem() {}

	public void setEntityid(int entityid)
	{
		this.entityid = entityid;
	}
	public int getEntityid()
	{
		return this.entityid;
	}

	public void setAction(int action)
	{
		this.action = action;
	}
	public int getAction()
	{
		return this.action;
	}
}
