package net.pusuo.cms.core.bean;

import net.minidev.json.JSONObject;

import java.sql.Timestamp;

/**
 * 模版
 */
public class Template extends Item implements IHelper {

	private static final long serialVersionUID = -1522162035260271408L;
	private long id;
	private String name;
	private int type;
	private String content = "";
	private Timestamp createTime;
	private Timestamp uptime;
	private int status = STATUS_ENABLE;
	private int creator;    //  创建者

	/**
	 * 模版类型
	 */
	public enum Type {
		HOME_TEMPLATE(1, "首页模版"), SUBJECT_TEMPLATE(2, "栏目专题模版"), DETAIL_TEMPLATE(3, "正文页模版");
		public String str;
		int idx;

		Type(int idx, String name) {
			this.idx = idx;
			this.str = name;
		}
	}

	@Override
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("name", name);
		switch (type) {
			case 1:
				obj.put("type", Type.HOME_TEMPLATE.str);
				break;
			case 2:
				obj.put("type", Type.SUBJECT_TEMPLATE.str);
				break;
			case 3:
				obj.put("type", Type.DETAIL_TEMPLATE.str);
				break;
			default:
				break;
		}

		obj.put("content", content);
		obj.put("createTime", createTime);
		obj.put("uptime", uptime);
		obj.put("status", status);
		obj.put("creator", creator);
		return obj;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getUptime() {
		return uptime;
	}

	public void setUptime(Timestamp uptime) {
		this.uptime = uptime;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}

