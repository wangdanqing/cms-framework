/*
 * Created on 2006-3-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.castor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.hexun.cms.search.SearchConfig;
import com.hexun.cms.search.entry.Entry;
import com.hexun.cms.search.util.SearchUtils;
import com.hexun.cms.search.worker.IndexWorker;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SequenceCastor extends IndexCastor {
	
	private static Log log = LogFactory.getLog(SequenceCastor.class);
	
	/**
	 * Count of indexed records for increment directory.
	 */
	private int recordCountIncrement = 140000;
	
	/**
	 * Time for merging increment directory and repository directory
	 */
	private int mergeTimeIncrement = 3;
	
	/**
	 * IndexWriter for increment directory.
	 */
	private IndexWriter indexWriter = null;
	
	/**
	 * Searcher for all indexes.
	 */
	private Searcher searcher = null;
	
	public SequenceCastor() {
		
        // Configuration for parameters.
        String recordCountIncrementParam = SearchConfig.getProperty("search.recordCountIncrement");
        try {
        	recordCountIncrement = Integer.parseInt(recordCountIncrementParam);
        }
        catch (Exception e) { }

        String mergeTimeIncrementParam = SearchConfig.getProperty("search.mergeTimeIncrement");
        try {
        	mergeTimeIncrement = Integer.parseInt(mergeTimeIncrementParam);
        }
        catch (Exception e) { }
	}
	
	private int getRecordCountNow() {
		
		int recordCountNow = 0;
		
        String recordCountNowParam = SearchConfig.getProperty("search.recordCountNow");
        try {
        	recordCountNow = Integer.parseInt(recordCountNowParam);
        }
        catch (Exception e) { }

        return recordCountNow;
	}
	
	private void setRecordCountNow(int count) {
		
		SearchConfig.setProperty("search.recordCountNow", String.valueOf(count));
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#startTask()
	 */
	public Map startTask(Map properties) {
		
		initIndexWriter();
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#endTask()
	 */
	public Map endTask(Map properties) {
		
		// Add record count of increment directory.
		addRecordCountNow(properties);
		
        // Destroy IndexWriters.
        destroyIndexWriter();
        
        // Now update entries that interface with users.
        updateEntriesOfUser();
		
        // Merge increment index and repository index if necessary
		mergeIndexesIfNecessary(properties);
        
        // Reset search.
        resetSearch();
        
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private void initIndexWriter() {
		
		destroyIndexWriter();
		
		indexWriter = createIndexWriter(incrementPath, true);
	}
	
	private void destroyIndexWriter() {
		
		try {
			if (indexWriter != null) {
				indexWriter.optimize();
				indexWriter.close();
				
				indexWriter = null;
			}
		}
		catch (Exception e) {
			log.error(e);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#addDocument(org.apache.lucene.document.Document)
	 */
	public void addDocument(Document doc) {
		
	    try {
	    	indexWriter.addDocument(doc);
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
	}
	
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#addIndexes(org.apache.lucene.store.Directory[])
	 */
	public void addIndexes(Directory[] dirs) {
		
	    try {
	    	indexWriter.addIndexes(dirs);
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Add record count of increment directory.
	 */
	private void addRecordCountNow(Map properties) {
		
		if (properties == null)
			return;
		
		if (properties.containsKey(PROPERTY_NAME_SUM_OF_RECORDS)) {
			
			int sumOfRecords = ((Integer)properties.get(PROPERTY_NAME_SUM_OF_RECORDS)).intValue();
			
			int count = getRecordCountNow();
			count += sumOfRecords;
			
			setRecordCountNow(count);
		}
	}
	
    /**
     * Update entries that interface with users.
     *
     */
    private void updateEntriesOfUser() {
        try {
        	Map updatedEntries = searchManager.getUpdatedEntries();
        	Map removedEntries = searchManager.getRemovedEntries();
        	Map addedEntries = searchManager.getAddedEntries();
        	
            synchronized (SearchUtils.updateLock) {
            	             
	            synchronized (SearchUtils.removeLock) {
	                
	                Iterator iterator = updatedEntries.keySet().iterator();
	                while (iterator.hasNext()) {
	                    Object id = iterator.next();
	                    Object entry = updatedEntries.get(id);
	                    removedEntries.put(id, entry);
	                }
	                
	                if (removedEntries.size() > 0) {
	                	String[] indexPathChildren = new String[2];
	                	indexPathChildren[0] = repositoryPath;
	                	indexPathChildren[1] = incrementPath;
	                	
			            for (int i = 0; i < indexPathChildren.length; i++) {
			                String indexPathChild = indexPathChildren[i];
			                
			                if (IndexReader.indexExists(indexPathChild)) {
				                IndexReader reader = IndexReader.open(indexPathChild);
				                
				                if (reader != null) {
					                Iterator iter = removedEntries.keySet().iterator();
					                while (iter.hasNext()) {
					                    String id = (String)iter.next();   	                        
					                    reader.delete(new Term(Entry.FIELD_NAME_ID, id));
					                }
					                
					                reader.close();
					                reader = null;
				            	}
			            	}
			            }
			            
			            removedEntries.clear();
	                }
	            }
	            
	            synchronized (SearchUtils.addLock) {
	                
	                Iterator iterator = updatedEntries.keySet().iterator();
	                while (iterator.hasNext()) {
	                    Object id = iterator.next();
	                    Object entry = updatedEntries.get(id);
	                    addedEntries.put(id, entry);
	                }
	                
	                if (addedEntries.size() > 0) {
		                String indexPathChild = incrementPath;
		                
		                if (indexPathChild != null) {
			                IndexWriter writer = createIndexWriter(indexPathChild, true);
					        
			                if (writer != null) {
						        Iterator iter = addedEntries.keySet().iterator();
						        while (iter.hasNext()) {
						            Object id = iter.next();
						            Entry entry = (Entry)addedEntries.get(id);
						            IndexWorker.addEntryToIndex(entry, writer);
						        }
						        
						        writer.optimize();
						        writer.close();
						        writer = null;
			                }
		                }
				        
				        addedEntries.clear();
	                }
	            }
	            
	            updatedEntries.clear();
            }
        }
        catch (Exception e) {
            log.error(e);
        }        
    }
	
	/**
	 * Merge increment index and repository index if necessary
	 */
	private void mergeIndexesIfNecessary(Map properties) {

        int recordCountNow = getRecordCountNow();
		if (recordCountNow < recordCountIncrement)
			return;
		
		// Decide whether or not time of now is between 03:00 and 04:00
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour != mergeTimeIncrement)
			return;
		
        log.info("Mergering increment index and repository index beginning.");
        long fromTime = System.currentTimeMillis();
        
		// Merge increment index and repository index.
        // And the process is so surprising!Wo......
		try {
			IndexReader reader = IndexReader.open(incrementPath);
			if (reader != null) {
				// Read index into RAM.
	            Directory ramDirectory = new RAMDirectory();
	            IndexWriter ramIndexWriter = new IndexWriter(ramDirectory, analyzer, true);
                ramIndexWriter.addIndexes(new IndexReader[]{reader});
                ramIndexWriter.optimize();
                ramIndexWriter.close();
                ramIndexWriter = null;
                
                // Close the reader
				reader.close();
				reader = null;
				
				// Write index into repository directory
				IndexWriter writer = createIndexWriter(repositoryPath, true);
				if (writer != null) {
					writer.addIndexes(new Directory[]{ramDirectory});
					writer.optimize();
					writer.close();
					writer = null;
				}				
			}
		}
		catch (Exception e) {
			log.error(e);
		}
		
		// Reset searcher to repository directory
		resetSearchToRepositoryPath();
		
		// Delete files of increment directory
		SearchUtils.deleteFilesOfDir(incrementPath);
		
		// Reset record count of increment directory to zero(0)
		setRecordCountNow(0);
		
        long toTime = System.currentTimeMillis();
        long cost = toTime - fromTime;
        log.info("Mergering increment index and repository index ended.Cost is: " + cost);
	}
	
    /**
     * Reset all Searchables.
     * 
     */
    private void resetSearch() {
        
        synchronized (SearchUtils.searcherLock) { 
        	
        	resetSearcher();
        }
    }
    
	///////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#getSearcher()
	 */
	public Searcher getSearcher() {
		
        if (searcher == null) {
            initSearcher();
        }
        
        return searcher;
	}
	
	public void initSearcher() {
		
	    try {
	    	int count = 1;
	    	if (IndexReader.indexExists(incrementPath)) {
	    		count++;
	    	}
	    	
	        Searchable[] searchables = new Searchable[count];
	        searchables[0] = new IndexSearcher(repositoryPath);
	        if (count == 2) {
	        	searchables[1] = new IndexSearcher(incrementPath);
	        }
	        
	        searcher = new ParallelMultiSearcher(searchables);
	    }
	    catch (Exception e) {
	        log.error(e);
	    }     	    
	}
	
    /**
     * Destroy searcher.
     */
    private void destroySearcher() {
        
        try {
	        if (searcher != null) {
	            searcher.close();
	            searcher = null;
	        }
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    /**
     * Reset searcher.
     */
    private void resetSearcher() {
        
        destroySearcher();
        initSearcher();
    }   
    
    /**
     * Set searcher only to repository path
     */
    private void resetSearchToRepositoryPath() {
    	
        synchronized (SearchUtils.searcherLock) {  
        	
	        // Destroy searcher
	        destroySearcher();

	        // Initilize searcher
	        try {
		        Searchable[] searchables = new Searchable[1];
		        searchables[0] = new IndexSearcher(repositoryPath);
		        
		        searcher = new ParallelMultiSearcher(searchables);
	        } 
	        catch (Exception e) {}     
        }
    }
    
}
