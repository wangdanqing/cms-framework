package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.util.Set;

/**
 * @hibernate.class table="cms_category"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Category implements Item {
    private static final long serialVersionUID = 1L;

    private int category_id = -1;
    private String category_name = null;
    private String category_desc = null;

    private Set templates = null;
//	private Set frags = null;

    Category() {
    }

    /**
     * @hibernate.id column="category_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_ctf"
     */
    public int getId() {
        return category_id;
    }

    /**
     * @hibernate.property column="category_name"
     * not-null="true"
     */
    public String getName() {
        return category_name;
    }

    /**
     * @hibernate.property column="category_desc"
     */
    public String getDesc() {
        return category_desc;
    }

    /**
     * @hibernate.set lazy="false"
     * table="cms_template"
     * @hibernate.collection-key column="template_category"
     * @hibernate.collection-one-to-many class="com.hexun.cms.core.Template"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getTemplates() {
        return templates;
    }

    /**
     * @hibernate.set lazy="false"
     * table="cms_frag"
     * @hibernate.collection-key column="frag_category"
     * @hibernate.collection-one-to-many class="com.hexun.cms.core.Frag"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    /*
    public Set getFrags()
    {
            return frags;
    }*/
    public void setId(int id) {
        this.category_id = id;
    }

    public void setName(String name) {
        this.category_name = name;
    }

    public void setDesc(String description) {
        this.category_desc = description;
    }

    public void setTemplates(Set set) {
        this.templates = set;
    }

    public Item getTemplatesInstance() {
        return CoreFactory.getInstance().createTemplate();
    }

    /*
     public void setFrags(Set set )
     {
         this.frags = set;
     }
     */
    public Item getFragsInstance() {
        return CoreFactory.getInstance().createFrag();
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

