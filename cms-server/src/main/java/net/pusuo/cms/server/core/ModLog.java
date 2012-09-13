package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.sql.Timestamp;

/**
 * @hibernate.class table="cms_modlog" package="com.hexun.cms.core"
 * dynamic-update="true" dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ModLog implements Item {

    private static final long serialVersionUID = 9193242523377230420L;
    protected int modlog_id = -1;
    protected int modlog_nid = -1;
    protected String modlog_title = "";
    protected String modlog_property = "";
    protected Timestamp modlog_time = null;
    protected String modlog_operator = "";
    protected int modlog_opid = -1;
    protected String modlog_url = "";

    /**
     * @hibernate.id column="modlog_id" unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="sq_modlog"
     */
    public int getId() {
        return modlog_id;
    }

    public String getName() {
        return String.valueOf(modlog_id);
    }

    public String getDesc() {
        return modlog_title;
    }

    /**
     * @hibernate.property column="modlog_nid"
     */
    public int getModlog_nid() {
        return modlog_nid;
    }

    /**
     * @hibernate.property column="modlog_title"
     */
    public String getModlog_title() {
        return modlog_title;
    }

    /**
     * @hibernate.property column="modlog_property"
     */
    public String getModlog_property() {
        return modlog_property;
    }

    /**
     * @hibernate.property column="modlog_time"
     */
    public Timestamp getModlog_time() {
        return modlog_time;
    }

    /**
     * @hibernate.property column="modlog_operator"
     */
    public String getModlog_operator() {
        return modlog_operator;
    }

    /**
     * @hibernate.property column="modlog_opid"
     */
    public int getModlog_opid() {
        return modlog_opid;
    }

    /**
     * @hibernate.property column="modlog_url"
     */
    public String getModlog_url() {
        return modlog_url;
    }

    public void setId(int modlog_id) {
        this.modlog_id = modlog_id;
    }

    public void setName(String noUse) {
    }

    public void setDesc(String noUse) {
    }

    public void setModlog_title(String modlog_title) {
        this.modlog_title = modlog_title;
    }

    public void setModlog_property(String modlog_property) {
        this.modlog_property = modlog_property;
    }

    public void setModlog_time(Timestamp modlog_time) {
        this.modlog_time = modlog_time;
    }

    public void setModlog_operator(String modlog_operator) {
        this.modlog_operator = modlog_operator;
    }

    public void setModlog_opid(int modlog_opid) {
        this.modlog_opid = modlog_opid;
    }

    public void setModlog_url(String modlog_url) {
        this.modlog_url = modlog_url;
    }

    public void setModlog_nid(int modlog_nid) {
        this.modlog_nid = modlog_nid;
    }
}
