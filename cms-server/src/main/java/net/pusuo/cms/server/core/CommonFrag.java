package net.pusuo.cms.server.core;

import com.hexun.cms.Item;

/**
 * @hibernate.class
 * table="cms_commonfrag"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache
 * usage="nonstrict-read-write"
 */
public class CommonFrag implements Item
{
	private static final long serialVersionUID = 1L;
	
	private int frag_id = -1;
	private String frag_name = null;
	private String frag_desc = null;
	private String frag_permission = null;
	private int channel_id = -1;
	private int frag_status = -1;

	CommonFrag() {}

	/**
	 * @hibernate.id
	 * column="frag_id"
	 * unsaved-value="-1"
	 * generator-class="sequence"
	 * @hibernate.generator-param
	 * name="sequence"
	 * value="sq_ctf"
	 */
	public int getId()
	{
		return this.frag_id;
	}
	public void setId( int id )
	{
		this.frag_id = id;
	}

	/**
	 * @hibernate.property
	 * column="frag_name"
	 * not-null="true"
	 */
	public String getName()
	{
		return this.frag_name;
	}
	public void setName( String name )
	{
		this.frag_name = name;
	}

	/**
	 * @hibernate.property
	 * column="frag_desc"
	 */
	public String getDesc()
	{
		return this.frag_desc;
	}
	public void setDesc( String desc )
	{
		this.frag_desc = desc;
	}

	/**
	 * @hibernate.property
	 * column="frag_permission"
	 */
	public String getPermission()
	{
		return this.frag_permission;
	}
	public void setPermission( String permission )
	{
		this.frag_permission = permission;
	}

	/**
	 * @hibernate.property
	 * column="channel_id"
	 */
	public int getChannel()
	{
		return this.channel_id;
	}
	public void setChannel( int id )
	{
		this.channel_id = id;
	}

	/**
	 * @hibernate.property
	 * column="frag_status"
	 */
	public int getStatus()
	{
		return this.frag_status;
	}
	public void setStatus( int status )
	{
		this.frag_status = status;
	}

        public String toString()
        {
                return getClass().getName()+" #"+getId();
        }
}

