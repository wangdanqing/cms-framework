package net.pusuo.cms.web.server;

public class CmsServer extends AbstractServer {

    public CmsServer(String[] anArgs) {
        super(anArgs);
    }

    public static void main(String... anArgs) throws Exception {
        new CmsServer(anArgs).run();
    }

    @Override
    public void init(Config config) {
        config.setMin_thread(5);
        config.setMax_thread(50);
    }

}
