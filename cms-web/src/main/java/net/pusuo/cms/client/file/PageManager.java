package net.pusuo.cms.client.file;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.core.*;
import com.hexun.cms.Item;
import com.hexun.cms.Configuration;
import com.hexun.cms.file.StoreRule;
import com.hexun.cms.util.Util;
import com.hexun.cms.Global;

import com.hexun.cms.client.ItemManager;

public class PageManager {
	private static final Log LOG = LogFactory.getLog(PageManager.class);
	public static final String separator = System.getProperty("file.separator");

	public static final String MODLOGROOT = separator + "modlog";

	/**
	 * �õ�ʵ��Ĵ洢·������ģ�� homepage /news/news.shtml column /news/guonei.shtml sub
	 * column /news/shehui.shtml subject /news/s2004/keji.shtml sub subject
	 * /news/s2004/3654/s2xxxxxx.shtml news /news/20040811/n2xxxxxx.shtml
	 */
	public static String getTStorePath(EntityItem entity) throws Exception {
		try {

			return entityPath(entity, 0, true);

		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	/**
	 * �õ�ʵ��Ĵ洢·������ģ�� homepage /news/templateID_news.shtml column
	 * /news/templateID_guonei.shtml sub column /news/templateID_shehui.shtml
	 * subject /news/s2004/templateID_keji.shtml sub subject
	 * /news/s2004/3654/templateID_s2xxxxxx.shtml news
	 * /news/20040811/templateID_n2xxxxxx.shtml
	 */
	public static String getTStorePath(EntityItem entity, int templateID)
			throws Exception {
		try {

			return entityPath(entity, templateID, true);

		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	/**
	 * �õ�ʵ���URL����Ҫ��Ϊ��ģ���ʵ��ʹ �ã����ڵ�ģ�����ͨ��EntityItem��getUrl�����õ�
	 * ��templateID����Ҳ���Եõ���ģ���
	 */
	public static String getURL(EntityItem entity, int templateID,
			boolean isAbsolute) throws Exception {
		try {
			String url = entity.getUrl();

			if (entity.getType() == EntityItem.PICTURE_TYPE) {
				return url;
			}

			if (entity.getType() == EntityItem.NEWS_TYPE) {
				String reurl = ((News) entity).getReurl();
				if (reurl != null && !reurl.equals("")) {
					return reurl;
				}
			}

			String domain = "";
			if (isAbsolute) {// �Ǿ��URL��ַ
				Channel channel = (Channel) ItemManager.getInstance().get(
						new Integer(entity.getChannel()), Channel.class);
				domain = "http://" + channel.getName();
			}

			return domain + entityPath(entity, templateID, false);

		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	/**
	 * �õ�ʵ��Ĵ洢·�� ���templateIDΪ�� homepage /news/news.shtml column
	 * /news/guonei.shtml sub column /news/shehui.shtml subject
	 * /news/s2004/keji.shtml sub subject /news/s2004/3654/s2xxxxxx.shtml news
	 * /news/20040811/n2xxxxxx.shtml ���templateID��Ϊ�� homepage
	 * /news/templateID_news.shtml column /news/templateID_guonei.shtml sub
	 * column /news/templateID_shehui.shtml subject
	 * /news/s2004/templateID_keji.shtml sub subject
	 * /news/s2004/3654/templateID_s2xxxxxx.shtml news
	 * /news/20040811/templateID_n2xxxxxx.shtml
	 */
	private static String entityPath(EntityItem entity, int templateID,
			boolean hasChannelDir) throws Exception {
		try {
			int subtype = -1;
			int psubid = -1;

			String shortName = "";
			if (templateID == 0) {
				shortName = entity.getShortname();
			} else {
				String templates = entity.getTemplate();
				templates = (templates != null ? templates : "");
				String templArray[] = templates.split(Global.CMSSEP);// Global.CMSSEP
																		// mean
																		// ;
				if (!templates.equals("")) {
					for (int i = 0; i < templArray.length; i++) {
						String template[] = templArray[i]
								.split(Global.CMSCOMMA);// Global.CMSCOMMA mean
														// ,
						if (templateID == Integer.parseInt(template[0])) {
							shortName = template[1];
							break;
						}
					}
				}
			}
			if (entity.getType() == EntityItem.SUBJECT_TYPE) {
				subtype = ((Subject) entity).getSubtype();
				psubid = getPSubjectID(entity);
			}

			String dir = "";
			String ext = "html";

			if (hasChannelDir) {
				if (entity.getType() == EntityItem.PICTURE_TYPE) {
					dir = "/" + StoreRule.PICTUREROOT;
					String purl = ((Picture) entity).getUrl();
					int pos = purl.lastIndexOf(".");
					if (pos >= 0) {
						ext = purl.substring(pos + 1);
					}
					ext = (ext.equals("") ? "gif" : ext);
				} else if (entity.getType() == EntityItem.VIDEO_TYPE) {
					// ������Ƶʵ��,��ͼƬ�Ĺ����һ��
					dir = "/" + StoreRule.MUMEROOT;
					String purl = entity.getUrl();
					int pos = purl.lastIndexOf(".");
					if (pos >= 0) {
						ext = purl.substring(pos + 1);
					}
				} else {
					Channel channel = (Channel) ItemManager.getInstance().get(
							new Integer(entity.getChannel()), Channel.class);
					dir = "/" + channel.getDir();
				}
			}

			return dir
					+ StoreRule.getInstance().entityRule(entity.getId(),
							entity.getType(), entity.getChannel(),
							entity.getTime(), subtype, shortName, psubid, ext);

		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	/**
	 * ������Ƭ��·����Ϣ��hasChannelDir��ʾ�Ƿ��Ƶ��dirǰ׺ format hasChannelDir is true:
	 * homepage: /news/frag/entityID/tf_e.inc column:
	 * /news/frag/entityID/tf_e.inc sub column: /news/frag/entityID/tf_e.inc
	 * subject: /news/s2004/frag/34/tf_e.inc sub subject:
	 * /news/s2004/frag/34/tf_e.inc news: /news/2005/09/16/34/tf_e.inc
	 * hasChannelDir is false: homepage: /frag/entityID/tf_e.inc column:
	 * /frag/entityID/tf_e.inc sub column: /frag/entityID/tf_e.inc subject:
	 * /s2004/frag/34/tf_e.inc sub subject: /s2004/frag/34/tf_e.inc news:
	 * /2005/09/16/34/tf_e.inc
	 */
	public static String getFStorePath(EntityItem entity, int tflinkID,
			boolean hasChannelDir) throws Exception {
		try {

			int subtype = -1;
			int psubid = -1;
			if (entity.getType() == EntityItem.SUBJECT_TYPE) {
				subtype = ((Subject) entity).getSubtype();
				psubid = getPSubjectID(entity);
			}

			String dir = "";
			if (hasChannelDir) {
				Channel channel = (Channel) ItemManager.getInstance().get(
						new Integer(entity.getChannel()), Channel.class);
				dir = File.separator + channel.getDir();
			}

			// �޸���Ƭ�洢����:id->name
			// Alfred.Yuan(2006.11.20)
			// final int FRAG_ID_SOMEDAY = 52311;
			// if (tflinkID > FRAG_ID_SOMEDAY) {
			// Item item = ItemManager.getInstance().get(new Integer(tflinkID),
			// TFMap.class);
			// if (item != null && item instanceof TFMap) {
			// TFMap tfmap = (TFMap)item;
			// if (tfmap.getName() != null && tfmap.getName().trim().length() >
			// 0) {
			// String tfName = tfmap.getName().trim();
			// int templateId = tfmap.getTemplate().getId();
			// String storePath = dir + StoreRule.getInstance().fragRule(
			// entity.getId(), entity.getType(), entity.getTime(), subtype,
			// psubid, tfName );
			// LOG.debug("PageManager-getFStorePath:(entity=" + entity.getId()
			// + ")(tfId=" + tflinkID + ")(storePath=" + storePath + ")");
			// return storePath;
			// }
			// }
			// }

			return dir
					+ StoreRule.getInstance().fragRule(entity.getId(),
							entity.getType(), entity.getTime(), subtype,
							psubid, tflinkID);

		} catch (Exception e) {
			LOG.error(e + "  ENTITYID:" + entity.getId());
			throw e;
		}
	}

	/**
	 * Ƶ���ڹ�����Ƭ ��Ҫ��Ƶ������/����ҳlogo
	 * 
	 * @param entityId
	 *            ��ҳʵ��id ���Ƶ���ڶ����ҳ�����ͻ, ���������д��ͬһ��Ƶ�������
	 * @param fragName
	 *            ָ������Ƭ���
	 * @return ������Ƭ�洢λ�� �÷�: entity==null ���ؿ�Ƶ�����õ���Ƭ�洢λ�� entity!=null
	 *         ����Ƶ���ڹ��õ���Ƭ�洢λ��
	 */
	public static String getFStorePath(int channelId, int entityId,
			String fragName, boolean hasChannelDir) throws Exception {
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelId), Channel.class);
		final String COMMONFRAGDIR = File.separator + "commonfrag"
				+ File.separator + entityId;
		if (hasChannelDir) {
			return File.separator + channel.getDir() + COMMONFRAGDIR
					+ File.separator + fragName + ".inc";
		} else {
			return COMMONFRAGDIR + File.separator + fragName + ".inc";
		}
	}

	/**
	 * HEXUN������Ƭ,����Ƶ��������Ƭ,��Ҫ��hexunͷ,β,����ҳͷ�����ֲ��� ��Ҫ��ÿ��Ƶ���±���һ��
	 * dir/commonfrag/fragname.inc
	 */
	public static String getFStorePath(int channelId, String fragName,
			boolean hasChannelDir) throws Exception {
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelId), Channel.class);
		final String COMMONFRAGDIR = File.separator + "commonfrag";
		if (hasChannelDir) {
			return File.separator + channel.getDir() + COMMONFRAGDIR
					+ File.separator + fragName + ".inc";
		} else {
			return COMMONFRAGDIR + File.separator + fragName + ".inc";
		}
	}

	/**
	 * �������ļ���������ͳһ���?��
	 */
	public static String getFStorePath(String fragName) {
		final String COMMONFRAGDIR = File.separator + "commonfrag";
		return COMMONFRAGDIR + File.separator + fragName + ".inc";
	}

	/**
	 * �����ר��ĵõ������ר������ĸ�ר��ID
	 */
	private static int getPSubjectID(EntityItem item) {
		int psubid = -1;

		try {
			if (item == null || item.getPid() <= 0)
				return psubid;

			psubid = item.getId();
			while (item != null && item.getId() > 0) {
				if (item.getType() == EntityItem.SUBJECT_TYPE) {// ����Ϊר��
					if (((Subject) item).getSubtype() == 1) {// ������Ϊר��
						psubid = item.getId();
						break;
					}
				}
				item = (EntityItem) ItemManager.getInstance().get(
						new Integer(item.getPid()), item.getClass());
			}
			psubid = (psubid == -1 ? item.getId() : psubid);

		} catch (Exception e) {
			LOG.error("StoreRule.getPSubjectID error. " + e.toString());
		}

		return psubid;
	}

	// added by Mark 2004.6.15
	// get client side template/frag store path
	// eg. /usr/local/resin/webapp/cms/cms_page/template/jsp/template1234.jsp
	public static String FTPath(Item item, boolean isHTML) {
		String path = null;
		path = FTWebPath(item, isHTML);
		if (path != null) {
			path = Configuration.getInstance().get("cms4.client.file.root")
					+ path;
		}
		return path;
	}

	public static String FTWebPath(Item item, boolean isHTML) {
		String path = null;
		Configuration config = Configuration.getInstance();
		if (item instanceof Template) {
			if (isHTML) {
				path = config.get("cms4.client.file.template.page")
						+ config.get("cms4.client.file.template.page.html")
						+ separator + "template" + item.getId() + ".html";
			} else {
				path = config.get("cms4.client.file.template.page")
						+ config.get("cms4.client.file.template.page.jsp")
						+ separator + "template" + item.getId() + ".jsp";
			}
		} else if (item instanceof Frag) {
			path = config.get("cms4.client.file.frag.page") + separator
					+ "frag" + item.getId() + ".jsp";
		}
		return path;
	}

	// //////////////////////////////////////////////////////////////////////////

	// �������
	public static String getRelativeNewsPath(News news, boolean hasChannelDir) {

		return getRelativesStorePath(news, "_news", hasChannelDir);
	}

	// �������
	public static String getRelativeHintPath(News news, boolean hasChannelDir) {

		return getRelativesStorePath(news, "_hint", hasChannelDir);
	}

	// ��ز���
	public static String getRelativeBlogPath(News news, boolean hasChannelDir) {

		return getRelativesStorePath(news, "_blog", hasChannelDir);
	}

	// ���˵��
	public static String getRelativeSaybarPath(News news, boolean hasChannelDir) {

		return getRelativesStorePath(news, "_saybar", hasChannelDir);
	}

	private static String getRelativesStorePath(News news, String tag,
			boolean hasChannelDir) {

		String result = "";

		try {
			String storePath = entityPath(news, 0, hasChannelDir);
			if (storePath == null || storePath.trim().length() == 0
					|| storePath.indexOf(".") == -1)
				return null;
			int index = storePath.lastIndexOf(".");
			result = storePath.substring(0, index);
		} catch (Exception e) {
			return null;
		}

		return result + tag + ".inc";
	}

	public static String getModlogPath(News news, int id) {
		return MODLOGROOT + separator
				+ Util.formatTime(news.getTime(), "yyyy-MM-dd") + separator
				+ news.getId() + separator + news.getId() + "_" + id + ".xml";
	}
}
