package net.pusuo.cms.search;

import net.pusuo.cms.search.entry.Entry;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface SearchManager extends Remote {

    /**
     * Adds an individual message to the index. This method is useful for doing
     * real-time indexing. However, for maximum posting speed this method can
     * be ignored. In that case, the automatic indexer will pick up all new
     * messages at the next index interval.
     *
     * @param entry the message to add to the index.
     */
    public void addToIndex(Entry entry) throws RemoteException;
    
    /**
     * Get entries that will be added to indexes in next task.
     * @return
     */
    public Map getAddedEntries() throws RemoteException;

    /**
     * Removes an individual message from the index.
     *
     * @param entry the message to remove from the index.
     */
    public boolean removeFromIndex(Entry entry) throws RemoteException;
 
    /**
     * Get entries that will be removed from indexes in next task.
     * @return
     */
    public Map getRemovedEntries() throws RemoteException;

    /**
     * Update an individual message.
     * @param entry
     */
    public void updateToIndex(Entry entry) throws RemoteException;

    /**
     * Get entries that will be updated to indexes in next task.
     * @return
     */
    public Map getUpdatedEntries() throws RemoteException;
    
    /**
     * Search documents matching query.
     * @param query
     * @param fromId - include.
     * @param range
     * @return
     * @throws java.rmi.RemoteException
     */
    public Map search(Query query, int fromId, int range) throws RemoteException;
    
    /**
     * Search documents matching query sorted by sort.
     * @param query
     * @param sort
     * @param fromId - include.
     * @param range
     * @return
     */
    public Map search(Query query, Sort sort, int fromId, int range) throws RemoteException;
    
    /**
     * Search the documents matching query and filter.
     * @param query
     * @param filter
     * @param fromId - include.
     * @param range
     * @return
     * @throws java.rmi.RemoteException
     */
    public Map search(Query query, Filter filter, int fromId, int range) throws RemoteException;
    
    /**
     * Search documents matching query and filter, sorted by sort.
     * @param query
     * @param filter
     * @param sort
     * @param fromId - include.
     * @param range
     * @return
     * @throws java.rmi.RemoteException
     */
    public Map search(Query query, Filter filter, Sort sort,
                      int fromId, int range) throws RemoteException;
    
    /**
     * Lower-level search API.
     * @param query
     * @param results
     * @param fromId - include.
     * @param range
     * @return
     * @throws java.rmi.RemoteException
     */
    public Map search(Query query, HitCollector results, int fromId, int range) throws RemoteException;
    
    /**
     * Lower-level search API.
     * @param query
     * @param filter
     * @param results
     * @param fromId - include.
     * @param range
     * @return
     * @throws java.rmi.RemoteException
     */
    public Map search(Query query, Filter filter, HitCollector results,
                      int fromId, int range) throws RemoteException;
}
