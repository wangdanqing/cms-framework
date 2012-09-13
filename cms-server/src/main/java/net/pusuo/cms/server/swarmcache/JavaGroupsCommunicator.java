package net.pusuo.cms.server.swarmcache;

import org.jgroups.*;

import org.apache.commons.logging.*;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: jwat
 * Date: Jul 25, 2003
 * Time: 11:47:57 AM
 * To change this template use Options | File Templates.
 */
public class JavaGroupsCommunicator extends Communicator implements NotificationBus.Consumer {

	Log log = LogFactory.getLog(this.getClass());

	public static final String BUS_NAME = "CMS4_CacheBus";
	public static final String CHANNEL_PROPERTIES = "multi.cache.properties";

	private NotificationBus bus;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	public JavaGroupsCommunicator(String properties) {
		log.info("Starting a JavaGroups Communicator..." + properties);
		setGroupName(BUS_NAME);
		try {
			if (properties == null) {
				bus = new NotificationBus(getGroupName());
				log.info("Create NotificationBus: " + getGroupName());
			} else {
				bus = new NotificationBus(getGroupName(), properties);
				log.info("Create NotificationBus: " + getGroupName()+"  @properties: "+properties);
			}
			bus.start();
			bus.getChannel().setOpt(Channel.LOCAL, new Boolean(false));
			bus.getChannel().setOpt(Channel.AUTO_RECONNECT, new Boolean(true));
			bus.setConsumer(this);
			log.info("... finished starting new JavaGroups Communicator.");
		} catch (Exception e) {
			log.error("There was a problem initiating the cache notification bus: ");
			e.printStackTrace();
		}
	}

	public void shutDown() {
		bus.stop();
	}

	protected void finalize() throws Throwable {
		shutDown();
	}

	protected final void send(CacheNotification notification) {
		bus.sendNotification(notification);
	}

	public Serializable getCache() {
		// We don't care about this,
		// so let's just return something that identifies us.
		// return "MultiCacheManager: " + bus.getLocalAddress();
		return null;
	}

	public void handleNotification(Serializable object) {
		log.info("Received cache notification: " + object);
		CacheNotification notification = (CacheNotification) object;
		receive(notification);
	}

	public void memberJoined(Address who) {
		log.info("A host has joined the cache notification bus: " + who + ".");
	}

	public void memberLeft(Address who) {
		log.info("A host has left the cache notification bus: " + who + ".");
	}

}
