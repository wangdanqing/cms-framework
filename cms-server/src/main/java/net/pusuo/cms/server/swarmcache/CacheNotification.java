package net.pusuo.cms.server.swarmcache;

import java.io.Serializable;

/**
 * The actual object that gets sent to the cluster to indicate that an object
 * needs to be cleared from the cache.
 *
 * @author John Watkinson
 */

public class CacheNotification implements Serializable {

    private String type;
    private Serializable key;

    public CacheNotification() {
    }

    public CacheNotification(String type,
                             Serializable key) {
        this.type = type;
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setKey(Serializable key) {
        this.key = key;
    }

    public Serializable getKey() {
        return key;
    }

    /*
     public String toString()
     {
         return " Type: "+this.type+", Key: "+this.key;
     }
     */
}
