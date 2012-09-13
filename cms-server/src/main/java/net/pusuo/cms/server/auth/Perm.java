package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.util.Util;

import java.util.Set;

/**
 * @hibernate.class table="auth_perm"
 * package="com.hexun.cms.auth"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Perm implements Item {

    private static final long serialVersionUID = 6173408457541632576L;
    private int perm_id = -1;
    private String perm_name = null;
    private String perm_desc = null;
    private int perm_realm = 0;
    private Set roles;

    Perm() {
        roles = new java.util.HashSet();
    }

    /**
     * @hibernate.id column="perm_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_object"
     */
    public int getId() {
        return perm_id;
    }

    /**
     * @hibernate.property column="perm_name"
     * not-null="true"
     */
    public String getName() {
        return perm_name;
    }

    /**
     * @hibernate.property column="perm_desc"
     */
    public String getDesc() {
        return perm_desc;
    }

    /**
     * @hibernate.property column="perm_realm"
     */
    public int getRealm() {
        return perm_realm;
    }

    public void setId(int id) {
        this.perm_id = id;
    }

    public void setName(String name) {
        this.perm_name = name;
    }

    public void setDesc(String description) {
        this.perm_desc = description;
    }

    public void setRealm(int realm) {
        this.perm_realm = realm;
    }

    /**
     * @hibernate.set lazy="false"
     * name="roles"
     * table="auth_r_p_map"
     * cascade="none"
     * inverse="false"
     * @hibernate.collection-key column="perm_id"
     * @hibernate.collection-many-to-many class="com.hexun.cms.auth.Role"
     * column="role_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getRoles() {
        return roles;
    }

    public void setRoles(Set set) {
        this.roles = set;
    }

    public void addRole(Item item) {
        roles.add(item);
    }

    public Item getRolesInstance() {
        return AuthFactory.getInstance().createRole();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.perm_id < 0) return false;
        if (!(obj instanceof Perm)) return false;
        return (((Perm) obj).getId() == this.perm_id);
    }

    public int hashCode() {
        return perm_id < 0 ? super.hashCode() : Util.buildHashCode(perm_id, ItemInfo.PERM_TYPE);
    }

    /*
     public boolean onSave(Session s)
         throws CallbackException
         {
                 return false;
         }

         public boolean onUpdate(Session s)
         throws CallbackException
         {
                 return false;
         }

         public boolean onDelete(Session s)
         throws CallbackException
         {
                 System.out.println("===== onDelete: roles size = " + roles.size());
                 if ( roles!=null )
                 {
                         Iterator it = roles.iterator();
                         while ( it.hasNext() )
                         {
                                 try
                                 {
                                         s.refresh(it.next());
                                 }
                                 catch ( Exception e )
                                 {
                                 }
                         }
                 }
         return false;
         }

         public void onLoad(Session s,Serializable id)
         {
         System.out.println("===== onLoad: id = " + perm_id + ", roles size = " + roles.size());
         }
     */
}
