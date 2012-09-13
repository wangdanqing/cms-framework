package net.pusuo.cms.client.util;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;

public final class ClientUtil
{
	private static final Log log = LogFactory.getLog(ClientUtil.class);

	public static Remote renewRMI( String name )
	{
		try
		{
			//log.info(Configuration.getInstance().get("cms4.rmi.host"));
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}
			log.debug("rmi://"+Configuration.getInstance().get("cms4.rmi.host")+"/"+name);
			return Naming.lookup("rmi://"+Configuration.getInstance().get("cms4.rmi.host")+"/"+name);
		}
		catch ( Exception re )
		{
			log.error("unable to bind rmi interface . "+re.toString());
			throw new IllegalStateException("bind "+name+" failure . ");
		}
	}
}
