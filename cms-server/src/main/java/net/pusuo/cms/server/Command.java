package net.pusuo.cms.server;

public interface Command
{
	public abstract Item save(Item item);

	public abstract Item update(Item item);

	public abstract Item delete(Item item);

}
