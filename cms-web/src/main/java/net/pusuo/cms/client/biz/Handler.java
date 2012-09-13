/**
 * 
 */
package net.pusuo.cms.client.biz;

import java.util.Map;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 *
 */
public interface Handler {

	public News preHandle(News news, Map extend)
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
	public News postHandle(News news, Map extend)
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
}
