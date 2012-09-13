package net.pusuo.cms.server.swarmcache;

/**
 * Abstract class that handles the communications for SwarmCache.
 *
 * To provide a full implementation:
 * <ul>
 *  <li>Extend this class and implement a constructor/instantiator that accepts a MultiCacheManager and a group name.
 *  <li>Implement the {@link #send send} method.
 *  <li>Have your communications layer call {@link #receive receive} upon receipt of a cache notification.
 * </ul>
 *
 * @author John Watkinson
 */
public abstract class Communicator {

	private String groupName;

	protected void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	private MultiCacheManager manager;

	public void setManager(MultiCacheManager manager) {
		this.manager = manager;
	}

	public MultiCacheManager getManager() {
		return manager;
	}

	/**
	 * Call when a notification is received by the communications layer.
	 * @param notification the parsed cache notification.
	 */
	protected void receive(CacheNotification notification) {
		manager.receiveNotification(notification);
	}

	/**
	 * Implement this to send a cache notification over the communications layer.
	 * @param notification the cache notification to send.
	 */
	protected abstract void send(CacheNotification notification);

	/**
	 * Called by the cache manager to shut down the communicator.
	 */
	public abstract void shutDown();
}
