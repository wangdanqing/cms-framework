package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.Session;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @hibernate.class table="cms_media"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Media implements Item, Lifecycle {
    private static final long serialVersionUID = 1L;

    private int media_id = -1;
    private String media_name = null;
    private String media_desc = null;
    private String media_logo = null;
    private String media_url = null;
    private Timestamp media_time = null;

    Media() {
    }

    /**
     * @hibernate.id column="media_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_media"
     */
    public int getId() {
        return media_id;
    }

    /**
     * @hibernate.property column="media_name"
     * not-null="true"
     */
    public String getName() {
        return media_name;
    }

    /**
     * @hibernate.property column="media_desc"
     */
    public String getDesc() {
        return media_desc;
    }

    /**
     * @hibernate.property column="media_logo"
     */
    public String getLogo() {
        return media_logo;
    }

    /**
     * @hibernate.property column="media_url"
     */
    public String getUrl() {
        return media_url;
    }

    /**
     * @hibernate.property column="media_time"
     */
    public Timestamp getTime() {
        return media_time;
    }

    public void setId(int id) {
        this.media_id = id;
    }

    public void setName(String name) {
        this.media_name = name;
    }

    public void setDesc(String description) {
        this.media_desc = description;
    }

    public void setLogo(String logo) {
        this.media_logo = logo;
    }

    public void setUrl(String url) {
        this.media_url = url;
    }

    public void setTime(Timestamp time) {
        this.media_time = time;
    }

    public boolean onSave(Session s)
            throws CallbackException {
        media_time = new Timestamp(System.currentTimeMillis());
        return false;
    }

    public boolean onUpdate(Session s)
            throws CallbackException {
        return false;
    }

    public boolean onDelete(Session s)
            throws CallbackException {
        return false;
    }

    public void onLoad(Session s, Serializable id) {
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

