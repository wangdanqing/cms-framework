package net.pusuo.cms.core.bean.auth;

import net.pusuo.cms.core.bean.Item;

/**
 * 作者itme
 */
public class Author extends Item {

    private static final long serialVersionUID = 8998288499976219376L;
    private long id;
    private String name;
    private String desc;
    private String url;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

