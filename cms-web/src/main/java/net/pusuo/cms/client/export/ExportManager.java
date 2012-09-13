/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.export;

import java.io.Writer;
import java.util.List;


/**
 * @author huaiwenyuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ExportManager {

    /**
     * Export newes to file.
     * @param newses
     * @param fileName
     * @throws ExportException
     */
    public void exportNewses(List newses, String fileName) throws ExportException;
    /**
     * Export newes to writer.
     * @param newses
     * @param writer
     * @throws ExportException
     */
    public void exportNewses(List newses, Writer writer) throws ExportException;
    
    /**
     * Export subjects to file.
     * @param subjects
     * @param fileName
     * @throws ExportException
     */
    public void exportSubjects(List subjects, String fileName) throws ExportException;
    /**
     * Export subjects to writer.
     * @param subjects
     * @param writer
     * @throws ExportException
     */
    public void exportSubjects(List subjects, Writer writer) throws ExportException;

    /**
     * Export items to file.
     * @param items
     * @param fileName
     * @throws ExportException
     */
    public void exportItems(List items, String fileName) throws ExportException;
    /**
     * Export items to writer.
     * @param items
     * @param writer
     * @throws ExportException
     */
    public void exportItems(List items, Writer writer) throws ExportException;

}
