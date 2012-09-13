package net.pusuo.cms.server;

import net.pusuo.cms.server.ad.ADManagerImpl;
import net.pusuo.cms.server.ad.ADPManagerImpl;
import net.pusuo.cms.server.auth.AuthFactory;
import net.pusuo.cms.server.cache.CacheFactory;
import net.pusuo.cms.server.cache.CacheFilter;
import net.pusuo.cms.server.cache.CacheMonitor;
import net.pusuo.cms.server.core.CoreFactory;
import net.pusuo.cms.server.file.ServerFile;
import net.pusuo.cms.server.image.JAIServerImage;
import net.pusuo.cms.server.tool.ChannelFav;
import net.pusuo.cms.server.tool.HWProxy;
import net.pusuo.cms.server.tool.StockCodeProxy;
import net.pusuo.cms.server.tool.UserFav;
import net.pusuo.cms.server.util.JProbeTrigger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.Timer;

public class RMIServer {
    private static Log log = LogFactory.getLog(RMIServer.class);

    public static void main(String[] args) throws Exception {
        try {
            log.info("init cms config ...");
            System.out.println("111111");
            Configuration config = Configuration.getInstance();
            CoreFactory.buildFactory(config);
            AuthFactory.buildFactory(config);
            // more factory here ..
            log.info("end cms config .");
        } catch (Exception e1) {
            log.error("unable to init cms config . " + e1.toString());
            throw e1;
        }

        log.info("RMI Server start ...");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try {
            log.info("create rmi registry ...");
            LocateRegistry.createRegistry(1099);
            log.info("ItemProxy bind ...");
            Naming.rebind("ItemProxy", ItemProxy.getInstance());

            log.info("ServerFile bind ...");
            Naming.rebind("ServerFile", ServerFile.getInstance());

            log.info("ServerImage bind ...");
            Naming.rebind("ServerImage", JAIServerImage.getInstance());

            // more bind here
            log.info("CompileProxy bind ...");
            Naming.rebind("CompileProxy", CompileProxy.getInstance());

            log.info("ADManagerImpl bind ...");
            Naming.rebind("ADManager", ADManagerImpl.getInstance());

            // add by wzg
            log.info("ADPManagerImpl bind ...");
            Naming.rebind("ADPManager", ADPManagerImpl.getInstance());
            // add end

            log.info("ListCache bind ...");
            Naming.rebind("ListCacheFilter", CacheFilter.getInstance());

            log.info("ListCache monitor bind ...");
            Naming.rebind("ListCacheMonitor", CacheMonitor.getInstance());

            CacheFactory
                    .buildFactory(new Configuration("/listcache.properties"));

            log.info("HotWord bind ...");
            Naming.rebind("HWProxy", HWProxy.getInstance());

            log.info("ChannelFav bind ...");
            Naming.rebind("ChannelFav", ChannelFav.getInstance());

            log.info("UserFav bind ...");
            Naming.rebind("UserFav", UserFav.getInstance());

            log.info("StockCode bind ...");
            Naming.rebind("StockCode", StockCodeProxy.getInstance());

            log.info("DataToSearchProxy bind ...");
            Naming.rebind("DataToSearchProxy", DataToSearchProxy.getInstance());

            if ("true".equals(Configuration.getInstance().get("jprobe.use"))) {

                log.info("Start JProbe Trigger...");
                int delay = 0; // default 0
                int interval = 3600; // default 1 hour

                try {
                    delay = Integer.parseInt(Configuration.getInstance().get(
                            "jprobe.trigger.delay"));
                    interval = Integer.parseInt(Configuration.getInstance()
                            .get("jprobe.trigger.interval"));
                } catch (Exception e) {
                }
                new Timer().schedule(new JProbeTrigger(), delay * 1000,
                        interval * 1000);
            }

            log.info("RMI Server ready .");
        } catch (Exception e2) {
            log.error("start RMI server error . " + e2.toString());
            e2.printStackTrace();
            throw e2;
        }
    }
}
