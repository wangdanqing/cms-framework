package net.pusuo.cms.client.ad;

import com.hexun.cms.client.util.ClientUtil;
import com.hexun.cms.ad.ADManagerInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ADManager
{
	private static final Log log = LogFactory.getLog(ADManager.class);

	private static final Object lock = new Object();
	private static ADManager instance;

	private ADManagerInterface adminterface;

	public static ADManager getInstance()
	{
		if( instance==null )
		{
			synchronized(lock)
			{
				if( instance==null ) instance = new ADManager();
			}
		}
		return instance;
	}
	private ADManager()
	{
		adminterface = (ADManagerInterface)ClientUtil.renewRMI("ADManager");
	}

	public void load()
	{
		try
		{
			adminterface.load();
		} catch(Exception e) {
			log.error("load addata exception. "+e.toString());
		}
	}

	public boolean belong(int fragId, int entityId)
	{
		try
		{
			return adminterface.belong( fragId, entityId );
		} catch(Exception e) {
			log.error("belong addata exception. "+e.toString());
			return false;
		}
	}
	public boolean append(int fragId, int entityId)
	{
		try
		{
			return adminterface.append( fragId, entityId );
		} catch(Exception e) {
			log.error("append addata exception. "+e.toString());
			return false;
		}
	}
	public boolean delete(int fragId, int entityId)
	{
		try
		{
			return adminterface.delete( fragId, entityId );
		} catch(Exception e) {
			log.error("delete addata exception. "+e.toString());
			return false;
		}
	}
	public StringBuffer list()
	{
		try
		{
			return adminterface.list();
		} catch(Exception e) {
			log.error("delete addata exception. "+e.toString());
			return null;
		}
	}
}

