package net.pusuo.cms.core.bean;

import java.io.Serializable;

public class Media implements Serializable {

	private static final long serialVersionUID = -3714437623945153430L;
	private long id;
	private String desc;
	private String siteurl;
	private String logurl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getLogurl() {
		return logurl;
	}

	public void setLogurl(String logurl) {
		this.logurl = logurl;
	}
}
