package net.pusuo.cms.server.tool;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.core.CoreFactory;
import net.pusuo.cms.server.core.Subject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 收藏夹类，进行收藏夹的管理
 *
 * @author denghua
 * @version 1.5
 * @since cms4
 */
public class Favorite {

    private String name;

    private boolean isUser;

    private String PATH = null;

    private final static String USER_ROOT = "user";

    private final static String CHANNEL_ROOT = "channel";

    private static final Log LOG = LogFactory.getLog(Favorite.class);

    Document document = null;

    Element root = null;

    // 写文件的锁
    private Object FILE_LOCK = new Object();

    /**
     * 初始化
     *
     * @param name   如果isUser为false ,则是频道的name 如果isUser是true，则是用户的name
     * @param isUser 是否用户收藏夹，如果为true 则是，如果false则是频道收藏夹
     * @throws Exception
     */
    public Favorite(String name, boolean isUser) throws Exception {
        this.name = name;
        this.isUser = isUser;
        if (isUser) {
            PATH = Configuration.getInstance().get("cms4.userfav.path");
        } else {
            PATH = Configuration.getInstance().get("cms4.channelfav.path");
        }
        if (PATH != null) {
            File temp = new File(PATH);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            PATH = temp.getPath();
        } else {
            throw new NullPointerException("配置文件未配置路径");
        }
        FileInputStream fiStream = null;
        InputStreamReader isReader = null;
        File ff = getFile();
        try {
            SAXReader reader = new SAXReader();
            fiStream = new FileInputStream(ff);
            isReader = new InputStreamReader(fiStream, "UTF-8");
            document = reader.read(isReader);
            root = document.getRootElement();
        } catch (Exception e) {
            LOG.error(e);
            throw new Exception("列表错误", e);
        } finally {
            try {
                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }
                if (fiStream != null) {
                    fiStream.close();
                    fiStream = null;
                }
                if (ff != null)
                    ff = null;
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    /**
     * 根据目录名，得到该目录名下的所有的列表
     *
     * @param categoryName
     * @return
     * @throws Exception
     */
    public List listFavorite(String categoryName) {
        List result = new ArrayList();
        Iterator categoryIter = root.elementIterator("category");
        while (categoryIter.hasNext()) {
            Element categoryElement = (Element) categoryIter.next();
            if (categoryName.equalsIgnoreCase(categoryElement
                    .attributeValue("name"))) {
                Iterator favoriteIter = (Iterator) categoryElement
                        .elementIterator("favorite");
                while (favoriteIter.hasNext()) {
                    Element favoriteElement = (Element) favoriteIter.next();
                    Subject subject = CoreFactory.getInstance().createSubject();
                    int id = getAttrIntValue(favoriteElement, "id", -1);
                    String name = getAttrStringValue(favoriteElement, "name",
                            null);
                    subject.setId(id);
                    if (name != null)
                        subject.setName(name);
                    result.add(subject);
                }
            }
        }
        return result;
    }

    public List listCategory() {
        List result = new ArrayList();
        Iterator categoryIter = root.elementIterator("category");
        while (categoryIter.hasNext()) {
            Element favoriteElement = (Element) categoryIter.next();
            Subject subject = CoreFactory.getInstance().createSubject();
            String name = getAttrStringValue(favoriteElement, "name", null);

            subject.setId(0);
            if (name != null)
                subject.setName(name);
            result.add(subject);
        }
        return result;
    }

    public void addFavorite(int id, String name, String categoryName)
            throws Exception {

        Element cc = null; // category对应的节点
        Iterator categoryIter = root.elementIterator("category");
        boolean isExist = false;
        // 需要检查是否已经添加了
        while (categoryIter.hasNext()) {
            Element cate = (Element) categoryIter.next();
            if (categoryName.equals(cate.attributeValue("name"))) {
                cc = cate;
            }
            Iterator favIter = cate.elementIterator("favorite");
            while (favIter.hasNext()) {
                Element fav = (Element) favIter.next();
                if (id == getAttrIntValue(fav, "id", 0)) {
                    isExist = true;
                    cc = cate;
                }
            }
        }
        if (cc != null) {
            if (!isExist) {
                Element addElement = cc.addElement("favorite");
                addElement.addAttribute("id", "" + id);
                addElement.addAttribute("name", name);
                save2file(root);
            } else {
                throw new RemoteException("父对象已经存在于分类：" + cc.attributeValue("name"));
            }
        } else {
//            throw new NullObjectException("分类不存在");
        }
    }

    /**
     * 根据文件路径保存xml文件. 该方法是保存category一级
     *
     * @param categoryName
     * @throws Exception
     */
    public void addCategory(String categoryName) throws Exception {
        Iterator categoryIter = root.elementIterator("category");
        boolean isExist = false; // 该条目是否存在
        while (categoryIter.hasNext()) {
            Element categoryElement = (Element) categoryIter.next();
            if (categoryName.equals(categoryElement.attributeValue("name"))) {
                isExist = true;
            }
        }
        if (isExist) {
            return;
        } else {
            Element categoryElement = root.addElement("category");
            categoryElement.addAttribute("name", categoryName);
            try {
                save2file(root);
            } catch (Exception e) {
                throw new Exception("保存xml文件错误", e);
            }
        }
    }

    public void deleteFavorite(int id, String categoryName) throws Exception {
        // 先查找到该fav所在的category,然后删掉
        Iterator categoryIter = root.elementIterator("category");
        Element currCategory = null;
        while (categoryIter.hasNext()) {
            Element cate = (Element) categoryIter.next();
            if (categoryName.equals(cate.attributeValue("name"))) {
                currCategory = cate;
            }
        }
        Element currFav = null;
        if (currCategory != null) { // 找到了category节点
            Iterator favIter = currCategory.elementIterator("favorite");
            while (favIter.hasNext()) {
                Element fav = (Element) favIter.next();
                if (id == getAttrIntValue(fav, "id", 0)) {
                    currFav = fav;
                }
            }
            if (currFav != null) { // 进行删除
                currCategory.remove(currFav);
                save2file(root);
            }
        }
    }

    public void deleteCategory(String categoryName) throws Exception {
        Iterator categoryIter = root.elementIterator("category");
        Element removeCategory = null;
        while (categoryIter.hasNext()) {
            Element cate = (Element) categoryIter.next();
            if (categoryName.equals(cate.attributeValue("name"))) {
                removeCategory = cate;
            }
        }
        if (removeCategory != null) {
            root.remove(removeCategory);
        }
        save2file(root);
    }

    /**
     * 得到相应的文件
     *
     * @return
     * @throws Exception
     */
    private File getFile() throws Exception {
        if (name == null || name.trim().length() == 0)
            return null;

        File temp = new File(PATH + File.separator + name + ".xml");
        if (!temp.exists()) {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                LOG.error(e);
                throw new IOException("创建文件错误");
            }
            // 创建新的文件
            Document dd = DocumentHelper.createDocument();
            Element root = null;
            if (isUser)
                root = dd.addElement(USER_ROOT);
            else
                root = dd.addElement(CHANNEL_ROOT);
            save2file(root);
        }

        return temp;
    }

    private void save2file(Element root) throws Exception {

        FileOutputStream foStream = null;
        OutputStreamWriter osWriter = null;
        String filePath = getFile().getPath();
        // 编码
        try {
            foStream = new FileOutputStream(filePath);
            osWriter = new OutputStreamWriter(foStream, "UTF-8");
            // 格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 输出
            XMLWriter writer = new XMLWriter(osWriter, format);

            synchronized (FILE_LOCK) {
                writer.write(root);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            LOG.error(e);
            throw e;
        } catch (IOException e) {
            LOG.error(e);
            throw e;
        } finally {
            if (osWriter != null)
                try {
                    osWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (foStream != null)
                try {
                    foStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    private static int getAttrIntValue(Element element, String attrName,
                                       int defaultValue) {

        Attribute attr = element.attribute(attrName);

        if (attr == null)
            return defaultValue;

        if (attr.getValue() != null) {
            try {
                return Integer.parseInt(attr.getValue());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    private static String getAttrStringValue(Element element, String attrName,
                                             String defaultValue) {

        Attribute attr = element.attribute(attrName);

        if (attr == null)
            return defaultValue;

        if (attr.getValue() != null) {
            return attr.getValue();
        }

        return defaultValue;
    }

}
