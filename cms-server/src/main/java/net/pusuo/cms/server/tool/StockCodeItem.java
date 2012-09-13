package net.pusuo.cms.server.tool;

import net.pusuo.cms.server.Configuration;

import java.io.Serializable;

public class StockCodeItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String url;

	private int type;

	public StockCodeItem(String code, String name, int type) {
		super();
		this.code = code;
		this.name = name;
		this.type = type;
		String template = null;
		switch (type) {
		case StockCodeInterface.TYPE_STOCK:
			template = Configuration.getInstance().get(
					"cms4.gubar.url.template");
			break;
		case StockCodeInterface.TYPE_FUNDS:
			template = Configuration.getInstance().get(
					"cms4.jijinbar.url.template");
			break;
		}
		if (this.code != null && template != null) {
			url = template.replaceAll("\\$\\{code\\}", code);
			url = url.replaceAll("\\$\\{name\\}", name);
		}
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
