//package net.pusuo.cms.server.file;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.io.*;
//
//public class LocalFile {
//    private static final Log LOG = LogFactory.getLog(LocalFile.class);
//
//    public static boolean delete(String f) {
//        try {
//            File file = new File(f);
//            file.delete();
//            return true;
//        } catch (Exception e) {
//            LOG.error("delete(String) String f:" + "f", e);
//            return false;
//        }
//    }
//
//    public static String read(String file) {
//        String ret = null;
//
//        File f = null;
//        BufferedInputStream result = null;
//        ByteArrayOutputStream baos = null;
//
//        try {
//            f = new File(file);
//            if (!f.exists()) {
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug(file + " does not exist.");
//                }
//                return ret;
//            } else if (!f.isFile()) {
//                //fix bug:�ж��Ƿ����ļ�,�����һ��Ŀ¼�Ǻ�Σ�յ�
//                LOG.warn(file + " is not a file.");
//                return ret;
//            }
//            result = new BufferedInputStream(new FileInputStream(f));
//            baos = new ByteArrayOutputStream();
//            byte[] cont = new byte[1024];
//            int conlen;
//            while ((conlen = result.read(cont)) >= 0) {
//                baos.write(cont, 0, conlen);
//            }
//            ret = new String(baos.toByteArray());
//        } catch (Exception e) {
//            LOG.error("read(String)  file:" + file, e);
//        } finally {
//            try {
//                if (result != null) result.close();
//                if (baos != null) baos.close();
//                f = null;
//            } catch (Exception e) {
//                LOG.error("read finally ", e);
//            }
//        }
//        return ret;
//    }
//
//    public static boolean write(String content, String file) {
//        try {
//            return write(content, file, false);
//        } catch (Exception e) {
//            LOG.error("write(String,String)  file=" + file + "   ", e);
//            return false;
//        }
//    }
//
//    public static boolean write(byte[] content, String file) {
//        boolean ret = false;
//
//        FileOutputStream fos = null;
//        try {
//            File filedir = new File(getPath(file));
//            if (!filedir.exists()) filedir.mkdirs();
//            fos = new FileOutputStream(file);
//            fos.write(content);
//            ret = true;
//        } catch (Exception e) {
//            LOG.error("write(byte,String) file=" + file, e);
//        } finally {
//            try {
//                if (fos != null) fos.close();
//            } catch (Exception e) {
//                LOG.error(e);
//            }
//        }
//        return ret;
//    }
//
//    public static boolean write(String content, String file, boolean append) {
//        boolean ret = false;
//        FileOutputStream fos = null;
//
//        try {
//            long t1 = System.currentTimeMillis();
//            File filedir = new File(getPath(file));
//            if (!filedir.exists()) filedir.mkdirs();
//            fos = new FileOutputStream(file, append);
//            fos.write(content.getBytes("UTF-8"));
//            long t2 = System.currentTimeMillis();
//            ret = true;
//        } catch (Exception e) {
//            LOG.error("write(String,String,boolean) file=" + file, e);
//            return false;
//        } finally {
//            try {
//                if (fos != null) fos.close();
//            } catch (Exception e) {
//                LOG.error(e);
//            }
//        }
//        return ret;
//    }
//
//    public static String getPath(String f) {
//        try {
//            return f.substring(0, f.lastIndexOf("/"));
//        } catch (Exception e) {
//            return "./";
//        }
//    }
//
//    public static String[] getFileList(String dir) {
//        try {
//            File parent = new File(dir);
//            if (!parent.isAbsolute() || !parent.isDirectory()) {
//                return null;
//            }
//            return parent.list();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//}
//
