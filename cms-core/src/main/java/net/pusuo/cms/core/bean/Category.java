package net.pusuo.cms.core.bean;

import java.util.List;

/**
 * 目录结构的节点
 * @deprecated
 */
public class Category extends Item {
    private static final long serialVersionUID = -2328535590752746149L;

    private long id;
    private String name;
    private String desc;
    private List templatesIds;

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

    public List getTemplatesIds() {
        return templatesIds;
    }

    public void setTemplatesIds(List templatesIds) {
        this.templatesIds = templatesIds;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

