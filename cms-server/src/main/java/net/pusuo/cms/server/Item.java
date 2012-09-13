package net.pusuo.cms.server;

import java.io.Serializable;


/**
 * 由于我们对Proxy进行了统一的接口抽象，所以还需要对存放在容器里面的各种实体进行抽象，这就是Item。
 * 这就要求我们所有的底层类都必须实体Item这个接口，不管是Entity还是SortObject。
 *
 * @author Mark
 * @version 4.0
 * @see Proxy
 * @since CMS4.0
 */

public interface Item extends Serializable {
    public int getId();

    public void setId(int id);

    public String getName();

    public void setName(String name);

    public String getDesc();

    public void setDesc(String desc);
}
