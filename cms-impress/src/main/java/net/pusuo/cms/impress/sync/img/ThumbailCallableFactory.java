package net.pusuo.cms.impress.sync.img;

import java.util.concurrent.Callable;

import com.hexun.slideshow.sync.task.ICallableFactory;
import com.hexun.slideshow.sync.task.ITaskSource;
import com.hexun.slideshow.sync.task.TaskRunner;

public class ThumbailCallableFactory implements ICallableFactory {
	private ImageUtil imageUtil;

	private String syncFile;

	public String getSyncFile() {
		return syncFile;
	}

	public void setSyncFile(String syncFile) {
		this.syncFile = syncFile;
	}

	public ImageUtil getImageUtil() {
		return imageUtil;
	}

	public void setImageUtil(ImageUtil imageUtil) {
		this.imageUtil = imageUtil;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hexun.slideshow.img.ICallableFactory#createCallable(com.hexun.slideshow.task.ITaskSource,
	 *      com.hexun.slideshow.task.TaskRunner)
	 */
	public Callable<ITaskSource> createCallable(ITaskSource tb, TaskRunner main) {
		return new ThumbailCallable((SyncThumbnail) tb, imageUtil, main, syncFile);
	}
}
