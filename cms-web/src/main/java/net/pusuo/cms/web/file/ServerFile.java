//package net.pusuo.cms.server.file;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.rmi.RemoteException;
//import java.rmi.server.UnicastRemoteObject;
//
//public class ServerFile extends UnicastRemoteObject implements FileInterface {
//
//    private static final long serialVersionUID = -1L;
//
//    private static final Log LOG = LogFactory.getLog(ServerFile.class);
//
//    private static ServerFile sf;
//
//    private String pageroot = null;
//    private String syncpageroot = null;
//
//    public static ServerFile getInstance() throws Exception, RemoteException {
//        if (sf == null)
//            sf = new ServerFile();
//        return sf;
//    }
//
//
//    public String read(String file) throws RemoteException {
//        try {
//            return LocalFile.read(pageroot + file);
//        } catch (Exception e) {
//            LOG.error(e);
//            return null;
//        }
//    }
//
//    public boolean write(String content, String file) throws RemoteException {
//        return write(content, file, true);
//    }
//
//    public boolean write(String content, String file, boolean sync)
//            throws RemoteException {
//        boolean suc = false;
//        try {
//            LocalFile.write(content, syncpageroot + file);
//            suc = LocalFile.write(content, pageroot + file);
//            if (sync) {
//                SyncList.add(pageroot + file);
//            }
//        } catch (Exception e) {
//            LOG.error(e);
//            return false;
//        }
//        return suc;
//    }
//
//    public boolean write(byte[] content, String file) throws RemoteException {
//        return write(content, file, true);
//    }
//
//    public boolean write(byte[] content, String file, boolean sync)
//            throws RemoteException {
//        boolean suc = false;
//        try {
//            LocalFile.write(content, syncpageroot + file);
//            suc = LocalFile.write(content, pageroot + file);
//            if (sync) {
//                SyncList.add(pageroot + file);
//            }
//        } catch (Exception e) {
//            LOG.error(e);
//            return false;
//        }
//        return suc;
//    }
//
//    public boolean delete(String file) throws RemoteException {
//        boolean suc = false;
//        try {
//            LocalFile.delete(syncpageroot + file);
//            suc = LocalFile.delete(pageroot + file);
//            SyncList.delete(pageroot + file);
//        } catch (Exception e) {
//            LOG.error(e);
//            return false;
//        }
//        return suc;
//    }
//
//    public String[] getFileList(String dir) throws RemoteException {
//        try {
//            return LocalFile.getFileList(pageroot + dir);
//        } catch (Exception e) {
//            LOG.error(e);
//            return null;
//        }
//    }
//
//    /*
//      * add by xulin at 2007.08.18 for only write a record to sync_list.dat for
//      * sync file to realserver
//      */
//    public void writesync(String file) throws RemoteException {
//        try {
//            SyncList.add(pageroot + file);
//        } catch (Exception e) {
//            LOG.error(e);
//        }
//    }
//
//}
