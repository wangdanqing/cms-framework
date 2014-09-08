package net.pusuo.cms.core.bean;

import net.minidev.json.JSONObject;

import java.sql.Timestamp;

public class Subject extends Item implements IHelper, Comparable<Subject> {
	private static final long serialVersionUID = -676062571458696121L;

	public static final int TYPE_HOME_PAGE = 0;    //	首页
	public static final int TYPE_SUBJECT = 1;      //	栏目
	public static final int TYPE_TOPIC = 2;        //	专题

	private int id;
	private int pid;        //父栏目id
	private String category;//父对象的全路径，以";"分割
	private String shortName;//发布英文名称
	private String name;    //栏目名字
	private String tags;    //关键词
	private String desc;    //描述
	private Timestamp ctime;     //create time
	private Timestamp uptime;    //update time
	private int priority = 60;
	private int status = 0;
	private int channelId;
	private int editorId;   //编辑
	private int templateId; //当前模板Id
	private String bakTemplateList;    //备用模板id列表, 以";"分割
	private int type;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Timestamp getCtime() {
		return ctime;
	}

	public void setCtime(Timestamp ctime) {
		this.ctime = ctime;
	}

	public Timestamp getUptime() {
		return uptime;
	}

	public void setUptime(Timestamp uptime) {
		this.uptime = uptime;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("pid", pid);
		json.put("name", name);
		json.put("shortName", shortName);
		json.put("type", type);
		json.put("tags", tags);
		json.put("ctime", ctime);
		json.put("uptime", uptime);
		json.put("priority", priority);
		json.put("channelId", channelId);
		json.put("desc", desc);
		json.put("status", status);
		json.put("editerId", editorId);
		json.put("category", category);
		json.put("templateId", templateId);
		json.put("bakTemplateList", bakTemplateList);

		return json;
	}

	@Override
	public int compareTo(Subject sub) {
		//  先比较pid，再比较id
		//  按照 小->大 顺序
		if (pid != sub.getPid()) {
			return sub.getPid() - pid;
		} else {
			return sub.getId() - id;
		}
	}
}
