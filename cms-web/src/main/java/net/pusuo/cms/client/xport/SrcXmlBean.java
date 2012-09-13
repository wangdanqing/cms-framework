package net.pusuo.cms.client.xport;


public class SrcXmlBean {

	private String id;// identy
	private String pid;// 父对象
	private String mediaId;// 媒体
	private String newsPriority;// 新闻的权重
	private String templateId; // 模板id

	private String sourceUrl;//rss url
	private String startTag;//起始标记
	private String endTag;//起始标记
	private String translate; //翻译标记. 0:不翻译; 1: 英文翻译成中文; 2: 中文翻译成英文; 3: 简体翻译成繁体; 4:繁体翻译成简体
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getStartTag() {
		return startTag;
	}
	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}
	public String getEndTag() {
		return endTag;
	}
	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}
	public String getTranslate() {
		return translate;
	}
	public void setTranslate(String translate) {
		this.translate = translate;
	}
}
