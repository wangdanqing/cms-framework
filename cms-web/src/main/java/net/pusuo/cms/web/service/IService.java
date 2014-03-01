package net.pusuo.cms.web.service;

import net.pusuo.cms.core.bean.Channel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 13-4-8
 * Time: 下午10:18
 * To change this template use File | Settings | File Templates.
 */
public interface IService<T> {

	void insert(T channel);

	/**
	 * get by primary id
	 *
	 * @param id
	 *
	 * @return
	 */
	T getById(int id);

	/**
	 * delete by Id
	 *
	 * @param id
	 */
	void delete(int id);

	/**
	 * query by {@literal key}
	 *
	 * @param key
	 *
	 * @return T list
	 */
	List<T> query(int key);

	boolean update(T obj);

}
