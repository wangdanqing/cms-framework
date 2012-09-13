package net.pusuo.cms.server.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import com.hexun.cms.Configuration;
import com.hexun.cms.core.EntityItem;
import com.hexun.cms.util.Util;

/**
 * StoreRule�����ҳ�����Ƭ�洢����ĺ�����
 * 
 * @since CMS1.0
 * @version 1.0
 * @author XuLin
 */
public class StoreRule {
	private static final Log LOG = LogFactory.getLog(StoreRule.class);

	public static final String PICTUREROOT = "img";
	
	public static final String MUMEROOT = "video";

	public static final String PICTUREDOMAIN =Configuration.getInstance().get("cms4.file.picture.domain");

	public static final String MUMEDAIDOMAIN =Configuration.getInstance().get("cms4.file.video.domain");

	private static StoreRule storerule = null;

	static{
		if(LOG.isInfoEnabled()){
			LOG.info("PICTUREDOMAIN:"+PICTUREDOMAIN);
			LOG.info("MUMEDAIDOMAIN :"+MUMEDAIDOMAIN);
		}
	}

	public static StoreRule getInstance() {
		if (storerule == null) {
			try {
				synchronized (StoreRule.class) {
					if (storerule == null) {
						storerule = new StoreRule();
					}
				}
			} catch (Exception e) {
				LOG.error("unable to create ItemProxy instance ."
						+ e.toString());
			}
		}

		return storerule;
	}

	/**
	 * ʵ��洢����-homepage,column,sub column,subject,sub subject,news,picture format
	 * homepage: /news.shtml column: /guonei.shtml sub column: shehui.shtml
	 * subject: /s2004/keji.shtml sub subject: /s2004/3654/s2xxxxxx.shtml news:
	 * /20040811/n2xxxxxx.shtml picture: /20040811/Img2xxxxxx.*
	 */
	public static String entityRule(int entityID, int entityType,
			Timestamp entityTime, int subtype, String shortName, int psubject,
			String ext) throws Exception {
		try {
			return entityRule(entityID, entityType, 0, entityTime, subtype,
					shortName, psubject, ext);
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	public static String entityRule(int entityID, int entityType,
			int entityChannel, Timestamp entityTime, int subtype,
			String shortName, int psubject, String ext) throws Exception {
		try {
			if(LOG.isDebugEnabled()){
				LOG.debug(entityID + "  " + entityType + "  " + entityChannel
					+ "  " + entityTime + " " + subtype + "  " + shortName
					+ "  " + psubject + "  " + ext);
			}
			if (entityID < 0 || entityType < 0 || entityTime == null
					|| ext.equals(""))
				return "";

			StringBuffer filesb = new StringBuffer();
			filesb.append("/");

			switch (entityType) {
			case EntityItem.SUBJECT_TYPE: // Subject
			{				
				switch (subtype) {
				// format: /2004/shortname.shtml
				case 1: // ������Ϊר��
				{
					// ������Ϊר��Ĺ����ǣ�
					// �����ڶ����֣�ʹ�ö�������Ϊ�洢����
					// ����ʹ��s����IDΪ�ļ��洢����
					filesb.append(Util.formatTime(entityTime, "yyyy") + "/");

					if (shortName.trim().length() > 0) {
						filesb.append(shortName + "/index");
						
					} else {
						filesb.append(entityID);
					}
				}
					break;
				// format: /2004/last4(Psubject)/sid.shtml
				case 2: // ������Ϊ��ר��
				{
					// ������Ϊ��ר��Ĺ����ǣ�
					// ʹ��s����IDΪ�ļ��洢����
					filesb.append(Util.formatTime(entityTime, "yyyy") + "/");

					String psubid = "" + psubject;
					psubid = ((psubid != null && psubid.length() >= 4) ? psubid
							: String.valueOf(entityID));

					filesb.append(psubid.substring(psubid.length() - 4) + "/");
					if (shortName.trim().length() > 0) {
						filesb.append(shortName + "/index");
					} else {
						filesb.append(entityID + "/index");
					}
				}
					break;
				case 3: // ������Ϊ��Ŀ
				case 4: // ������Ϊ����Ŀ
				{
					//��Ŀ������Ŀ������ͬ�������ڶ����֣����ն����ֺϳ�
					//�����ڶ����֣���ô����ͳһ�Ĺ������
					if (shortName.trim().length() > 0
							&& (subtype == 3 || subtype == 4)) {
						filesb.append(shortName + "/index");
					} else {
						filesb.append(entityID + "/index");
					}
				}
				default:
					break;
				}

				break;
			}
			case EntityItem.NEWS_TYPE: // News
			{
				filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd") + "/");
				filesb.append(entityID);
				break;
			}
			case EntityItem.PICTURE_TYPE: // Picture
			{
				filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd") + "/");
				filesb.append(entityID);

				break;
			}
			case EntityItem.VIDEO_TYPE:
			{
				//��Ƶ�Ĵ洢����:yyyy-MM-dd/XXXXXXXXX
				filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd")+ "/");
				filesb.append(entityID);
				break;
			}
			case EntityItem.HOMEPAGE_TYPE: // Homepage
			{
				if (shortName.trim().length() > 0) {
					filesb.append(shortName);
				} else {
					filesb.append("h" + entityID);
				}
			}
			default:
				break;
			}

			filesb.append(".");
			filesb.append(ext);
			return filesb.toString();
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	/**
	 * INC��Ƭ�洢���� format homepage: /frag/entityID/tflink_e.inc column:
	 * /frag/entityID/tflink_e.inc sub column: /frag/entityID/tflink_e.inc
	 * subject: /s2004/frag/34/tflink_e.inc sub subject:
	 * /s2004/frag/34/tflink_e.inc news: /2005/09/16/34/tflink_e.inc
	 */
	public static String fragRule(int entityID, int entityType,
			Timestamp entityTime, int subtype, int psubject, int tflinkID)
			throws Exception {
		try {
			if (entityID < 0 || entityType < 0 || tflinkID < 0)
				return "";

			StringBuffer filesb = new StringBuffer();

			switch (entityType) {
			case EntityItem.SUBJECT_TYPE: // Subject
			{
				switch (subtype) {
				case 0:
					filesb.append("/frag/");
					filesb.append(entityType);
					filesb.append("/");
					filesb.append(Util.formatTime(entityTime, "MMyy"));
					filesb.append("/");
					String entityStr = entityID + "";
					int len = entityStr.length();
					filesb.append(entityStr.substring(len - 4, len - 2));
					filesb.append("/");
					filesb.append(tflinkID + "_" + entityID);
					filesb.append(".inc");
					break;

				// format: /2004/frag/34/tflink_e.inc
				case 1: //������Ϊר�⡢��ר��
				case 2: {
					filesb.append("/");
					filesb.append(Util.formatTime(entityTime, "yyyy")
							+ "/frag/");

					String psubid = "" + psubject;
					psubid = ((!psubid.equals("") && psubid.length() >= 4) ? psubid
							.substring(psubid.length() - 4)
							: psubid);
					filesb.append(psubid.substring(0, 2));

					filesb.append("/");
					filesb.append(tflinkID + "_" + entityID);
					filesb.append(".inc");
				}
					break;
				// format: /frag/entityid/tflink_e.inc
				case 3: // ������Ϊ��Ŀ
				case 4: // ������Ϊ����Ŀ
				{
					filesb.append("/frag/");
					filesb.append(entityID);

					filesb.append("/");
					filesb.append(tflinkID + "_" + entityID);
					filesb.append(".inc");
				}
				default:
					break;
				}

				break;
			}
			case EntityItem.HOMEPAGE_TYPE: // Homepage
			{
				filesb.append("/frag/");
				filesb.append(entityID);

				filesb.append("/");
				filesb.append(tflinkID + "_" + entityID);
				filesb.append(".inc");
				break;
			}
			case EntityItem.NEWS_TYPE: // News
			{
				filesb.append(Util.formatDatetime(entityTime, "/yyyy/MM/dd/"));
				filesb.append(("" + entityID).substring(5, 7));
				filesb.append("/");
				filesb.append(tflinkID + "_" + entityID);
				filesb.append(".inc");
				break;
			}
			default:
				break;
			}

			return filesb.toString();
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}
	
	/**
	 * �޸�֮��:ʹ����Ƭ������ԭ�ȹ����е���ƬID.
	 * �������:ͬһ��Ƭ���±����,��ʹID����ı�,��Ƭ·�����Ǳ��ֲ���.����ԭ�ȵ�����Ҳ���ᶪʧ.
	 * @author Alfred.Yuan
	 */
	public static String fragRule(int entityID, int entityType,
			Timestamp entityTime, int subtype, int psubject, String tfName)
			throws Exception {
		try {
			if (entityID < 0 || entityType < 0 || tfName == null || tfName.trim().length() == 0)
				return "";
			tfName = tfName.trim();

			StringBuffer filesb = new StringBuffer();

			switch (entityType) {
			case EntityItem.SUBJECT_TYPE: // Subject
			{
				switch (subtype) {
				case 0:
					filesb.append("/frag/");
					filesb.append(entityType);
					filesb.append("/");
					filesb.append(Util.formatTime(entityTime, "MMyy"));
					filesb.append("/");
					String entityStr = entityID + "";
					int len = entityStr.length();
					filesb.append(entityStr.substring(len - 4, len - 2));
					filesb.append("/");
					filesb.append(tfName + "_" + entityID);
					filesb.append(".inc");
					break;

				// format: /2004/frag/34/tflink_e.inc
				case 1: //������Ϊר�⡢��ר��
				case 2: {
					filesb.append("/");
					filesb.append(Util.formatTime(entityTime, "yyyy")
							+ "/frag/");

					String psubid = "" + psubject;
					psubid = ((!psubid.equals("") && psubid.length() >= 4) ? psubid
							.substring(psubid.length() - 4)
							: psubid);
					filesb.append(psubid.substring(0, 2));

					filesb.append("/");
					filesb.append(tfName + "_" + entityID);
					filesb.append(".inc");
				}
					break;
				// format: /2004/frag/entityid/tflink_e.inc
				case 3: // ������Ϊ��Ŀ
				case 4: // ������Ϊ����Ŀ
				{
					filesb.append("/frag/");
					filesb.append(entityID);

					filesb.append("/");
					filesb.append(tfName + "_" + entityID);
					filesb.append(".inc");
				}
				default:
					break;
				}

				break;
			}
			case EntityItem.HOMEPAGE_TYPE: // Homepage
			{
				filesb.append("/frag/");
				filesb.append(entityID);

				filesb.append("/");
				filesb.append(tfName + "_" + entityID);
				filesb.append(".inc");
				break;
			}
			case EntityItem.NEWS_TYPE: // News
			{
				filesb.append(Util.formatDatetime(entityTime, "/yyyy/MM/dd/"));
				filesb.append(("" + entityID).substring(5, 7));
				filesb.append("/");
				filesb.append(tfName + "_" + entityID);
				filesb.append(".inc");
				break;
			}
			default:
				break;
			}

			return filesb.toString();
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}
	
}
