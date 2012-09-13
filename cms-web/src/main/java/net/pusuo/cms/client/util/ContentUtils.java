package net.pusuo.cms.client.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.hexun.cms.ItemInfo;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.core.Picture;

/**
 * ���ݹ�����
 * 
 * @author DengHua
 * 
 */
public class ContentUtils {
	/**
	 * ���image�����ID����������
	 * 
	 * @param ids
	 *            images�����ID����
	 * @param pageSize
	 *            ÿҳ��С
	 * @param newsText
	 *            �������ݡ� ���ڵ�һҳ
	 * @return ��ɺõķ�ҳ����
	 */
	public static String buildImageContent(String[] ids, int pageSize,
			String newsText) {
		String[] imagesContent = preImagesContent(ids);
		String content = makePagination(imagesContent, pageSize, newsText);
		content = "&nbsp;" + content; // cms�����������ڷ�ҳ����ǰ�����û���κ��ַ�Ļ�����ѱ�ǩɾ����
		return content;
	}

	/**
	 * �����ݽ��з�ҳ
	 * 
	 * @param imagesContent
	 * @param pageSize
	 * @return
	 */
	private static String makePagination(String[] imagesContent, int pageSize,
			String newsText) {
		String result = "";
		StringBuffer temp = new StringBuffer();
		int totalSize = imagesContent.length; // �ܴ�С
		int totalPageNum = totalSize / pageSize; // ��ҳ��
		if (totalPageNum % pageSize > 0)
			totalPageNum++;
		int pageNum = 1;
		String pageTemp = "";
		int page_images_size = 0;
		if (totalSize > pageSize) {
			for (int i = 0; i < totalSize; i++) {
				if ((i >= (pageNum - 1) * pageSize) && (i < pageNum * pageSize)) {
					pageTemp += imagesContent[i];
					page_images_size++;
					if (page_images_size == pageSize
							|| (i == (totalSize - 1) ? (page_images_size < pageSize)
									: false)) {
						temp.append("<HEXUNMPCODE><HEXUNSUBHEAD></HEXUNSUBHEAD>");
						temp.append(pageTemp);
						if (StringUtils.isNotEmpty(newsText) && 1 == pageNum)
							temp.append(newsText);
						temp.append("</HEXUNMPCODE>");
						pageTemp = "";
						page_images_size = 0;
						pageNum++;
					}
				}
			}
			temp
					.append("<HEXUNMPCOMMON></HEXUNMPCOMMON><HEXUNMPBANNER>2</HEXUNMPBANNER>");
			result = temp.toString();
		} else {
			for (int i = 0; i < totalSize; i++) {
				temp.append(imagesContent[i]);
			}
			temp.append(newsText);
			result = temp.toString();
		}
		return result;
	}

	/**
	 * Ԥ��image����ʾ����
	 * 
	 * @param ids
	 * @param pageSize
	 * @return
	 */
	private static String[] preImagesContent(String[] ids) {
		String[] imageContents = new String[ids.length];

		// �������ݵ�Ԥ���ͼƬ��ID�õ�URL�������Ϣ��Ȼ��õ���ͼƬ�����������ʾ�Ĵ���

		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			if (!StringUtils.isNumeric(id))
				continue;
			Picture pictureItem = (Picture) ItemManager.getInstance().get(
					new Integer(id),
					ItemInfo.getItemClass(ItemInfo.PICTURE_TYPE));
			if (pictureItem != null) {
				StringBuffer temp = new StringBuffer();
				temp
						.append("<table cellSpacing=\"0\" cellPadding=\"0\" align=\"center\" border=\"0\"><tr><td align=\"middle\">");
				temp.append("<img alt=\"\" src=\"" + pictureItem.getUrl()
						+ "\" border=\"0\" />");
				temp.append("<br /><br /></td></tr></table>");
				imageContents[i] = temp.toString();
			} else {
				ArrayUtils.remove(ids, i);
			}
		}
		return imageContents;
	}

}
