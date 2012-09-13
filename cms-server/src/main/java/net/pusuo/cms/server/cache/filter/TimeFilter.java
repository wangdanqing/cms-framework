/**
 *
 */
package net.pusuo.cms.server.cache.filter;

import net.pusuo.cms.server.cache.Filter;
import net.pusuo.cms.server.cache.ListManager;
import net.pusuo.cms.server.cache.Query;
import net.pusuo.cms.server.cache.util.TimeComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alfred.Yuan
 *         按照时间排序.支持分页
 */
public class TimeFilter implements Filter {

    private static final Log log = LogFactory.getLog(TimeFilter.class);

    public List filter(Query query) {

        if (query == null || !Query.verify(query))
            return null;

        if (query.getId() > 0)
            return filter4Single(query.getId(),
                    query.getType(),
                    query.getSubtype(),
                    query.getStart(),
                    query.getCount());
        else if (query.getIdList() != null)
            return filter4Multi(query.getIdList(),
                    query.getType(),
                    query.getSubtype(),
                    query.getStart(),
                    query.getCount());

        return null;
    }

    private List filter4Single(int id, int type, int subtype, int start, int count) {

        long startTime = System.currentTimeMillis();

        if (count < 0 && count != -1) {
            throw new IllegalStateException("count cant't be less 0 unless equal -1");
        }

        List items = null;

        if (start < 0)
            start = 0;

        items = ListManager.getInstance().getItemList(id, type, subtype);
        if (items == null || start > items.size()) {
            return new ArrayList();
        }

        // 排序
        Collections.sort(items, new TimeComparator(true));

        // 过滤
        int sizeBefore = items.size();
        long startTime1 = System.currentTimeMillis();
        if (type == 2)
            CollectionUtils.filter(items, new UniqueDescPredicate());
        long endTime1 = System.currentTimeMillis();
        int sizeAfter = items.size();

        // 截长
        int toIndex = start + count;
        if (count < 0 || toIndex > items.size()) {
            toIndex = items.size();
        }

        long endTime = System.currentTimeMillis();
        log.info("TimeFilter: (id=" + id + ")(costSum:costFilter=" + (endTime - startTime) +
                " : " + (endTime1 - startTime1) + ")(sizeBefore=" + sizeBefore +
                ")(sizeFilter=" + (sizeBefore - sizeAfter) + ")");

        return new ArrayList(items.subList(start, toIndex));
    }

    private List filter4Multi(List idList, int type, int subtype, int start, int count) {

        if (idList == null || idList.size() == 0 || idList.size() > MAX_SIZE_ID_LIST) {
            throw new IllegalStateException("idList is empty or more than " + MAX_SIZE_ID_LIST);
        }

        List items = new ArrayList();

        for (int i = 0; i < idList.size(); i++) {
            Integer id = (Integer) idList.get(i);
            List list = filter4Single(id.intValue(), type, subtype, start, count);
            if (list != null && list.size() > 0) {
                items.addAll(list);
            }
            list = null;
        }

        if (start > items.size()) {
            return new ArrayList();
        }

        // 排序
        Collections.sort(items, new TimeComparator(true));

        // 过滤
        if (type == 2)
            CollectionUtils.filter(items, new UniqueDescPredicate());

        // 截长
        int toIndex = start + count;
        if (count < 0 || toIndex > items.size()) {
            toIndex = items.size();
        }

        return new ArrayList(items.subList(start, toIndex));
    }

}
