package net.pusuo.cms.web.server;

/**
 * cms server
 */
public class CmsWebServer extends AbstractServer {

	public CmsWebServer(String[] anArgs) {
		super(anArgs);
	}

	public static void main(String... anArgs) throws Exception {
		new CmsWebServer(anArgs).run();
	}

	@Override
	public void init(Config config) {
		config.setMin_thread(5);
		config.setMax_thread(50);
	}

}
