package net.pusuo.cms.server.util;

import net.pusuo.cms.server.core.EntityItem;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;


public class SearchUtil {
    private static final Log log = LogFactory.getLog(SearchUtil.class);

    ///////////////////////////////////////////////////////////////////////////

    public static CmsEntry item2Entry(EntityItem item) {
        if (item == null || item.getId() < 0)
            return null;

        CmsEntry entry = null;

        try {
            entry = new CmsEntry();
            Iterator iter = entry.getFieldTypes().keySet().iterator();
            while (iter.hasNext()) {
                String propertyName = (String) iter.next();

                Object propertyValue = null;
                if (PropertyUtils.isReadable(item, propertyName)) {
                    propertyValue = PropertyUtils.getProperty(item, propertyName);
                }

                if (propertyValue != null &&
                        !propertyName.equalsIgnoreCase("content") &&
                        PropertyUtils.isWriteable(entry, propertyName)) {
                    PropertyUtils.setProperty(entry, propertyName, propertyValue);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        return entry;
    }

}
