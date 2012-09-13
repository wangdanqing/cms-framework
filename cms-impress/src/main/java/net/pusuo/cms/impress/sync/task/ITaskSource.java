package net.pusuo.cms.impress.sync.task;

import java.util.concurrent.atomic.AtomicInteger;

public interface ITaskSource {
    public int getId();

    public void setId(int id);

    public AtomicInteger getCount();

    public void setCount(AtomicInteger count);
}