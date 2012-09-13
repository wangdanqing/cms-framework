package net.pusuo.cms.impress.sync.img;

import java.util.List;


public interface ImageUtil {

    public abstract boolean genThumbnail(SyncThumbnail tb);

    public abstract int getImgMissTimeOut();

    public abstract void setImgMissTimeOut(int imgMissTimeOut);

    public abstract List<IThumbailRule> getRules();

    public abstract void setRules(List<IThumbailRule> rules);

    public String getImageToolkitClass();

    public void setImageToolkitClass(String imageToolkitClass);

}