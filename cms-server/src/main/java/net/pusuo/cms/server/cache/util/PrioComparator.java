package net.pusuo.cms.server.cache.util;

import net.pusuo.cms.server.cache.CmsSortItem;

public class PrioComparator implements java.util.Comparator, java.io.Serializable {
    private boolean reverse = false;

    public PrioComparator(boolean reverse) {
        this.reverse = reverse;
    }

    public int compare(Object o1, Object o2) {
        int ret = 0;
        CmsSortItem si1 = (CmsSortItem) o1;
        CmsSortItem si2 = (CmsSortItem) o2;
        ret = si1.getPriority() - si2.getPriority();
        if (ret == 0) {
            ret = si1.getTime().compareTo(si2.getTime());
        }
        if (reverse) {
            ret = -ret;
        }
        return ret;
    }
}
