package net.pusuo.cms.core.bean;

public class SFrag extends Item {

	private static final long serialVersionUID = -2677034351334044628L;
	public final int type = TYPE_SFRAG;
	private long id;
	private String name;
	private String desc;
	private String content;
	private int status = STATUS_ENABLE;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
}

