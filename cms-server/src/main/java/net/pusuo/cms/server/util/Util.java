package net.pusuo.cms.server.util;

import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.ItemProxy;
import net.pusuo.cms.server.core.EntityItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public final class Util {
    private static final Log log = LogFactory.getLog(Util.class);

    public static String formatTime(Timestamp time, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        return formater.format(time);
    }

    public static String unicodeToGB2312(String str) {
        try {
            //return new String(str.getBytes("ISO_8859_1"), "GB2312");
            return new String(str.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("unicodeToGB2312 error. " + e.toString());
            return "";
        }
    }

    public static String GB2312ToUnicode(String str) {
        try {
            //return new String(str.getBytes("GB2312"), "ISO_8859_1");
            return new String(str.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("GB2312ToUnicode error. " + e.toString());
            return "";
        }
    }

    public static String unicodeToGBK(String str) {
        try {
            //return new String( str.getBytes("ISO_8859_1"), "GBK");
            return new String(str.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("unicodeToGBK error. " + e.toString());
            return "";
        }
    }

    public static String GBKToUnicode(String str) {
        try {
            //return new String( str.getBytes("GBK"), "ISO_8859_1" );
            return new String(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("GBKToUnicode error. " + e.toString());
            return "";
        }
    }

    public static String formatDatetime(Timestamp time, String pattern) {
        return formatDatetime(time.getTime(), pattern);
    }

    public static String formatDatetime(long time, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(new java.util.Date(time));
        } catch (IllegalArgumentException e) {
            log.error("format datetime pattern is invalid. " + e.toString());
            return "";
        } catch (Exception e) {
            log.error("format datetime error. " + e.toString());
            return "";
        }
    }

    /*
      * author:shijinkui
      * �������α�׼ʱ���ʽת��
      * time:2007-11-09T07:15:20.0000000+08:00 ���� 2007-10-17T09:43:00+08:00
      * rePattern:���ʱ���ʽ���磺yyyy-MM-dd HH:mm:ss
      * srcpattern:�����ʱ���ʽ
      * ע�⣺time��srcpattern��rePattern��������ͬ��ʱ���ʽ
      */
    public static String formatGreenwichTime(String time, String srcpattern, String rePattern) {

        SimpleDateFormat df = new SimpleDateFormat(srcpattern);
        SimpleDateFormat df2 = new SimpleDateFormat(rePattern);
        String times = null;

        java.util.Date dd = null;
        try {
            dd = df.parse(time);
            times = df2.format(dd);
        } catch (ParseException e) {
            log.error("ʱ���ʽת������" + e);
            log.error("����ȱʡ��ʱ���ʽ��");
            getDefaultGreenwichTime(time);
            e.printStackTrace();
        }
        return times;
    }

    /*
      * Convert the Greenwich time to default time format.
      * author:shijinkui
      * time:input time
      */
    public static String getDefaultGreenwichTime(String time) {
        java.util.Date date = new java.util.Date();
        String retime = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = df.parse(time);
            retime = df2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retime;
    }


    public static String httpRequest(String url) {
        String ret = null;
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URL rURL = new URL(url);
            in = rURL.openConnection().getInputStream();
            baos = new ByteArrayOutputStream();
            int len = 0;
            final int MAXLEN = 1024;
            byte[] b = new byte[MAXLEN];
            while ((len = in.read(b)) > 0) {
                baos.write(b, 0, len);
            }
            //ret = new String(baos.toString());
            ret = new String(baos.toString().getBytes(), "utf-8");
        } catch (MalformedURLException me) {
            log.error("MalformedURL error. " + me.toString());
        } catch (IOException ie) {
            log.error("io error. " + ie.toString());
        } catch (Exception e) {
            log.error("format datetime error. " + e.toString());
        } finally {
            try {
                if (in != null) in.close();
                if (baos != null) baos.close();
            } catch (Exception fe) {
                log.error("close error. " + fe.toString());
            }
        }
        return ret;
    }

    public static byte[] getContentByHttp(String url) {
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URL rURL = new URL(url);
            in = rURL.openConnection().getInputStream();
            baos = new ByteArrayOutputStream();
            int len = 0;
            final int MAXLEN = 1024;
            byte[] b = new byte[MAXLEN];
            while ((len = in.read(b)) > 0) {
                baos.write(b, 0, len);
            }
            return baos.toByteArray();
        } catch (MalformedURLException me) {
            log.error("MalformedURL error. " + me.toString());
            return new byte[0];
        } catch (IOException ie) {
            log.error("io error. " + ie.toString());
            return new byte[0];
        } catch (Exception e) {
            log.error("format datetime error. " + e.toString());
            return new byte[0];
        } finally {
            try {
                in.close();
                baos.close();
            } catch (Exception fe) {
                log.error("close error. " + fe.toString());
            }
        }
    }

    public static List getEntityAllParent(EntityItem item) {
        ArrayList parents = new ArrayList();
        int maxloop = 0;
        try {
            while (item != null && item.getPid() > 0) {
                item = (EntityItem) ItemProxy.getInstance().get(
                        new Integer(item.getPid()),
                        ItemInfo.getEntityClass());
                ++maxloop;
                if (maxloop > 20) break;
                parents.add(item);
            }
        } catch (Exception e) {
            log.error("getEntityAllParent error. " + e.toString());
        }
        return parents;

    }

    /**
     * @param origin origin file name. "/sports/2008/Img2222.jpg"
     * @param prefix such as "s_"
     * @return result file name with prefix. "/sports/2008/s_Img2222.jpg"
     */
    public static String addFilePrefix(String origin, String prefix) {
        StringBuffer result = new StringBuffer();
        int idx = origin.lastIndexOf("/");
        result.append(origin.substring(0, idx + 1)).append(prefix).append(
                origin.substring(idx + 1));
        return result.toString();
    }

    /**
     * @return format xx/xx/
     */
    public static String hashID(int entityID) {
        StringBuffer sb = new StringBuffer();
        String ts = "" + entityID;
        ts = ts.substring(ts.length() - 4);
        sb.append(ts.substring(2) + "/");
        sb.append(ts.substring(0, 2) + "/");

        return sb.toString();
    }

    public static String formatCh(String msg, int len) {
        byte[] bMsg = msg.getBytes();
        //���ȳ����ַ�ֱ�ӷ���
        if (bMsg.length <= len)
            return new String(msg);
        int end = 0;
        for (int i = 0; i < len; i++) {
            if (bMsg[i] > 0) //�ɼ�ASC�ַ�
                end++;
            else if (bMsg[i] < 0) //���ֵ�˫�ֽ��ַ�
            {
                end += 2;
                i++;
            }
        }
        return new String(bMsg, 0, end);
    }

    /**
     * build hashCode
     * using by auth,template..etc.
     * don't use in EntityItem
     */
    public static int buildHashCode(int itemID, int itemType) {
        int hashcode = 0;
        if (itemID > 10000000) {
            hashcode = itemID;
        } else {
            hashcode = itemType * 10000000 + itemID;
        }
        return hashcode;
    }

    /**
     * filter invalide xml character
     */
    public static String filterInvalidChars(String text) {
        if (text == null)
            return null;

        StringBuffer ret = new StringBuffer();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 0xFFFD                                            // Invalid Unicode Character
                    || (c < 0x20 && c != '\t' && c != '\n' && c != '\r')) {      // Invalid Xml Character
                continue;
            } else {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    public static String changeCode(String tmp) {
        String tmp1 = "";
        try {
            tmp1 = new String(tmp.getBytes("iso_8859_1"), "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            log.error("changeCode error");
        }
        return tmp1;
    }

    /**
     * RemoveHTML
     *
     * @return String
     */
    public static String RemoveHTML(String str) {
        java.util.regex.Pattern p = null; // ����������ʽ����
        java.util.regex.Matcher m = null; // ����ƥ�����

        String s = ""; // ���巵���ַ�
        if (str != null && !"".equals(str)) {
            // �滻�����з���Ҵ���
            p = java.util.regex.Pattern.compile("\r\n");
            m = p.matcher(str);
            s = m.replaceAll(""); // �滻���л��з�

            // �滻��SCRIPT����ֹ��ҳ����
            p = java.util.regex.Pattern.compile("(\\<.*?[\\>])",
                    java.util.regex.Pattern.CASE_INSENSITIVE);
            m = p.matcher(s);
            s = m.replaceAll(""); // �滻�����ַ����������������ַ�Ϊ��

        } else {
            return "";
        }
        return s;
    }
}
