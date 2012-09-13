package net.pusuo.cms.server.localcache;

public class LCacheException extends Exception
{
	public LCacheException( Throwable cause )
	{
		super( cause );
	}

	public LCacheException( String message, Throwable cause )
	{
		super( message, cause);
	}

	public LCacheException( String message )
	{
		super( message );
	}
}

