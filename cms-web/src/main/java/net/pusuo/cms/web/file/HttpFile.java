//package net.pusuo.cms.server.file;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.io.*;
//import java.net.*;
//
//public class HttpFile {
//    private static final Log LOG = LogFactory.getLog(HttpFile.class);
//
//    public static String read(String file) {
//        try {
//            BufferedInputStream result = new BufferedInputStream((new URL(file)).openStream());
//            byte[] cont = new byte[1024];
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            int conlen;
//            while ((conlen = result.read(cont)) >= 0) {
//                baos.write(cont, 0, conlen);
//            }
//            result.close();
//            return new String(baos.toByteArray());
//        } catch (Exception e) {
//            LOG.error(e);
//            return null;
//        }
//    }
//
//    public static String getPost(String weburl, String inputData) {
//        String returnCode = "";
//        StringBuffer sb = new StringBuffer();
//
//        try {
//            URL url = new URL(weburl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setAllowUserInteraction(false);
//            conn.setUseCaches(false);
//            BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
//            byte[] bdat = inputData.getBytes();
//            out.write(bdat, 0, bdat.length);
//            out.flush();
//            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
//
//            byte[] buff = new byte[2048];
//            int bytesRead;
//            // Simple read/write loop.
//            while (-1 != (bytesRead = in.read(buff, 0, buff.length))) {
//                sb.append(new String(buff, 0, bytesRead));
//            }
//
//            returnCode = sb.toString();
//        } catch (Exception e) {
//            System.out.println("Exception:" + sb.toString());
//        }
//
//        return returnCode;
//    }
//}
