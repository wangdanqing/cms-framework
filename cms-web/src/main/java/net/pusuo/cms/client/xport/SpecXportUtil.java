/**
 * 
 */
package net.pusuo.cms.client.xport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.xport.config.CommonXportConfig;
import com.hexun.cms.client.xport.config.PropertyManager;

/**
 * @author Alfred.Yuan
 * 
 */
public class SpecXportUtil {

	private static Log log = LogFactory.getLog(SpecXportUtil.class);

	private static CommonXportConfig cxc;
	
	public SpecXportUtil() {
	}
	public SpecXportUtil(CommonXportConfig cxconfig) {
		cxc = cxconfig;
	}

	public int getSubjectNewId(int index) {

		return getSubjectSomeId(index, "newId");
	}

	public int getSubjectOldId(int index) {

		return getSubjectSomeId(index, "oldId");
	}

	public int getSubjectLastId(int index) {

		return getSubjectSomeId(index, "lastId");
	}
        public String getSubjectTag(int index) {
		String tag = getSomeChar(index, "tag");	
                return tag;
        }

	public static void setSubjectLastId(int index, int value) {

		setSubjectSomeId(index, "lastId", value);
	}
    	//�ֶ���Ŀ��ǩ��ȡ
	public int getSubjectHandTagId(int index) {

		return getSubjectSomeId(index, "handTagId");
	}
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * xml�ļ����ַ�����
	 * @param index
	 * @param idName
	 * @return
	 */
	private String getSomeChar(int index, String idName) {

		String idParam = cxc.getProperty("subject" + index + "." + idName);
		if (idParam == null || idParam.trim().length() == 0) {
			return "";
		}

		return idParam;
	}

	private int getSubjectSomeId(int index, String idName) {

		int id = -1;

		String idParam = cxc.getProperty("subject" + index + "." + idName);
		if (idParam == null || idParam.trim().length() == 0) {
			return -1;
		}

		try {
			id = Integer.parseInt(idParam);
		} catch (Exception e) {
			id = -1;
			log.error("", e);
		}

		return id;
	}

	private static void setSubjectSomeId(int index, String idName, int value) {

		cxc.setProperty("subject" + index + "." + idName, String
						.valueOf(value));
	}

	public static String getPropValue(String path,String key) {
		Properties properties = loadProps(path);
		if (properties == null)
			return null;
		if (properties.getProperty(key) == null)
		{	
			return "";
		}
		else
			return properties.getProperty(key).trim();
	}

	public static void setPropKey(String path,String key,String value) {
		Properties properties = loadProps(path);
		properties.put(key, value);
		saveProps(path, properties);
	}

	private static Properties loadProps(String resourceURI) {
		Properties properties = new Properties();

		InputStream in = null;
		try {
			in = new FileInputStream(resourceURI);
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}

		return properties;
	}

	private static void saveProps(String resourceURI, Properties properties) {

		OutputStream out = null;
		try {
			out = new FileOutputStream(resourceURI);
			properties.store(out, "");
		} catch (Exception ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}
}
