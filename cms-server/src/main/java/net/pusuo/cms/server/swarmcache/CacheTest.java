package net.pusuo.cms.server.swarmcache;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;

import java.net.*;

/**
 * Test cache app. Run this on multiple machines at once.
 * @author John Watkinson
 */

public class CacheTest {

	private static Random random = new Random();

	/**
	 * String length to use for randomly generated keys.
	 */
	public static final int RANDOM_KEY_LENGTH = 20;

	/**
	 * String length to use for randomly generated values.
	 */
	public static final int RANDOM_VALUE_LENGTH = 1000;

	public static class LoadTester implements Runnable {

		private ObjectCache cache;
		private int numKeys;
		private int operationsPerSecond;
		private int writesPerThousand;
		private int numberOfSeconds;

		public LoadTester(ObjectCache cache,
		                  int numKeys,
		                  int operationsPerSecond,
		                  int writesPerThousand,
		                  int numberOfSeconds) {
			this.cache = cache;
			this.numKeys = numKeys;
			this.operationsPerSecond = operationsPerSecond;
			this.writesPerThousand = writesPerThousand;
			this.numberOfSeconds = numberOfSeconds;
		}


		public void run() {
			System.out.println(">>> Thread " + Thread.currentThread().getName() + " starting load test.");
			loadTest();
			System.out.println(">>> Thread " + Thread.currentThread().getName() + " completed load test.");
		}

		private void loadTest() {
			// Calculate average time between accesses * 2 and clearsPerSecond * 2
			int maxWaitTime = 2 * 1000 / operationsPerSecond;
			long startTime = System.currentTimeMillis();
			long stopTime = startTime + 1000 * numberOfSeconds;
			long currentTime = System.currentTimeMillis();
			while (currentTime < stopTime) {
				int waitTime = random.nextInt(maxWaitTime);
				currentTime = System.currentTimeMillis();
				// Do access
				String key = "" + random.nextInt(numKeys);
				if (random.nextInt(1000) < writesPerThousand) {
					// Do a write
					cache.put(key, key);
				} else {
					// Do a read
					cache.get(key);
				}
				long pauseTime = waitTime - (System.currentTimeMillis() - currentTime);
				if (pauseTime > 0) {
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
					}
				}
			}

		}

	}

	/**
	 * A String that outputs to STDOUT when it has been finalized.
	 */
	public static class FinalizerString {
		private String s;

		public FinalizerString(String string) {
			s = string;
		}

		public boolean equals(Object o) {
			return s.equals(o);
		}

		public String toString() {
			return s;
		}

		protected void finalize() throws Throwable {
			System.out.println("Finalized: \"" + s + "\".");
		}
	}

	private static String readLine(InputStream in) throws IOException {
		String result = "";
		char c = (char) in.read();
		while (c != '\n' && c != '\0') {
			result += in.read();
			c = (char) in.read();
		}
		if (result.length() == 0) {
			return null;
		} else {
			return result;
		}
	}

	private static String randomString(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) ('a' + (int) (Math.random() * 26));
		}
		return new String(chars);
	}

	public static void main(String[] args) {
		try {
			System.out.println("Local: " + InetAddress.getLocalHost());
			System.out.println("Args: [[[<multicast IP>] <cache-type>] <lru-cache-size>]");
			CacheConfiguration conf = new CacheConfiguration();
			if (args.length > 0) {
				conf.setMulticastIP(args[0]);
			}
			if (args.length > 1) {
				conf.setCacheType(args[1]);
			}
			if (args.length > 2) {
				conf.setLRUCacheSize(args[2]);
			}
			boolean finalizerString = false;
			if (CacheConfiguration.TYPE_AUTO.equals(conf.getCacheType()) ||
			        CacheConfiguration.TYPE_HYBRID.equals(conf.getCacheType())) {
				finalizerString = true;
			}
			String local = InetAddress.getLocalHost().getHostAddress();
			System.out.println("LOCAL: " + local);
			// conf.setChannelProperties("UDP(mcast_addr=237.0.0.2;bind_addr=" + local + ";mcast_port=45566;ip_ttl=32;mcast_send_buf_size=150000;mcast_recv_buf_size=80000):PING(timeout=2000;num_initial_members=3):MERGE2(min_interval=5000;max_interval=10000):FD_SOCK:VERIFY_SUSPECT(timeout=1500):pbcast.STABLE(desired_avg_gossip=20000):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):UNICAST(timeout=5000):FRAG(frag_size=8096;down_thread=false;up_thread=false):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=false;print_local_addr=true)");
			CacheFactory factory = new CacheFactory(conf);
			ObjectCache cache = factory.createCache("TEST");
			System.out.println("Cache has been initialized.");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("New stream created.");
			String input = in.readLine();
			while (input != null) {
				StringTokenizer st = new StringTokenizer(input);
				String command = st.nextToken();
				if ("PUT".equalsIgnoreCase(command)) {
					String key = st.nextToken();
					String value = st.nextToken();
					// Clear old value
					// cache.clear(key);
					if (finalizerString) {
						cache.put(key, new FinalizerString(value));
					} else {
						cache.put(key, value);
					}
					System.out.println("Put '" + value + "' in to the cache with key '" + key + "'.");
				} else if ("GET".equalsIgnoreCase(command)) {
					String key = st.nextToken();
					Object o = cache.get(key);
					System.out.println("Getting '" + key + "' from cache returned '" + o + "'.");
				} else if ("CLEAR".equalsIgnoreCase(command)) {
					String key = st.nextToken();
					cache.clear(key);
					System.out.println("Cleared '" + key + "' from the cache.");
				} else if ("CLEARALL".equalsIgnoreCase(command)) {
					cache.clearAll();
					System.out.println("Cleared all keys from cache.");
				} else if ("FILL".equalsIgnoreCase(command)) {
					int n = Integer.parseInt(st.nextToken());
					for (int i = 0; i < n; i++) {
						String key = randomString(RANDOM_KEY_LENGTH);
						String value = randomString(RANDOM_VALUE_LENGTH);
						cache.put(key, value);
					}
					System.out.println("Put " + n + " random strings of length " + RANDOM_VALUE_LENGTH + " in to the cache.");
				} else if ("LOADTEST".equalsIgnoreCase(command)) {
					// Run loadtester
					int numKeys = Integer.parseInt(st.nextToken());
					int accessesPerSecond = Integer.parseInt(st.nextToken());
					int writesPerThousand = Integer.parseInt(st.nextToken());
					int numberOfSeconds = Integer.parseInt(st.nextToken());
					int numberOfThreads = Integer.parseInt(st.nextToken());
					for (int i = 0; i < numberOfThreads; i++) {
						LoadTester tester = new LoadTester(cache, numKeys, accessesPerSecond, writesPerThousand, numberOfSeconds);
						new Thread(tester, "LoadTest-" + (i + 1)).start();
					}
				} else if ("MEMORY".equalsIgnoreCase(command)) {
					int numberOfThreads = Integer.parseInt(st.nextToken());
					System.out.println("Causing an OutOfMemoryError with " + numberOfThreads + " threads...");
					for (int i = 0; i < numberOfThreads; i++) {
						new Thread(new Runnable() {
							public void run() {
								List l = new LinkedList();
								while (true) {
									l.add(randomString(RANDOM_VALUE_LENGTH));
								}
							}
						}, "Memory-" + (i + 1)).start();
					}
				} else if ("CPU".equalsIgnoreCase(command)) {
					final int numberOfSeconds = Integer.parseInt(st.nextToken());
					System.out.println("Causing high CPU load for " + numberOfSeconds + " seconds...");
					new Thread(new Runnable() {
						public void run() {
							long startTime = System.currentTimeMillis();
							long endTime = startTime + 1000 * numberOfSeconds;
							while (System.currentTimeMillis() < endTime) {
								double a = 0;
								for (int i = 0; i < 1000; i++) {
									a = Math.random() * 50000;
									a = Math.sqrt(a);
								}
							}
						}
					}, "CPU-loader").start();
				} else {
					System.out.println("Unrecognized command: '" + command + "'.");
				}
				input = in.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
