/**
 *
 */
package net.pusuo.cms.server.cache.filter;

import net.pusuo.cms.server.cache.Query;
import net.pusuo.cms.server.cache.util.PrioComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alfred.Yuan
 *         在权重范围内,按照权重排序
 */
public class PriorityFilter extends AbstractPriorityFilter {

    private static final Log log = LogFactory.getLog(PriorityFilter.class);

    public List filter(Query query) {

        if (query == null || !Query.verify(query))
            return null;

        if (query.getId() > 0)
            return filter4Single(query.getId(),
                    query.getType(),
                    query.getSubtype(),
                    query.getMinPriority(),
                    query.getMaxPriority(),
                    query.getCount());
        else if (query.getIdList() != null)
            return filter4Multi(query.getIdList(),
                    query.getType(),
                    query.getSubtype(),
                    query.getMinPriority(),
                    query.getMaxPriority(),
                    query.getCount());

        return null;
    }

    private List filter4Single(int id, int type, int subtype, int minp, int maxp, int count) {

        if ((minp < 0 && maxp < 0) || (minp > maxp) || count < 0) {
            throw new IllegalStateException("invalid parameter minp=" + minp + " maxp=" + maxp + " count=" + count);
        }

        List ret = null;
        try {
            ret = filterWithComparator(id, type, subtype, minp, maxp, count, new PrioComparator(true));
        } catch (Exception e) {
            log.error("filterWithComparator err.id=" + id + ",type=" + type);
            log.error(e.toString());
        }

        return ret;
    }

    private List filter4Multi(List idList, int type, int subtype, int minp, int maxp, int count) {

        if (idList == null || idList.size() == 0 || idList.size() > MAX_SIZE_ID_LIST) {
            throw new IllegalStateException("idList is empty or more than " + MAX_SIZE_ID_LIST);
        }

        List items = new ArrayList();

        for (int i = 0; i < idList.size(); i++) {
            Integer id = (Integer) idList.get(i);
            List list = filter4Single(id.intValue(), type, subtype, minp, maxp, count);
            if (list != null && list.size() > 0) {
                items.addAll(list);
            }
            list = null;
        }

        Collections.sort(items, new PrioComparator(true));

        if (type == 2)
            CollectionUtils.filter(items, new UniqueDescPredicate());

        int endIndex = Math.min(count, items.size());
        return new ArrayList(items.subList(0, endIndex));
    }

}
