package net.pusuo.cms.client.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author wzg
 * @version 1.0
 */
import java.util.*;
import java.io.*;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.News;
import com.hexun.cms.core.Media;

import com.hexun.cms.client.util.PropertyManager;
import com.hexun.cms.file.LocalFile;
import com.hexun.cms.Global;
import com.hexun.cms.client.ItemManager;

public class MediaPushImpl implements PushInterface {

  /**   ��ʼ��־�Ǹ����ñ��� */
  public static boolean isRun = false;

  /**  no media flag */
  private int NO_MEDIA = -1;

  /** log save --*/
  private static final Log log = LogFactory.getLog( MediaPushImpl.class );

  /** properties ---save max id */
  public static final String  path = "/root/wzg/pushmedia.properties";

  /** key is --- */
  public static final String key = "push.max.id";

  /** PropertyManager  */
  PropertyManager p = null;

  /** ItemManager */
  ItemManager itm = null;

  public MediaPushImpl() {
    this.p = new PropertyManager(path);
    itm = ItemManager.getInstance();
  }

  /**
   *
   * @param key
   * @param value
   */
  public void setProp(String key,String value) {
    p.setProp(key,value);
  }

  /**
   *
   * @param oldMaxId
   * @return
   */
  public int getOldMaxId(String oldMaxId) {
    String vlaue = p.getProp(oldMaxId);
    int pvalue;
    if(vlaue==null || vlaue.equals("")) {
      pvalue = -1;
    } else {
       try {
         pvalue = Integer.parseInt(vlaue);
       }catch(NumberFormatException e) {
         log.error("the getOldMaxId's Integer.parseInt(vlaue) method is error" +
                  e.toString());
         pvalue = -1;
       }
    }
    return pvalue;
  }

  /**
   *
   * @return
   * @throws Exception
   */
  public int getNewMaxId() throws Exception {
    /**  ��ѯ��ݿ�����  �õ�ʵ������ֵ */
    String hql = "select max(item.id) from " +
        ItemInfo.getEntityClass().getName() + " item";
    //+ " item where item.pid=? and item.type=? order by item.priority desc";
    List maxList = null;
    int nMaxId =-1;
    try {
      maxList = itm.getList(hql, null, -1, -1);
      if (maxList != null) {
        Iterator itor = maxList.iterator();
        if (itor.hasNext()) {
          Integer i = (Integer) itor.next();
          nMaxId = i.intValue();
        }
      }
    }
    catch (NumberFormatException nfe) {
      log.error("MediaPushImpl getNewMaxId is error -----"
                + nfe.toString());
      nMaxId = -1;
    }
    return nMaxId;
  }

  /**
   *
   * @param oldMaxId
   * @param newMaxId
   * @return
   * @throws Exception
   */
  public List getPushNews(int oldMaxId,int newMaxId) throws Exception {
    String hql = "select item.id from " + ItemInfo.getEntityClass().getName()
        + " item where item.id<=? and item.id>? and "
        +" item.type =? and item.status = 2 order by item.id";
    List ids = null;
    List ret = new ArrayList();
    News newItem = null;
    try {
      Collection values = new ArrayList();
      values.add(new Integer(newMaxId));
      values.add(new Integer(oldMaxId));
       values.add(new Integer(ItemInfo.NEWS_TYPE));
      ids = ItemManager.getInstance().getList(hql, values, -1, -1);
      if (ids != null) {
        Iterator itor = ids.iterator();
        while (itor.hasNext()) {
          try {
            newItem = (News) itm.get( ((Integer) itor.next()),News.class);
            //����û��ý�������ʵ��
            if(newItem.getMedia()!=NO_MEDIA) {
              ret.add(newItem);
            }
          }
          catch (Exception nfe) {
            log.error("MediaPushImpl getPushNews parse item id error---"
                      + nfe.toString());
            continue;
          }
        }
      }
    }
    catch (Exception e) {
      log.error("MediaPushImpl.getPushNews error---" + e.toString());
    }
    return ret;
  }

  /**
   * ���͵������߼����Ʒ���
   * @return
   * @throws Exception
   */
  public boolean doPush() throws Exception {
   return true;
  }

  /**
   *
   * @param pushrecord
   * @return
   * @throws Exception
   */
  public String getPushPids(String pushrecord) throws Exception {
    String pr_pid = "";
    if (pushrecord != null && !pushrecord.equals("")) {   //1
      String[] plist = pushrecord.split(Global.CMSSEP);
      if (plist != null && plist.length > 0) {          //2
        for (int i = 0; i < plist.length; i++) {        //3
          if (plist[i] != null && !plist[i].equals("")) {  //4
            try {
              EntityItem entity = (EntityItem) ItemManager.getInstance().get(new Integer(
                  Integer.parseInt(plist[i])), EntityItem.class);
              if (entity != null) {
                pr_pid = pr_pid + ";" + entity.getPid();
              }
            }
            catch (Exception pe) {
              log.error("MediaPushImpl.getPushPids  parse EntityItem id error--"
                        + pe.toString());
            }
          }    //4
        }     //3
      }      //2
    }       //1
    return pr_pid;
  }

}
