package net.pusuo.cms.core.bean;


public class Channel extends Item {

    private static final long serialVersionUID = -72762959500782694L;
    private int id;
    private String name;
    private String dir;

    public Channel() {
    }

    public Channel(int id, String name, String dir) {
        this.id = id;
        this.name = name;
        this.dir = dir;
    }


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String toString() {
        return getClass().getName() + " #" + getId();
    }
}

