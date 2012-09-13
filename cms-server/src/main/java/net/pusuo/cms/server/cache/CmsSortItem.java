package net.pusuo.cms.server.cache;

import java.sql.Timestamp;

/**
 * @hibernate.class
 * table="cms_entity"
 * package="com.hexun.cms.cache"
 * optimistic-lock="version"
 */
public class CmsSortItem implements Comparable, java.io.Serializable, com.hexun.cms.Item {

	protected int id;

	protected Timestamp time;

	protected int priority;

	protected int type;
	
	protected int subtype;

	private String desc;

	private String url;

	private String category;

	private int status;

	public CmsSortItem() {
	}

	public CmsSortItem(int id, Timestamp time, int priority, int type, String desc, String url,
			String category) {
		this.id = id;
		this.time = time;
		this.priority = priority;
		this.type = type;
		this.desc = desc;
		this.url = url;
		this.category = category;
	}

	/**
	 *  @hibernate.id
	 *  column="entity_id"
	 *  unsaved-value="-1"
	 *  generator-class="sequence"
	 *  @hibernate.generator-param
	 *  name="sequence"
	 *  value="sq_entity"
	 */
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 * column="entity_time"
	 * update="false"
	 * insert="false"
	 */
	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp p) {
		this.time = p;
	}

	/**
	 * @hibernate.property
	 * column="entity_priority"
	 * update="false"
	 * insert="false"
	 */
	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int pp) {
		this.priority = pp;
	}

	/**
	 * @hibernate.property
	 * column="entity_type"
	 * update="false"
	 * insert="false"
	 */
	public int getType() {
		return this.type;
	}

	public void setType(int pp) {
		this.type = pp;
	}

	/**
	 * @hibernate.property
	 * column="entity_subtype"
	 * update="false"
	 * insert="false"
	 */
	public int getSubtype() {
		return this.subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int compareTo(Object o) {
		try {
			return time.compareTo(((CmsSortItem) o).getTime());
		} catch (ClassCastException e) {
			return 0;
		}
	}

	public String getName() {
		return null;
	}

	public void setName(String p) {
	}

	/**
	 * @hibernate.property
	 * column="entity_desc"
	 * update="false"
	 * insert="false"
	 */
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String p) {
		this.desc = p;
	}

	/**
	 * @hibernate.property
	 * column="entity_url"
	 * update="false"
	 * insert="false"
	 */
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String p) {
		this.url = p;
	}

	/**
	 * @hibernate.property
	 * column="entity_category"
	 * update="false"
	 * insert="false"
	 */
	public String getCategory() {
		return this.category;
	}

	public void setCategory(String p) {
		this.category = p;
	}

	/**
	 * @hibernate.property
	 * column="entity_status"
	 * update="false"
	 * insert="false"
	 */
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int p) {
		this.status = p;
	}
}
