/*
 * Created on 2006-3-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.castor;

import net.pusuo.cms.search.AbstractSearchManager;
import net.pusuo.cms.search.SearchConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Alfred.Yuan
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class IndexCastor {

    private static Log log = LogFactory.getLog(IndexCastor.class);

    /**
     * Property names for interaction between IndexCastor and SearchManager.
     */
    public static final String PROPERTY_NAME_SUM_OF_RECORDS = "sumOfRecords";
    public static final String PROPERTY_NAME_RECORD_COUNTS = "recordCounts";
    public static final String PROPERTY_NAME_NEED_RESET_CASTOR = "needResetCastor";

    protected Analyzer analyzer = null;

    protected AbstractSearchManager searchManager = null;

    /**
     * Index path, but there maybe not index files, and this depends on castor implement.
     */
    public static final String indexPath = SearchConfig.getHome() + File.separator + "index";

    /**
     * Real search path - repositoryPath,and increased index path - incrementPath
     */
    public static final String repositoryPath = indexPath + File.separator + "repository";
    public static final String incrementPath = indexPath + File.separator + "increment";

    public IndexCastor() {
    }

    /**
     * Set analyzer, and this method must be invoked before others.
     *
     * @param analyzer
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Set searchManager, and this method must be invoked before others.
     *
     * @param searchManager
     */
    public void setSearchManager(AbstractSearchManager searchManager) {
        this.searchManager = searchManager;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * When a new task starts, castor maybe do something.
     */
    public abstract Map startTask(Map properties);

    /**
     * When a task is going to end, castor maybe do something.
     */
    public abstract Map endTask(Map properties);

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create a IndexWriter for index path.
     */
    protected IndexWriter createIndexWriter(String indexPath, boolean compoundFile) {

        if (indexPath == null || indexPath.trim().length() == 0)
            return null;

        IndexWriter writer = null;

        try {
            boolean create = !IndexReader.indexExists(indexPath);
            writer = new IndexWriter(indexPath, analyzer, create);
            writer.setUseCompoundFile(compoundFile);
            writer.mergeFactor = 2000;
            writer.maxMergeDocs = 999999999;
            writer.minMergeDocs = 1;
        } catch (IOException e) {
            writer = null;
            log.error(e);
        }

        return writer;
    }

    /* (non-Javadoc)
     * @see com.hexun.cms.search.InternalSearchManager#addDocument(org.apache.lucene.document.Document)
     */
    public abstract void addDocument(Document doc);

    /* (non-Javadoc)
    * @see com.hexun.cms.search.InternalSearchManager#addIndexes(org.apache.lucene.store.Directory[])
    */
    public abstract void addIndexes(Directory[] dirs);

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get searcher.
     */
    public abstract Searcher getSearcher();

}
