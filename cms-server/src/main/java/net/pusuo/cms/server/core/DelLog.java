/**
 * 
 */
package net.pusuo.cms.server.core;

import java.sql.Timestamp;

import com.hexun.cms.Item;

/**
 * @hibernate.class table="cms_dellog" package="com.hexun.cms.core"
 *                  dynamic-update="true" dynamic-insert="true"
 *                  optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class DelLog implements Item {

	private static final long serialVersionUID = 1L;

	private int dellog_id = -1;
	private String dellog_input = "";
	private String dellog_ids = "";
	private String dellog_titles = "";
	private String dellog_reason = "";
	private String dellog_initiator = "";
	private Timestamp dellog_time = null;
	private String dellog_operator = "";
	private int dellog_opid = -1;
	private int dellog_redo = 0;
	private Timestamp dellog_redotime = null;

	/**
	 * @hibernate.id
	 * column="dellog_id"
	 * unsaved-value="-1"
	 * generator-class="sequence"
	 * @hibernate.generator-param
	 * name="sequence"
	 * value="sq_dellog"
	 */
	public int getId() {
		return dellog_id;
	}

	public String getName() {
		return String.valueOf(dellog_id);
	}

	public String getDesc() {
		return String.valueOf(dellog_id);
	}

	/**
	 * @hibernate.property column="dellog_input"
	 */
	public String getDellog_input() {
		return dellog_input;
	}

	/**
	 * @hibernate.property column="dellog_ids"
	 */
	public String getDellog_ids() {
		return dellog_ids;
	}

	/**
	 * @hibernate.property column="dellog_titles"
	 */
	public String getDellog_titles() {
		return dellog_titles;
	}

	/**
	 * @hibernate.property column="dellog_reason"
	 */
	public String getDellog_reason() {
		return dellog_reason;
	}

	/**
	 * @hibernate.property column="dellog_initiator"
	 */
	public String getDellog_initiator() {
		return dellog_initiator;
	}

	/**
	 * @hibernate.property column="dellog_time"
	 */
	public Timestamp getDellog_time() {
		return dellog_time;
	}

	/**
	 * @hibernate.property column="dellog_operator"
	 */
	public String getDellog_operator() {
		return dellog_operator;
	}

	/**
	 * @hibernate.property column="dellog_opid"
	 */
	public int getDellog_opid() {
		return dellog_opid;
	}

	/**
	 * @hibernate.property column="dellog_redo"
	 */
	public int getDellog_redo() {
		return dellog_redo;
	}

	/**
	 * @hibernate.property column="dellog_redotime"
	 */
	public Timestamp getDellog_redotime() {
		return dellog_redotime;
	}

	public void setId(int dellog_id) {
		this.dellog_id = dellog_id;
	}

	public void setName(String noUse) {
	}

	public void setDesc(String noUse) {
	}

	public void setDellog_input(String dellog_input) {
		this.dellog_input = dellog_input;
	}

	public void setDellog_ids(String dellog_ids) {
		this.dellog_ids = dellog_ids;
	}

	public void setDellog_titles(String dellog_titles) {
		this.dellog_titles = dellog_titles;
	}

	public void setDellog_reason(String dellog_reason) {
		this.dellog_reason = dellog_reason;
	}

	public void setDellog_initiator(String dellog_initiator) {
		this.dellog_initiator = dellog_initiator;
	}

	public void setDellog_time(Timestamp dellog_time) {
		this.dellog_time = dellog_time;
	}

	public void setDellog_operator(String dellog_operator) {
		this.dellog_operator = dellog_operator;
	}

	public void setDellog_opid(int dellog_opid) {
		this.dellog_opid = dellog_opid;
	}

	public void setDellog_redo(int dellog_redo) {
		this.dellog_redo = dellog_redo;
	}

	public void setDellog_redotime(Timestamp dellog_redotime) {
		this.dellog_redotime = dellog_redotime;
	}

}
