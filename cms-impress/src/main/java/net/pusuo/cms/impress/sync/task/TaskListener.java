package net.pusuo.cms.impress.sync.task;


public interface TaskListener {

	public abstract void onThumbnailed(ITaskSource th, boolean success);

}