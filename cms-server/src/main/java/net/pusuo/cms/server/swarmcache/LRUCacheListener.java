package net.pusuo.cms.server.swarmcache;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: jwat
 * Date: Jun 27, 2003
 * Time: 3:22:59 PM
 * To change this template use Options | File Templates.
 */
public interface LRUCacheListener {
	public void objectRemoved(Serializable key, Object value);
}
