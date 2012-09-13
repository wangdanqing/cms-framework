package net.pusuo.cms.impress.sync.task;

import java.util.concurrent.Callable;


public interface ICallableFactory {

    public abstract Callable<ITaskSource> createCallable(ITaskSource tb, TaskRunner main);

}