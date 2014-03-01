package net.pusuo.cms.core.bean;

import java.util.List;

/**
 * EntityItem所有Entity的基类
 */
public class EntityItem extends Item {

	private static final long serialVersionUID = -3721729566644823912L;

	private long id;
	private long pid;
	private String name;
	private String desc;
	private long time;
	private int priority = 0;
	private int status = 0;
	private int channel;
	private int editor; //编辑
	private long template;//唯一的模板
	private String url;
	private List<Long> category; //目录id
	private String param;
	private String shortname;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
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

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}

	public long getTemplate() {
		return template;
	}

	public void setTemplate(long template) {
		this.template = template;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Long> getCategory() {
		return category;
	}

	public void setCategory(List<Long> category) {
		this.category = category;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
}

