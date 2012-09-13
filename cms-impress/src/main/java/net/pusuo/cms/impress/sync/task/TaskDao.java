package net.pusuo.cms.impress.sync.task;

import java.util.List;


public interface TaskDao {

    /**
     * 从数据库中读取出需要被处理的数据
     *
     * @return
     */
    public abstract List<ITaskSource> readThumbnailList();

    /**
     * 从数据库中删除数据
     *
     * @param tb
     */
    public abstract void delete(final ITaskSource tb);

}