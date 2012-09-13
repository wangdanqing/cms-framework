/*

 * Created on 2005-7-26

 */

package net.pusuo.cms.client.util;

import java.io.BufferedInputStream;

import java.io.BufferedReader;

import java.io.ByteArrayOutputStream;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.HashMap;

import java.util.Iterator;

import java.util.Map;

import java.util.Set;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;

import com.hexun.cms.client.file.ClientFile;

/**
 * 
 * @author agilewang
 * 
 */

public class FragZipProcessor {

	private Log log = LogFactory.getLog(FragZipProcessor.class);

	private static final String DATE_SHORT = "yyyyMMdd";

	private static final String DATE_LONG = "HHmmssSSS";

	private static final String FRAG_IMG_PRE = "/ImgFrag/";

	/** ����html�е�img��ǺͰ���background�ı�ǵ�������ʽ */

	private static final String pattern = "<img[^<]*src=[\"|']?([/|\\w|.]*)[\"|']?[^<]*>|<[^<]*background=[\"|']?([/|\\w|.]*)[\"|']?[^<]*>";

	private static final Pattern img_p = Pattern.compile(pattern,

	Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

	private ZipExpress jp = null;

	private String domain = null;

	private String content = null;

	public FragZipProcessor(final ZipExpress zip, final String domain) {

		if (zip == null || domain == null) {

			throw new IllegalArgumentException("args is null");

		}

		this.jp = zip;

		this.domain = domain;

		this.anylize();

	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	private void anylize() {

		StringBuffer oldContent = new StringBuffer(); // ԭhtml�ļ�������

		StringBuffer newContent = new StringBuffer(); // �������html����

		Map fileMap = jp.getFiles();

		Set set = fileMap.entrySet();

		Iterator si = set.iterator();

		/* ȡ��һ��html�ļ�,���һ��HTML�ļ�,������Ĳ��ٽ��д��� ,������content�� */

		while (si.hasNext()) {

			Map.Entry entry = (Map.Entry) si.next();

			String name = (String) entry.getKey();

			File file = (File) entry.getValue();

			String name_lowcase = name.toLowerCase();

			BufferedReader br = null;

			if (name_lowcase.endsWith(".htm") || name_lowcase.endsWith(".html")) {

				getFileContent(oldContent, file);

				break;

			}

		}

		set = null;

		si = null;

		// ͨ��������ʽ���д���

		Matcher match = img_p.matcher(oldContent);

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_SHORT);

		SimpleDateFormat ldf = new SimpleDateFormat(DATE_LONG);

		int start = 0;

		Map imgesMap = new HashMap();

		int imgI = 0;

		while (match.find(start)) {

			newContent.append(oldContent.substring(start, match.start()));

			int count = match.groupCount();

			if (count != 2) {

				throw new IllegalStateException("group count != 2");

			}

			String img = null;

			for (int i = 1; i <= count; i++) {

				img = match.group(i);

				if (img != null) {

					break;

				}

			}

			String temp = oldContent.substring(match.start(), match.end());

			File imgFile = null;

			if (img != null && (imgFile = (File) fileMap.get(img)) != null) {

				int img_s = temp.indexOf(img);

				int name_dot_s = img.lastIndexOf(".");

				int name_end = (name_dot_s > 0) ? name_dot_s : img.length();

				String imgHttpUrl = null;

				imgHttpUrl = (String) imgesMap.get(img);

				if (imgHttpUrl == null) {

					String name_suffix = img.substring(name_end);

					Date now = new Date();

					String dayDir = sdf.format(now); // ÿ�����һ��Ŀ¼

					/* ���ʱ�����һ��ʱ��µ��ļ��� ��ʽ:ʱ��+��ǰ�̵߳�hashCode+���к� */

					String dayFile = ldf.format(now)

					+ System.identityHashCode(Thread.currentThread())

					+ "" + (imgI++);

					String httpLocation = FRAG_IMG_PRE + dayDir + "/" + dayFile

					+ name_suffix;

					// String realLocation = "/photocdn" + httpLocation;

					String realLocation = Configuration.getInstance().get(
							"cms4.file.picture.root")
							+ httpLocation;

					if (log.isInfoEnabled()) {

						log.info("realLocal:" + realLocation);

						log.info("httpLocal:" + httpLocation);

					}

					byte[] imgContent = getFileByteContent(imgFile);

					try {

						// �ϴ��ļ��������� /opt/picture/ImgFrag��

						if (!ClientFile.getInstance().write(imgContent,

						realLocation, true)) {

							throw new RuntimeException("remote call error");

						}

					} catch (Exception e) {

						log.error("remote call error", e);

						throw new RuntimeException("remote call error", e);

					}

					imgHttpUrl = (domain + httpLocation);

					imgesMap.put(img, imgHttpUrl);

				}

				temp = temp.substring(0, img_s) + imgHttpUrl

				+ temp.substring(img_s + img.length());

			}

			newContent.append(temp);

			start = match.end();

		}

		if (start < oldContent.length()) {

			newContent.append(oldContent.substring(start));

		}

		content = newContent.toString();

	}

	/**
	 * 
	 * @param oldContent
	 * 
	 * @param file
	 * 
	 * @param br
	 * 
	 */

	private void getFileContent(StringBuffer oldContent, File file) {

		BufferedReader br = null;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(

			file), "GBK"));

			String line = null;

			while ((line = br.readLine()) != null) {

				oldContent.append(line);

			}

		} catch (IOException e) {

		} finally {

			if (br != null) {

				try {

					br.close();

				} catch (Exception e) {

				}

			}

		}

	}

	/**
	 * 
	 * @param imgFile
	 * 
	 * @return
	 * 
	 */

	private byte[] getFileByteContent(File imgFile) {

		byte[] imgContent = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		BufferedInputStream bio = null;

		try {

			byte[] buffer = new byte[1024];

			bio = new BufferedInputStream(new FileInputStream(imgFile));

			int len = 0;

			while ((len = bio.read(buffer)) != -1) {

				bos.write(buffer, 0, len);

			}

			bos.close();

			imgContent = bos.toByteArray();

		} catch (IOException e) {

			log.error("read file error", e);

		} finally {

			if (bio != null) {

				try {

					bio.close();

				} catch (IOException e1) {

				}

			}

		}

		return imgContent;

	}

	public String getContent() {

		return content;

	}

	public static void main(String[] args) {

		InputStream in = null;

		ZipExpress jp = null;

		try {

			in = new FileInputStream("E:\\music_index3_0720.zip");

			jp = new ZipExpress("c:\\a\\b", in);

			System.out.println("fileCount:" + jp.getFileCount());

			FragZipProcessor zpr = new FragZipProcessor(jp,

			"http://img.pusuo.net");

			System.out.println(zpr.getContent());

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} finally {

			if (jp != null) {

				jp.clearDestDir();

			}

		}

	}

}
