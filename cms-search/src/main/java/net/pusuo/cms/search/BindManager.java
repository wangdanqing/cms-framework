/*
 * Created on 2006-1-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search;

import net.pusuo.cms.search.util.PropertyManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BindManager {

    private static Log log = LogFactory.getLog(BindManager.class);

    public static final String REMOTE_SERVICE_NAME_SEARCHMANAGER = "SearchManager";

    public static String getRmiServerName() {
        return PropertyManager.getProperty("Search.rmiServerName");
    }
    
    public static int getRmiServerPort() {
        int serverPort = 1097;
        String serverPortParam = PropertyManager.getProperty("Search.rmiServerPort");
        try {
            serverPort = Integer.parseInt(serverPortParam);
        }
        catch (Exception e) {
            log.error(e);
        }
        return serverPort;
    }
    
    public static String getSearchManagerName() {
        return "//" + getRmiServerName() + ":" + getRmiServerPort() + "/" + REMOTE_SERVICE_NAME_SEARCHMANAGER;
    }
    
    public static void rebindSearchManager(SearchManager searchManager) {
        try {
            Naming.rebind(getSearchManagerName(), searchManager);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    public static void unbindSearchManager() {
        try {
            Naming.unbind(getSearchManagerName());
        }
        catch (Exception e) {
            log.error(e);
        }        
    }
   
    public static void createRegistry() {
        try {
            LocateRegistry.createRegistry(getRmiServerPort());
        }
        catch (Exception e) {
            log.error(e);
        }
    }

}
