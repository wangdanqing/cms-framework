/**
 * 
 */
package net.pusuo.cms.client.biz.util;

/**
 * @author Alfred.Yuan
 *
 */
public class MultiPageItem {
	
	private String subhead = null;
	private String subtext = null;
	
	public MultiPageItem() {
		
	}
	
	public MultiPageItem(String subhead, String subtext) {
		this.subhead = subhead;
		this.subtext = subtext;
	}
	
	public String getSubhead() {
		return subhead;
	}
	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}
	
	public String getSubtext() {
		return subtext;
	}
	public void setSubtext(String subtext) {
		this.subtext = subtext;
	}

}
