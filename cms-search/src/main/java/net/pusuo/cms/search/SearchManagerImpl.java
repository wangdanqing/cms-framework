package net.pusuo.cms.search;

import net.pusuo.cms.search.castor.IndexCastor;
import net.pusuo.cms.search.castor.RotationCastor;
import net.pusuo.cms.search.castor.SequenceCastor;
import net.pusuo.cms.search.entry.Entry;
import net.pusuo.cms.search.util.SearchUtils;
import net.pusuo.cms.search.util.TaskEngine;
import net.pusuo.cms.search.worker.IndexWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Database implementation of SearchManager using the Lucene search package.
 * Search indexes are stored in the "search" subdirectory of <tt>index</tt>.
 */
public class SearchManagerImpl extends AbstractSearchManager implements Runnable {

    private static Log log = LogFactory.getLog(SearchManagerImpl.class);
    
    /**
     * Workers that automaticly update index.
     */
    private IndexWorker[] indexWorkers = null;
    private List indexWorkerClassNames = null;

    /**
     * True if all indexers are busy. False otherwise.
     */
    private Map indexWorkerBusyes = null;
    
    /**
     * Record count for workers that indexed in a task.
     */
    private Map indexWorkerRecordCounts = null;
    
    /**
     * Entries that will be indexed in next task.
     */
    private Map addedEntries = new HashMap();
    
    /**
     * Entries that will be removed from index in next task.
     */
    private Map removedEntries = new HashMap();
    
    /**
     * Entries that will be updated in next task.
     */
    private Map updatedEntries = new HashMap();

    /**
     * The scheduled task for auto-indexing.
     */
    private TimerTask timerTask = null;

    /**
     * Maintains the amount of time in minutes should elapse before the next
     * index auto-update.
     */
    private int autoIndexInterval = 10;
    
    /**
     * IndexCastor for index persistence.
     */
    private IndexCastor indexCastor = null;
            
    private SearchManagerImpl() throws RemoteException {
        super();
        
        // Configuration for parameters.
        int indexWorkerCount = 0;
        String indexWorkerCountParam = SearchConfig.getProperty("search.indexWorker.count");
        try {
            indexWorkerCount = Integer.parseInt(indexWorkerCountParam);
        }
        catch (Exception e) {}
        indexWorkerClassNames = new ArrayList();
        for (int i = 0; i < indexWorkerCount; i++) {
            String indexWorkerClassName = SearchConfig.getProperty("search.indexWorker.worker" + (i + 1));
            indexWorkerClassNames.add(indexWorkerClassName);
        }
        
        String autoIndexIntervalParam = SearchConfig.getProperty("search.autoIndexInterval");
        try {
            autoIndexInterval = Integer.parseInt(autoIndexIntervalParam);
        }
        catch (Exception e) { }
        
        // Init IndexCastor.
        initCastor();
        
        // Update indexes in background.
        timerTask = TaskEngine.scheduleTask(
               this, 
               autoIndexInterval*SearchConfig.MINUTE,
               autoIndexInterval*SearchConfig.MINUTE);
    }

    @Override
    public void addToIndex(Entry entry) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private static Object initLock = new Object();
    private static SearchManagerImpl manager = null;

    public static SearchManagerImpl getInstance() {
    	
    	if (manager == null) {
    		
    		synchronized (initLock) {
    			
    			if (manager == null) {
    				try {
    					manager = new SearchManagerImpl();
    				} 
    				catch (Exception e) {
    					log.error(e.toString());
    					return null;
    				}
    			}
    			
    		}
    	}
    	
    	return manager;
    }

    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Initialize castor.
     *  
     */
	private void initCastor() {
		
		boolean isRotationCastor = true;
		
        File repositoryDir = new File(IndexCastor.repositoryPath);
        if (repositoryDir.exists()) {
        	if (IndexReader.indexExists(repositoryDir)) {
        		isRotationCastor = false;
        	}
        }

		if (isRotationCastor)
			indexCastor = new RotationCastor();
		else
			indexCastor = new SequenceCastor();
		
		indexCastor.setAnalyzer(analyzer);
		indexCastor.setSearchManager(this);
	}

	/**
	 * Decide whether the current castor is RotationCastor.
	 * 
	 * @return
	 */
	private boolean isRotationCastor() {

		if (indexCastor == null)
			return false;

		return indexCastor instanceof RotationCastor;
	}

	/**
	 * Decide whether the current castor is SequenceCastor.
	 * 
	 * @return
	 */
	private boolean isSequenceCastor() {

		if (indexCastor == null)
			return false;

		return indexCastor instanceof SequenceCastor;
	}
	
	/**
	 * Decide whether to reset castor, and do it if necessary. 
	 *
	 */
	private void resetCastor() {
		
	    synchronized (SearchUtils.searcherLock) {
	        
			if (isSequenceCastor())
				return;
			
			indexCastor = new SequenceCastor();
			indexCastor.setAnalyzer(analyzer);
			indexCastor.setSearchManager(this);		
	    }
	}
    
    ///////////////////////////////////////////////////////////////////////////
	
    /**
	 * Auto-indexing logic. It will automatically be scheduled to run at the
	 * desired interval if auto-indexing is turned on.
	 */
    public void run() {

        try {
            // If another index operation is already occuring, do nothing.
	        if (isBusy())
	            return;
	        
            log.info("A new task started.");
            
	        // Init IndexWorkers.
	        initIndexWorkers();
	        
	        // Notify indexCastor that the task will start.
	        indexCastor.startTask(null);
	        
	        // Start tasks for indexing.
	        for (int i = 0; i < indexWorkers.length; i++) {
	            IndexWorker indexWorker = indexWorkers[i];
	            TaskEngine.addTask(TaskEngine.LOW_PRIORITY, new IndexWorkerTask(indexWorker));
	        }
        }
        catch (Exception e) { 
            log.error(e);
        }        
    }

    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Init IndexWorkers.
     *
     */
    private void initIndexWorkers() {
        
        // Destroy IndexWorkers.
        destroyIndexWorkers();
        
        // Construct IndexWorkers.
        int size = indexWorkerClassNames.size();
        indexWorkers = new IndexWorker[size];
        for (int i = 0; i < size; i++) {
            try {
                String indexWorkerClassName = (String)indexWorkerClassNames.get(i);
                Class indexWorkerClass = Class.forName(indexWorkerClassName);
                IndexWorker indexWorker = (IndexWorker)indexWorkerClass.newInstance();
                indexWorkers[i] = indexWorker;
            }
            catch (Exception e) {}
        }

        // Construct IndexWorkers status.
        indexWorkerBusyes = new HashMap();
        indexWorkerRecordCounts = new HashMap();

        // Init IndexWorkers and status.
        for (int i = 0; i < indexWorkers.length; i++) {
            IndexWorker indexWorker = indexWorkers[i];           
            indexWorker.setSearchManager(this); 
            
            // It`s important!See IndexWorkerTask.
            indexWorkerBusyes.put(indexWorker.getClass().getName(), new Boolean(true));
            indexWorkerRecordCounts.put(indexWorker.getClass().getName(), new Integer(0));
        }
    }
    
    /**
     * Destroy IndexWorkers.
     *
     */
    private void destroyIndexWorkers() {
        
        if (indexWorkers != null) {
            for (int i = 0; i < indexWorkers.length; i++) {
                indexWorkers[i] = null;
            }
            indexWorkers = null;
        }        
        
        indexWorkerBusyes = null;
        indexWorkerRecordCounts = null;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public boolean isBusy() throws RemoteException {
        
        if (indexWorkerBusyes == null)
            return false;
        
        boolean isBusy = false;
        
        Iterator iter = indexWorkerBusyes.keySet().iterator();
        while (iter.hasNext()) {
            String className = (String)iter.next();
            boolean indexWorkerBusy = ((Boolean)indexWorkerBusyes.get(className)).booleanValue();
            if (indexWorkerBusy) {
                isBusy = true;
                break;
            }
        }
        
        return isBusy;
    }
    
    private boolean isBusy(IndexWorker indexWorker) {
        
        if (indexWorker == null || indexWorkerBusyes == null)
            return false;
        
        String className = indexWorker.getClass().getName();
        Boolean isBusy = (Boolean)indexWorkerBusyes.get(className);
        if (isBusy == null)
            return false;
        
        return isBusy.booleanValue();
    }
    
    private boolean isBusyOthers(IndexWorker indexWorker) {
        
        if (indexWorker == null || indexWorkerBusyes == null) {
            return false;
        }
        
        boolean isBusy = false;
        
        String className = indexWorker.getClass().getName();
        Iterator iter = indexWorkerBusyes.keySet().iterator();
        while (iter.hasNext()) {
            String aclassName = (String)iter.next();
            if (className.equalsIgnoreCase(aclassName))
                continue;
            boolean busy = ((Boolean)indexWorkerBusyes.get(aclassName)).booleanValue();
            if (busy) {
                isBusy = true;
                break;
            }
        }
        
        return isBusy;
    }
    
    private void setBusy(IndexWorker indexWorker, boolean busy) {
        
        if (indexWorker == null || indexWorkerBusyes == null)
            return;
        
        String className = indexWorker.getClass().getName();
        indexWorkerBusyes.put(className, new Boolean(busy));
    }

    ///////////////////////////////////////////////////////////////////////////
    
    private void setRecordCount(IndexWorker indexWorker, int recordCount) {
    	
    	if (indexWorker == null || indexWorkerRecordCounts == null)
    		return;
    	
    	String className = indexWorker.getClass().getName();  	
    	indexWorkerRecordCounts.put(className, new Integer(recordCount));   	
    }
    
    private int getSumOfRecords() {
    	
    	if (indexWorkerRecordCounts == null)
    		return 0;
    	
    	int sum = 0;
    	
    	Iterator iter = indexWorkerRecordCounts.keySet().iterator();
    	while (iter.hasNext()) {
    		String className = (String)iter.next();
    		Integer recordCount = (Integer)indexWorkerRecordCounts.get(className);
    		sum += recordCount.intValue();
    	}
    	
    	return sum;
    }
    
    private int[] getRecordCounts() {
    	
    	if (indexWorkerRecordCounts == null)
    		return null;
    	
    	int[] recordCounts = new int[indexWorkerRecordCounts.size()];
    	
    	int i = 0;
    	Iterator iter = indexWorkerRecordCounts.keySet().iterator();
    	while (iter.hasNext()) {
    		String className = (String)iter.next();
    		Integer recordCount = (Integer)indexWorkerRecordCounts.get(className);
    		recordCounts[i] = recordCount.intValue();
    		i++;
    	}    		
    	
    	return recordCounts;
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#addToIndex(com.hexun.cms.search.Entry)
     */
    public void addToIndex(Entry entry) throws RemoteException {
        
        if (entry == null || entry.getId() <= 0)
            return;
        
        synchronized (SearchUtils.addLock) {
            addedEntries.put(String.valueOf(entry.getId()), entry);
        }
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getAddedEntries()
     */
    public Map getAddedEntries() throws RemoteException {
        return addedEntries;
    }

    @Override
    public boolean removeFromIndex(Entry entry) throws RemoteException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#removeFromIndex(com.hexun.cms.search.Entry)
     */
    public boolean removeFromIndex(Entry entry) throws RemoteException {  
        
        if (entry == null || entry.getId() <= 0)
            return false;

        synchronized (SearchUtils.removeLock) {
            removedEntries.put(String.valueOf(entry.getId()), entry);
        }
        
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getRemovedEntries()
     */
    public Map getRemovedEntries() throws RemoteException {
        return removedEntries;
    }

    @Override
    public void updateToIndex(Entry entry) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
    * @see com.hexun.cms.search.SearchManager#updateToIndex(com.hexun.cms.search.Entry)
    */
    public void updateToIndex(Entry entry) throws RemoteException {

        if (entry == null || entry.getId() <= 0)
            return;
        
        synchronized (SearchUtils.updateLock) {
            updatedEntries.put(String.valueOf(entry.getId()), entry);
        }
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#getUpdatedEntries()
     */
    public Map getUpdatedEntries() throws RemoteException {
        return updatedEntries;
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, int, int)
     */
    public Map search(Query query, int fromId, int range) throws RemoteException {
        
    	Sort sort = null;
        return search(query, sort, fromId, range);
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.Sort, int, int)
     */
    public Map search(Query query, Sort sort, int fromId, int range) throws RemoteException {
        
        if (query == null || fromId < 0 || range < 1)
            return null;
        
        Map result = null;
        
        synchronized (SearchUtils.searcherLock) {
            
        try {
            long begin = System.currentTimeMillis();
            
            Hits hits = null;
            if (sort != null)
                hits = indexCastor.getSearcher().search(query, sort);
            else
                hits = indexCastor.getSearcher().search(query);
            
            long end = System.currentTimeMillis();
            long cost = end - begin;
            
            result = hits2entires(hits, fromId, range);
            
            result.put(SearchUtils.SEARCH_RESULT_COST, new Long(cost));
        }
        catch (Exception e) {
            log.error(e);
        }
        
        }
        
        return result;
    }
    
    private Map hits2entires(Hits hits, int fromId, int range) {
    	
        int count = 0;
        List list = new ArrayList();
        
        try {
            if (hits != null) {
                count = hits.length();
                if (fromId < count) {
                    int toId = count; // exclude.
                    if (fromId + range < count)
                        toId = fromId + range;
                    
                    for (int i = fromId; i < toId; i++) {
                        try {
	                        Document doc = hits.doc(i);
	                        Object entry = SearchUtils.doc2entry(doc);
	                        if (entry != null)
	                            list.add(entry);
                        }
                        catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        
        Map result = new HashMap();
        result.put(SearchUtils.SEARCH_RESULT_COUNT, new Integer(count));
        result.put(SearchUtils.SEARCH_RESULT_LIST, list);
        
        return result;
    }

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.Filter, int, int)
	 */
	public Map search(Query query, Filter filter, int fromId, int range) throws RemoteException {
		
		Sort sort = null;
		return search(query, filter, sort, fromId, range);
	}

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.Filter, org.apache.lucene.search.Sort, int, int)
	 */
	public Map search(Query query, Filter filter, Sort sort, 
			int fromId, int range) throws RemoteException {
		
        if (query == null || fromId < 0 || range < 1)
            return null;
        
        Map result = null;
        
        synchronized (SearchUtils.searcherLock) {
            
        try {
            long begin = System.currentTimeMillis();
            
            Hits hits = null;
            if (sort != null)
                hits = indexCastor.getSearcher().search(query, filter, sort);
            else
                hits = indexCastor.getSearcher().search(query, filter);
            
            long end = System.currentTimeMillis();
            long cost = end - begin;
            
            result = hits2entires(hits, fromId, range);
            
            result.put(SearchUtils.SEARCH_RESULT_COST, new Long(cost));
        }
        catch (Exception e) {
            log.error(e);
        }
        
        }
        
        return result;
	}

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.HitCollector, int, int)
	 */
	public Map search(Query query, HitCollector results, int fromId, int range) throws RemoteException {
		
		Filter filter = null;
		return search(query, filter, results, fromId, range);
	}
    
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.SearchManager#search(org.apache.lucene.search.Query, org.apache.lucene.search.Filter, org.apache.lucene.search.HitCollector, int, int)
	 */
	public Map search(Query query, Filter filter, HitCollector results, 
			int fromId, int range) throws RemoteException {
		
        return null;
	}
    
    ///////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
     * @see com.hexun.cms.search.IndexMerger#addDocument(org.apache.lucene.document.Document)
     */
    public void addDocument(Document doc) {
        
    	indexCastor.addDocument(doc);
    }
    
    /* (non-Javadoc)
     * @see com.hexun.cms.search.IndexMerger#addIndexes(org.apache.lucene.store.Directory[])
     */
	public void addIndexes(Directory[] dirs) {
        
		indexCastor.addIndexes(dirs);
	}
	
	///////////////////////////////////////////////////////////////////////////

    private class IndexWorkerTask implements Runnable {
        
        private IndexWorker indexWorker = null;
        
        public IndexWorkerTask(IndexWorker indexWorker) {
            this.indexWorker = indexWorker;
            
            log.info(indexWorker + " started.");
        }
        
        public void run() {
        	
            /**
             * There a terrible problem here. Sometime the first worker run into block code - 
             * "finally", but the others don`t set their status busy. So it results in 
             * NullPointerException. My first answer is if cost of indexWorker updating index is nearly
             * zero, let it sleep 30 seconds. My second answer is before starting these tasks
             * we have set their status busy. Of course, I choose the last.
             */             
            try {                
                // Update indexes.
                int recordCount = indexWorker.updateIndex();
                log.info("Number of records indexed by " + indexWorker + " is : " + recordCount);
                
            	// Set the indexWorker indexed recordCount 
            	setRecordCount(indexWorker, recordCount);            	
            } 
            finally {
                log.info(indexWorker + " ended.");

                if (!isBusyOthers(indexWorker)) {
                	
                	// Notify indexCastor that the task will end.
                	Map properties = new HashMap();
                	properties.put(IndexCastor.PROPERTY_NAME_SUM_OF_RECORDS, new Integer(getSumOfRecords()));
                	properties.put(IndexCastor.PROPERTY_NAME_RECORD_COUNTS, getRecordCounts());
                	
                	Map result = indexCastor.endTask(properties);
                	
                	// Maybe need reset castor.
                	if (result != null) {
                		if (result.containsKey(IndexCastor.PROPERTY_NAME_NEED_RESET_CASTOR)) {
                			boolean needResetCastor = ((Boolean)result.get(IndexCastor
                					.PROPERTY_NAME_NEED_RESET_CASTOR)).booleanValue();
                			if (needResetCastor)
                				resetCastor();
                		}
                	}
                    
                    // Destroy IndexWorkers.Note that the call must be invoked in .
                    destroyIndexWorkers();  
                    
                    log.info("The task ended.");
                }
                else {
                    // Reset the indexWorker to idle.
                    setBusy(indexWorker, false);
                }
            }
        }
    }
    
}
