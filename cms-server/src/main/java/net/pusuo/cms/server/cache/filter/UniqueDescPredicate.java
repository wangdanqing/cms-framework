/**
 * 
 */
package net.pusuo.cms.server.cache.filter;

import net.pusuo.cms.server.Item;

import javax.sql.RowSet;
import javax.sql.rowset.Predicate;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alfred.Yuan
 * 保持标题的唯一性
 */
public class UniqueDescPredicate implements Predicate, Serializable {
	
    /** Serial version UID */
    static final long serialVersionUID = -1L;
    
    /** The set of previously seen objects */
    private final Set descSet = new HashSet();
    
    /**
     * Factory to create the predicate.
     * 
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static Predicate getInstance() {
        return new UniqueDescPredicate();
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     */
    public UniqueDescPredicate() {
        super();
    }

    /**
     * Evaluates the predicate returning true if the input object hasn't been
     * received yet.
     * 
     * @param object  the input object
     * @return true if this is the first time the object is seen
     */
    public boolean evaluate(Object object) {
    	
    	if (object instanceof Item) {
    		Item item = (Item)object;
    		return descSet.add(item.getDesc());
    	}
    	
        return true;
    }

    @Override
    public boolean evaluate(RowSet rs) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean evaluate(Object value, int column) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean evaluate(Object value, String columnName) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
