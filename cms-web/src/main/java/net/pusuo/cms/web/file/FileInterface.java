package net.pusuo.cms.web.file;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote {
    public String read(String file) throws RemoteException;

    public boolean write(String content, String file, boolean sync)
            throws RemoteException;

    public boolean write(String content, String file) throws RemoteException;

    public boolean write(byte[] content, String file, boolean sync)
            throws RemoteException;

    public boolean write(byte[] content, String file) throws RemoteException;

    public boolean delete(String file) throws RemoteException;

    public String[] getFileList(String dir) throws RemoteException;

    public void writesync(String file) throws RemoteException;
}
