/**
 *
 */
package net.pusuo.cms.server.tool;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 根据以前的用户收藏夹修改.<br/> 将以前的接口声明为不赞成使用<br/> 主要目的是保存用户的收藏夹, 保存形式为xml文件.<br/>
 * 保存的文件名为: 用户名.xml 其中用户名是系统的唯一KEY.可以根据该KEY区分用户<br/>
 * 保存的路径在系统配置文件cms4.properties中的cms4.userfav.path<br/> 保存的XML文件结构为:<br/>
 * <p>
 * &lt;user&gt;<br/> &nbsp;&nbsp;&lt;category name="xxx"&gt;
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&lt;favorite id="111" name="xxx"&gt;
 * <br/>&nbsp;&lt;category&gt; <br/>&lt;/user&gt;
 * </p>
 *
 * @author denghua
 * @version 1.5
 * @since cms4
 */
public interface UFInterface extends Remote {

    /**
     * <b>该方法在增加分类的时候遗弃</b>
     *
     * @param userName
     * @return
     * @throws RemoteException
     * @deprecated
     */
    public List list(String userName) throws RemoteException;

    /**
     * <b>该方法在增加分类的时候遗弃</b>
     *
     * @param userName
     * @param id
     * @param name
     * @throws RemoteException
     * @deprecated
     */
    public void add(String userName, int id, String name)
            throws RemoteException;

    /**
     * <b>该方法在增加分类的时候遗弃</b>
     *
     * @param userName
     * @param subjects
     * @throws RemoteException
     * @deprecated
     */
    public void add(String userName, List subjects) throws RemoteException;

    /**
     * <b>该方法在增加分类的时候遗弃</b>
     *
     * @param userName
     * @param id
     * @throws RemoteException
     * @deprecated
     */
    public void delete(String userName, int id) throws RemoteException;

    /**
     * <b>该方法在增加分类的时候遗弃</b>
     *
     * @param userName
     * @param ids
     * @throws RemoteException
     * @deprecated
     */
    public void delete(String userName, List ids) throws RemoteException;

    /**
     * 添加分类
     *
     * @param category 分类名称
     */
    public void addCategory(String username, String category)
            throws RemoteException;

    /**
     * 删除
     *
     * @param category
     */
    public void deleteCategory(String username, String category)
            throws RemoteException;

    public void deleteCategory(String username, List names)
            throws RemoteException;

    /**
     * 根据用户名得到分类列表
     *
     * @param username
     * @return
     */
    public List listCategory(String username) throws RemoteException;

    /**
     * 添加父对象
     *
     * @param category 分类名
     * @param userName 用户名
     * @param id       添加的父对象ID
     * @param name     添加的父对象名称
     * @throws RemoteException
     */
    public void add(String userName, String category, int id, String name)
            throws RemoteException;

    /**
     * 得到父对象的列表
     *
     * @param userName
     * @param category
     * @return
     */
    public List list(String userName, String category) throws RemoteException;

    /**
     * 删除父对象
     *
     * @param userName 用户名
     * @param category 分类名
     * @param ids      父对象的id列表
     */
    public void delete(String userName, String category, List ids)
            throws RemoteException;

}
