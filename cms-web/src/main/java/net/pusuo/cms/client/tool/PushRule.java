package net.pusuo.cms.client.tool;

public class PushRule {
	String fromName, toName,copyNews;
	int changePriority,fromPriority;
	boolean deepSearch,spanChannel;
	String type;
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public int getChangePriority() {
		return changePriority;
	}

	public void setChangePriority(int changePriority) {
		this.changePriority = changePriority;
	}

	public boolean isDeepSearch() {
		return deepSearch;
	}

	public void setDeepSearch(boolean deepSearch) {
		this.deepSearch = deepSearch;
	}




	public String getCopyNews() {
		return copyNews;
	}

	public void setCopyNews(String copyNews) {
		this.copyNews = copyNews;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSpanChannel() {
		return spanChannel;
	}

	public void setSpanChannel(boolean spanChannel) {
		this.spanChannel = spanChannel;
	}

	public int getFromPriority() {
		return fromPriority;
	}

	public void setFromPriority(int fromPriority) {
		this.fromPriority = fromPriority;
	}
}
