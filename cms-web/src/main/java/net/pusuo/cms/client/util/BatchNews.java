package net.pusuo.cms.client.util;


public class BatchNews{

	String desc,text;
	String pname;
	String keyword,author;
	String fileAbsolutePath,imageShowPath,thumb;
	Integer media,priority,status;
	
	String mediaName;
	String addprio;
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}
	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getMedia() {
		return media;
	}
	public void setMedia(Integer media) {
		this.media = media;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}

	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImageShowPath() {
		return imageShowPath;
	}
	public void setImageShowPath(String imageShowPath) {
		this.imageShowPath = imageShowPath;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getAddprio() {
		return addprio;
	}
	public void setAddprio(String addprio) {
		this.addprio = addprio;
	}
	public String getMediaName() {
		return mediaName;
	}
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	
}