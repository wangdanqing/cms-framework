package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.io.Serializable;

/**
 * @hibernate.class table="cms_t_f_map"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class TFMap implements Item, Serializable {
    private static final long serialVersionUID = 1L;

    private int tf_id = -1;
    private Template template = null;
    private int tf_type = -1;
    private String tf_name = null;
    private String tf_desc = null;
    private int tf_entityid = -1;
    private String tf_otherids = "";
    private String tf_permission = null;
    private int tf_quotetype = -1;
    private String tf_quotefrag = null;
    private int tf_ut = -1;
    private int tf_uet = -1;
    private int tf_listcount = -1;
    private int tf_styletype = -1;
    private int tf_timetype = -1;
    private int tf_sorttype = -1;
    private String tf_prio = null;
    private String tf_range = null;

    TFMap() {
    }


    /**
     * @hibernate.id column="tf_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_ctf"
     */
    public int getId() {
        return tf_id;
    }

    public void setId(int tf_id) {
        this.tf_id = tf_id;
    }

    /**
     * @hibernate.many-to-one column="template_id"
     * class="com.hexun.cms.core.Template"
     * not-null="true"
     */
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @hibernate.property column="tf_type"
     * not-null="true"
     */
    public int getType() {
        return tf_type;
    }

    public void setType(int tf_type) {
        this.tf_type = tf_type;
    }

    /**
     * @hibernate.property column="tf_name"
     */
    public String getName() {
        return tf_name;
    }

    public void setName(String tf_name) {
        this.tf_name = tf_name;
    }

    /**
     * @hibernate.property column="tf_desc"
     */
    public String getDesc() {
        return tf_desc;
    }

    public void setDesc(String tf_desc) {
        this.tf_desc = tf_desc;
    }

    /**
     * @hibernate.property column="tf_entityid"
     */
    public int getEntityid() {
        return tf_entityid;
    }

    public void setEntityid(int tf_entityid) {
        this.tf_entityid = tf_entityid;
    }

    /**
     * @hibernate.property column="tf_otherids"
     */
    public String getOtherIds() {
        return this.tf_otherids;
    }

    public void setOtherIds(String otherIds) {
        this.tf_otherids = otherIds;
    }

    /**
     * @hibernate.property column="tf_permission"
     */
    public String getPermission() {
        return tf_permission;
    }

    public void setPermission(String tf_permission) {
        this.tf_permission = tf_permission;
    }

    /**
     * @hibernate.property column="tf_quotefrag"
     */
    public String getQuotefrag() {
        return tf_quotefrag;
    }

    public void setQuotefrag(String tf_quotefrag) {
        this.tf_quotefrag = tf_quotefrag;
    }

    /**
     * @hibernate.property column="tf_quotetype"
     */
    public int getQuotetype() {
        return tf_quotetype;
    }

    public void setQuotetype(int tf_quotetype) {
        this.tf_quotetype = tf_quotetype;
    }

    /**
     * @hibernate.property column="tf_ut"
     */
    public int getUt() {
        return tf_ut;
    }

    public void setUt(int tf_ut) {
        this.tf_ut = tf_ut;
    }

    /**
     * @hibernate.property column="tf_uet"
     */
    public int getUet() {
        return tf_uet;
    }

    public void setUet(int tf_uet) {
        this.tf_uet = tf_uet;
    }

    /**
     * @hibernate.property column="tf_listcount"
     */
    public int getListcount() {
        return tf_listcount;
    }

    public void setListcount(int tf_listcount) {
        this.tf_listcount = tf_listcount;
    }

    /**
     * @hibernate.property column="tf_styletype"
     */
    public int getStyletype() {
        return tf_styletype;
    }

    public void setStyletype(int tf_styletype) {
        this.tf_styletype = tf_styletype;
    }

    /**
     * @hibernate.property column="tf_timetype"
     */
    public int getTimetype() {
        return tf_timetype;
    }

    public void setTimetype(int tf_timetype) {
        this.tf_timetype = tf_timetype;
    }

    /**
     * @hibernate.property column="tf_sorttype"
     */
    public int getSorttype() {
        return tf_sorttype;
    }

    public void setSorttype(int tf_sorttype) {
        this.tf_sorttype = tf_sorttype;
    }

    /**
     * @hibernate.property column="tf_prio"
     */
    public String getPrio() {
        return tf_prio;
    }

    public void setPrio(String tf_prio) {
        this.tf_prio = tf_prio;
    }

    /**
     * @hibernate.property column="tf_range"
     */
    public String getRange() {
        return this.tf_range;
    }

    public void setRange(String tf_range) {
        this.tf_range = tf_range;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }

}

