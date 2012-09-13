package net.pusuo.cms.server.core;

import net.pusuo.cms.server.Item;

import java.util.Set;

/**
 * @hibernate.class
 * table="cms_type"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache
 * usage="nonstrict-read-write"
 */
public class TypeItem implements Item
{
	private static final long serialVersionUID = 1L;
	
    private int type_id = 0;
    private String type_name = null;
    private String type_desc = null;
    private Set ptypes = null;
    private Set ctypes = null;

    TypeItem() 
	{
	}

	/**
	 *  @hibernate.id
	 *  column="type_id"
	 *  unsaved-value="-1"
	 *  generator-class="sequence"
	 *  @hibernate.generator-param
	 *  name="sequence"
	 *  value="sq_object"
	 */
	public int getId() 
	{
		return type_id;
	}

	/**
	 * @hibernate.property
	 * column="type_name"
	 * not-null="true"
	 */
	public String getName() 
	{
		return type_name;
	}
	/**
	 * @hibernate.property
	 * column="type_desc"
	 */
	public String getDesc() 
	{
		return type_desc;
	}

        /**
         * @hibernate.set
         *  lazy="false"
         *  name="ctypes"
         *  table="cms_type_map"
         *  inverse="false"
         * @hibernate.collection-key
         *  column="ptype"
         * @hibernate.collection-many-to-many
         *  class="com.hexun.cms.core.TypeItem"
         *  column="type"
         * @hibernate.collection-cache
         *  usage="nonstrict-read-write"
         */
	public Set getCtypes()
	{
		return ctypes;
	}
	
        /**
         * @hibernate.set
         *  lazy="false"
         *  name="ptypes"
         *  table="cms_type_map"
         *  inverse="true"
         * @hibernate.collection-key
         *  column="type"
         * @hibernate.collection-many-to-many
         *  class="com.hexun.cms.core.TypeItem"
         *  column="ptype"
         * @hibernate.collection-cache
         *  usage="nonstrict-read-write"
         */
	public Set getPtypes()
	{
		return ptypes;
	}
	
	public void setId(int id) 
	{
		this.type_id = id;
	}

	public void setName(String name) 
	{
		this.type_name = name;
	}

	public void setDesc(String description) 
	{
		this.type_desc= description;
	}

	public void setCtypes(Set types) 
	{
		this.ctypes= types;
	}

	public Item getCtypesInstance()
	{
		return CoreFactory.getInstance().createTypeItem();
	}

	public void setPtypes(Set types) 
	{
		this.ptypes= types;
	}

	public Item getPTypesInstance()
	{
		return CoreFactory.getInstance().createTypeItem();
	}

        public String toString()
        {
                return getClass().getName()+" #"+getId();
        }
}

