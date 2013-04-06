//package net.pusuo.cms.server.file;
//
//import net.pusuo.cms.core.bean.Channel;
//import net.pusuo.cms.core.bean.EntityItem;
//import net.pusuo.cms.core.bean.Subject;
//import net.pusuo.cms.server.ItemProxy;
//import net.pusuo.cms.server.core.Channel;
//import net.pusuo.cms.server.core.EntityItem;
//import net.pusuo.cms.server.core.Subject;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//public class EntityStoreRule {
//    private static final Log LOG = LogFactory.getLog(EntityStoreRule.class);
//
//    /**
//     * 得到实体的URL存储规则在实体被创建的时候调用
//     * format:http://news.hexun.com/guoneixinwen.shtml
//     */
//    public static String getURL(T entity, String ext)
//            throws Exception {
//        if (entity == null) return "";
//
//        int subtype = -1;
//        int psubid = -1;
//        String shortName = entity.getShortname();
//
//        if (entity.getType() == EntityItem.SUBJECT_TYPE) {
//            subtype = ((Subject) entity).getSubtype();
//            psubid = getPSubjectID(entity);
//        }
//
//        String domain = "http://";
//
//        if (entity.getType() == EntityItem.PICTURE_TYPE) {
//            domain += StoreRule.PICTUREDOMAIN;
//        } else if (entity.getType() == EntityItem.VIDEO_TYPE) {
//            domain += StoreRule.MUMEDAIDOMAIN;
//        } else {
//            Channel channel = (Channel) ItemProxy.getInstance().get(new Integer(entity.getChannel()), Channel.class);
//            domain += channel.getName();
//        }
//
//        return domain + StoreRule.getInstance().entityRule(entity.getId(), entity.getType(), entity.getTime(), subtype, shortName, psubid, ext);
//    }
//
//    /**
//     * 根据子专题的得到这个子专题从属的父专题ID
//     */
//    private static int getPSubjectID(EntityItem item) {
//        int psubid = -1;
//
//        try {
//            if (item == null || item.getPid() <= 0) return psubid;
//
//            psubid = item.getId();
//            while (item != null && item.getId() > 0) {
//                if (item.getType() == EntityItem.SUBJECT_TYPE) {//类型为专题
//                    if (((Subject) item).getSubtype() == 1) {//子类型为专题
//                        psubid = item.getId();
//                        break;
//                    }
//                }
//                item = (EntityItem) ItemProxy.getInstance().get(new Integer(item.getPid()), item.getClass());
//            }
//
//        } catch (Exception e) {
//            LOG.error("EntityStoreRule.getPSubjectID error. " + e.toString());
//        }
//
//        return psubid;
//    }
//
//
//}
