/**
 *
 */
package net.pusuo.cms.server.cache.filter;

import net.pusuo.cms.server.cache.*;
import net.pusuo.cms.server.cache.exception.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractPriorityFilter implements Filter {

    private static final Log log = LogFactory.getLog(AbstractPriorityFilter.class);

    private static int initSize = 500;

    static {
        try {
            initSize = CacheConfig.getInstance().getInt("cache.object.list.initsize", 500);
        } catch (Exception e) {
            initSize = 500;
        }
    }

    public abstract List filter(Query query);

    protected List filterWithComparator(int id, int type, int subtype, int minp, int maxp, int count, Comparator c)
            throws CacheException {

        long timeStart = System.currentTimeMillis();

        List items = ListManager.getInstance().getItemList(id, type, subtype);

        if (items == null)
            return null;

        List prio_list = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            CmsSortItem csi = (CmsSortItem) items.get(i);
            if (csi == null) {
                log.error("cmssortitem=null index=" + i);
                continue;
            }
            if (csi.getPriority() >= minp && csi.getPriority() <= maxp) {
                prio_list.add(csi);
            }
        }

        // 重载策略：当items的长度小于系统要求的初始化大小时，说明DB中没有足够的数据，此时不再重新加载
        int sizeNow = items.size();
        if (count > prio_list.size() && sizeNow >= initSize) {

            long t0 = System.currentTimeMillis();
            List db_list = CmsListFactory.getInstance().getByPriority(id, type, subtype, minp, maxp, count);
            long t1 = System.currentTimeMillis();
            log.info("No enough items, query database. id=" + id + " type=" + type + " subtype=" + subtype
                    + " count=" + count + " listsize=" + prio_list.size()
                    + " minp=" + minp + " maxp=" + maxp + " Time=" + (t1 - t0) + "ms");

            if (db_list == null) {
                log.error("filterWithComparator: cmsListFactory.getByPriority return null" + " id=" + id
                        + " type=" + type + " minp=" + minp + " maxp=" + maxp + " count=" + count);
            } else {
                try {
                    //更新ItemList的权重队列
                    ListManager.getInstance().updateItemListPrioData(id, type, subtype, db_list);
                } catch (Throwable te) {
                    log.error("update priodate error.", te);
                }
                prio_list = db_list;
            }
        }

        // 排序
        Collections.sort(prio_list, c);

        // 过滤
        int sizeBefore = prio_list.size();
        long startTime1 = System.currentTimeMillis();
        if (type == 2)
            CollectionUtils.filter(prio_list, new UniqueDescPredicate());
        long endTime1 = System.currentTimeMillis();
        int sizeAfter = prio_list.size();

        long timeEnd = System.currentTimeMillis();
        log.info("AbstractPriorityFilter: (id=" + id + ")(costSum:costFilter=" + (timeEnd - timeStart) +
                " : " + (endTime1 - startTime1) + ")(sizeBefore=" + sizeBefore +
                ")(sizeFilter=" + (sizeBefore - sizeAfter) + ")");

        int endIndex = Math.min(count, prio_list.size());
        return new ArrayList(prio_list.subList(0, endIndex));
    }

}