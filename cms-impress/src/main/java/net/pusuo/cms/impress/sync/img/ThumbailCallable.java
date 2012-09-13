package net.pusuo.cms.impress.sync.img;

import net.pusuo.cms.impress.io.FileUtil;
import net.pusuo.cms.impress.sync.task.ITaskSource;
import net.pusuo.cms.impress.sync.task.TaskListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行压缩图片的工作
 *
 * @author agilewang
 */
class ThumbailCallable implements Callable<ITaskSource> {

    private static final Log log = LogFactory.getLog(ThumbailCallable.class);

    static final Lock syncLock = new ReentrantLock();

    private SyncThumbnail tb;

    private ImageUtil imageUtil;

    private TaskListener listener = null;

    private String syncFile;

    public ThumbailCallable(final SyncThumbnail tb, final ImageUtil imageUtil,
                            TaskListener listener, String syncFile) {
        this.tb = tb;
        this.imageUtil = imageUtil;
        this.listener = listener;
        this.syncFile = syncFile;
    }

    public SyncThumbnail call() throws Exception {

        boolean result = false;
        try {
            tb.thumbPaths.clear();
            result = imageUtil.genThumbnail(this.tb);
            if (log.isInfoEnabled()) {
                log.info("Comprress picture " + this.tb.getSrcFilePath()
                        + (result ? " Success." : " Fail."));
            }
            if (result) {
                for (String path : tb.thumbPaths) {
                    StringBuffer sb = new StringBuffer();
                    sb.append((long) (System.currentTimeMillis() / 1000));
                    sb.append("\t");
                    sb.append("+");
                    sb.append("\t");
                    sb.append(path);
                    sb.append("\r\n");
                    log.info("write to sync file:" + syncFile + " data:" + sb);
                    syncLock.lock();
                    try {
                        FileUtil.write(sb.toString(), syncFile, true);
                    } finally {
                        syncLock.unlock();
                    }
                }
            }
        } finally {
            if (this.listener != null) {
                listener.onThumbnailed(tb, result);
            }
        }

        return this.tb;
    }

}