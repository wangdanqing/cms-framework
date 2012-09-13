/**
 * 
 */
package net.pusuo.cms.client.util;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Alfred.Yuan
 *
 */
public class Relatives {

	private List keywordList = null;
	
	private List newsList = null;
	
	private List hintList = null;
	
	private List blogList = null;
	
	private List saybarList = null;

	public List getBlogList() {
		return blogList;
	}

	public void setBlogList(List blogList) {
		this.blogList = blogList;
	}

	public List getHintList() {
		return hintList;
	}

	public void setHintList(List hintList) {
		this.hintList = hintList;
	}

	public List getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List keywordList) {
		this.keywordList = keywordList;
	}

	public List getNewsList() {
		return newsList;
	}

	public void setNewsList(List newsList) {
		this.newsList = newsList;
	}

	public List getSaybarList() {
		return saybarList;
	}

	public void setSaybarList(List saybarList) {
		this.saybarList = saybarList;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public class Item {
		private String title = null;
		private String href = null;
		
		private String picUrl = null;
		private String summary = null;
		
		private Timestamp time = null;
		
		private String secondTitle = null;
		private String secondHref = null;
		
		public String getHref() {
			return href;
		}
		public void setHref(String href) {
			this.href = href;
		}
		
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		
		public String getSummary() {
			return summary;
		}
		public void setSummary(String summary) {
			this.summary = summary;
		}
		
		public String getSecondHref() {
			return secondHref;
		}
		public void setSecondHref(String secondHref) {
			this.secondHref = secondHref;
		}
		
		public String getSecondTitle() {
			return secondTitle;
		}
		public void setSecondTitle(String secondTitle) {
			this.secondTitle = secondTitle;
		}
		
		public Timestamp getTime() {
			return time;
		}
		public void setTime(Timestamp time) {
			this.time = time;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
}

