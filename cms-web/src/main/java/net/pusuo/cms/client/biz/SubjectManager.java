/**
 * 
 */
package net.pusuo.cms.client.biz;

import java.util.Map;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
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
public interface SubjectManager extends CmsManager {
	
	public static final String PROPERTY_NAME_NEED_HANDLED_TEMPLATE = "SubjectManager.needHandledTemplate";
	
	////////////////////////////////////////////////////////////////////////////
	
	public Subject getSubject(int subjectId) throws DaoException;

	public Subject addSubject(Subject subject, Map extend) 
		throws PropertyException, ParentNameException, RelationException,
		UnauthenticatedException, DaoException, ShortnameException;
	
	public Subject updateSubject(Subject subject, Map extend) 
		throws PropertyException, ParentNameException, RelationException,
		UnauthenticatedException, DaoException, ShortnameException;
	
	public boolean deleteSubject(Subject subject) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
}
