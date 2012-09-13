/**
 * 
 */
package net.pusuo.cms.client.tool;

import java.util.List;

/**
 * @author shijinkui
 *
 */
public class TreeNodeEntity {

	private String tid = null;
	private String pid = null;
	private String desc = null;
	private String actionId = null;
	
	private List subNodes = null;

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List subNodes) {
		this.subNodes = subNodes;
	}
	
	
	
}
