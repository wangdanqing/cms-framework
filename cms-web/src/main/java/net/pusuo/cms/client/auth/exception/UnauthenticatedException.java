package net.pusuo.cms.client.auth.exception;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:
 * @author
 * @version 1.0
 */

public class UnauthenticatedException extends Exception {

     public UnauthenticatedException() {
        super();
    }

    public UnauthenticatedException(String msg) {
        super(msg);
    }

    public String errorMessage() {
        return new String("no Permission!");
    }

}