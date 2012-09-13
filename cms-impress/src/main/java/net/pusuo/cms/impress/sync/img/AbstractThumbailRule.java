package net.pusuo.cms.impress.sync.img;

public abstract class AbstractThumbailRule implements IThumbailRule {
    protected String suffix = null;

    protected int thumbWidth;

    protected int thumbHeight;

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#getThumbnailPath(java.lang.String)
      */
    public String getThumbnailPath(String srcFilePath) {
        if (srcFilePath == null) {
            return null;
        }
        if (this.suffix == null || this.suffix.trim().length() == 0) {
            throw new IllegalArgumentException("suffix must not be empty");
        }
        int lastDot = srcFilePath.lastIndexOf(".");
        if (lastDot < 0) {
            return null;
        }
        String suff = srcFilePath.substring(lastDot);
        if (!(suff.toLowerCase().equals(".jpg") || suff.toLowerCase().equals(".jpeg"))) {
            suff = ".jpg";
        }
        return srcFilePath.substring(0, lastDot) + suffix + suff;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#getSuffix()
      */
    public String getSuffix() {
        return suffix;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#setSuffix(java.lang.String)
      */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#getThumbHeight()
      */
    public int getThumbHeight() {
        return thumbHeight;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#setThumbHeight(int)
      */
    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#getThumbWidth()
      */
    public int getThumbWidth() {
        return thumbWidth;
    }

    /*
      * (non-Javadoc)
      *
      * @see IThumbailRule#setThumbWidth(int)
      */
    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }
}
