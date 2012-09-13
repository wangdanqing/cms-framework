package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Item;

import java.util.Set;

/**
 * @hibernate.class table="auth_group"
 * package="com.hexun.cms.auth"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Group implements Item {
    private static final long serialVersionUID = 1L;

    private int group_id = -1;
    private String group_name = null;
    private String group_desc = null;
    private int group_realm = 0;
    private Set users;

    Group() {
        users = new java.util.HashSet();
    }

    /**
     * @hibernate.id column="group_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_object"
     */
    public int getId() {
        return group_id;
    }

    /**
     * @hibernate.property column="group_name"
     * not-null="true"
     */
    public String getName() {
        return group_name;
    }

    /**
     * @hibernate.property column="group_desc"
     */
    public String getDesc() {
        return group_desc;
    }

    /**
     * @hibernate.property column="group_realm"
     */
    public int getRealm() {
        return group_realm;
    }

    /**
     * @hibernate.set lazy="false"
     * table="auth_user"
     * @hibernate.collection-key column="user_group"
     * @hibernate.collection-one-to-many class="com.hexun.cms.auth.User"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getUsers() {
        return users;
    }

    public void setUsers(Set set) {
        this.users = set;
    }

    public void addUser(Item item) {
        users.add(item);
    }

    public Item getUsersInstance() {
        return AuthFactory.getInstance().createUser();
    }

    public void setId(int id) {
        this.group_id = id;
    }

    public void setName(String name) {
        this.group_name = name;
    }

    public void setDesc(String description) {
        this.group_desc = description;
    }

    public void setRealm(int realm) {
        this.group_realm = realm;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.group_id < 0) return false;
        if (!(obj instanceof Group)) return false;
        return (((Group) obj).getId() == this.group_id);
    }

    public int hashCode() {
        return group_id < 0 ? super.hashCode() : com.hexun.cms.util.Util.buildHashCode(group_id, com.hexun.cms.ItemInfo.GROUP_TYPE);
    }
}

