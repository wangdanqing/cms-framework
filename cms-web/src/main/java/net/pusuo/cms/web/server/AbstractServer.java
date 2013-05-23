package net.pusuo.cms.web.server;

import com.twitter.ostrich.admin.AdminHttpService;
import com.twitter.ostrich.admin.RuntimeEnvironment;
import com.twitter.ostrich.admin.TimeSeriesCollectorFactory;
import com.twitter.ostrich.stats.Stats;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Example WebServer class which sets up an embedded Jetty appropriately
 */
public abstract class AbstractServer {

    private static final String WEB_XML = "webapp/WEB-INF/web.xml";

    private Server server;
    private final Config config = new Config();

    public AbstractServer(String[] args) {

        if (args != null && args.length == 2) {
            config.ip = args[0];
            config.port = Integer.parseInt(args[1]);
        } else {
            config.ip = System.getProperty("server_ip", "127.0.0.1");
            config.port = (args != null && args.length > 0) ? Integer.parseInt(args[0]) : config.port;
        }
        config.logpath = System.getProperty("server_log_home", "./logs/") + "/access_" + config.port + ".log";
    }

    public abstract void init(Config config);

    public void run() throws Exception {
        init(config);

        startAdminMonitor();
        start();
        join();
    }

    public void start() throws Exception {
        server = new Server();

        server.setThreadPool(createThreadPool());
        server.addConnector(createConnector());
        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);

        server.start();
    }

    private void startAdminMonitor() {
        AdminHttpService admin = new AdminHttpService(
                config.port + 1,
                123,
                new RuntimeEnvironment(this));
        TimeSeriesCollectorFactory seriesCollectorFactory = new TimeSeriesCollectorFactory();
        seriesCollectorFactory.apply(Stats.get(""), admin).start();
        admin.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private ThreadPool createThreadPool() {
        // TODO: You should configure these appropriately
        // for your environment - this is an example only
        QueuedThreadPool _threadPool = new QueuedThreadPool();
        _threadPool.setMinThreads(10);
        _threadPool.setMaxThreads(100);
        return _threadPool;
    }

    private SelectChannelConnector createConnector() {
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(config.port);
        connector.setHost(config.ip);
        connector.setAcceptors(config.acceptorThreads);
        connector.setForwarded(config.useForwardedHeaders);

        connector.setMaxIdleTime((int) config.maxIdleTime.toMilliseconds());
        connector.setLowResourcesMaxIdleTime((int) config.lowResourcesMaxIdleTime.toMilliseconds());
        connector.setAcceptorPriorityOffset(config.acceptorThreadPriorityOffset);
        connector.setAcceptQueueSize(Config.acceptQueueSize);
        connector.setMaxBuffers(config.maxBufferCount);
        connector.setRequestBufferSize((int) config.requestBufferSize.toBytes());
        connector.setRequestHeaderSize((int) config.requestHeaderBufferSize.toBytes());
        connector.setResponseBufferSize((int) config.responseBufferSize.toBytes());
        connector.setResponseHeaderSize((int) config.responseHeaderBufferSize.toBytes());
        connector.setReuseAddress(config.reuseAddress);
        connector.setSoLingerTime((int) config.soLingerTime.toMilliseconds());

        connector.setName("main-jetty");
        return connector;
    }

    private HandlerCollection createHandlers() {

        System.setProperty("jetty.home", "/tmp/_work");

        WebAppContext _ctx = new WebAppContext();
        _ctx.setContextPath("/");

        _ctx.setWar(getShadedWarUrl());

        List<Handler> _handlers = new ArrayList<Handler>();

        _handlers.add(_ctx);

        HandlerList _contexts = new HandlerList();
        _contexts.setHandlers(_handlers.toArray(new Handler[0]));

        RequestLogHandler _log = new RequestLogHandler();
        _log.setRequestLog(createRequestLog());

        HandlerCollection _result = new HandlerCollection();
        _result.setHandlers(new Handler[]{_contexts, _log});
        return _result;
    }

    private RequestLog createRequestLog() {
        NCSARequestLog _log = new NCSARequestLog();

        File _logPath = new File(config.logpath);
        _logPath.getParentFile().mkdirs();

        _log.setFilename(_logPath.getPath());
        _log.setRetainDays(7);
        _log.setExtended(false);
        _log.setAppend(true);
        _log.setLogTimeZone("GMT");
        _log.setLogLatency(true);
        return _log;
    }

    //---------------------------
    // Discover the war path
    //---------------------------
    public URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader().getResource(aResource);
    }

    private String getShadedWarUrl() {
        String _urlStr = getResource(WEB_XML).toString();
        return _urlStr.substring(0, _urlStr.length() - 15);
    }
}
