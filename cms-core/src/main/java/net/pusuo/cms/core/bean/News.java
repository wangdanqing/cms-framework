package net.pusuo.cms.core.bean;


public class News extends EntityItem {

	public static final int SUBTYPE_DEFAULT = 0; // 不区分(兼容)
	public static final int SUBTYPE_TEXT = 1; // 文本新闻
	public static final int SUBTYPE_PICTURE = 2; // 图片新闻
	public static final int SUBTYPE_ZUTU = 3; // 组图新闻

	private String keyword;
	private String author;
	private String dutyEditor;
	private String subhead;
	private String relativenews;
	private String pictures;
	private String pushrecord;
	private int referid;
	private String reurl;
	private String medianame;
	private String tag;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public String getRelativenews() {
		return relativenews;
	}

	public void setRelativenews(String relativenews) {
		this.relativenews = relativenews;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getPushrecord() {
		return pushrecord;
	}

	public void setPushrecord(String pushrecord) {
		this.pushrecord = pushrecord;
	}

	public String getDutyEditor() {
		return dutyEditor;
	}

	public void setDutyEditor(String dutyEditor) {
		this.dutyEditor = dutyEditor;
	}

	public int getReferid() {
		return referid;
	}

	public void setReferid(int referid) {
		this.referid = referid;
	}

	public String getReurl() {
		return reurl;
	}

	public void setReurl(String reurl) {
		this.reurl = reurl;
	}

	public String getMedianame() {
		return medianame;
	}

	public void setMedianame(String medianame) {
		this.medianame = medianame;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
