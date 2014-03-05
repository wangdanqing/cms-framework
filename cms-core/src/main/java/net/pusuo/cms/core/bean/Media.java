package net.pusuo.cms.core.bean;

import net.minidev.json.JSONObject;

public class Media extends Item implements IHelper {

	private static final long serialVersionUID = -5258789177543119069L;
	private int id;
	private String desc;
	private String siteurl;
	private String logourl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSiteurl() {
		return siteurl;
	}

	public void setSiteurl(String siteurl) {
		this.siteurl = siteurl;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

	@Override
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("desc", desc);
		json.put("siteurl", siteurl);
		json.put("logourl", logourl);

		return json;
	}
}
