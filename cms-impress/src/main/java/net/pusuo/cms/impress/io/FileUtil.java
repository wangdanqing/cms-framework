package net.pusuo.cms.impress.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * 读写文件的常用工具
 *
 * @author agilewang
 */
public class FileUtil {

    private static final Log logger = LogFactory.getLog(FileUtil.class);

    /**
     * 删除文件
     *
     * @param f
     * @return 文件删除是否成功
     * @throws RuntimeException 如果f不是一个文件,那么会抛出一个异常
     */
    public static boolean delete(String f) {
        try {
            File file = new File(f);
            if (file.isFile()) {
                return file.delete();
            } else {
                throw new RuntimeException(String.format(
                        "can't delete no file %s", file));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e);
            }
        }
        return false;
    }

    /**
     * 读取文件的数据
     *
     * @param file
     * @return 字节数组, 如有异常, file 不是文件，则返回null
     */
    public static byte[] read(String file) {
        FileInputStream fin = null;
        BufferedInputStream bufferIn = null;
        ByteArrayOutputStream out = null;
        try {
            File f = new File(file);
            if (!f.exists()) {
                if (logger.isWarnEnabled()) {
                    logger.warn(file + " does not exist.");
                }
                return null;
            } else if (!f.isFile()) {
                if (logger.isWarnEnabled()) {
                    logger.warn(file + " is not a file.");
                }
                return null;
            }
            fin = new FileInputStream(f);
            bufferIn = new BufferedInputStream(fin);
            out = new ByteArrayOutputStream();
            byte[] cont = new byte[1024];
            int conlen = -1;
            while ((conlen = bufferIn.read(cont)) >= 0) {
                out.write(cont, 0, conlen);
            }
            return out.toByteArray();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e);
            }
        } finally {
            closeStream(fin);
            closeStream(bufferIn);
            closeStream(out);
        }
        return null;
    }

    public static boolean write(byte[] content, String file) {
        boolean ret = false;
        FileOutputStream fos = null;
        try {
            File filedir = new File(getPath(file));
            if (!filedir.exists())
                filedir.mkdirs();
            fos = new FileOutputStream(file);
            fos.write(content);
            ret = true;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            closeStream(fos);
        }
        return ret;
    }

    public static boolean write(String content, String file, boolean append) {
        boolean ret = false;
        FileOutputStream fos = null;

        try {
            File filedir = new File(getPath(file));
            if (!filedir.exists())
                filedir.mkdirs();
            fos = new FileOutputStream(file, append);
            fos.write(content.getBytes());
            ret = true;
        } catch (Exception e) {
            logger.error(e);
            return false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return ret;
    }

    private static String getPath(String f) {
        try {
            return f.substring(0, f.lastIndexOf(File.separator));
        } catch (Exception e) {
            return "." + File.separator;
        }
    }

    public static String[] getFileList(String dir) {
        try {
            File parent = new File(dir);
            if (!parent.isAbsolute() || !parent.isDirectory()) {
                return null;
            }
            return parent.list();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 关闭流,异常会被隐藏起来
     *
     * @param in
     */
    public static void closeStream(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("close in error", e);
                }

            }
        }
    }
}
