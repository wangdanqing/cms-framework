package net.pusuo.cms.client.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hexun.cms.Configuration;
import com.hexun.cms.client.file.ClientFile;



/**
 * ģ���ļ���¼��ȡ�ࡣ<br/>
 * ��ģ��ÿ�α����ʱ��,��¼ģ���ļ�. �γɼ�¼�б�.
 * ���ģ���ļ���·�����γɼ�¼�ļ���·��<br/>
 * 
 * @author denghua
 *
 */
public class TemplateLog {
	private static final Log log = LogFactory.getLog(TemplateLog.class);

	private static final Object lock = new Object();

	private static final String TEMPLATELOGROOT = File.separator
			+ "templatelog";

	private static final String FILESEP = File.separator;

	/**
	 * ���ģ���ļ�����path, ȡ�ñ����ļ�����Ŀ¼
	 * 
	 * @param filepath �ļ�����path
	 * @return �����ļ�����Ŀ¼
	 */
	public static String getTemplateLogDir(String filepath) {
		try {
			if (filepath == null || filepath.lastIndexOf(FILESEP) == -1) {
				log.error("getTemplateLogDir --> filepath is invalid."
						+ String.valueOf(filepath));
				return null;
			}
			File fff=new File(filepath);
			String filename=fff.getName();
			Configuration config = Configuration.getInstance();
			File s=new File(config.get("cms4.client.file.template.log")+FILESEP+ filename.substring(0,filename.lastIndexOf(".")));
			return s.getPath()+FILESEP;
		} catch (Exception e) {
			log.error("getTemplateLogDir --> exception. " + e.toString());
			return null;
		}
	}

	/**
	 * ȡ�����б����ļ�
	 * 
	 * @param filepath  �ļ�����path
	 * @return �ļ�������
	 */
	public static String[] getTemplateLogNames(String filepath) {
		try {
			// log.debug("filepath:"+filepath);
			int idx1 = filepath.lastIndexOf(FILESEP);
			int idx2 = filepath.lastIndexOf(".");

			String logTemplateName = filepath.substring(idx1 + 1, idx2);
			String logTemplateExtName = filepath.substring(idx2); //�������õ�".jsp"

			// log.debug("logTemplateName:"+logTemplateName);

			String logTemplateDir = getTemplateLogDir(filepath);

			// log.debug("logTemplateDir:"+logTemplateDir);

			if (logTemplateDir != null) {
				List list = new ArrayList();
				String[] fileList = LocalClientFile.getFileList(
						logTemplateDir);
				for (int i = 0; fileList != null && i < fileList.length; i++) {
					if (fileList[i].indexOf(logTemplateName) != -1) {
						if (fileList[i].indexOf(logTemplateExtName) != -1) {
							list.add(fileList[i]);
						}
					}
				}
				Object[] temp = list.toArray();
				Arrays.sort(temp);
				String[] ret = new String[temp.length];
				System.arraycopy(temp, 0, ret, 0, temp.length);
				return ret;
			} else {
				log.error("getTemplateLogNames --> templateTemplateDir is null.");
				return null;
			}
		} catch (Exception e) {
			log.error("getTemplateLogNames --> exception. " + e.toString());
			return null;
		}
	}

	/**
	 * дlog�ļ�, ͬʱɾ����ɵ�log�ļ�
	 * 
	 * @param filepath  ģ���ļ�·��
	 * @param username  ģ���޸��ߵ��û���
	 */
	public synchronized static void writeLog(String filepath, String username) {
		try {
			int idx1 = filepath.lastIndexOf(FILESEP);
			int idx2 = filepath.lastIndexOf(".");
			String logFileName = filepath.substring(idx1 + 1, idx2);
			String logFileExtName = filepath.substring(idx2 + 1); // ��չ��

			long currenttime = System.currentTimeMillis();
			logFileName += "_" + currenttime + "_" + username + "."
					+ logFileExtName.toLowerCase();

			String content = LocalClientFile.read(filepath);

			// ��Ƭ����==null, ˵���ǵ�һ�θ�����Ƭ, ���ñ���
			if (content == null) {
				return;
			}
			String logFilePath = getTemplateLogDir(filepath) + logFileName;

			boolean flag = LocalClientFile.write(content, logFilePath,
					false);
			log.info("writeLog --> " + logFilePath + "..."
					+ (flag == true ? "OK" : "FAILURE"));

			// ɾ����ɵ���Ƭ
			deleteTemplateFile(filepath);
		} catch (Exception e) {
			log.error("writeLog --> exception. " + e.toString());
		}
	}

	/**
	 * ɾ��ģ���ļ�,ֻ�������10���޸ĵ��ļ�
	 * 
	 * @param filepath ģ���ļ�·��
	 */
	private static void deleteTemplateFile(String filepath) {
		try {
			String logTemplateDir = TemplateLog.getTemplateLogDir(filepath);
			String[] logTemplateNames = TemplateLog
					.getTemplateLogNames(filepath);

			if (logTemplateNames.length > 10) {
				synchronized (lock) {
					if (logTemplateNames.length > 10) {
						boolean ret = LocalClientFile.delete(
								logTemplateDir + logTemplateNames[0]);
						log.info("deleteLogTemplate --> delete template log file [ "
								+ logTemplateDir + logTemplateNames[0]
								+ " ] ... " + (ret == true ? "OK" : "FAILURE"));
					}
				}
			}
		} catch (Exception e) {
			log.error("deleteLogTemplate --> exception. " + e.toString());
		}
	}

	/*
	 * private static int getOldestFile(List fileNames) { long oldestTemplate =
	 * System.currentTimeMillis(); int ret = -1;
	 * 
	 * for(int i=0; fileNames!=null && i<fileNames.size(); i++) { String
	 * templateName = (String)fileNames.get(i); log.info("templateName:"+templateName); int
	 * idx1 = templateName.indexOf("."); int idx2 = templateName.lastIndexOf("_");
	 * String templateNameTime = templateName.substring(idx1+1, idx2); if(
	 * Long.parseLong(templateNameTime)<oldestTemplate ) { oldestTemplate =
	 * Long.parseLong(templateNameTime); ret = i; } } log.info("ret:"+ret); return
	 * ret; }
	 */
}
