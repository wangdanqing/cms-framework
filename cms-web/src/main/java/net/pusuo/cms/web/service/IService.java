package net.pusuo.cms.web.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午10:18
 * To change this template use File | Settings | File Templates.
 */
public interface IService<T> {

	boolean insert(T channel);

	/**
	 * get by primary id
	 *
	 * @param id id
	 *
	 * @return T
	 */
	T getById(int id);

	/**
	 * delete by primary Id
	 *
	 * @param id id
	 */
	boolean delete(int id);

	/**
	 * query by {@literal key}
	 *
	 * @param key key
	 *
	 * @return T list
	 */
	List<T> query(int key);

	boolean update(T obj);

}
