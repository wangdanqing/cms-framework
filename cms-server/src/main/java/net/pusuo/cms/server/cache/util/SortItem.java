package net.pusuo.cms.server.cache.util;

import java.sql.Timestamp;
/**
 * @hibernate.class
 * table="cms_entity"
 * package="com.hexun.cms.cache"
 * optimistic-lock="version"
 */
public class SortItem implements Comparable,java.io.Serializable,com.hexun.cms.Item
{
	protected int id;
	protected Timestamp time;

	public SortItem()
	{
	}

	public SortItem( int id , Timestamp time )
	{
		this.id = id;
		this.time = time;
	}

	/**
         *  @hibernate.id
         *  column="entity_id"
         *  unsaved-value="-1"
         *  generator-class="sequence"
         *  @hibernate.generator-param
         *  name="sequence"
         *  value="sq_entity"
         */
	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	/**
         * @hibernate.property
         * column="entity_time"
	 * update="false"
         * insert="false"
         */
	public Timestamp getTime()
	{
		return this.time;
	}

	public void setTime(Timestamp p )
	{
		this.time = p;
	}

	public int compareTo(Object o) 
	{
		try{
			return time.compareTo(((SortItem)o).getTime());
		}catch(ClassCastException e){
			return 0;
		}		
	}	

	public String getName()
	{
		return null;
	}
	public String getDesc()
	{
		return null;
	}

	public void setName(String p)
	{
	}
	public void setDesc(String p)
	{
	}
}
