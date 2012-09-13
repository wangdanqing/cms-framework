package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Item;

import java.util.Set;

/**
 * @hibernate.class table="auth_realm"
 * package="com.hexun.cms.auth"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Realm implements Item {
    private static final long serialVersionUID = 1L;

    private int realm_id = -1;
    private String realm_name = null;
    private String realm_desc = null;
    private Set realm_groups;

    Realm() {
        realm_groups = new java.util.HashSet();
    }

    /**
     * @hibernate.id column="realm_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_object"
     */
    public int getId() {
        return realm_id;
    }

    /**
     * @hibernate.property column="realm_name"
     * not-null="true"
     */
    public String getName() {
        return realm_name;
    }

    /**
     * @hibernate.property column="realm_desc"
     */
    public String getDesc() {
        return realm_desc;
    }

    /**
     * @hibernate.set lazy="false"
     * table="auth_group"
     * @hibernate.collection-key column="group_realm"
     * @hibernate.collection-one-to-many class="com.hexun.cms.auth.Group"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getGroups() {
        return realm_groups;
    }

    public void setId(int id) {
        this.realm_id = id;
    }

    public void setName(String name) {
        this.realm_name = name;
    }

    public void setDesc(String description) {
        this.realm_desc = description;
    }

    public void setGroups(Set set) {
        this.realm_groups = set;
    }

    public void addGroup(Item item) {
        realm_groups.add(item);
    }

    public Item getGroupsInstance() {
        return AuthFactory.getInstance().createGroup();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.realm_id < 0) return false;
        if (!(obj instanceof Realm)) return false;
        return (((Realm) obj).getId() == this.realm_id);
    }

    public int hashCode() {
        return realm_id < 0 ? super.hashCode() : com.hexun.cms.util.Util.buildHashCode(realm_id, com.hexun.cms.ItemInfo.REALM_TYPE);
    }
}

