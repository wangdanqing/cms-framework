/**
 *
 */
package net.pusuo.cms.server.cache;

import java.util.List;

/**
 * @author Alfred.Yuan
 *         ListCache过滤器.不同的实现对应不同的过滤策略.
 */
public interface Filter {

    public static final int MAX_SIZE_ID_LIST = 10;

    public List filter(Query query);

}
