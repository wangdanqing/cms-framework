package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.util.Set;

/**
 * @hibernate.class table="cms_template"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Template implements Item {
    private static final long serialVersionUID = 1L;

    private int template_id = -1;
    private String template_name = null;
    private String template_desc = null;
    private int template_category = -1;
    private int template_status = -1;
    private int template_type = -1;
    private int template_mpage = 0;
    private String template_reference = "";

    private Set tfm = null;

    Template() {
    }

    /**
     * @hibernate.id column="template_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_ctf"
     */
    public int getId() {
        return template_id;
    }

    /**
     * @hibernate.property column="template_name"
     * not-null="true"
     */
    public String getName() {
        return template_name;
    }

    /**
     * @hibernate.property column="template_desc"
     */
    public String getDesc() {
        return template_desc;
    }

    /**
     * @hibernate.property column="template_category"
     */
    public int getCategory() {
        return template_category;
    }

    /**
     * @hibernate.property column="template_status"
     */
    public int getStatus() {
        return template_status;
    }

    /**
     * @hibernate.property column="template_type"
     */
    public int getType() {
        return template_type;
    }

    /**
     * @hibernate.property column="template_mpage"
     */
    public int getMpage() {
        return template_mpage;
    }

    /**
     * @hibernate.property column="template_reference"
     */
    public String getReference() {
        return this.template_reference;
    }

    /**
     * @hibernate.set inverse="true"
     * lazy="false"
     * cascade="all-delete-orphan"
     * table="cms_t_f_map"
     * @hibernate.collection-key column="template_id"
     * @hibernate.collection-one-to-many class="com.hexun.cms.core.TFMap"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getTFMaps() {
        return tfm;
    }

    public void setId(int id) {
        this.template_id = id;
    }

    public void setName(String name) {
        this.template_name = name;
    }

    public void setDesc(String description) {
        this.template_desc = description;
    }

    public void setCategory(int category) {
        this.template_category = category;
    }

    public void setStatus(int status) {
        this.template_status = status;
    }

    public void setType(int type) {
        this.template_type = type;
    }

    public void setMpage(int mpage) {
        this.template_mpage = mpage;
    }

    public void setReference(String reference) {
        this.template_reference = reference;
    }

    public void setTFMaps(Set set) {
        this.tfm = set;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }

}

