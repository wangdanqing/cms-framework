package net.pusuo.cms.search;

/*
 * RMI��������
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RMISecurityManager;

public class SearchServer {

    private static final Log log = LogFactory.getLog(SearchServer.class);

    public static SearchManager searchManager = null;

    public static void main(String[] args) {

        log.info(" ==== Search Server starting ... ====");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try {
            searchManager = SearchManagerImpl.getInstance();

            BindManager.createRegistry();
            BindManager.rebindSearchManager(searchManager);

            log.info(" ==== Search Server started ...  OK ==== ");
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}

