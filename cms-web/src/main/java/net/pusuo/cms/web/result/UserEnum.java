package net.pusuo.cms.web.result;

/**
 * @author 玄畅
 * @date 5/1/14 11:12
 */
public enum UserEnum {
	//	todo i18n
	SUCCESS(200, "登录成功"),
	EXPIRE(600, "用户登录时间过期，请重新登录"),
	UNLOGINED(601, "用户未登录");

	public int code;
	public String msg;

	UserEnum(int i, String s) {
		this.code = i;
		this.msg = s;
	}
}
