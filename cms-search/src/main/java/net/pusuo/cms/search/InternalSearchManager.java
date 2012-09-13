/*
 * Created on 2006-1-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface InternalSearchManager {
	
	/**
	 * Get Analyzer for index.
	 * @return
	 */
	public Analyzer getAnalyzer();
	
	/**
	 * Adds a document to this index
	 * @param doc
	 */
	public void addDocument(Document doc);
	
    /**
     * Merges all segments from an array of indexes into this index.
     * @param dirs
     */
	public void addIndexes(Directory[] dirs);
}
