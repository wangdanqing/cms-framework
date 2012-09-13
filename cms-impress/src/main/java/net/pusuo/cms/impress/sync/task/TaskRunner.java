package net.pusuo.cms.impress.sync.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskRunner implements TaskListener {

    private static final Log log = LogFactory.getLog(TaskRunner.class);

    /**
     * 执行压缩图片的线程池
     */
    private ScheduledThreadPoolExecutor thumPool = null;

    private TaskDao dao = null;

    private ICallableFactory cf = null;

    private int coreThreads;

    private int maxThreads;

    private int readerPeriod;

    private boolean isRun = true;

    private Thread th = null;

    /**
     * 失败的次数,大于等于fails的任务将被认为无法再处理,从失败队列中删除,如果fails小于等于则不进行错误处理
     */
    private int fails = 2;

    private final Lock lock = new ReentrantLock();

    private final Condition thQueueSingnal = lock.newCondition();

    private final AtomicInteger tasks = new AtomicInteger(0);

    private final ConcurrentLinkedQueue<ITaskSource> tbFailList = new ConcurrentLinkedQueue<ITaskSource>();

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.slideshow.img.ImgCompressListener#onThumbnailed(com.hexun.slideshow.img.ImageUtil.Thumbnail)
      */
    public void onThumbnailed(ITaskSource th, boolean success) {
        try {
            if (th == null) {
                return;
            }
            if (success) {
                try {
                    dao.delete(th);
                } catch (Throwable te) {
                    if (log.isErrorEnabled()) {
                        log.error("delete Thumbnail error", te);
                    }
                } finally {
                    tbFailList.remove(th);
                }
            } else {
                if (fails <= 0) {
                    return;
                }
                int oldValue = th.getCount().incrementAndGet();
                if (oldValue >= fails) {
                    try {
                        if (log.isWarnEnabled()) {
                            log.warn(String.format("ID %d fails %d,so delete",
                                    th.getId(), oldValue));
                        }
                        dao.delete(th);
                    } catch (Throwable te) {
                        if (log.isErrorEnabled()) {
                            log.error("delete Thumbnail error", te);
                        }
                    }
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn(String.format(
                                "Fail,so add id %d to tbFailList size:%d", th
                                .getId(), tbFailList.size()));
                    }
                    tbFailList.add(th);
                }
            }
        } finally {
            endTask();
        }

    }

    /**
     * 初始化
     */
    public void init() {
        this.thumPool = new ScheduledThreadPoolExecutor(coreThreads);
        this.thumPool.setMaximumPoolSize(maxThreads);
        if (log.isInfoEnabled()) {
            log.info(String.format(
                    "thumPool core size %d,max size %d,readerPeriod %d second",
                    coreThreads, maxThreads, readerPeriod));
        }
    }

    public void run() {
        while (isRun) {
            try {
                lock.lock();
                // 处理失败的任务
                ITaskSource failTb = null;
                if (log.isInfoEnabled()) {
                    log.info(String.format("tbFailList size %d", tbFailList
                            .size()));
                }
                while ((failTb = tbFailList.poll()) != null) {
                    this.thumPool.submit(cf.createCallable(failTb, this));
                    tasks.incrementAndGet();
                }
                while (tasks.get() > 0) {
                    thQueueSingnal.await(readerPeriod, TimeUnit.SECONDS);
                }
                try {
                    List<ITaskSource> result = dao.readThumbnailList();
                    if (result.size() > 0) {
                        for (ITaskSource tb : result) {
                            this.thumPool.submit(cf.createCallable(tb, this));
                            int tasksLeft = tasks.incrementAndGet();
                            if (log.isInfoEnabled()) {
                                log.info(String.format(
                                        "%d add to queue,tasks wait to run:%d",
                                        tb.getId(), tasksLeft));
                            }
                        }
                    } else {
                        boolean waitype = thQueueSingnal.await(readerPeriod,
                                TimeUnit.SECONDS);
                        if (log.isInfoEnabled()) {
                            log.info("thQueueSingnal weak up :" + waitype);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                if (log.isErrorEnabled()) {
                    log.error("Thread interrupted", e);
                }
            } catch (Throwable le) {
                if (log.isErrorEnabled()) {
                    log.error("Error", le);
                }
            }
        }
    }

    public void endTask() {
        lock.lock();
        try {
            int tasksLeft = tasks.decrementAndGet();
            if (log.isInfoEnabled()) {
                log.info(String.format("Tasks %d wait to exexute", tasksLeft));
            }
            if (tasksLeft <= 0) {
                thQueueSingnal.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public int getCoreThreads() {
        return coreThreads;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getReaderPeriod() {
        return readerPeriod;
    }

    public void setReaderPeriod(int readerPeriod) {
        this.readerPeriod = readerPeriod;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public TaskDao getDao() {
        return dao;
    }

    public void setDao(TaskDao dao) {
        this.dao = dao;
    }

    public ICallableFactory getCf() {
        return cf;
    }

    public void setCf(ICallableFactory cf) {
        this.cf = cf;
    }

    public void stop() {
        this.isRun = false;
        this.thumPool.shutdown();
        if (this.th != null) {
            this.th.interrupt();
            this.th = null;
        }
        if (log.isInfoEnabled()) {
            log.info("Task Runner stoped");
        }
    }

    public synchronized void runInAnotherThread() {
        if (th == null) {
            if (log.isInfoEnabled()) {
                log.info("Task Runner begin run");
            }
            Thread t = new Thread() {
                public void run() {
                    TaskRunner.this.run();
                }
            };
            th = t;
            t.start();
            if (log.isInfoEnabled()) {
                log.info("Task Runner started.Thread is " + th.getId());
            }
        }
    }
}
