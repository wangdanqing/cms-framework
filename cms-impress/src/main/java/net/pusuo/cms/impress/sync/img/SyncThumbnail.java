/**
 *
 */
package net.pusuo.cms.impress.sync.img;

import net.pusuo.cms.impress.sync.task.ITaskSource;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncThumbnail implements ITaskSource {

    public String srcFilePath;

    public int id;

    public String url;

    public List<String> thumbPaths = new LinkedList<String>();

    AtomicInteger count = new AtomicInteger();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SyncThumbnail)) {
            return false;
        }

        return ((SyncThumbnail) obj).id == this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format("id:%d,url:%s", this.id, this.url);
    }

    public SyncThumbnail(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

}