/**
 * 
 */
package net.pusuo.cms.client.biz.event;

import java.util.EventObject;

/**
 * @author Alfred.Yuan
 * 
 */
public class CmsEvent extends EventObject {

	protected transient String eventCode;
	protected transient Object dest;
	protected transient Object auth;

	/**
	 * @param source
	 */
	public CmsEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	public CmsEvent(Object source, String eventCode) {
		super(source);
		this.eventCode = eventCode;
	}

	public CmsEvent(Object source, Object dest, String eventCode) {
		super(source);
		this.dest = dest;
		this.eventCode = eventCode;
	}

	public CmsEvent(Object source, Object dest, Object auth, String eventCode) {
		super(source);
		this.dest = dest;
		this.eventCode = eventCode;
		this.auth = auth;
	}

	public String getEventCode() {
		return this.eventCode;
	}

	public Object getDest() {
		return this.dest;
	}

	public Object getAuth() {
		return this.auth;
	}

}
