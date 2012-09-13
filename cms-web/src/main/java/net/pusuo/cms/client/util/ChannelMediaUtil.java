/*
 * Created on 2005-11-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hexun.cms.Configuration;
import com.hexun.cms.client.ItemManager;
import com.hexun.cms.client.file.ClientFile;
import com.hexun.cms.core.Channel;
import com.hexun.cms.core.Media;

/**
 * @author huaiwenyuan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ChannelMediaUtil {
	private static Map mediaMap = new HashMap();

	private static Map channelMediaMap = new HashMap();

	private static final Object mediaMapLock = new Object();

	private static String PATH = null;

	static {
		PATH = Configuration.getInstance().get("cms4.channelmedia.path");
		if (PATH == null)
			PATH = "cms4/channelmedia";
		String firstChar = PATH.substring(0, 1);
		if (!firstChar.equals("/") && !firstChar.equals("\\"))
			PATH = File.separator + PATH;
		String endChar = PATH.substring(PATH.length() - 1, PATH.length());
		if (!endChar.equals("/") && !endChar.equals("\\"))
			PATH += File.separator;
	}

	public static String getChannelMediaPath() {
		return PATH;
	}

	public static String getChannelMediaFileName(int channelid) {
		if (channelid < 0)
			return "";
		Channel channel = (Channel) ItemManager.getInstance().get(
				new Integer(channelid), Channel.class);
		if (channel == null)
			return "";
		String fileName = PATH + channel.getDir() + ".dat";
		return fileName;
	}

	public static List getChannelMedia(int channelid) {
		if (channelid < 0) {
			return null;
		}
		Integer key = new Integer(channelid);
		List channelMedias = (List) channelMediaMap.get(key);
		if (channelMedias == null) {
			String fileName = ChannelMediaUtil
					.getChannelMediaFileName(channelid);
			String fileContent = null;
			try {
				fileContent = ClientFile.getInstance().read(fileName);
			} catch (Exception e) {
				fileContent = null;
			}
			channelMedias = ChannelMediaUtil.decodeMedias(fileContent);
			if (channelMedias != null) {
				channelMediaMap.put(key, channelMedias);
			}
		}
		return channelMedias;
	}

	public static void removeChannelMedia(int channelid) {
		if (channelid < 0) {
			return;
		}
		Integer key = new Integer(channelid);
		channelMediaMap.remove(key);
	}

	public static List decodeMedias(String mediaContent) {
		if (mediaContent == null)
			return null;
		//long splitStart = System.currentTimeMillis();
		mediaContent = mediaContent.trim();
		String[] mediaArray = mediaContent.split(";");
		//long splitEnd = System.currentTimeMillis();
		//System.out.println("split time:" + (splitEnd - splitStart));
		if (mediaArray == null)
			return null;

		List medias = new ArrayList();
		long loopStart = System.currentTimeMillis();
		for (int i = 0; i < mediaArray.length; i++) {
			int mediaId = -1;
			try {
				mediaId = Integer.parseInt(mediaArray[i]);
			} catch (NumberFormatException e) {
				continue;
			}
			Media media = find(mediaId);
			if (media != null)
				medias.add(media);
		}
		//long loopEnd = System.currentTimeMillis();
		//System.out.println("loop time:" + (loopEnd - loopStart));
		return medias;
	}

	public static Media find(int mediaId) {
		//System.err.println("find media:"+mediaId);
		long findStart = System.currentTimeMillis();
		Integer key = new Integer(mediaId);
		Media media = null;
		synchronized (mediaMapLock) {
			media = (Media) mediaMap.get(key);
			if (media == null) {
				long rmiGetStart = System.currentTimeMillis();
				media = (Media) ItemManager.getInstance().get(key, Media.class);
				long rmiGetEnd = System.currentTimeMillis();
				//System.out.println("rmi get id:"+key+""+ (rmiGetEnd - rmiGetStart));
				if (media != null) {
					mediaMap.put(key, media);
				}
			}
		}
		long findEnd = System.currentTimeMillis();
		//System.out.println("find time:"+(findEnd-findStart));
		return media;
	}

	public static void remove(int mediaId) {
		Integer key = new Integer(mediaId);
		synchronized (mediaMapLock) {
			mediaMap.remove(key);

		}
	}

	public static String encodeMedias(String[] mediaids) {
		if (mediaids == null)
			return "";

		String mediaContent = "";
		for (int i = 0; i < mediaids.length; i++) {
			String mediaid = mediaids[i];
			mediaContent += mediaid + ";";
		}
		if (mediaContent.length() > 0)
			mediaContent = mediaContent.substring(0, mediaContent.length() - 1);

		return mediaContent;
	}

}
