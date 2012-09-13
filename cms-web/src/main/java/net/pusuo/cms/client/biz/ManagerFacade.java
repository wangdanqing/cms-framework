/**
 * 
 */
package net.pusuo.cms.client.biz;

import com.hexun.cms.client.biz.impl.NewsManagerImpl;
import com.hexun.cms.client.biz.impl.NewsManagerProxy;
import com.hexun.cms.client.biz.impl.PictureManagerImpl;
import com.hexun.cms.client.biz.impl.PictureManagerProxy;
import com.hexun.cms.client.biz.impl.SubjectManagerImpl;
import com.hexun.cms.client.biz.impl.SubjectManagerProxy;

/**
 * @author Alfred.Yuan
 *
 */
public class ManagerFacade {
	
	private static NewsManager newsManager = null;
	private static final Object lock4NewsManager = new Object();
	
	private static PictureManager pictureManager = null;
	private static final Object lock4PictureManager = new Object();

	private static SubjectManager subjectManager = null;
	private static final Object lock4SubjectManager = new Object();

	private ManagerFacade() {
	}
	
	public static NewsManager getNewsManager() {	
		try {
			if (newsManager == null) {
				synchronized (lock4NewsManager) {
					if (newsManager == null) {
						newsManager = new NewsManagerProxy(new NewsManagerImpl());
					}
				}
			}
			return newsManager;
		}
		catch (Exception e) {
			throw new IllegalStateException();
		}
	}
	
	public static PictureManager getPictureManager() {
		try {
			if (pictureManager == null) {
				synchronized (lock4PictureManager) {
					if (pictureManager == null) {
						pictureManager = new PictureManagerProxy(new PictureManagerImpl());
					}
				}
			}
			return pictureManager;
		}
		catch (Exception e) {
			throw new IllegalStateException();
		}
	}
	
	public static SubjectManager getSubjectManager() {
		try {
			if (subjectManager == null) {
				synchronized (lock4SubjectManager) {
					if (subjectManager == null) {
						subjectManager = new SubjectManagerProxy(new SubjectManagerImpl());
					}
				}
			}
			return subjectManager;
		}
		catch (Exception e) {
			throw new IllegalStateException();
		}
	}

}
