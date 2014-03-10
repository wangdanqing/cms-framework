package net.pusuo.cms.core.bean;


import net.minidev.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

/**
 * 实体对象
 * 可以是新闻, 文章等文字内容
 */
public class EntityItem extends Item implements IHelper {

	private static final long serialVersionUID = -928413065422581527L;
	private long id;
	private int pid;                //	父栏目ID
	private String title;            //	标题
	private String subhead;            //	副标题
	private String content;            //	正文内容
	private Timestamp ctime;        //	创建时间
	private Timestamp uptime;        //	修改时间
	private int priority = 60;        //	权重
	private int status = STATUS_ENABLE;
	private int channelId;            //	频道
	private int mediaId;        //	媒体Id
	private String author;        //	作者
	private int editor;                //	编辑
	private int dutyEditor;        //	责任编辑
	private String url;                //	生成的静态永久地址
	private String category;    //	目录id
	private String shortName;        //	短引用
	private String keyword;            //	关键词
	private List<Long> pictures;        //	图片id list
	private String reurl;            //	跳转链接
	private String tags;            //	tag


	@Override
	public JSONObject toJson() {
		JSONObject j = new JSONObject();
		j.put("id", id);
		j.put("pid", pid);
		j.put("title", title);
		j.put("subhead", subhead);
		j.put("content", content);
		j.put("ctime", ctime);
		j.put("uptime", uptime);
		j.put("priority", priority);
		j.put("channelId", channelId);
		j.put("status", status);
		j.put("mediaId", mediaId);
		j.put("editor", editor);
		j.put("author", author);
		j.put("dutyEditor", dutyEditor);
		j.put("url", url);
		j.put("category", category);
		j.put("shortName", shortName);
		j.put("keyword", keyword);
		j.put("tags", tags);
		j.put("reurl", reurl);

		return j;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public int getMediaId() {
		return mediaId;
	}

	public void setMediaId(int mediaId) {
		this.mediaId = mediaId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}

	public int getDutyEditor() {
		return dutyEditor;
	}

	public void setDutyEditor(int dutyEditor) {
		this.dutyEditor = dutyEditor;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<Long> getPictures() {
		return pictures;
	}

	public void setPictures(List<Long> pictures) {
		this.pictures = pictures;
	}

	public String getReurl() {
		return reurl;
	}

	public void setReurl(String reurl) {
		this.reurl = reurl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
