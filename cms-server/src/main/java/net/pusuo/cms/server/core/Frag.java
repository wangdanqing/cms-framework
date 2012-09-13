package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

/**
 * @hibernate.class table="cms_frag"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Frag implements Item {

    private static final long serialVersionUID = 1033775719629469059L;
    private int frag_id = -1;
    private String frag_name = null;
    private String frag_desc = null;
    private int frag_category = -1;
    private int frag_status = -1;
    private int frag_type = -1;
    private int frag_updatetype = -1;

    Frag() {
    }

    /**
     * @hibernate.id column="frag_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_ctf"
     */
    public int getId() {
        return frag_id;
    }

    /**
     * @hibernate.property column="frag_name"
     * not-null="true"
     */
    public String getName() {
        return frag_name;
    }

    /**
     * @hibernate.property column="frag_desc"
     */
    public String getDesc() {
        return frag_desc;
    }

    /**
     * @hibernate.property column="frag_category"
     */
    public int getCategory() {
        return frag_category;
    }

    /**
     * @hibernate.property column="frag_status"
     */
    public int getStatus() {
        return frag_status;
    }

    /**
     * @hibernate.property column="frag_type"
     */
    public int getType() {
        return frag_type;
    }

    /**
     * @hibernate.property column="frag_updatetype"
     */
    public int getUpdatetype() {
        return frag_updatetype;
    }

    public void setId(int id) {
        this.frag_id = id;
    }

    public void setName(String name) {
        this.frag_name = name;
    }

    public void setDesc(String description) {
        this.frag_desc = description;
    }

    public void setCategory(int category) {
        this.frag_category = category;
    }

    public void setStatus(int status) {
        this.frag_status = status;
    }

    public void setType(int type) {
        this.frag_type = type;
    }

    public void setUpdatetype(int updatetype) {
        this.frag_updatetype = updatetype;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

