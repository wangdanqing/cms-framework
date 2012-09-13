package net.pusuo.cms.client.util;

import java.io.*;
import com.agile.zip.ZipEntry;
import com.agile.zip.ZipInputStream;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZipExpress {

	private static final Log log = LogFactory.getLog(ZipExpress.class);

	public static final String FILE_MIDDLE = "_&*_";

	public static final String FILE_SEP = File.separator;

	public static final int MAX_BUFF = 2048;

	private HashMap fileNameHas = null;

	private String destDir = null;

	private InputStream ins = null;

	private FileFilter fileFilter = null;
	
	private  int th = 0;

	public static final FileFilter DEFAULT_FILE_FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			/* ֻ����html�ļ���gif,jpg,png�ļ� */
			String name = pathname.getName().toLowerCase();
			if (name.endsWith(".html") || name.endsWith(".htm")) {
				return true;
			} else if (name.endsWith(".gif") || name.endsWith(".jpg")
					|| name.endsWith(".jpeg") || name.endsWith(".png")) {
				return true;
			} else {
				return false;
			}
		}
	};

	public ZipExpress(final String dest, final InputStream ins) {
		this(dest, ins, DEFAULT_FILE_FILTER);
	}
	
	public ZipExpress(final String dest, final InputStream ins,
			final FileFilter filter) {
		this(dest,ins,filter,System.identityHashCode(Thread.currentThread()));
	}

	/**
	 * 
	 * @param dest
	 *            ���浽��Ŀ��Ŀ¼
	 * @param ins
	 *            zip��ʽ��ѹ���ļ�InputStream,�������ڷ������ý���󱻹ر�, �����������ĸ�ʽ��zip
	 * @param filter
	 * @throws RuntimeException
	 *             �ڽ�ѹ�Ĺ�̷����쳣���׳�RuntimeException
	 */
	public ZipExpress(final String dest, final InputStream ins,
			final FileFilter filter,final int th) {
		if (dest == null) {
			throw new IllegalArgumentException("dest is null");
		}
		if (ins == null) {
			throw new IllegalArgumentException("ins is null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("filter is null");
		}
		this.destDir = dest;
		this.ins = ins;
		this.fileFilter = filter;
		this.th = th;
		fileNameHas = new HashMap();
		this.dealJarReal();
	}

	/**
	 * ����������,��zip ���е��ļ���Ŀ¼��ʽ��ѹ��һָ����destDirĿ¼��
	 * 
	 * @return
	 */
	private void dealJarReal() {
		ZipInputStream jins = null;

		boolean isZip = false;
		try {
			jins = new ZipInputStream(ins);
			ZipEntry jarentry = null;
			byte[] datebuff = new byte[MAX_BUFF];

			long allready_read = 0;
			long dataSize = 0;

			int ava = 0;
			int readbyte = 0;			
			while ((ava = jins.available()) > 0) {
				jarentry = jins.getNextEntry();
				if (jarentry == null) {
					break;
				} else {
					isZip = true;
				}

				if (jarentry.isDirectory())
					continue;

				String fileName = null;
				String destFileName = null;

				fileName = jarentry.getName();
				fileName = new String(fileName.getBytes("UTF-8"));
				dataSize = jarentry.getSize();

				if (dataSize > 0) {
					allready_read = 0;
					destFileName = getCurrentDestDir()
							+ modifyPathSep(fileName);
					File destFile = new File(destFileName);
					log.info(destFileName);
					if (!fileFilter.accept(destFile)) {
						if (log.isWarnEnabled()) {
							log.warn("we cant't accept this file type:"
									+ destFile.getName());
						}
						continue;
					}
					if (destFile.exists()) {
						destFile.delete();
					}
					while (allready_read < dataSize) {
						readbyte = jins.read(datebuff, 0, MAX_BUFF);
						allready_read += readbyte;
						append2File(datebuff, readbyte, destFile);
					}
					try{
						String deststr = new String(destFile.toString().getBytes("UTF-8"));
						destFile = new File(deststr);
					}catch(Exception e){
						log.error("transfer file name error",e);
					}
					fileNameHas.put(fileName, destFile);

				}
			}
			if (!isZip) {
				throw new RuntimeException("zip format error");
			}
		} catch (Exception e) {
			throw new RuntimeException("dealJarReal error", e);
		} finally {
			if (jins != null) {
				try {
					jins.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * �����ڵ�ǰ�߳�����ɵ��ļ���Ŀ¼,���Ǳ����,����Ӳ�̻ᱻ������ռ��
	 */
	public void clearDestDir() {
		String dir = getCurrentDestDir();
		if (isWin()) {
			log.info("cmd /C \"rmdir /Q /S " + dir + "\"");
			localExec("cmd /C \"rmdir /Q /S " + dir + "\"");
		} else {
			log.error("rm -rf " + dir);
			localExec("rm -rf " + dir);
		}
	}

	/**
	 * ִ�б�������
	 * 
	 * @param command
	 *            ��ִ�е�����
	 */
	private void localExec(String command) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(command);
			//process.waitFor();
		} catch (Exception e) {
			log.error("localExec",e);			
		}
	}

	public static String getFileRealName(String str) {
		if (str == null)
			return str;
		int pos = str.indexOf(FILE_MIDDLE);
		if (pos >= 0) {
			return str.substring(pos + FILE_MIDDLE.length());
		}
		return str;
	}

	public static String getFileName(String pathstr_1) {
		try {
			if (pathstr_1 == null)
				return pathstr_1;
			if (FILE_SEP.equals("\\")) {
				pathstr_1 = pathstr_1.replace('/', '\\');
			} else if (FILE_SEP.equals("/")) {
				pathstr_1 = pathstr_1.replace('\\', '/');
			}
			int pos = pathstr_1.lastIndexOf(FILE_SEP);
			if (pos >= 0)
				return pathstr_1.substring(pos + 1);
			return pathstr_1;
		} catch (Exception e) {
			e.printStackTrace();
			return pathstr_1;
		}
	}

	private String modifyPathSep(String pathstr) {
		if (pathstr == null)
			return pathstr;
		if (FILE_SEP.equals("\\")) {
			pathstr = pathstr.replace('/', '\\');
		} else if (FILE_SEP.equals("/")) {
			pathstr = pathstr.replace('\\', '/');
		}
		return pathstr;
	}

	public static void append2File(byte[] date, int bytelen, File destfile) {
		FileOutputStream fs = null;
		try {
			String parent = destfile.getParent();
			if (parent != null) {
				File parentObj = new File(parent);
				if (!parentObj.exists()) {
					if (!parentObj.mkdirs()) {
						throw new RuntimeException("can't create dirs for '"
								+ destfile + "'");
					}
				}
			}
			fs = new FileOutputStream(destfile, true);
			fs.write(date, 0, bytelen);
			fs.flush();
		} catch (Exception e) {
			throw new RuntimeException("append2File to [" + destfile
					+ "] error", e);
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static String osVersion() {
		return System.getProperty("os.name");
	}

	public static boolean isWin() {
		String osver = osVersion();
		osver = osver.toLowerCase();
		if (osver.indexOf("win") >= 0)
			return true;
		else
			return false;
	}

	private String getCurrentDestDir() {		
		return modifyPathSep(destDir) + FILE_SEP + th + FILE_SEP;
	}

	public int getFileCount() {
		return fileNameHas.size();
	}

	public Map getFiles() {
		return Collections.unmodifiableMap(fileNameHas);
	}

	public static void main(String[] args) throws IOException {
		InputStream in = null;
		ZipExpress jp = null;
		try {
			in = new FileInputStream("E:\\music_index3_0720.zip");
			jp = new ZipExpress("c:\\a\\b", in);
			System.out.println("fileCount:" + jp.getFileCount());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (jp != null) {
				jp.clearDestDir();
			}
		}
	}

}
