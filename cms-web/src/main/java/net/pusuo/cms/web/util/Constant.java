package net.pusuo.cms.web.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
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
	 * @param channelStr 带分隔符的频道id
	 *
	 * @return list
	 */
	public List<Integer> getChannelList(String channelStr) {
		if (StringUtils.isBlank(channelStr)) {
			return null;
		}

		List<Integer> list = Lists.newArrayList();
		Iterator<String> it = Splitter.on(Constant.CATEGORY_SEP).split(channelStr).iterator();
		while (it.hasNext()) {
			list.add(Integer.parseInt(it.next()));
		}
		return list;
	}
}
