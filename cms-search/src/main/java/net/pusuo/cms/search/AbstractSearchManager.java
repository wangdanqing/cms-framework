/*
 * Created on 2006-1-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search;

import net.pusuo.cms.search.entry.Entry;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractSearchManager 
		extends UnicastRemoteObject
		implements SearchManager, InternalSearchManager {
    
	/**
	 * The analyzer governs how words are tokenized.
	 */
	protected static Analyzer analyzer = new StandardAnalyzer();
	
    public AbstractSearchManager() throws RemoteException {        
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#addToIndex(com.hexun.cms.search.Entry)
     */
    public abstract void addToIndex(Entry entry) throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getAddedEntries()
     */
    public abstract Map getAddedEntries() throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#removeFromIndex(com.hexun.cms.search.Entry)
     */
    public abstract boolean removeFromIndex(Entry entry) throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getRemovedEntries()
     */
    public abstract Map getRemovedEntries() throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#updateToIndex(com.hexun.cms.search.Entry)
     */
    public abstract void updateToIndex(Entry entry) throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getUpdatedEntries()
     */
    public abstract Map getUpdatedEntries() throws RemoteException;

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, int, int)
     */
    public abstract Map search(Query query, int fromId, int range) throws RemoteException;
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.Sort, int, int)
     */
    public abstract Map search(Query query, Sort sort, int fromId, int range) throws RemoteException;
    
    ///////////////////////////////////////////////////////////////////////////
    
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.InternalSearchManager#getAnalyzer()
	 */
	public Analyzer getAnalyzer() {
		
		return analyzer;
	}
	
    /* (non-Javadoc)
     * @see com.hexun.cms.search.InternalSearchManager#addDocument(org.apache.lucene.document.Document)
     */
    public abstract void addDocument(Document doc);
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.InternalSearchManager#addIndexes(org.apache.lucene.store.Directory[])
     */
    public abstract void addIndexes(Directory[] dirs);
}
