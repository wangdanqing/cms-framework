/**
 * 
 */
package net.pusuo.cms.client.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alfred.Yuan
 *
 */
public class StatisticItem {

	private int id = -1;
	private String name = null;
	private String parent = null;
	private int type = -1;
	private int channel = -1;
	
	private String display = null;
	
	private Set children = null;
	
	public StatisticItem() {
		
	}
	
	public StatisticItem(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public StatisticItem(int id, String name, String parent, int type, int channel, String display) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.type = type;
		this.channel = channel;
		this.display = display;
	}

	public Set getChildren() {
		return children;
	}

	public void setChildren(Set children) {
		this.children = children;
	}

	public void addChild(StatisticItem child) {
		if (child == null)
			return;
		
		if (children == null)
			children = new HashSet();
		
		if (!children.contains(child))
			children.add(child);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}	
	
	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	////////////////////////////////////////////////////////////////////////////

	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof StatisticItem))
			return false;
		
		StatisticItem other = (StatisticItem)obj;
		
		return this.getId() == other.getId();
	}

	public int hashCode() {
		
		return this.getId();
	}

}
