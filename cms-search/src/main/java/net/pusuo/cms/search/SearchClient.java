package net.pusuo.cms.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

public class SearchClient {
    
	private static Log log = LogFactory.getLog(SearchClient.class);
	
	private static SearchClient client = null;
	
	private SearchManager searchManager = null;
	
	private String searchServer = null;
	
	private SearchClient() {}

	public static SearchClient getInstance() {
	    
		try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
            
			if ( client == null )
			{
				synchronized ( SearchClient.class ) 
				{
					if ( client == null ) 
					{
						client = new SearchClient();
					}
				}
			}
			return client;
		} catch ( Exception e ) {
			log.error("Unable to create SearchClient instance . "+e.toString());
			throw new IllegalStateException("Unable to create SearchClient instance.");
		}
	}
	
	public SearchManager getSearchManager() {
	    
	    return getSearchManager(true);
	}

	public SearchManager getSearchManager(boolean fromCache) {
	    
        if ((fromCache && searchManager == null) || !fromCache) {
            searchManager = (SearchManager)lookupRemote(BindManager.REMOTE_SERVICE_NAME_SEARCHMANAGER);
        }
        
        return searchManager;
    }

  	private Object lookupRemote(String name) {
  	    
  	    if (name == null || !name.equals(BindManager.REMOTE_SERVICE_NAME_SEARCHMANAGER))
  	        return null;
  	    
  	    Object object = null;
  	    
  	    try {
  	        object = Naming.lookup("//" + getSearchServer() + "/" + name);
  	    }
  	    catch (Exception e) {
  	        log.error(e);
  	    }
  	    
  	    return object;
  	}
  	
  	private String getSearchServer() {
  	    if (searchServer == null || searchServer.trim().length() == 0)
  	      searchServer = BindManager.getRmiServerName() + ":" + BindManager.getRmiServerPort();
  	    
  	    return searchServer;
  	}
  	
}
