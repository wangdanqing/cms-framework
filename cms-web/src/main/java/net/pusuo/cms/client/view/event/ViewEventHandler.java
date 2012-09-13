/**
 * 
 */
package net.pusuo.cms.client.view.event;

import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

/**
 * @author Alfred.Yuan
 *
 */
public class ViewEventHandler implements ReferenceInsertionEventHandler,
		NullSetEventHandler, MethodExceptionEventHandler {

	/* (non-Javadoc)
	 * @see org.apache.velocity.app.event.ReferenceInsertionEventHandler#referenceInsert(java.lang.String, java.lang.Object)
	 */
	public Object referenceInsert(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.app.event.NullSetEventHandler#shouldLogOnNullSet(java.lang.String, java.lang.String)
	 */
	public boolean shouldLogOnNullSet(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.app.event.MethodExceptionEventHandler#methodException(java.lang.Class, java.lang.String, java.lang.Exception)
	 */
	public Object methodException(Class arg0, String arg1, Exception arg2)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
