package net.pusuo.cms.client.util;

/**
 * <p>Title: PushInterface</p>
 * <p>Description: �������͵Ĳ����ӿ�</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author wzg
 * @version 1.0
 */
import java.util.List;

public interface PushInterface {
  /**
   * ���в�������Ա��浽�ļ���
   * @param key
   * @param value
   */
  public void setProp(String key, String value);
  /**
   * ͨ�������ļ��õ��ϴβ��������ʵ��Id
   * @param oldMaxId
   * @return
   */
  public int getOldMaxId(String oldMaxId);
  /**
   * ͨ���ѯ��ݿ�õ��������ڵ����ʵ��Id
   * @return
   * @throws Exception
   */
  public int getNewMaxId() throws Exception;
  /**
   * ������id֮���״̬Ϊ���ӵģ�����ý�岻Ϊ�յ�
   * @return
   * @throws Exception
   */
  public List getPushNews(int oldMaxId, int newMaxId) throws Exception;
  /**
   * �����ͽ��д���
   * @return
   * @throws Exception
   */
  public boolean doPush() throws Exception;
  /**
   * �õ����͵����Ÿ�����ĺͣ��ã��ָ�
   * @return
   * @throws Exception
   */
  public String getPushPids(String pushrecord) throws Exception;
}
