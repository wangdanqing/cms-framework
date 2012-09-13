/**
 * 
 */
package net.pusuo.cms.client.biz;

import java.util.List;
import java.util.Map;

import com.hexun.cms.client.auth.exception.UnauthenticatedException;
import com.hexun.cms.client.biz.exception.DaoException;
import com.hexun.cms.client.biz.exception.ParentNameException;
import com.hexun.cms.client.biz.exception.PropertyException;
import com.hexun.cms.core.News;

/**
 * @author Alfred.Yuan
 * �ӿڷ����г����в���(...,Map extend).�����ǹ��ڸò��������.
 * Ŀ��:���ݲ�������News���󴫵ݵĲ���,����֤����auth���߸�����pname.
 * �÷�:
 *     1.������Ʊ�����NewsManager�ж���.
 *     2.ÿ�ε��ýӿڷ�����ʱ��,������¹���Map����.
 * ����:
 * 	   News newsConfig = ...;
 * 
 *     Map extend = new HashMap();
 *     Authentication auth = ...;
 *     extend.put(NewsManager.PROPERTY_NAME_AUTH, auth);
 *     String parentName = ...;
 *     extend.put(NewsManager.PROPERTY_NAME_PNAME, parentName);
 *     
 *     newsManager.addNews(newsConfig, extend);
 *     ......
 */
public interface NewsManager extends CmsManager {
	
	public final static String PROPERTY_NAME_CANONICAL = "NewsManager.canonical";
	
	public final static String PROPERTY_NAME_PUSH_MODE = "NewsManager.push.mode";
	public final static int PROPERTY_NAME_PUSH_MODE_LINK = 0;
	public final static int PROPERTY_NAME_PUSH_MODE_COPY = 1;
	public final static int PROPERTY_NAME_PUSH_MODE_MOVE = 2;
	
	public final static String PROPERTY_NAME_PUSH_PRIORITY = "NewsManager.push.priority";
	
	public final static String PROPERTY_NAME_PUSH_DESC = "NewsManager.push.desc";
	
	public final static String PROPERTY_NAME_PUSH_TIME = "NewsManager.push.time";
	public final static int PROPERTY_NAME_PUSH_TIME_OLD = 0;
	public final static int PROPERTY_NAME_PUSH_TIME_NOW = 1;
	
	public final static String PROPERTY_NAME_IS_SAVE_ACTION = "NewsManager.isSaveAction";
	
	////////////////////////////////////////////////////////////////////////////
	
	public News getNews(int newsId) throws DaoException;

	public News addNews(News news, Map extend) throws PropertyException, ParentNameException, 
										  UnauthenticatedException, DaoException;
	
	public News updateNews(News news, Map extend) throws PropertyException, ParentNameException, 
	  									     UnauthenticatedException, DaoException;

	public boolean deleteNews(News news) throws PropertyException, ParentNameException, 
		 										UnauthenticatedException, DaoException;
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * ��������: һƪ���ŵ����������
	 * ������:name
	 */
	public News pushNews(News news, List parentNameList, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
	/**
	 * ��������: һƪ���ŵ����������
	 * ������:id
	 */
	public News pushNewsByIds(News news, List parentIdList, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
	/**
	 * ��������: һƪ���ŵ�һ��������
	 * ������:name
	 */
	public News pushNews(News news, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;

	/**
	 * ��������: һƪ���ŵ�һ��������
	 * ������:id
	 */
	public News pushNews(News news, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;

	////////////////////////////////////////////////////////////////////////////

	/**
	 * Ǩ������: һƪ����
	 * ������:name
	 */
	public News moveNews(News news, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;

	/**
	 * Ǩ������: һƪ����
	 * ������:id
	 */
	public News moveNews(News news, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;

	/**
	 * Ǩ������: ��ƪ����
	 * ������:name
	 */
	public List moveNews(List newsList, String parentName, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;

	/**
	 * Ǩ������: ��ƪ����
	 * ������:id
	 */
	public List moveNews(List newsList, int parentId, Map extend) 
		throws PropertyException, ParentNameException, UnauthenticatedException, DaoException;
	
	////////////////////////////////////////////////////////////////////////////

}
