package net.pusuo.cms.server.auth;

import net.pusuo.cms.server.Item;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.util.Util;

import java.util.Set;


/**
 * @hibernate.class table="auth_role"
 * package="com.hexun.cms.auth"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache usage="nonstrict-read-write"
 */

public class Role implements Item {
    private static final long serialVersionUID = 1L;

    private int role_id = -1;
    private String role_name = null;
    private String role_desc = null;
    private int role_realm = 0;
    private Set perms;

    Role() {
        perms = new java.util.HashSet();
    }

    /**
     * @hibernate.id column="role_id"
     * unsaved-value="-1"
     * generator-class="sequence"
     * @hibernate.generator-param name="sequence"
     * value="sq_object"
     */
    public int getId() {
        return role_id;
    }

    /**
     * @hibernate.property column="role_name"
     * not-null="true"
     */
    public String getName() {
        return role_name;
    }

    /**
     * @hibernate.property column="role_desc"
     */
    public String getDesc() {
        return role_desc;
    }

    /**
     * @hibernate.property column="role_realm"
     */
    public int getRealm() {
        return role_realm;
    }


    public Item getUsersInstance() {
        return AuthFactory.getInstance().createUser();
    }

    /**
     * @hibernate.set lazy="false"
     * name="perms"
     * table="auth_r_p_map"
     * cascade="none"
     * inverse="false"
     * @hibernate.collection-key column="role_id"
     * @hibernate.collection-many-to-many class="com.hexun.cms.auth.Perm"
     * column="perm_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public Set getPerms() {
        return perms;
    }

    public void setPerms(Set set) {
        this.perms = set;
    }

    public void addPerm(Item item) {
        perms.add(item);
    }

    public Item getPermsInstance() {
        return AuthFactory.getInstance().createPerm();
    }

    public void setId(int id) {
        this.role_id = id;
    }

    public void setName(String name) {
        this.role_name = name;
    }

    public void setDesc(String description) {
        this.role_desc = description;
    }

    public void setRealm(int realm) {
        this.role_realm = realm;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.role_id < 0) return false;
        if (!(obj instanceof Role)) return false;
        return (((Role) obj).getId() == this.role_id);
    }

    public int hashCode() {
        return role_id < 0 ? super.hashCode() : Util.buildHashCode(role_id, ItemInfo.ROLE_TYPE);
    }

    /*
     public boolean onSave(Session s)
         throws CallbackException
         {
                 System.out.println("===== onSave: perms size = " + perms.size());
                 if ( perms!=null )
                 {
                         Iterator it = perms.iterator();
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

         public boolean onUpdate(Session s)
         throws CallbackException
         {
                 System.out.println("===== onUpdate: perms size = " + perms.size());
                 if ( perms!=null )
                 {
                         Iterator it = perms.iterator();
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

         public boolean onDelete(Session s)
         throws CallbackException
         {
                 return false;
         }

         public void onLoad(Session s,Serializable id)
         {
         }
     */

}
