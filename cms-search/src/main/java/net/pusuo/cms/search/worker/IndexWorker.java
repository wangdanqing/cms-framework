/*
 * Created on 2005-12-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.worker;

import com.hexun.cms.search.util.SearchUtils;
import net.pusuo.cms.search.AbstractSearchManager;
import net.pusuo.cms.search.SearchConfig;
import net.pusuo.cms.search.entry.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;

/**
 * @author Alfred.Yuan
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class IndexWorker {

    private static Log log = LogFactory.getLog(IndexWorker.class);

    /**
     * Keep a reference to SearchManager.
     */
    private AbstractSearchManager searchManager = null;

    /**
     * Set the reference to SearchManager,and this method must be invoked before others.
     *
     * @param searchManager
     */
    public void setSearchManager(AbstractSearchManager searchManager) {
        this.searchManager = searchManager;
    }

    /**
     * Get the reference to SearchManager.
     *
     * @return
     */
    public AbstractSearchManager getSearchManager() {
        return this.searchManager;
    }

    /**
     * Get the analyzer for index.
     *
     * @return
     */
    public Analyzer getAnalyzer() {
        Analyzer analyzer = null;
        try {
            analyzer = searchManager.getAnalyzer();
        } catch (Exception e) {

        }

        return analyzer;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * The number of messages to retrieve at once during index rebuilds.
     */
    protected int blockSize = 1000;

    /**
     * The max number of blocks that a task can handle once.
     */
    protected int blockCount = 100;

    /**
     * Property name of last indexed id for worker.
     */
    protected String lastIndexedPropertyName = null;

    /**
     * Maintains the index that the last index took place.
     */
    protected int lastIndexed = 0;

    /**
     * Constructor
     */
    public IndexWorker() {

        // Configuration for parameters.
        String blockSizeParam = SearchConfig.getProperty("search.blockSize");
        try {
            blockSize = Integer.parseInt(blockSizeParam);
        } catch (Exception e) {
        }

        String blockCountParam = SearchConfig.getProperty("search.blockCount");
        try {
            blockCount = Integer.parseInt(blockCountParam);
        } catch (Exception e) {
        }
    }

    /**
     * Get total count of subjects that will be indexed.
     * Subclass must implement this method.
     *
     * @param fromId
     * @return
     */
    public abstract int getTotalCount(int fromId);

    /**
     * Get maximum id of subject that will be indexed.
     * Subclass must implement this method.
     *
     * @return
     */
    public abstract int getMaxId();

    /**
     * Get minimum id of subject that will be indexed.
     * Subclass must implement this method.
     *
     * @return
     */
    public abstract int getMinId();

    /**
     * Retrive block data for index.
     * Subclass must implement this method.
     *
     * @param fromId
     * @return
     */
    public abstract List getBlockData(int fromId);

    /**
     * Manually update the index to include all new entries since the last index.
     *
     * @return
     */
    public synchronized int updateIndex() {

        int fromId = lastIndexed;

        // Number of record that had been indexed in this task.
        int recordCount = 0;

        try {
            Directory ramDirectory = new RAMDirectory();
            IndexWriter ramIndexWriter = new IndexWriter(ramDirectory, getAnalyzer(), true);

            int maxId = getMaxId();

            // Time a new block starts.
            long fromTime = System.currentTimeMillis();

            int count = 0; // Number of blocks that had been indexed in this task.        
            while (fromId < maxId) {
                // Get block data
                long fromDBTime = System.currentTimeMillis();
                List entries = getBlockData(fromId);
                long toDBTime = System.currentTimeMillis();
                long costDB = toDBTime - fromDBTime;

                if (entries == null || entries.size() == 0)
                    break;

                recordCount += entries.size();

                // Index block data
                for (int i = 0; i < entries.size(); i++) {
                    Entry entry = (Entry) entries.get(i);
                    addEntryToIndex(entry, ramIndexWriter);

                    fromId = entry.getId();
                    lastIndexed = fromId;

                    // memory leak!
                    entry = null;
                }

                // Flush everything buffered in RAMDirectory into FSDirectory.
                ramIndexWriter.optimize();
                ramIndexWriter.close();
                ramIndexWriter = null;

                searchManager.addIndexes(new Directory[]{ramDirectory});

                ramIndexWriter = new IndexWriter(ramDirectory, getAnalyzer(), true);

                // Write property value of lastIndexed into file.
                SearchConfig.setProperty(lastIndexedPropertyName, String.valueOf(lastIndexed));

                // Time the block ends.
                long toTime = System.currentTimeMillis();
                long cost = toTime - fromTime;
                fromTime = toTime;
                log.info(this + " - cost of indexing a block of data (millis): "
                        + cost + " (" + costDB + ").");

                // Now we exit this task to merge all segments.
                count++;
                if (count >= blockCount)
                    break;
            }

            System.gc();
        } catch (Exception e) {
            log.error(e);
        }

        return recordCount;
    }

    /**
     * Indexes an indivual entry. The writer is assumed to be open when
     * passed in and will remain open after the method is done executing.
     */
    public static synchronized void addEntryToIndex(Entry entry, IndexWriter writer)
            throws IOException {

        if (entry == null || writer == null)
            return;

        Document doc = SearchUtils.entry2Document(entry);
        if (doc != null)
            writer.addDocument(doc);
    }

    /**
     * Deletes a entry from the index.
     */
    public static synchronized boolean removeEntryFromIndex(Entry entry, IndexReader reader) {

        if (entry == null || reader == null)
            return false;

        boolean result = true;

        try {
            Term entryIdTerm = new Term(Entry.FIELD_NAME_ID,
                    String.valueOf(entry.getId()));
            reader.delete(entryIdTerm);
        } catch (Exception e) {
            result = false;
            log.error(e);
        }

        return result;
    }

}
