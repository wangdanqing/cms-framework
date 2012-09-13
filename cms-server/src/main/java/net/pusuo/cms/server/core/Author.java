package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import javax.persistence.Entity;


@Entity
public class Author implements Item {

    private int author_id = -1;
    private String author_name = null;
    private String author_desc = null;
    private String author_url = null;

    Author() {
    }

    public int getId() {
        return author_id;
    }

    /**
     * @hibernate.property column="author_name"
     * not-null="true"
     */
    public String getName() {
        return author_name;
    }

    /**
     * @hibernate.property column="author_desc"
     * not-null="true"
     */
    public String getDesc() {
        return author_desc;
    }

    /**
     * @hibernate.property column="author_url"
     * not-null="true"
     */
    public String getUrl() {
        return author_url;
    }

    public void setId(int id) {
        this.author_id = id;
    }

    public void setName(String name) {
        this.author_name = name;
    }

    public void setDesc(String description) {
        this.author_desc = description;
    }

    public void setUrl(String url) {
        this.author_url = url;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

