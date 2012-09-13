/**
 * 
 */
package net.pusuo.cms.client.biz.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.SubjectManager;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.client.biz.exception.RelationException;
import com.hexun.cms.client.biz.exception.ShortnameException;
import com.hexun.cms.core.Subject;

/**
 * @author Alfred.Yuan
 *
 */
public class SubjectManagerProxy extends CmsManagerProxy implements	SubjectManager {
	
	private static final Log log = LogFactory.getLog(SubjectManagerProxy.class);
	
	private SubjectManager instance = null;

	private SubjectManagerProxy() {
		
	}
	
	public SubjectManagerProxy(SubjectManager subjectManager) {
		instance = subjectManager;
	}

	public Subject addSubject(Subject subjectConfig, Map extend)
			throws PropertyException, ParentNameException, RelationException,
			UnauthenticatedException, DaoException, ShortnameException {
		
		Subject subject = null;
		if (instance != null) {
			subject = instance.addSubject(subjectConfig, extend);
		}
		
		return subject;
	}

	public Subject updateSubject(Subject subjectConfig, Map extend)
			throws PropertyException, ParentNameException, RelationException,
			UnauthenticatedException, DaoException, ShortnameException {
		
		Subject subject = null;
		if (instance != null) {
			subject = instance.updateSubject(subjectConfig, extend);
		}
		
		return subject;
	}

	public boolean deleteSubject(Subject subject) throws PropertyException,
			ParentNameException, UnauthenticatedException, DaoException {
		
		return instance.deleteSubject(subject);
	}

	public Subject getSubject(int subjectId) throws DaoException {
		
		return instance.getSubject(subjectId);
	}

}
