/*
 * 
 * @author chenqj
 * Created on 2004-8-19
 *
 */
package net.pusuo.cms.server.image;

import magick.*;
import net.pusuo.cms.server.file.ServerFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * @author chenqj
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class ServerImage extends UnicastRemoteObject implements ImageInterface {

    private static final long serialVersionUID = -1L;

    private static final Log log = LogFactory.getLog(ServerImage.class
            .getName());

    private static ServerImage si = null;

    /**
     * @throws java.rmi.RemoteException
     */
    private ServerImage() throws RemoteException {
        super();

    }

    public static ServerImage getInstance() throws RemoteException {
        if (si == null) {
            synchronized (ServerImage.class) {
                if (si == null)
                    si = new ServerImage();
            }
        }
        return si;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.hexun.cms.image.ImageInterface#genThumbnail(com.hexun.cms.image.ThumbnailInfo)
      */
    public boolean genThumbnail(String srcFilePath, int thumbWidth,
                                int thumbHeight, boolean keepProportion)
            throws RemoteException {
        if (log.isDebugEnabled()) {
            log.debug("genThumbnail src=" + srcFilePath + " width="
                    + thumbWidth + " height=" + thumbHeight
                    + " keep proportion=" + keepProportion);
        }

        MagickImage mi = null;

        log.debug("Read source image file");
        try {
            mi = readImage(srcFilePath);
            if (mi == null) throw new Exception();
        } catch (Exception re) {
            String estr = "Read source image file " + srcFilePath
                    + " failed " + re;
            log.error(estr);
            return false;
        }

        MagickImage scaledMi = null;
        String thumbFilePath = new ImageRule()
                .getThumbnailFileName(srcFilePath);

        try {
            if (keepProportion) {
                int srcWidth = mi.getDimension().width;
                int srcHeight = mi.getDimension().height;
                double xRatio = ((double) thumbWidth)
                        / srcWidth;
                double yRatio = ((double) thumbHeight)
                        / srcHeight;
                double factor = Math.min(xRatio, yRatio);
                thumbWidth = (int) (factor * srcWidth);
                thumbHeight = (int) (factor * srcHeight);
            }
            log.debug("thumbnail width=" + thumbWidth + " height="
                    + thumbHeight);

            scaledMi = mi.scaleImage(thumbWidth, thumbHeight);
            if (scaledMi == null) throw new Exception();

            byte[] scaledData = scaledMi
                    .imageToBlob(new ImageInfo());
            log.debug("write thumbnail to file " + thumbFilePath);
            writeImage(scaledData, thumbFilePath);
        } catch (Exception we) {
            String estr = "Generate thumbnail file "
                    + thumbFilePath + " failed " + we;
            log.error(estr);
            return false;
        }
        log.info("Generate thumbnail file from " + srcFilePath + " to "
                + thumbFilePath);
        return true;
    }

    public boolean genWatermarkImageT(TextWatermarkInfo info)
            throws RemoteException {
        if (log.isDebugEnabled()) {

            log.debug("genWatermarkImageT info=" + info);
        }

        String srcFilePath = info.getSrcFilePath();
        String markText = info.getMarkText();
        int offsetX = info.getOffsetX();
        int offsetY = info.getOffsetY();
        int placement = info.getPlacement();
        String fontName = info.getFontName();
        int fontSize = info.getFontSize();
        String fillColor = info.getFillColor();

        MagickImage srcImage = null;

        log.debug("Read source image file");
        try {
            srcImage = readImage(srcFilePath);
            if (srcImage == null) throw new Exception();
        } catch (Exception re) {
            String estr = "Read source image file " + srcFilePath
                    + " failed " + re;
            log.error(estr);
            return false;
        }

        String destFilePath = new ImageRule()
                .getWatermarkFileName(srcFilePath);
        Dimension od = calTextOffset(placement, offsetX, offsetY,
                fontSize);
        offsetX = od.width;
        offsetY = od.height;

        DrawInfo annotateInfo;
        try {
            annotateInfo = new DrawInfo(new ImageInfo());

            annotateInfo.setFont(fontName);
            annotateInfo.setPointsize(fontSize);
            annotateInfo.setText(markText);
            annotateInfo.setGravity(placement);

            annotateInfo.setFill(PixelPacket
                    .queryColorDatabase(fillColor));
            annotateInfo.setGeometry(getGeometry(offsetX, offsetY));
            srcImage.annotateImage(annotateInfo);

            writeImage(srcImage.imageToBlob(new ImageInfo(
                    destFilePath)), destFilePath);
        } catch (Exception e) {
            String estr = "write wartermark image file "
                    + destFilePath + " failed " + e;
            log.error(estr);
            return false;
        }
        log.info("Generate watermark file from " + srcFilePath + " to "
                + destFilePath);
        return true;
    }

    public boolean genWatermarkImageG(String srcFilePath,
                                      String markFilePath, int placement, int offsetX,
                                      int offsetY) throws RemoteException {
        if (log.isDebugEnabled()) {

            log.debug("genWatermarkImageG src=" + srcFilePath
                    + "markFilePath=" + markFilePath
                    + " placement=" + placement
                    + " offsetX=" + offsetX + " offsetY="
                    + offsetY);

        }

        MagickImage srcImage = null;
        MagickImage markImage = null;

        log.debug("Read source image file");
        try {
            srcImage = readImage(srcFilePath);
        } catch (Exception re) {
            String estr = "Read source image file " + srcFilePath
                    + " failed " + re;
            log.error(estr);
            return false;
        }

        log.debug("Read watermark image file");
        try {
            markImage = readImage(markFilePath);
        } catch (Exception re) {
            String estr = "Read wartermark image file "
                    + markFilePath + " failed " + re;
            log.error(estr);
            return false;
        }

        String destFilePath = new ImageRule()
                .getWatermarkFileName(srcFilePath);

        try {
            Dimension sd = srcImage.getDimension();
            Dimension md = markImage.getDimension();
            Dimension pos = calImgPostion(placement, offsetX,
                    offsetY, md.width, md.height, sd.width,
                    sd.height);

            srcImage.compositeImage(
                    CompositeOperator.OverCompositeOp,
                    markImage, pos.width, pos.height);
            writeImage(srcImage.imageToBlob(new ImageInfo(
                    destFilePath)), destFilePath);
        } catch (Exception e) {
            String estr = "write wartermark image file "
                    + destFilePath + " failed " + e;
            log.error(estr);
            return false;
        }
        log.info("Generate watermark file from " + srcFilePath + " to "
                + destFilePath);
        return true;
    }

    private MagickImage readImage(String imagePath) throws RemoteException,
            Exception {
        byte[] srcData = ServerFile.getInstance().read(imagePath)
                .getBytes();
        if (srcData == null)
            throw new IOException();
        return new MagickImage(new ImageInfo(imagePath), srcData);
    }

    public MagickImage getMagickImage(String file)
            throws Exception {
        try {
            ImageInfo info = new ImageInfo(file);
            MagickImage mi = new MagickImage(info);
            return mi;
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
    }

    public Dimension getDimension(String file)
            throws RemoteException {
        try {
            return getMagickImage(file).getDimension();
        } catch (Exception e) {
            return new Dimension(-1, -1);
        }
    }

    private void writeImage(byte[] data, String imagePath)
            throws RemoteException, Exception {
        ServerFile.getInstance().write(data, imagePath);
    }

    private Dimension calImgPostion(int placement, int offsetX,
                                    int offsetY, int sWidth, int sHeight, int lWidth,
                                    int lHeight) {

        int posX = offsetX;
        int posY = offsetY;

        if (placement == Placement.EastGravity
                || placement == Placement.CenterGravity
                || placement == Placement.WestGravity)
            posY = lHeight / 2 - sHeight / 2 + offsetY;
        if (placement == Placement.SouthEastGravity
                || placement == Placement.SouthGravity
                || placement == Placement.SouthWestGravity)
            posY = lHeight - sHeight - offsetY;

        if (placement == Placement.SouthGravity
                || placement == Placement.CenterGravity
                || placement == Placement.NorthGravity)
            posX = lWidth / 2 - sWidth / 2 + offsetX;

        if (placement == Placement.NorthEastGravity
                || placement == Placement.EastGravity
                || placement == Placement.SouthEastGravity)
            posX = lWidth - sWidth - offsetX;

        return new Dimension(posX, posY);
    }

    private Dimension calTextOffset(int placement, int offsetX,
                                    int offsetY, int fontSize) {

        int offX = offsetX;
        int offY = offsetY;

        /*
           * if (placement == Placement.SouthEastGravity || placement ==
           * Placement.SouthGravity || placement ==
           * Placement.SouthWestGravity)
           */
        offY = offsetY + fontSize;

        return new Dimension(offX, offY);
    }

    private String getGeometry(int posX, int posY) {
        StringBuffer geoStr = new StringBuffer();
        geoStr.append((posX >= 0 ? '+' : '-')).append(posX);
        geoStr.append((posY >= 0 ? '+' : '-')).append(posY);
        return geoStr.toString();
    }

    /*
      * Just For Test
      */
    public static void main(String[] args) throws RemoteException {
        ServerImage si = ServerImage.getInstance();
        String srcFilePath = "/home/chenqj/java/test/pic/src/Img221502455.jpg";

        String result = new ImageRule()
                .getThumbnailFileName(srcFilePath);
        System.out.println("thumbfile " + result);

        result = new ImageRule()
                .getThumbnailFileName("Img221502455.jpg");
        System.out.println("thumbfile " + result);

        Dimension pos;
        int offX = 50;
        int offY = 100;
        int w = 100;
        int h = 200;
        int height = 800;
        int width = 400;

        pos = si.calImgPostion(Placement.NorthWestGravity, offX, offY,
                w, h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.NorthGravity, offX, offY, w,
                h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.NorthEastGravity, offX, offY,
                w, h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.WestGravity, offX, offY, w, h,
                width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.CenterGravity, offX, offY, w,
                h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.EastGravity, offX, offY, w, h,
                width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.SouthWestGravity, offX, offY,
                w, h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.SouthGravity, offX, offY, w,
                h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);

        pos = si.calImgPostion(Placement.SouthEastGravity, offX, offY,
                w, h, width, height);
        System.out.println("posX=" + pos.width + " posY=" + pos.height);
    }
}
