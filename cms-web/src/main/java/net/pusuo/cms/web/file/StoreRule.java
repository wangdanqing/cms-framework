//package net.pusuo.cms.server.file;
//
//import net.pusuo.cms.server.Configuration;
//import net.pusuo.cms.server.core.EntityItem;
//import net.pusuo.cms.server.util.Util;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.sql.Timestamp;
//
//
///**
// * StoreRule是生成页面和碎片存储规则的核心类
// *
// * @author XuLin
// * @version 1.0
// * @since CMS1.0
// */
//public class StoreRule {
//    private static final Log LOG = LogFactory.getLog(StoreRule.class);
//
//    public static final String PICTUREROOT = "img";
//
//    public static final String MUMEROOT = "video";
//
//    public static final String PICTUREDOMAIN = Configuration.getInstance().get("cms4.file.picture.domain");
//
//    public static final String MUMEDAIDOMAIN = Configuration.getInstance().get("cms4.file.video.domain");
//
//    private static StoreRule storerule = null;
//
//    static {
//        if (LOG.isInfoEnabled()) {
//            LOG.info("PICTUREDOMAIN:" + PICTUREDOMAIN);
//            LOG.info("MUMEDAIDOMAIN :" + MUMEDAIDOMAIN);
//        }
//    }
//
//    public static StoreRule getInstance() {
//        if (storerule == null) {
//            try {
//                synchronized (StoreRule.class) {
//                    if (storerule == null) {
//                        storerule = new StoreRule();
//                    }
//                }
//            } catch (Exception e) {
//                LOG.error("unable to create ItemProxy instance ."
//                        + e.toString());
//            }
//        }
//
//        return storerule;
//    }
//
//    /**
//     * 实体存储规则-homepage,column,sub column,subject,sub subject,news,picture format
//     * homepage: /news.shtml column: /guonei.shtml sub column: shehui.shtml
//     * subject: /s2004/keji.shtml sub subject: /s2004/3654/s2xxxxxx.shtml news:
//     * /20040811/n2xxxxxx.shtml picture: /20040811/Img2xxxxxx.*
//     */
//    public static String entityRule(int entityID, int entityType,
//                                    Timestamp entityTime, int subtype, String shortName, int psubject,
//                                    String ext) throws Exception {
//        try {
//            return entityRule(entityID, entityType, 0, entityTime, subtype,
//                    shortName, psubject, ext);
//        } catch (Exception e) {
//            LOG.error(e);
//            throw e;
//        }
//    }
//
//    public static String entityRule(int entityID, int entityType,
//                                    int entityChannel, Timestamp entityTime, int subtype,
//                                    String shortName, int psubject, String ext) throws Exception {
//        try {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug(entityID + "  " + entityType + "  " + entityChannel
//                        + "  " + entityTime + " " + subtype + "  " + shortName
//                        + "  " + psubject + "  " + ext);
//            }
//            if (entityID < 0 || entityType < 0 || entityTime == null
//                    || ext.equals(""))
//                return "";
//
//            StringBuffer filesb = new StringBuffer();
//            filesb.append("/");
//
//            switch (entityType) {
//                case EntityItem.SUBJECT_TYPE: // Subject
//                {
//                    switch (subtype) {
//                        // format: /2004/shortname.shtml
//                        case 1: // 子类型为专题
//                        {
//                            // 子类型为专题的规则是：
//                            // 如果存在短名字，使用短名字做为存储规则
//                            // 否则使用s加上ID为文件存储规则
//                            filesb.append(Util.formatTime(entityTime, "yyyy") + "/");
//
//                            if (shortName.trim().length() > 0) {
//                                filesb.append(shortName + "/index");
//
//                            } else {
//                                filesb.append(entityID);
//                            }
//                        }
//                        break;
//                        // format: /2004/last4(Psubject)/sid.shtml
//                        case 2: // 子类型为子专题
//                        {
//                            // 子类型为子专题的规则是：
//                            // 使用s加上ID为文件存储规则
//                            filesb.append(Util.formatTime(entityTime, "yyyy") + "/");
//
//                            String psubid = "" + psubject;
//                            psubid = ((psubid != null && psubid.length() >= 4) ? psubid
//                                    : String.valueOf(entityID));
//
//                            filesb.append(psubid.substring(psubid.length() - 4) + "/");
//                            if (shortName.trim().length() > 0) {
//                                filesb.append(shortName + "/index");
//                            } else {
//                                filesb.append(entityID + "/index");
//                            }
//                        }
//                        break;
//                        case 3: // 子类型为栏目
//                        case 4: // 子类型为子栏目
//                        {
//                            //栏目和子栏目规则相同，如果存在短名字，按照短名字合成
//                            //如果不存在短名字，那么按照统一的规则生成
//                            if (shortName.trim().length() > 0
//                                    && (subtype == 3 || subtype == 4)) {
//                                filesb.append(shortName + "/index");
//                            } else {
//                                filesb.append(entityID + "/index");
//                            }
//                        }
//                        default:
//                            break;
//                    }
//
//                    break;
//                }
//                case EntityItem.NEWS_TYPE: // News
//                {
//                    filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd") + "/");
//                    filesb.append(entityID);
//                    break;
//                }
//                case EntityItem.PICTURE_TYPE: // Picture
//                {
//                    filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd") + "/");
//                    filesb.append(entityID);
//
//                    break;
//                }
//                case EntityItem.VIDEO_TYPE: {
//                    //视频的存储规则:yyyy-MM-dd/XXXXXXXXX
//                    filesb.append(Util.formatTime(entityTime, "yyyy-MM-dd") + "/");
//                    filesb.append(entityID);
//                    break;
//                }
//                case EntityItem.HOMEPAGE_TYPE: // Homepage
//                {
//                    if (shortName.trim().length() > 0) {
//                        filesb.append(shortName);
//                    } else {
//                        filesb.append("h" + entityID);
//                    }
//                }
//                default:
//                    break;
//            }
//
//            filesb.append(".");
//            filesb.append(ext);
//            return filesb.toString();
//        } catch (Exception e) {
//            LOG.error(e);
//            throw e;
//        }
//    }
//
//    /**
//     * INC碎片存储规则 format homepage: /frag/entityID/tflink_e.inc column:
//     * /frag/entityID/tflink_e.inc sub column: /frag/entityID/tflink_e.inc
//     * subject: /s2004/frag/34/tflink_e.inc sub subject:
//     * /s2004/frag/34/tflink_e.inc news: /2005/09/16/34/tflink_e.inc
//     */
//    public static String fragRule(int entityID, int entityType,
//                                  Timestamp entityTime, int subtype, int psubject, int tflinkID)
//            throws Exception {
//        try {
//            if (entityID < 0 || entityType < 0 || tflinkID < 0)
//                return "";
//
//            StringBuffer filesb = new StringBuffer();
//
//            switch (entityType) {
//                case EntityItem.SUBJECT_TYPE: // Subject
//                {
//                    switch (subtype) {
//                        case 0:
//                            filesb.append("/frag/");
//                            filesb.append(entityType);
//                            filesb.append("/");
//                            filesb.append(Util.formatTime(entityTime, "MMyy"));
//                            filesb.append("/");
//                            String entityStr = entityID + "";
//                            int len = entityStr.length();
//                            filesb.append(entityStr.substring(len - 4, len - 2));
//                            filesb.append("/");
//                            filesb.append(tflinkID + "_" + entityID);
//                            filesb.append(".inc");
//                            break;
//
//                        // format: /2004/frag/34/tflink_e.inc
//                        case 1: //子类型为专题、子专题
//                        case 2: {
//                            filesb.append("/");
//                            filesb.append(Util.formatTime(entityTime, "yyyy")
//                                    + "/frag/");
//
//                            String psubid = "" + psubject;
//                            psubid = ((!psubid.equals("") && psubid.length() >= 4) ? psubid
//                                    .substring(psubid.length() - 4)
//                                    : psubid);
//                            filesb.append(psubid.substring(0, 2));
//
//                            filesb.append("/");
//                            filesb.append(tflinkID + "_" + entityID);
//                            filesb.append(".inc");
//                        }
//                        break;
//                        // format: /frag/entityid/tflink_e.inc
//                        case 3: // 子类型为栏目
//                        case 4: // 子类型为子栏目
//                        {
//                            filesb.append("/frag/");
//                            filesb.append(entityID);
//
//                            filesb.append("/");
//                            filesb.append(tflinkID + "_" + entityID);
//                            filesb.append(".inc");
//                        }
//                        default:
//                            break;
//                    }
//
//                    break;
//                }
//                case EntityItem.HOMEPAGE_TYPE: // Homepage
//                {
//                    filesb.append("/frag/");
//                    filesb.append(entityID);
//
//                    filesb.append("/");
//                    filesb.append(tflinkID + "_" + entityID);
//                    filesb.append(".inc");
//                    break;
//                }
//                case EntityItem.NEWS_TYPE: // News
//                {
//                    filesb.append(Util.formatDatetime(entityTime, "/yyyy/MM/dd/"));
//                    filesb.append(("" + entityID).substring(5, 7));
//                    filesb.append("/");
//                    filesb.append(tflinkID + "_" + entityID);
//                    filesb.append(".inc");
//                    break;
//                }
//                default:
//                    break;
//            }
//
//            return filesb.toString();
//        } catch (Exception e) {
//            LOG.error(e);
//            throw e;
//        }
//    }
//
//    /**
//     * 修改之处:使用碎片名称替代原先规则中的碎片ID.
//     * 解决问题:同一碎片重新编译后,即使ID发生改变,碎片路径还是保持不变.这样原先的内容也不会丢失.
//     *
//     * @author Alfred.Yuan
//     */
//    public static String fragRule(int entityID, int entityType,
//                                  Timestamp entityTime, int subtype, int psubject, String tfName)
//            throws Exception {
//        try {
//            if (entityID < 0 || entityType < 0 || tfName == null || tfName.trim().length() == 0)
//                return "";
//            tfName = tfName.trim();
//
//            StringBuffer filesb = new StringBuffer();
//
//            switch (entityType) {
//                case EntityItem.SUBJECT_TYPE: // Subject
//                {
//                    switch (subtype) {
//                        case 0:
//                            filesb.append("/frag/");
//                            filesb.append(entityType);
//                            filesb.append("/");
//                            filesb.append(Util.formatTime(entityTime, "MMyy"));
//                            filesb.append("/");
//                            String entityStr = entityID + "";
//                            int len = entityStr.length();
//                            filesb.append(entityStr.substring(len - 4, len - 2));
//                            filesb.append("/");
//                            filesb.append(tfName + "_" + entityID);
//                            filesb.append(".inc");
//                            break;
//
//                        // format: /2004/frag/34/tflink_e.inc
//                        case 1: //子类型为专题、子专题
//                        case 2: {
//                            filesb.append("/");
//                            filesb.append(Util.formatTime(entityTime, "yyyy")
//                                    + "/frag/");
//
//                            String psubid = "" + psubject;
//                            psubid = ((!psubid.equals("") && psubid.length() >= 4) ? psubid
//                                    .substring(psubid.length() - 4)
//                                    : psubid);
//                            filesb.append(psubid.substring(0, 2));
//
//                            filesb.append("/");
//                            filesb.append(tfName + "_" + entityID);
//                            filesb.append(".inc");
//                        }
//                        break;
//                        // format: /2004/frag/entityid/tflink_e.inc
//                        case 3: // 子类型为栏目
//                        case 4: // 子类型为子栏目
//                        {
//                            filesb.append("/frag/");
//                            filesb.append(entityID);
//
//                            filesb.append("/");
//                            filesb.append(tfName + "_" + entityID);
//                            filesb.append(".inc");
//                        }
//                        default:
//                            break;
//                    }
//
//                    break;
//                }
//                case EntityItem.HOMEPAGE_TYPE: // Homepage
//                {
//                    filesb.append("/frag/");
//                    filesb.append(entityID);
//
//                    filesb.append("/");
//                    filesb.append(tfName + "_" + entityID);
//                    filesb.append(".inc");
//                    break;
//                }
//                case EntityItem.NEWS_TYPE: // News
//                {
//                    filesb.append(Util.formatDatetime(entityTime, "/yyyy/MM/dd/"));
//                    filesb.append(("" + entityID).substring(5, 7));
//                    filesb.append("/");
//                    filesb.append(tfName + "_" + entityID);
//                    filesb.append(".inc");
//                    break;
//                }
//                default:
//                    break;
//            }
//
//            return filesb.toString();
//        } catch (Exception e) {
//            LOG.error(e);
//            throw e;
//        }
//    }
//
//}
