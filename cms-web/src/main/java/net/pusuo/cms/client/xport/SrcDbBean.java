package net.pusuo.cms.client.xport;

/**
 * 
 * @author shijinkui source is db,convert it to bean.
 */
public class SrcDbBean {
	private String id;// identy
	private String pid;// 父对象
	private String mediaId;// 媒体
	private String newsPriority;// 新闻的权重
	private String templateId; // 模板id

	private String sql; //对应的sql语句
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getNewsPriority() {
		return newsPriority;
	}

	public void setNewsPriority(String newsPriority) {
		this.newsPriority = newsPriority;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
