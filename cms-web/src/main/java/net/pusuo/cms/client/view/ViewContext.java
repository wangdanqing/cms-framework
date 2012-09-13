/**
 * 
 */
package net.pusuo.cms.client.view;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/**
 * @author Alfred.Yuan
 *
 */
public class ViewContext implements Context {
	
	private VelocityContext context = null;

	public ViewContext() {
		context = new VelocityContext();
	}
	
	public VelocityContext getContext() {
		return context;
	}
	
	////////////////////////////////////////////////////////////////////////////

	public boolean containsKey(Object arg0) {
		
		return context.containsKey(arg0);
	}

	public Object get(String arg0) {
		
		return context.get(arg0);
	}

	public Object[] getKeys() {
		
		return context.getKeys();
	}

	public Object put(String arg0, Object arg1) {
		
		return context.put(arg0, arg1);
	}

	public Object remove(Object arg0) {
		
		return context.remove(arg0);
	}
	
}
