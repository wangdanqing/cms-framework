package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

/**
 * @hibernate.class table="cms_t_f_map_ex"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ExTFMap implements Item {
    private static final long serialVersionUID = 1L;

    private int id = -1;
    private int tf_id = -1;
    private int tf_entityid = -1;
    private int tf_listcount = -1;

    private String name;
    private String desc;

    ExTFMap() {
    }

    /**
     * @hibernate.id column="id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_exctf"
     */
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @hibernate.property column="tf_id"
     * not-null="true"
     */
    public int getTfid() {
        return this.tf_id;
    }

    public void setTfid(int tf_id) {
        this.tf_id = tf_id;
    }

    /**
     * @hibernate.property column="tf_entityid"
     * not-null="true"
     */
    public int getEntityid() {
        return this.tf_entityid;
    }

    public void setEntityid(int tf_entityid) {
        this.tf_entityid = tf_entityid;
    }

    /**
     * @hibernate.property column="tf_listcount"
     * not-null="true"
     */
    public int getListcount() {
        return this.tf_listcount;
    }

    public void setListcount(int tf_listcount) {
        this.tf_listcount = tf_listcount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

