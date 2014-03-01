package net.pusuo.cms.core.bean;

import net.minidev.json.JSONObject;

/**
 * @author 玄畅
 * @date: 14-3-1 下午8:57
 */
public interface IHelper {
	/**
	 * java bean转成需要的JSON
	 *
	 * @return json
	 */
	JSONObject toJson();
}
