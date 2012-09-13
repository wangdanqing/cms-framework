package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.util.Util;

import java.util.Set;

/**
 * @hibernate.class table="auth_user"
 * package="com.hexun.cms.auth"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class User implements Item {
    private static final long serialVersionUID = 1L;

    private int user_id = -1;
    private String user_name = null;
    private String user_desc = null;
    private int user_realm = 0;
    private int user_group = 0;
    private String user_email = null;
    private String user_passwd = null;
    private String user_phone = null;
    private String user_mobile = null;
    private String user_address = null;
    private String user_param = null;
    private Set roles;

    User() {
        roles = new java.util.HashSet();
    }

    /**
     * @hibernate.id column="user_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_object"
     */
    public int getId() {
        return user_id;
    }

    /**
     * @hibernate.property column="user_name"
     * not-null="true"
     */
    public String getName() {
        return user_name;
    }

    /**
     * @hibernate.property column="user_password"
     * not-null="true"
     */
    public String getPasswd() {
        return user_passwd;
    }

    /**
     * @hibernate.property column="user_desc"
     */
    public String getDesc() {
        return user_desc;
    }

    /**
     * @hibernate.property column="user_realm"
     */
    public int getRealm() {
        return user_realm;
    }

    /**
     * @hibernate.property column="user_group"
     */
    public int getGroup() {
        return user_group;
    }

    /**
     * @hibernate.property column="user_email"
     */
    public String getEmail() {
        return user_email;
    }

    /**
     * @hibernate.property column="user_phone"
     */
    public String getPhone() {
        return user_phone;
    }

    /**
     * @hibernate.property column="user_mobile"
     */
    public String getMobile() {
        return user_mobile;
    }

    /**
     * @hibernate.property column="user_address"
     */
    public String getAddress() {
        return user_address;
    }

    /**
     * @hibernate.property column="user_param"
     */
    public String getParam() {
        return user_param;
    }

    /**
     * @hibernate.set lazy="false"
     * name="roles"
     * table="auth_u_r_map"
     * inverse="false"
     * @hibernate.collection-key column="user_id"
     * @hibernate.collection-many-to-many class="com.hexun.cms.auth.Role"
     * column="role_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */

    public Set getRoles() {
        return this.roles;
    }

    public void setRoles(Set set) {
        this.roles = set;
    }

    public Item getRolesInstance() {
        return AuthFactory.getInstance().createRole();
    }

    public void addRole(Item item) {
        roles.add(item);
    }

    public void setId(int id) {
        this.user_id = id;
    }

    public void setName(String name) {
        this.user_name = name;
    }

    public void setPasswd(String p) {
        this.user_passwd = p;
    }

    public void setDesc(String description) {
        this.user_desc = description;
    }

    public void setRealm(int realm) {
        this.user_realm = realm;
    }

    public void setGroup(int group) {
        this.user_group = group;
    }

    public void setEmail(String email) {
        this.user_email = email;
    }

    public void setPhone(String phone) {
        this.user_phone = phone;
    }

    public void setMobile(String mobile) {
        this.user_mobile = mobile;
    }

    public void setAddress(String address) {
        this.user_address = address;
    }

    public void setParam(String param) {
        this.user_param = param;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.user_id < 0) return false;
        if (!(obj instanceof User)) return false;
        return (((User) obj).getId() == this.user_id);
    }

    public int hashCode() {
        return user_id < 0 ? super.hashCode() : Util.buildHashCode(user_id, ItemInfo.USER_TYPE);
    }
}
