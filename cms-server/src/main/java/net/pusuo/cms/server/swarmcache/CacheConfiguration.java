package net.pusuo.cms.server.swarmcache;

/**
 * A class that contains the configuration information to be fed in to a new {@link CacheFactory CacheFactory}.
 *
 * @author John Watkinson
 */

public class CacheConfiguration {

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	/**
	 * The first half of the default channel properties. They default channel properties are:
	 * <pre>
	 * UDP(mcast_addr=*.*.*.*;mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):pbcast.STABLE(desired_avg_gossip=20000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)
	 * </pre>
	 * Where <code>*.*.*.*</code> is the specified multicast IP, which defaults to <code>231.12.21.132</code>.
	 */
	public static final String DEFAULT_CHANNEL_PROPERTIES_PRE =
	        "UDP(mcast_addr=";
	//           "UDP(mcast_addr=";
	/**
	 * The second half of the default channel properties. They default channel properties are:
	 * <pre>
	 * UDP(mcast_addr=*.*.*.*;mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):pbcast.STABLE(desired_avg_gossip=20000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)
	 * </pre>
	 * Where <code>*.*.*.*</code> is the specified multicast IP, which defaults to <code>231.12.21.132</code>.
	 */
	public static final String DEFAULT_CHANNEL_PROPERTIES_POST =
// JW: The following are old JavaGroups config strings:
//            ";mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.STABLE(desired_avg_gossip=20000):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)";
//            ";mcast_port=45566):PING:FD:VERIFY_SUSPECT:pbcast.STABLE:pbcast.NAKACK:UNICAST:FRAG:pbcast.GMS";
//            ";mcast_port=5678):PING:FD:STABLE:NAKACK:UNICAST:FRAG:FLUSH:GMS:VIEW_ENFORCER:STATE_TRANSFER:QUEUE";
//            ";mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.STABLE(desired_avg_gossip=20000):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)";

// JW: The following is the default JGroups 2.2 config string. It's use of FD instead of FD_SOCK is unsatisfying.
//	        ";mcast_port=45566;mcast_send_buf_size=32000;mcast_recv_buf_size=64000;ucast_send_buf_size=32000;ucast_recv_buf_size=64000;use_packet_handler=false;loopback=true;ip_ttl=32):" +
//	        "PING(timeout=2000;num_initial_members=3):" +
//	        "MERGE2(min_interval=5000;max_interval=10000):" +
//	        "FD(timeout=2000;max_tries=3;shun=true):" +
//	        "VERIFY_SUSPECT(timeout=1500):" +
//	        "pbcast.NAKACK(gc_lag=50;retransmit_timeout=600,1200,2400,4800;max_xmit_size=8192;use_mcast_xmit=false):" +
//	        "UNICAST(timeout=1200,2400,3600):" +
//	        "pbcast.STABLE(desired_avg_gossip=20000;max_bytes=0;stability_delay=1000):" +
//	        "FRAG(frag_size=8192;down_thread=false;up_thread=false):" +
//	        "pbcast.GMS(join_timeout=3000;join_retry_timeout=2000;shun=true;print_local_addr=true)";
// JW: This config string is recommended by Andre Schild, it re-arranges the protocols to avoid deadlock.
			";mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):" +
            "PING(timeout=2000;num_initial_members=3):" +
            "MERGE2(min_interval=5000;max_interval=10000):" +
            "FD_SOCK:" +
            "VERIFY_SUSPECT(timeout=1500):" +
            "pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):" +
            "UNICAST(timeout=5000):" +
            "pbcast.STABLE(desired_avg_gossip=20000):" +
            "FRAG(frag_size=8096;down_thread=false;up_thread=false):" +
            "pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)";

	/**
	 * The default multicast IP to be used by the cache manager. Its value is <code>231.12.21.132</code>.
	 */
	public static final String DEFAULT_MULTICAST_IP =
	        "231.12.21.132";

	/**
	 * The default LRU cache size. Its value is <code>10000</code>.
	 */
	public static final int DEFAULT_LRU_CACHE_SIZE = 10000;

	/**
	 * The <i>LRU</i> cache type.
	 */
	public static final String TYPE_LRU = "LRU";

	/**
	 * The <i>Automatic</i> cache type.
	 */
	public static final String TYPE_AUTO = "Auto";

	/**
	 * The <i>Timer</i> cache type.
	 */
	public static final String TYPE_TIMER = "Timer";

	/**
	 * The <i>Hybrid</i> cache type.
	 */
	public static final String TYPE_HYBRID = "Hybrid";

	//-------------------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------------------

	private String channelProperties;
	private String multicastIP;
	private String cacheType;
	private String lruCacheSize;

	//-------------------------------------------------------------------------
	// Public methods
	//-------------------------------------------------------------------------

	/**
	 * Gets the JavaGroups channel properties.
	 * If not specified, default properties will be generated using the multicast IP.
	 * Either this or the multicast IP must be set, or else <tt>null</tt> will be returned.
	 */
	public String getChannelProperties() {
		if (channelProperties == null) {
			if (multicastIP == null) {
				multicastIP = DEFAULT_MULTICAST_IP;
			}
			return DEFAULT_CHANNEL_PROPERTIES_PRE + multicastIP + DEFAULT_CHANNEL_PROPERTIES_POST;
		} else {
			return channelProperties;
		}
	}

	/**
	 * Sets the JavaGroup channel properties. See the
	 * JavaGroups <a href="http://www.javagroups.com/javagroupsnew/docs/index.html">homepage</a> for more imformation.
	 * If not specified, default properties will be generated using the multicast IP.
	 * Either this or the multicast IP must be set.
	 */
	public void setChannelProperties(String v) {
		channelProperties = v;
	}

	/**
	 * Gets the multicast IP address for the JavaGroup.
	 */
	public String getMulticastIP() {
		return multicastIP;
	}

	/**
	 * Sets the multicast IP address for the JavaGroup.
	 */
	public void setMulticastIP(String v) {
		multicastIP = v;
	}

	/**
	 * Gets the underlying cache type to use on each server.
	 * The two options are <tt>LRU</tt> and <tt>Auto</tt>.
	 */
	public String getCacheType() {
		return cacheType;
	}

	/**
	 * Sets the underlying cache type to use on each server.
	 * The two options are <tt>LRU</tt> and <tt>Auto</tt>.
	 */
	public void setCacheType(String v) {
		cacheType = v;
	}

	/**
	 * Gets the capacity of the LRU cache.
	 * If the cache type is LRU, then this is the maximum number of objects in the LRU queue.
	 * Otherwise, this is ignored.
	 */
	public String getLRUCacheSize() {
		if (lruCacheSize == null) {
			return "" + DEFAULT_LRU_CACHE_SIZE;
		} else {
			return lruCacheSize;
		}
	}

	/**
	 * Sets the capacity of the LRU cache.
	 * If the cache type is LRU, then this is the maximum number of objects in the LRU queue.
	 * Otherwise, this is ignored.
	 */
	public void setLRUCacheSize(String v) {
		lruCacheSize = v;
	}
}
