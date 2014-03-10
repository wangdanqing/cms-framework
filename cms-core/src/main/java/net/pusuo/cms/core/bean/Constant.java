package net.pusuo.cms.core.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 玄畅
 * @date 14-3-1 下午3:14
 */
public class Constant {

	public static final String COMMON_JSON_PAGE = "common/result";    //	json数据页面
	public static final String CATEGORY_SEP = ";";


	/**
	 * 根据频道ID字符串解析出来频道id list
	 *
	 * @param category 带分隔符的频道id
	 *
	 * @return list
	 */
	public List<Integer> getCategory(String category) {
		if (category == null || category.equals("")) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		String[] array = category.split(CATEGORY_SEP);
		for (String ch : array) {
			list.add(Integer.parseInt(ch.trim()));
		}

		return list;
	}
}
