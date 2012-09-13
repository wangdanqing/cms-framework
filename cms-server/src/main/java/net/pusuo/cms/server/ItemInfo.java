package net.pusuo.cms.server;

import net.pusuo.cms.server.auth.*;
import net.pusuo.cms.server.core.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ItemInfo {
    private static final Log log = LogFactory.getLog(ItemInfo.class);

    public static final Class[] ITEM_CLASSES = {
            EntityItem.class,
            Media.class,
            Template.class,
            Frag.class,
            Channel.class,
            Category.class,
            TypeItem.class,
            TFMap.class,
            User.class,
            Role.class,
            Realm.class,
            Perm.class,
            Group.class,
            CommonFrag.class,
            ExTFMap.class,
            Author.class,

            DelLog.class,
            ModLog.class,
            Magazine.class,
            MagazineSheet.class
    };

    public static final int SUBJECT_TYPE = 1;
    public static final int NEWS_TYPE = 2;
    public static final int PICTURE_TYPE = 3;
    public static final int HOMEPAGE_TYPE = 5;
    public static final int MEDIA_TYPE = 4;
    public static final int TEMPLATE_TYPE = 6;
    public static final int FRAG_TYPE = 7;
    public static final int CHANNEL_TYPE = 8;
    public static final int CATEGORY_TYPE = 9;
    public static final int TYPEITEM_TYPE = 10;
    public static final int USER_TYPE = 11;
    public static final int GROUP_TYPE = 12;
    public static final int ROLE_TYPE = 13;
    public static final int REALM_TYPE = 14;
    public static final int PERM_TYPE = 15;
    public static final int TFMAP_TYPE = 16;

    public static final int VIDEO_TYPE = 17;
    public static final int MAGAZINE_TYPE = 18;

    public static final int COMMONFRAG_TYPE = 100;
    public static final int EXTFMAP_TYPE = 101;
    public static final int AUTHOR_TYPE = 102;

    public static Class getItemClass(Class theClass) {
        Class ret = null;
        for (int i = 0; i < ITEM_CLASSES.length; i++) {
            if (ITEM_CLASSES[i].isAssignableFrom(theClass)) {
                ret = ITEM_CLASSES[i];
            }
        }
        /* disabled by Mark 2004.10.21
          if ( ret==null )
          {
              throw new IllegalStateException("ItemInfo unable to match this class["+theClass.getName()+"].");
          }
          */
        return ret;
    }

    public static Class getItemClass(String itemType) {
        try {
            if (itemType != null) {
                return (getItemClass(Integer.parseInt(itemType)));
            }
        } catch (NumberFormatException nfe) {
            log.error("getItemClass type[" + itemType + "] error. " + nfe.toString());
        }
        return null;
    }

    public static Class getItemClass(int itemType) {
        Class ret = null;
        switch (itemType) {
            case TYPEITEM_TYPE:
                ret = TypeItem.class;
                break;
            case SUBJECT_TYPE:
                ret = Subject.class;
                break;
            case NEWS_TYPE:
                ret = News.class;
                break;
            case PICTURE_TYPE:
                ret = Picture.class;
                break;
            case VIDEO_TYPE:
                ret = Video.class;
                break;
            case HOMEPAGE_TYPE:
                ret = EntityItem.class;
                break;
            case USER_TYPE:
                ret = User.class;
                break;
            case GROUP_TYPE:
                ret = Group.class;
                break;
            case ROLE_TYPE:
                ret = Role.class;
                break;
            case REALM_TYPE:
                ret = Realm.class;
                break;
            case PERM_TYPE:
                ret = Perm.class;
                break;
            case MEDIA_TYPE:
                ret = Media.class;
                break;
            case TEMPLATE_TYPE:
                ret = Template.class;
                break;
            case FRAG_TYPE:
                ret = Frag.class;
                break;
            case CHANNEL_TYPE:
                ret = Channel.class;
                break;
            case CATEGORY_TYPE:
                ret = Category.class;
                break;
            case TFMAP_TYPE:
                ret = TFMap.class;
                break;
            case COMMONFRAG_TYPE:
                ret = CommonFrag.class;
                break;
            case EXTFMAP_TYPE:
                ret = ExTFMap.class;
                break;
            case AUTHOR_TYPE:
                ret = Author.class;
                break;
            case MAGAZINE_TYPE:
                ret = Magazine.class;
                break;
            case 19:
                ret = MagazineSheet.class;
                break;
            default:
                ret = null;
                break;
        }
        return ret;
    }

    public static Item getItemByType(String itemType) {
        try {
            if (itemType != null) {
                return (getItemByType(Integer.parseInt(itemType)));
            }
        } catch (NumberFormatException nfe) {
            log.error("getItemByType type[" + itemType + "] error. " + nfe.toString());
        }
        return null;
    }

    public static Item getItemByType(int itemType) {
        Item ret = null;
        try {
            if (itemType > 0 && itemType <= 10) {
                ret = (Item) CoreFactory.getInstance().createItem(getItemClass(itemType));
            } else if (itemType >= 11 && itemType <= 15) {
                ret = (Item) AuthFactory.getInstance().createItem(getItemClass(itemType));
            } else if (itemType >= 16) {
                ret = (Item) CoreFactory.getInstance().createItem(getItemClass(itemType));
            }
        } catch (Exception e) {
            log.error("get item by type[" + itemType + "] error ." + e);
        }
        return ret;
    }

    public static boolean isEntity(String itemType) {
        try {
            if (itemType != null) {
                return (isEntity(Integer.parseInt(itemType)));
            }
        } catch (NumberFormatException nfe) {
            log.error("isEntity type[" + itemType + "] error. " + nfe.toString());
        }
        return false;
    }

    public static boolean isEntity(int itemType) {
        boolean ret = false;
        if (itemType == 1 || itemType == 2 || itemType == 3 || itemType == 5 || itemType == 17) {
            ret = true;
        }
        return ret;
    }

    public static boolean isEntity(Class theClass) {
        return theClass.isAssignableFrom(getEntityClass());
    }

    public static boolean isItem(String className) {
        boolean ret = false;
        try {
            ret = isItem(Class.forName(className));
        } catch (ClassNotFoundException cnfe) {
            log.error("isItem error:" + cnfe);
        }
        return ret;
    }

    public static boolean isItem(Class theClass) {
        boolean ret = false;
        if (getItemClass(theClass) != null) {
            ret = true;
        }
        return ret;
    }

    public static Class getEntityClass() {
        return EntityItem.class;
    }
}
