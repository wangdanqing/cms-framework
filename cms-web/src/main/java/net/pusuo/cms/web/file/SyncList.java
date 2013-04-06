//package net.pusuo.cms.server.file;
//
//import net.pusuo.cms.server.Configuration;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//public class SyncList {
//    private static final Log LOG = LogFactory.getLog(SyncList.class);
//    private static boolean write_sync_list = false;
//
//    private static String sync_list_file = null;
//    private static String sync_list_dir = null;
//
//    private static Integer intlock = null;
//
//    static {
//        try {
//            write_sync_list = Configuration.getInstance().getBoolean("cms4.file.sync.iswrite");
//            sync_list_file = Configuration.getInstance().get("cms4.file.sync.file");
//            sync_list_dir = Configuration.getInstance().get("cms4.file.sync.dir");
//            intlock = new Integer(0);
//        } catch (Exception e) {
//            write_sync_list = false;
//        }
//    }
//
//    public static void delete(String filename) {
//        if (!write_sync_list) return;
//        if (filename.indexOf(sync_list_dir) >= 0) return;
//        StringBuffer sb = new StringBuffer();
//        sb.append((long) (System.currentTimeMillis() / 1000));
//        sb.append("\t");
//        sb.append("-");
//        sb.append("\t");
//        sb.append(filename);
//        sb.append("\r\n");
//        synchronized (intlock) {
//            LocalFile.write(sb.toString(), sync_list_file, true);
//        }
//    }
//
//    public static void add(String filename) {
//        if (!write_sync_list) return;
//        if (filename.indexOf(sync_list_dir) >= 0) return;
//        StringBuffer sb = new StringBuffer();
//        sb.append((long) (System.currentTimeMillis() / 1000));
//        sb.append("\t");
//        sb.append("+");
//        sb.append("\t");
//        sb.append(filename);
//        sb.append("\r\n");
//        synchronized (intlock) {
//            LocalFile.write(sb.toString(), sync_list_file, true);
//        }
//    }
//}
