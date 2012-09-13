/*
 * 
 * @author chenqj
 * Created on 2004-8-23
 *
 */
package net.pusuo.cms.server.image;

import java.awt.Dimension;
import java.rmi.RemoteException;

import java.rmi.Remote;

public interface ImageInterface extends Remote {

    public abstract boolean genThumbnail(String srcFilePath,
                                         int thumbWidth, int thumbHeight, boolean keepProportion)
            throws RemoteException;

    public abstract boolean genWatermarkImageT(TextWatermarkInfo info)
            throws RemoteException;

    public abstract boolean genWatermarkImageG(String srcFilePath,
                                               String markFilePath, int placement, int offsetX,
                                               int offsetY) throws RemoteException;

    public Dimension getDimension(String file) throws RemoteException;
}
