package net.pusuo.cms.core.bean;

import net.minidev.json.JSONObject;

public class Subject implements IHelper {

	private int id;
	private int pid;
	private String fullpath;//父对象的全路径，以";"分割
	private String name;    //发布英文名称
	private String desc;    //描述
	private long ctime;      //create time
	private int priority = 60;
	private int status = 0;
	private int channelId;
	private int parentId;   //父栏目id
	private int editorId;   //编辑
	private int templateId;//当前模板Id
	private String bakTemplateList;    //备用模板id列表, 以";"分割
	private int type = 1;   //类型，0:首页      1:栏目   2:专题

	@Override
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("pid", pid);
		json.put("name", name);
		json.put("desc", desc);
		json.put("ctime", ctime);
		json.put("status", status);
		json.put("editerId", editorId);
		json.put("fullpath", fullpath);
		json.put("templateId", templateId);

		return json;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
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


	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getEditorId() {
		return editorId;
	}

	public void setEditorId(int editorId) {
		this.editorId = editorId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getBakTemplateList() {
		return bakTemplateList;
	}

	public void setBakTemplateList(String bakTemplateList) {
		this.bakTemplateList = bakTemplateList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
}
