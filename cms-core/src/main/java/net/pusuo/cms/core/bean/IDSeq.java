package net.pusuo.cms.core.bean;

/**
 * id序列对象
 *
 * @author 玄畅
 * @date: 14-3-2 下午11:44
 */
public class IDSeq {
	private long id;
	private String group;

	public IDSeq(String group, long id) {
		this.group = group;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
