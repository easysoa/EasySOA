/**
 * 
 */
package org.easysoa.logs;

import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.reports.Report;

/**
 * Contains all the stuff required for a log session
 * - Loggers
 * - reports
 * - ...
 * 
 * @author jguillemotte
 *
 */
public class LogSession {
    
    // Report
    private Report report;
    
    /**
     * Default constructor
     */
    public LogSession(){
        this(null);
    }

    /**
     * 
     * @param report
     */
    public LogSession(Report report){
        this.report = report;
    }
    
    /**
     * 
     * @param report
     */
    public void setReport(Report report){
        this.report = report;
    }
    
    /**
     * 
     * @return
     */
    public Report getReport(){
        return this.report;
    }
    
    /**
     * Save the logSession
     * @throws Exception
     */
    public void save() throws Exception {
        // Call the File store
        ProxyFileStore erfs = new ProxyFileStore();
        erfs.saveReport(report);
    }
    
}
