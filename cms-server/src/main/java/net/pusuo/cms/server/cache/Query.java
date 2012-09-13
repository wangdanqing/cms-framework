/**
 *
 */
package net.pusuo.cms.server.cache;

import net.pusuo.cms.server.ItemInfo;
import net.pusuo.cms.server.cache.filter.PriorityFilter;
import net.pusuo.cms.server.cache.filter.PriorityTimeFilter;
import net.pusuo.cms.server.cache.filter.TimeFilter;
import net.pusuo.cms.server.core.News;

import java.io.Serializable;
import java.util.List;

/**
 * @author Alfred.Yuan
 *         构建对ListCache的查询条件
 */
public class Query implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SORT_TYPE_TIME = 0;                    // 按照时间排序
    public static final int SORT_TYPE_PRIORITY = 1;                // 按照权重排序
    public static final int SORT_TYPE_PRIORITY_AND_TIME = 2;    // 在权重范围内,按照时间排序

    public static final int DEFAULT_SUBTYPE = 0;    // 默认的子类型(专题/新闻/图片)

    // 目前支持的子类型
    public static final int NEWS_SUBTYPE_ZUTU = News.SUBTYPE_ZUTU;
    public static final int NEWS_SUBTYPE_VIDEO = News.SUBTYPE_VIDEO;

    // 或者按照id查询,或者按照idList查询
    private int id = -1;
    private List idList = null;

    // 可以同时按照类型以及子类型查询
    private int type = 2;        // 专题:1;新闻2;图片3
    private int subtype = 0;    // 专题-默认:0
    // 新闻-(综合:0;文本:1;图片:2;)组图:3;视频:4
    // 图片-默认:0

    // 查询类型
    private int sortType = -1;

    // SORT_TYPE_TIME(支持分页)
    private int start = -1; // 从第多少条记录开始

    // SORT_TYPE_PRIORITY or SORT_TYPE_PRIORITY_AND_TIME
    private int minPriority = -1;    // 最小权重
    private int maxPriority = -1;      // 最大权重

    // 返回的最大数据条数
    private int count = -1;

    // 是否需要排重
    private boolean needUniqueDesc = true;

    public Query() {

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List getIdList() {
        return idList;
    }

    public void setIdList(List idList) {
        this.idList = idList;
    }

    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public int getMinPriority() {
        return minPriority;
    }

    public void setMinPriority(int minPriority) {
        this.minPriority = minPriority;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        if (start < 0)
            start = 0;
        this.start = start;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        // 即使此时type还没有设置,其默认值也是2
        if (type == ItemInfo.NEWS_TYPE && subtype < News.SUBTYPE_ZUTU)
            subtype = Query.DEFAULT_SUBTYPE;

        this.subtype = subtype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNeedUniqueDesc() {
        return needUniqueDesc;
    }

    public void setNeedUniqueDesc(boolean needUniqueDesc) {
        this.needUniqueDesc = needUniqueDesc;
    }

    /**
     * 根据排序算法类型实例化Filter接口
     *
     * @return
     */
    public Filter initFilter() {

        Filter filter = null;

        switch (sortType) {
            case SORT_TYPE_TIME:
                filter = new TimeFilter();
                break;
            case SORT_TYPE_PRIORITY:
                filter = new PriorityFilter();
                break;
            case SORT_TYPE_PRIORITY_AND_TIME:
                filter = new PriorityTimeFilter();
                break;
            default:
                filter = null;
                break;
        }

        return filter;
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * 验证查询条件是否符合设置正确
     *
     * @param query
     * @return
     */
    public static boolean verify(Query query) {

        if (query == null)
            return false;

        // 必需的条件
        if (query.getId() == -1 && query.getIdList() == null)
            return false;
        if (query.getType() == -1)
            return false;
        if (query.getSortType() == -1)
            return false;

        // SORT_TYPE_TIME
        if (query.getSortType() == Query.SORT_TYPE_TIME) {
            if (query.getStart() == -1)
                return false;
            // 如果count等于-1,则表示是List的初始化长度
            if (query.getCount() < 0 && query.getCount() != -1)
                return false;
        }

        // SORT_TYPE_PRIORITY or SORT_TYPE_PRIORITY_AND_TIME
        if (query.getSortType() == Query.SORT_TYPE_PRIORITY
                || query.getSortType() == Query.SORT_TYPE_PRIORITY_AND_TIME) {
            if (query.getMinPriority() == -1 || query.getMaxPriority() == -1
                    || query.getMinPriority() > query.getMaxPriority())
                return false;
            if (query.getCount() == -1)
                return false;
        }

        return true;
    }

}
