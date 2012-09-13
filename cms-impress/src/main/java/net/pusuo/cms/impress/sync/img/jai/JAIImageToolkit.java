package net.pusuo.cms.impress.sync.img.jai;

import com.sun.media.jai.codec.*;
import net.pusuo.cms.impress.sync.img.ImageToolkit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于JAI实现ImageToolkit
 *
 * @author agilewang
 */
public class JAIImageToolkit implements ImageToolkit {

    private static final Log log = LogFactory.getLog(JAIImageToolkit.class);

    private String src = null;

    private byte[] srcBytes = null;

    private String srcType = null;

    protected int imgMissTimeOut = 2000;

    private PlanarImage mi = null;

    private PlanarImage destMi = null;

    private JPEGEncodeParam encodeParam = null;

    private SeekableStream originalStream = null;

    private static final Map /* <String, Color> */colors;

    static {
        // 初始化颜色表
        colors = initColors();
    }

    public JAIImageToolkit() {
        encodeParam = new JPEGEncodeParam();
        encodeParam.setQuality(0.85F);
    }

    /**
     * 读取图片,完成初始化的工作,每个实例只能调用此方法一次
     */
    public boolean readImage() {
        if (this.mi != null) {
            throw new IllegalStateException("Image already loaded.");
        }
        boolean result = false;
        /*
           * 先从文件中加载图片,如果没有设置文件,则从数组中加载,如果还是没有只好抛出异常了
           *
           */
        if (this.src != null) {
            result = readImageFromFile();
        } else if (this.srcBytes != null) {
            result = readImageFromBytes();
        } else {
            throw new IllegalArgumentException(
                    "The image file src or image bytes is excepted.");

        }
        return result;
    }

    /**
     * 从字节数组中加载图片
     *
     * @return
     */
    private boolean readImageFromBytes() {
        boolean result = false;
        try {
            ByteArraySeekableStream stream = new ByteArraySeekableStream(
                    this.srcBytes);
            readImageFromStream(stream);
            result = true;
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Load image from stream error.", e);
            }
        }
        return result;
    }

    private boolean readImageFromFile() {
        File imgFile = new File(this.src);
        if (!imgFile.isFile() || !imgFile.exists()) {
            if (log.isErrorEnabled()) {
                log.error("Read image" + this.src + " not exit,sleep "
                        + imgMissTimeOut + " ms.. ");
            }
            if (imgMissTimeOut > 0) {
                try {
                    Thread.sleep(imgMissTimeOut);
                } catch (InterruptedException e1) {

                }
            }
        }

        boolean result = false;
        if (imgFile.isFile() && imgFile.exists()) {
            try {
                SeekableStream stream = null;

                stream = new FileSeekableStream(imgFile);
                readImageFromStream(stream);
                if (log.isInfoEnabled()) {
                    log.info("Load image for [" + this.src + "] success.");
                }
                result = true;
            } catch (Throwable e) {
                if (log.isErrorEnabled()) {
                    log.error("Load image for [" + this.src + " fail]", e);
                }
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error(this.src + " is not a file.");
            }
        }
        return result;
    }

    private void readImageFromStream(SeekableStream stream) {
        if (originalStream != null) {
            try {
                originalStream.close();
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("close originalStream error", e);
                }
            }
            originalStream = null;
        }
        originalStream = new MemoryCacheSeekableStream(stream);
        this.srcType = getFormat(originalStream);
        try {
            originalStream.seek(0);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("readImageFromStream error", e);
            }
        }
        mi = JAI.create("stream", originalStream);
    }

    /**
     * 分析图片的类型
     *
     * @param stream
     * @return 返回图片的类型
     */
    private String getFormat(SeekableStream stream) {
        String format = null;
        String[] formatNames = ImageCodec.getDecoderNames(stream);
        if (formatNames != null) {
            for (int i = 0; i < formatNames.length; i++) {
                ImageCodec codec = ImageCodec.getCodec(formatNames[i]);
                int numHeaderBytes = codec.getNumHeaderBytes();
                byte[] headerBytes = new byte[numHeaderBytes];
                try {
                    stream.readFully(headerBytes);
                    if (codec.isFormatRecognized(headerBytes)) {
                        format = formatNames[i];
                        break;
                    }
                    stream.seek(0L);
                } catch (Exception e) {
                    break;
                }
            }
        }
        return format;
    }

    /**
     * 缩放图片
     */
    public boolean scaleTo(int width, int heigth, boolean isSeq) {
        optCheck();
        Object src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }
        float xScale = ((float) width) / this.mi.getWidth();
        float yScale = ((float) heigth) / this.mi.getHeight();
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src); // The source image
        pb.add(xScale); // The xScale
        pb.add(yScale); // The yScale
        pb.add(0.0F); // The x translation
        pb.add(0.0F); // The y translation
        pb.add(new InterpolationNearest()); // The interpolation
        PlanarImage oldDestMi = destMi;
        destMi = JAI.create("scale", pb, null);
        if (oldDestMi != null) {
            oldDestMi.dispose();
            oldDestMi = null;
        }
        return true;
    }

    /**
     * 剪裁图片
     */
    public boolean crop(int x, int y, int width, int height, boolean isSeq) {
        optCheck();
        Object src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src); // The source image
        pb.add((float) x);
        pb.add((float) y);
        pb.add((float) width);
        pb.add((float) height);
        pb.add(new InterpolationNearest()); // The interpolation
        PlanarImage oldDestMi = destMi;

        destMi = JAI.create("crop", pb, null);
        if (oldDestMi != null) {
            oldDestMi.dispose();
            oldDestMi = null;
        }
        return true;
    }


    /**
     * 增加文字水印
     *
     * @param op
     * @param isSeq
     */
    public void addWatermarkText(WatermarkTextOp op, boolean isSeq) {
        optCheck();
        if (op == null) {
            throw new IllegalArgumentException("WatermarkTextOp is excepted.");
        }
        PlanarImage src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }

        Font font = op.getFont();
        float alaph = op.getAlapha();
        String text = op.getText();
        int x = 0;
        int y = 0;

        TiledImage ti = new TiledImage(src, false);

        Graphics2D gh = ti.createGraphics();
        if (font != null) {
            gh.setFont(font);
            op.setImageH(ti.getHeight());
            op.setImageW(ti.getWidth());
            Dimension dim = op.calTextPostition(gh);
            x = dim.width;
            y = dim.height;
        }
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                alaph);
        gh.setComposite(ac);
        gh.setColor(getColor(op.getColor(), Color.WHITE));
        gh.drawString(text, x, y);
        this.destMi = ti;
    }

    /**
     * 增加图片水印
     *
     * @param op
     * @param isSeq
     */
    public void addWatermarkImage(WatermarkImgOp op, boolean isSeq) {
        optCheck();
        PlanarImage src = this.mi;
        if (isSeq) {
            src = this.destMi;
        }
        if (src == null) {
            throw new IllegalArgumentException("No src for this operation");
        }

        float alaph = op.getAlapha();
        byte[] markImage = op.getMarkImage();
        TiledImage ti = new TiledImage(src, false);
        Graphics2D gh = ti.createGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                alaph);
        gh.setComposite(ac);
        PlanarImage logo = JAI.create("stream", new MemoryCacheSeekableStream(
                new ByteArrayInputStream(markImage)));
        Dimension dim = op.calImagePostition(logo.getWidth(), logo.getHeight(),
                ti.getWidth(), ti.getHeight());
        gh.drawImage(logo.getAsBufferedImage(), dim.width, dim.height, null);
        this.destMi = ti;
    }

    /**
     * 将图片以JPEG格式写入到写入到文件中去
     */
    public boolean writeImageTo(String file) {
        if (file == null) {
            throw new IllegalArgumentException("No file to be writed.");
        }
        FileOutputStream out = null;
        boolean result = false;
        try {
            out = new FileOutputStream(file);
            result = this.write(out);
        } catch (Throwable te) {
            if (log.isErrorEnabled()) {
                log.error("Enocde error", te);
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * 将图片以JPEG格式写入到一个输出流中
     *
     * @param stream
     * @return
     */
    public boolean writeToStream(OutputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("out stream is excepted");
        }
        return write(stream);
    }

    private boolean write(OutputStream stream) {
        PlanarImage toWrite = null;
        if (this.destMi != null) {
            toWrite = this.destMi;
        } else if (this.mi != null) {
            toWrite = this.mi;
        } else {
            throw new IllegalStateException("Image not processed.");
        }
        boolean result = false;
        BufferedImage bufferedImage = toWrite.getAsBufferedImage();
        try {
            ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG",
                    stream, encodeParam);
            encoder.encode(bufferedImage);
            result = true;
        } catch (Throwable te) {
            if (log.isErrorEnabled()) {
                log.error("Enocde error", te);
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return result;
    }

    private void optCheck() {
        if (this.mi == null) {
            throw new IllegalStateException("Image not loaded.");
        }
    }

    public void finalize() throws Throwable {
        this.release();
        super.finalize();
    }

    public int getImgMissTimeOut() {
        return this.imgMissTimeOut;
    }

    public void setImgMissTimeOut(int imgMissTimeOut) {
        this.imgMissTimeOut = imgMissTimeOut;
    }

    public void setImageSrc(String src) {
        this.src = src;
        this.srcBytes = null;
    }

    public String getImageSrc() {
        return this.src;
    }

    public void setImageSrcBytes(byte[] bytes) {
        this.srcBytes = bytes;
        this.src = null;
    }

    public byte[] getImageSrcBytes() {
        return this.srcBytes;
    }

    public String getImageSrcType() {
        return this.srcType;
    }

    public void release() {
        this.src = null;
        this.srcBytes = null;
        if (originalStream != null) {
            try {
                originalStream.close();
            } catch (IOException e) {
                // ingore
            }
        }

        if (this.mi != null) {
            this.mi.dispose();
            this.mi = null;
        }
        if (this.destMi != null) {
            this.destMi.dispose();
            this.destMi = null;
        }
    }

    public int getSrcHeight() {
        return this.mi.getHeight();
    }

    public int getSrcWidth() {
        return this.mi.getWidth();
    }

    /**
     * 遍历java.awt.Color中的定义的Color常量,将它们根据名字缓存起来
     *
     * @return
     */
    private static Map /* <String, Color> */initColors() {
        Map/* <String, Color> */colors = new HashMap /* <String, Color> */();
        Class clazz = Color.class;
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                int modif = field.getModifiers();
                if (Modifier.isFinal(modif) && Modifier.isPublic(modif)
                        && Modifier.isStatic(modif) && field.getType() == clazz) {
                    try {
                        colors.put(field.getName().toLowerCase(), (Color) field
                                .get(clazz));
                    } catch (Throwable e) {
                        // ingore
                    }
                }
            }
        } catch (Throwable te) {
            if (log.isErrorEnabled()) {
                log.error("Init color table error.", te);
            }
        }
        return colors;
    }

    /**
     * 根据颜色名称找到对应的Color对象,如果没有则调用Color.getColor()来取得相应的Color
     * 如果没有找到,返回defaultColor
     *
     * @param name
     * @param defaultColor
     * @return
     */
    private static Color getColor(String name, Color defaultColor) {
        if (name == null) {
            return defaultColor;
        }
        Color color = (Color) colors.get(name);
        if (color == null) {
            color = Color.getColor(name);
            if (color == null) {
                color = defaultColor;
            }
        }
        return color;
    }
}
