/*
 * Created on 2006-1-24
 */
package net.pusuo.cms.client.util;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.client.file.ClientFile;

/**
 * �ṩSEO��cms4���ù���
 * 
 * @author agilewang
 *  
 */
public class SEOUtil {
	private static final String META_ROOT = "/cmsdata/seometa/";

	public static final String META_KEYWORD = "metaKey";

	public static final String META_DESCRIPTION = "metaDesc";

	private static final Log logger = LogFactory.getLog(SEOUtil.class);

	public static class SEOMeta {
		public String keyword;

		public String description;
	}

	/**
	 * ȡ��ר��meta�ļ��Ĵ洢·��
	 * 
	 * @param subjectid
	 * @return
	 */
	public static String getSEOMetaPath(int subjectid) {
		return META_ROOT + subjectid + ".meta";
	}

	/**
	 * ��ȡר���meta����
	 * 
	 * @param id
	 *            ר��id
	 * @return
	 */
	public static Properties readMetaProp(int id) {
		Properties meat = new Properties();
		try {
			String usersContent = ClientFile.getInstance().read(
					getSEOMetaPath(id));
			PropertyUtil.propertiesFromString(meat, usersContent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("readMetaProp for id:" + id + " error", e);
			}
		}
		return meat;
	}

	/**
	 * ����metaProp
	 * 
	 * @param id
	 * @param metaProp
	 */
	public static void saveMetaProp(int id, Properties metaProp) {
		try {
			String content = PropertyUtil.stringToProperties(metaProp);
			if (content != null) {
				ClientFile.getInstance().write(content, getSEOMetaPath(id),
						false);
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("saveMetaProp for id:" + id + " error", e1);
			}
		}
	}

	/**
	 * ȡ��ר��meta��keyword��descritpion
	 * 
	 * @param id
	 * @return
	 */
	public static SEOMeta getKeyDescription(int id) {
		SEOMeta meta = null;
		Properties metaProp = readMetaProp(id);
		if (!metaProp.isEmpty()) {
			meta = new SEOMeta();
			meta.keyword = metaProp.getProperty(META_KEYWORD);
			meta.description = metaProp.getProperty(META_DESCRIPTION);
		}
		return meta;
	}

	public static void saveKeyDescritpion(int id, SEOMeta meta) {
		if (meta == null) {
			return;
		}
		//�滻ȫ�ǵ�keyword
		meta.keyword = meta.keyword.replaceAll("\uFF0C", ",");
		Properties metaProp = readMetaProp(id);
		metaProp.setProperty(META_KEYWORD, meta.keyword);
		metaProp.setProperty(META_DESCRIPTION, meta.description);
		saveMetaProp(id, metaProp);
	}
}
