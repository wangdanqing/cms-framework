package net.pusuo.cms.client.newshoo;

import java.util.List;
import java.util.Map;

import com.hexun.cms.core.News;

public interface ClientService {
	
	/**
	 * ��������
	 * @param news
	 * @param pnames
	 * @return
	 * @throws Exception
	 */
	public News addNews(News news, String pnames) throws Exception ;
	/**
	 * ��������
	 * @param newsConfig
	 * @param parentName
	 * @param extend
	 * @return
	 * @throws Exception
	 */
	public News pushNews(News newsConfig, String parentName, Map extend) throws Exception ;
	
	/**
	 * �������ź��Զ����͸�����
	 * @param news
	 * @param pname
	 * @return
	 * @throws Exception
	 */
	public News addNewsAndPush(News news, String pname) throws Exception ;
	public String touchMe(String pname) throws Exception ;
}
