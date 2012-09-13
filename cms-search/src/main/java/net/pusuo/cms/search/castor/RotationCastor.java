/*
 * Created on 2006-3-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.castor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;

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
public class RotationCastor extends IndexCastor {
	
	private static Log log = LogFactory.getLog(RotationCastor.class);

    /**
     * Directory where index is stored.
     */
    private int[] pathChildren = null;
    private int currentPathChild = 0;
        
    private String search1Path = null;
    private String search2Path = null;
    
    private static final int SEARCH_PATH_FIRST = 1;
    private static final int SEARCH_PATH_SECOND = 2;
    private int currentSearchPath = SEARCH_PATH_FIRST;
    
    /**
     * Cache index writers for update index once.
     */
    private Map indexWriters = null;
    
    /**
     * Searcher for all indexes.
     */
    private Searcher searcher = null;

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#init()
	 */
	public RotationCastor() {
		super();
		
		// Configuration for parameters.
        int indexCount = 1;
        String indexCountParam = SearchConfig.getProperty("search.indexCount");
        try {
            indexCount = Integer.parseInt(indexCountParam);
        }
        catch (Exception e) {}
        pathChildren = new int[indexCount];
        for (int i = 0; i < indexCount; i++) {
            pathChildren[i] = i;
        }
        
        // Compute index and search path.
        search1Path = SearchConfig.getHome() + File.separator + "search1";
        search2Path = SearchConfig.getHome() + File.separator + "search2";
		
        // Init index and search path children.
        initIndexPathChildren();
        initSearchPathChildren();        		
	}
	
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Init index path children.
     *
     */
    private void initIndexPathChildren() {
        for (int i = 0; i < pathChildren.length; i++) {           
           String indexPathChild = String.valueOf(pathChildren[i]);
 		   indexPathChild = indexPath + File.separator + indexPathChild;
 		   File pathChild = new File(indexPathChild);
 		   if (!pathChild.exists()) {
 		       pathChild.mkdir();
 		   }
 		   pathChild = null;
        }
        
        // Initilize repository path
        File repositoryDir = new File(repositoryPath);
        if (repositoryDir.exists()) {
        	SearchUtils.deleteFilesOfDir(repositoryPath);
        }
        else {
        	repositoryDir.mkdir();
        }
        repositoryDir = null;
        
        // Initilize increment path
        File incrementDir = new File(incrementPath);
        if (incrementDir.exists()) {
        	SearchUtils.deleteFilesOfDir(incrementPath);
        }
        else {
        	incrementDir.mkdir();
        }
        incrementDir = null;
    }   
    
    /**
     * Get current index path child.
     * @return
     */
    private String getCurrentIndexPathChild() {
        
        return indexPath + File.separator + currentPathChild;
    }
    
    /**
     * Get index path child.
     * @param indexPathChild
     * @return
     */
    private String getIndexPathChild(int indexPathChild) {
        
        int length = pathChildren.length;
        if (indexPathChild < pathChildren[0] || 
            indexPathChild > pathChildren[length - 1])
            return null;
        
        return indexPath + File.separator + indexPathChild;
    }

    /**
     * Compute next index path child.
     * @return
     */
    private String getNextIndexPathChild() {
        
        currentPathChild++;
        
        int length = pathChildren.length;
        if (currentPathChild > length - 1)
            currentPathChild = currentPathChild % length;
        
        return indexPath + File.separator + currentPathChild;       
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Init search path children.
     *
     */
    private void initSearchPathChildren() {
        
        for (int i = 0; i < pathChildren.length; i++) {            
           String searchPathChild = String.valueOf(pathChildren[i]);
           
           String search1PathChild = search1Path + File.separator + searchPathChild;
 		   File pathChild = new File(search1PathChild);
 		   if (!pathChild.exists()) {
 		       pathChild.mkdir();
 		   }
 		   else if (IndexReader.indexExists(search1PathChild)) {
 		       currentSearchPath = SEARCH_PATH_FIRST;
 		   }
 		   
 		   String search2PathChild = search2Path + File.separator + searchPathChild;
 		   pathChild = new File(search2PathChild);
 		   if (!pathChild.exists()) {
 		       pathChild.mkdir();
 		   }
 		   else if (IndexReader.indexExists(search2PathChild)) {
 		       currentSearchPath = SEARCH_PATH_SECOND;
 		   }
        }
        
        if (currentSearchPath != SEARCH_PATH_FIRST 
                && currentSearchPath != SEARCH_PATH_SECOND)
            currentSearchPath = SEARCH_PATH_FIRST;
    }
    
    /**
     * Get search path.
     * @param searchPath
     * @return
     */
    private String getSearchPath(int searchPath) {
        
        String result = null;
        
        switch (searchPath) {
        	case SEARCH_PATH_FIRST :
        	    result = search1Path;
        	    break;
        	case SEARCH_PATH_SECOND :
        	    result = search2Path;
        	    break;
        }    
        
        return result;
    }
    
    /**
     * Get current search path.
     * @return
     */
    private String getCurrentSearchPath() {
        
        return getSearchPath(currentSearchPath);
    }
    
    /**
     * Get other search path.
     * @return
     */
    private String getOtherSearchPath() {
        
        String result = null;
        
        switch (currentSearchPath) {
	    	case SEARCH_PATH_FIRST :
	    	    result = search2Path;
	    	    break;
	    	case SEARCH_PATH_SECOND :
	    	    result = search1Path;
	    	    break;
        }   
    	 
        return result;
    }

    /**
     * Get search path child.
     * @param searchPath
     * @param searchPathChild
     * @return
     */
    private String getSearchPathChild(String searchPath, int searchPathChild) {
        
        int length = pathChildren.length;
        if (searchPathChild < pathChildren[0] || 
            searchPathChild > pathChildren[length - 1])
            return null;
        
        return searchPath + File.separator + searchPathChild;
    }
    
    /**
     * Reset search pach.
     *
     */
    private void resetSearchPath() {
        
        switch (currentSearchPath) {
	    	case SEARCH_PATH_FIRST :
	    	    currentSearchPath = SEARCH_PATH_SECOND;
	    	    break;
	    	case SEARCH_PATH_SECOND :
	    	    currentSearchPath = SEARCH_PATH_FIRST;
	    	    break;
        }   
    }
    
	///////////////////////////////////////////////////////////////////////////
    
	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#startTask()
	 */
	public Map startTask(Map properties) {
		
        // Delete all files of other search path.
        deleteFilesOfOtherSearchPath();
        
        // Init IndexWriters.
        initIndexWriters(false);   
        
        return null;
	}

	/* (non-Javadoc)
	 * @see com.hexun.cms.search.castor.IndexCastor#endTask()
	 */
	public Map endTask(Map properties) {
		
		// Decide whether to reset castor.
		boolean needResetCastor = needResetCastor(properties);
		
        // Destroy IndexWriters.
        destroyIndexWriters();
        
        // Now update entries that interface with users.
        updateEntriesOfUser();
        
        // Merge index files from index path to search path.
        mergeIndexes();
        
        // Reset search.
        resetSearch();
        
        // Do something if need reset castor.
        if (needResetCastor) {
        	resetCastor();
        }
        
        // Return now.
        Map result = new HashMap();        
        result.put(PROPERTY_NAME_NEED_RESET_CASTOR, new Boolean(needResetCastor));       
        return result;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
    /**
     * Init IndexWriters.
     *
     */
    private void initIndexWriters(boolean compound) {
        
        // Destroy IndexWriters.
        destroyIndexWriters();
        
        // Construct IndexWriters.
        indexWriters = new HashMap();
        
        // Init IndexWriters.
        for (int i = 0; i < pathChildren.length; i++) {
            String indexPathChild = getIndexPathChild(i);
	        IndexWriter writer = createIndexWriter(indexPathChild, compound);
	        if (writer != null)
	            indexWriters.put(indexPathChild, writer);
        }
    }
    
    /**
     * Create a IndexWriter for search path.
     * @param indexPath
     * @param compoundFile
     * @return
     */
    private IndexWriter createSearchWriter(String indexPath, boolean compoundFile) {
        
        if (indexPath == null || indexPath.trim().length() == 0)
            return null;
        
        IndexWriter writer = null;
        
        try {
	        boolean create = !IndexReader.indexExists(indexPath);	        
            writer = new IndexWriter(indexPath, analyzer, create);
            writer.setUseCompoundFile(compoundFile);
            writer.mergeFactor = 10;
            writer.maxMergeDocs = 999999999;
            writer.minMergeDocs = 10;
        }
        catch (IOException e) {
            writer = null;
            log.error(e);
        }
        
        return writer;
    }

    /**
     * Destroy IndexWriters.
     *
     */
    private void destroyIndexWriters() {
        
        if (indexWriters != null) {
            Iterator iter = indexWriters.keySet().iterator();
            while (iter.hasNext()) {
                String indexPathChild = (String)iter.next();
                
                IndexWriter indexWriter = (IndexWriter)indexWriters.get(indexPathChild);
                try {
                    indexWriter.optimize();
                    indexWriter.close();                   
                } catch (IOException e) {}
                indexWriter = null;
                
                indexWriters.put(indexPathChild, "");
            }
            indexWriters = null;
            log.info("All IndexWriters destroyed.");
        }
    }

    /**
     * Returns a Lucene IndexWriter for a specified path.
     */
    private IndexWriter getIndexWriter(String path) {
        
        if (path == null || path.trim().length() == 0 || indexWriters == null)
            return null;
        
        return (IndexWriter)indexWriters.get(path);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public void addDocument(Document doc) {
        
	    try {
	        String currentIndexPathChild = getCurrentIndexPathChild();
	        IndexWriter indexWriter = getIndexWriter(currentIndexPathChild);
	        indexWriter.addDocument(doc);
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
    }

	public void addIndexes(Directory[] dirs) {
        
	    try {
	        String nextIndexPathChild = getNextIndexPathChild();
	        IndexWriter indexWriter = getIndexWriter(nextIndexPathChild);
	        indexWriter.addIndexes(dirs);
	    }
	    catch (Exception e) {
	        log.error(e);
	    }
	}
	
	///////////////////////////////////////////////////////////////////////////
	
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
	                    // These entries maybe reside in index path.
			            for (int i = 0; i < pathChildren.length; i++) {
			                String indexPathChild = getIndexPathChild(i);
			                
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
			            
			            // These entries maybe reside in search path.
			            for (int i = 0; i < pathChildren.length; i++) {
			                String searchPathChild = getSearchPathChild(getCurrentSearchPath(), i);
			                
			                if (IndexReader.indexExists(searchPathChild)) {
				                IndexReader reader = IndexReader.open(searchPathChild);
				                
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
		                int index = (int)(System.currentTimeMillis() % pathChildren.length);
		                String indexPathChild = getIndexPathChild(index);
		                
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
     * Merge index files from index path to search file.
     * 
     */
    private void mergeIndexes() {
        
        log.info("Mergering-indexes beginning.");
        long fromTime = System.currentTimeMillis();
        
        for (int i = 0; i < pathChildren.length; i++) {
            int counter = pathChildren[i];
            
            try {
                String indexPathChild = getIndexPathChild(counter);
                IndexReader readerIndex = null;
                if (IndexReader.indexExists(indexPathChild))
                    readerIndex = IndexReader.open(indexPathChild);
                
                String currentSearchPathChild = getSearchPathChild(getCurrentSearchPath(), counter);
                IndexReader readerSearch = null;
                if (IndexReader.indexExists(currentSearchPathChild))
                    readerSearch = IndexReader.open(currentSearchPathChild);
                
                if (readerIndex == null && readerSearch == null)
                    continue;
                int size = 1;
                if (readerIndex != null && readerSearch != null)
                    size = 2;
                IndexReader[] readers = new IndexReader[size];
                if (size == 2) {
                    readers[0] = readerIndex;
                    readers[1] = readerSearch;
                }
                else {
                    if (readerIndex != null)
                        readers[0] = readerIndex;
                    else
                        readers[0] = readerSearch;
                }
                
                String otherSearchPathChild = getSearchPathChild(getOtherSearchPath(), counter);
                IndexWriter writer = createSearchWriter(otherSearchPathChild, true);
                writer.addIndexes(readers);
                writer.optimize();
                writer.close();
                writer = null;
                
                if (readerSearch != null) {
                    readerSearch.close();
                    readerSearch = null;
                }
                
                if (readerIndex != null) {
                    readerIndex.close();
                    readerIndex = null;
                }
                
                SearchUtils.deleteFilesOfDir(indexPathChild);
            }
            catch (IOException e) {
                log.error(e);
            }
        }
        
        long toTime = System.currentTimeMillis();
        long cost = toTime - fromTime;
        log.info("Mergering-indexes ended.Cost is: " + cost);
    }
    
    /**
     * Reset all Searchables.
     * 
     */
    private void resetSearch() {
        
        synchronized (SearchUtils.searcherLock) {           
	        // Reset search path
	        resetSearchPath();
	        
	        // Reset searcher
	        resetSearcher();
	      
	        // Detelet all files of other search pach.
	        deleteFilesOfOtherSearchPath();       
        }
    }
    
    /**
     * Decide whether to reset castor.
     * @param properties
     * @return
     */
    private boolean needResetCastor(Map properties) {
    	
    	int blockSize = 1;
        String blockSizeParam = SearchConfig.getProperty("search.blockSize");
        try {
            blockSize = Integer.parseInt(blockSizeParam);
        }
        catch (Exception e) { }

        int blockCount = 1;
        String blockCountParam = SearchConfig.getProperty("search.blockCount");
        try {
            blockCount = Integer.parseInt(blockCountParam);
        }
        catch (Exception e) { }
        
        int recordCountIfSaturation = blockSize * blockCount; // Maybe a fault.
        
        boolean needResetCastor = false;
        
        if (properties != null && properties.containsKey(PROPERTY_NAME_RECORD_COUNTS)) {
        	int[] recordCounts = (int[])properties.get(PROPERTY_NAME_RECORD_COUNTS);
        	
        	if (recordCounts != null) {        		
                int count = 0;
        		for (int i = 0; i < recordCounts.length; i++) {
        			int recordCount = recordCounts[i];
        			if (recordCount < recordCountIfSaturation)
        				count++;
        		}
        		
        		if (count == recordCounts.length)
        			needResetCastor = true;
        	}
        }
        
    	return needResetCastor;
    }
    
    private void resetCastor() {
    	mergeAllIndexes();
    	resetSearchToIndexPath();
    }
    
    private void mergeAllIndexes() {
    	
        log.info("Mergering all indexes beginning.");
        long fromTime = System.currentTimeMillis();
        
        try {
	        // Create index writer for repository path
	        IndexWriter writer = createIndexWriter(repositoryPath, true);
	        
	        // Create all index readers for search directories
	        IndexReader[] readers = new IndexReader[pathChildren.length];
	        for (int i = 0; i < pathChildren.length; i++) {
	            int counter = pathChildren[i];	            
	        	String searchPathChild = getSearchPathChild(getCurrentSearchPath(), counter);
	        	
	        	IndexReader reader = null;
	        	if (IndexReader.indexExists(searchPathChild)) {
	        		reader = IndexReader.open(searchPathChild);
	        		if (reader != null) {
	        			readers[i] = reader;
	        		}
	        	}
	        	else {
	        		continue;
	        	}        	
	        }
	        
	        // If a reader is null(that is,a search directory is empty), it maybe crash.
	        // So ......
	        
	        // Merge all indexes into repository path and close index writer
	        writer.addIndexes(readers);
	        writer.optimize();
	        writer.close();
	        writer = null;
	        
	        // Now close all index readeres
	        for (int i = 0; i < readers.length; i++) {
	        	IndexReader reader = readers[i];
	        	if (reader != null) {
	        		reader.close();
	        		reader = null;
	        	}
	        }
	        readers = null;
        }
        catch (Exception e) {
        	log.error(e);
        }
    	
        long toTime = System.currentTimeMillis();
        long cost = toTime - fromTime;
        log.info("Mergering all indexes ended.Cost is: " + cost);
    }
    
    private void resetSearchToIndexPath() {
    	
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
	        
	        // Delete all files of current search path.
	        deleteFilesOfCurrentSearchPath();
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Delete all files of other search path.
     *
     */
    private void deleteFilesOfOtherSearchPath() {
        
        for (int i = 0; i < pathChildren.length; i++) {
            String searchPathChild = getOtherSearchPath() + File.separator + i;
            SearchUtils.deleteFilesOfDir(searchPathChild);
        }
    }
    
    private void deleteFilesOfCurrentSearchPath() {
    	
        for (int i = 0; i < pathChildren.length; i++) {
            String searchPathChild = getCurrentSearchPath() + File.separator + i;
            SearchUtils.deleteFilesOfDir(searchPathChild);
        }   	
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    public Searcher getSearcher() {
        
        if (searcher == null) {
            initSearcher();
        }
        
        return searcher;
    }
    
	public void initSearcher() {
		
	    try {
	        Searchable[] searchables = new Searchable[pathChildren.length];
	        
	        int initCount = 0;
	        for (int i = 0; i < pathChildren.length; i++) {
	            String searchPathChild = getSearchPathChild(getCurrentSearchPath(), i);
	            if (IndexReader.indexExists(searchPathChild)) {
	                searchables[i] = new IndexSearcher(searchPathChild);
	                initCount++;
	            }
	            else {
	               break;
	            }
	        }
	        
	        if (initCount == pathChildren.length) {
	            searcher = new ParallelMultiSearcher(searchables);
	        }
	        else {
	            for (int i = 0; i < searchables.length; i++) {
	                Searchable searchable = searchables[i];
	                if (searchable != null) {
	                    searchable.close();
	                    searchables[i] = null;
	                }
	            }
	            searchables = null;	            
	        }
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

}
