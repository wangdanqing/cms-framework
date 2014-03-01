package net.pusuo.cms.core.bean.auth;

import java.io.Serializable;
import java.util.List;

public class Role implements Serializable {
	private static final long serialVersionUID = -7246177386860885921L;
	private int id;
	private String name;
	private String desc;
	private List perms;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List getPerms() {
		return perms;
	}

	public void setPerms(List perms) {
		this.perms = perms;
	}
}
