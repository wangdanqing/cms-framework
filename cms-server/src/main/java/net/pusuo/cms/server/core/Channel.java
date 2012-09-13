package net.pusuo.cms.server.core;

import java.util.Map;

import com.hexun.cms.Item;

/**
 * @hibernate.class
 * table="cms_channel"
 * package="com.hexun.cms.core"
 * dynamic-update="true"
 * dynamic-insert="true"
 * optimistic-lock="version"
 * @hibernate.cache
 * usage="nonstrict-read-write"
 */
public class Channel implements Item 
{
	private static final long serialVersionUID = 1L;
	
    private int channel_id = -1;
    private String channel_name = null;
    private String channel_desc = null;
    private String channel_dir = null;
	private Map properties = null;

    Channel() 
	{
	}

	/**
	 *  @hibernate.id
	 *  column="channel_id"
	 *  unsaved-value="-1"
	 *  generator-class="sequence"
	 *  @hibernate.generator-param
	 *  name="sequence"
	 *  value="sq_channel"
	 */
	public int getId() 
	{
		return channel_id;
	}

	/**
	 * @hibernate.property
	 * column="channel_name"
	 * not-null="true"
	 */
	public String getName() 
	{
		return channel_name;
	}

	/**
	 * @hibernate.property
	 * column="channel_desc"
	 */
	public String getDesc() 
	{
		return channel_desc;
	}

	/**
	 * @hibernate.property
	 * column="channel_dir"
	 */
	public String getDir() 
	{
		return channel_dir;
	}

        /**
         * @hibernate.map
         *  lazy="false"
         *  name="properties"
         *  table="cms_channel_prop"
         *  cascade="all"
         * @hibernate.collection-key
         *  column="channel_id"
         * @hibernate.collection-index
         *  column="channel_prop"
         *  type="java.lang.String"
         * @hibernate.collection-element
         *  column="channel_prop_value"
         *  type="java.lang.String"
	 * @hibernate.collection-cache
         *  usage="nonstrict-read-write"
         */
        public Map getProperties()
        {
                return properties;
        }

	public void setId(int id) 
	{
		this.channel_id = id;
	}

	public void setName(String name) 
	{
		this.channel_name = name;
	}

	public void setDesc(String description) 
	{
		this.channel_desc= description;
	}

	public void setDir(String dir) 
	{
		this.channel_dir= dir;
	}

        public void setProperties(Map map)
        {
                this.properties = map;
        }

	public String toString()
	{
		return getClass().getName()+" #"+getId();
	}
}

