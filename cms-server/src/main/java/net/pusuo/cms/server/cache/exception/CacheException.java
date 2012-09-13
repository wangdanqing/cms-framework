package net.pusuo.cms.server.cache.exception;

public class CacheException extends Exception {
    public CacheException(Throwable root) {
        super(root);
    }


    public CacheException(String string, Throwable root) {
        super(string, root);
    }

    public CacheException(String s) {
        super(s);
    }
}

