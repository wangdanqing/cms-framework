package net.pusuo.cms.server.tool;

import java.io.Serializable;

/* HotWordItem
 * @since CMS1.0
 * @version 1.0
 * @author XuLin
 */
public class HotWordItem implements Serializable {

	private String kw = "";

	private String url = "";

	private String other = "";

	public HotWordItem() {
	}

	public String getKw() {
		return kw;
	}

	public String getUrl() {
		return url;
	}

	public String getOther() {
		return other;
	}

	public void setKw(String kw) {
		this.kw = kw;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOther(String other) {
		this.other = other;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		HotWordItem hi = (HotWordItem) arg0;
		if (hi == null)
			return false;
		return kw.equals(hi.getKw());
	}
}

