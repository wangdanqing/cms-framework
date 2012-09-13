package net.pusuo.cms.server.tool;

import net.pusuo.cms.server.Configuration;
import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.ItemProxy;
import net.pusuo.cms.server.core.Channel;
import net.pusuo.cms.server.util.XmlUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public final class HWProxy extends UnicastRemoteObject implements HWInterface {

    private static Log log = LogFactory.getLog(HWProxy.class);

    private static HWProxy hwproxy = null;
    private static String hotWordRootPath = Configuration.getInstance().get(
            "cms4.hotword.path");

    private static Hashtable hPool = new Hashtable(100);

    private HWProxy() throws RemoteException {
    }

    public static HWProxy getInstance() {
        if (hotWordRootPath == null)
            throw new NullPointerException(
                    "required cms4.hotword.path in configuration file.");
        if (hwproxy == null) {
            try {
                synchronized (HWProxy.class) {
                    if (hwproxy == null) {
                        hwproxy = new HWProxy();
                    }
                }
            } catch (RemoteException re) {
                log
                        .error("unable to create HWProxy instance ."
                                + re.toString());
                throw new IllegalStateException(
                        "unable to create HWProxy instance .");
            }
        }
        return hwproxy;
    }

    public void loadAll() throws RemoteException {
        try {
            List ret = ItemProxy.getInstance().getList(
                    "from com.hexun.cms.core.Channel item", null, -1, -1);
            Iterator it = ret.iterator();
            Channel c = (Channel) ItemInfo.getItemByType(ItemInfo.CHANNEL_TYPE);
            while (it.hasNext()) {
                c = (Channel) it.next();
                load(c.getId(), c.getDir());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection load(int channelID, String channelDir)
            throws RemoteException {

        Collection values = new ArrayList();

        try {

            Document doc = XmlUtil.getDocument(hotWordRootPath + File.separator
                    + channelDir + ".xml");
            Element ent = doc.getRootElement();
            List list = ent.elements();
            if (list != null && !list.isEmpty()) {
                Element nl;
                int size = list.size();
                values = new ArrayList(size);
                for (int i = 0; i < size; i++) {
                    nl = (Element) list.get(i);
                    HotWordItem hwi = new HotWordItem();
                    hwi.setKw(nl.attributeValue("kw"));
                    hwi.setUrl(nl.attributeValue("url"));
                    hwi.setOther(nl.attributeValue("other"));
                    values.add(hwi);

                }
            }

            hPool.put(String.valueOf(channelID), values);

        } catch (Exception e) {
            hPool.put(String.valueOf(channelID), values);
            log.error("hot word file " + hotWordRootPath + "/" + channelDir
                    + ".xml error!");

        }

        return values;
    }

    public Collection load(int channelID) throws RemoteException {

        Collection values = new ArrayList();

        try {
            // ͨ��channelID�õ�Ƶ���Ĵ洢����
            Channel channel = (Channel) ItemProxy.getInstance().get(
                    new Integer(channelID), Channel.class);

            values = load(channelID, channel.getDir());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    public Collection list(int channelID) throws RemoteException {
        try {
            Collection values = (Collection) hPool.get(String
                    .valueOf(channelID));
            if (values == null) {
                values = load(channelID);
            }
            return values;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Hashtable listAll() throws RemoteException {

        try {
            if (hPool == null)
                loadAll();
            return hPool;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public HotWordItem get(String keyword, int channelID)
            throws RemoteException {
        try {
            Collection values = (Collection) hPool.get(String
                    .valueOf(channelID));
            if (values == null)
                values = load(channelID);
            Iterator it = values.iterator();
            HotWordItem hwi = new HotWordItem();
            keyword = keyword.trim();
            while (it.hasNext()) {
                hwi = (HotWordItem) it.next();
                log.info("key:" + hwi.getKw());
                if (keyword.equals(hwi.getKw().trim())) {

                    return hwi;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public boolean add(HotWordItem hwi, int channelID) throws RemoteException {

        try {
            // set pool
            ArrayList values = (ArrayList) hPool.get(String.valueOf(channelID));
            if (values == null)
                values = (ArrayList) load(channelID);
            values.add(0, hwi);
            hPool.put(String.valueOf(channelID), values);

            // write file
            writeFile(values, channelID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean update(HotWordItem ohwi, HotWordItem nhwi, int channelID)
            throws RemoteException {
        try {
            // update pool
            ArrayList values = (ArrayList) hPool.get(String.valueOf(channelID));
            if (values == null)
                values = (ArrayList) load(channelID);
            values.remove(ohwi);
            values.add(0, nhwi);
            hPool.put(String.valueOf(channelID), values);

            // write file
            writeFile(values, channelID);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete(HotWordItem hwi, int channelID)
            throws RemoteException {

        try {
            // delete from pool
            Collection values = (Collection) hPool.get(String
                    .valueOf(channelID));
            if (values == null)
                values = load(channelID);
            values.remove(hwi);
            hPool.put("" + channelID, values);

            // write file
            writeFile(values, channelID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void writeFile(Collection values, int channelID) {
        try {
            // ͨ��channelID�õ�Ƶ���Ĵ洢����
            Channel channel = (Channel) ItemProxy.getInstance().get(
                    new Integer(channelID), Channel.class);
            String channelFile = channel.getDir() + ".xml";
            DocumentFactory factory = org.dom4j.DocumentFactory.getInstance();
            org.dom4j.Document doc = factory.createDocument();
            org.dom4j.Element root = factory.createElement("root");

            Iterator it = values.iterator();
            while (it.hasNext()) {
                HotWordItem hwi = (HotWordItem) it.next();
                org.dom4j.Element li = factory.createElement("li");
                li.addAttribute("kw", (hwi.getKw()));
                li.addAttribute("url", (hwi.getUrl()));
                li.addAttribute("other", (hwi.getOther()));
                root.add(li);
            }
            doc.setRootElement(root);
            XmlUtil.write(doc, hotWordRootPath + "/" + channelFile);
            // LocalFile.write(sbStr.toString(), hotWordRootPath + "/"
            // + channelFile);
        } catch (Exception e) {
            log.error(e);
        }
    }

}
