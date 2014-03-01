package net.pusuo.cms.web.util;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 解析form数据流，返回一个json对象
 *
 * @author 玄畅
 * @date 14-3-1 上午7:54
 */
public class FormRequestUtil {

	/**
	 * 解析stream为json对象
	 *
	 * @param request servlet request
	 *
	 * @return json
	 */
	public static JSONObject parseData(HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			ServletInputStream inputStream = request.getInputStream();
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			jsonObject = (JSONObject) JSONValue.parse(b);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}


}
