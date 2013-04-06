package net.pusuo.cms.core.bean;


public class Picture extends EntityItem {

    private static final long serialVersionUID = 3584056279542564715L;
    private int width;
    private int height;
    private String comment;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
