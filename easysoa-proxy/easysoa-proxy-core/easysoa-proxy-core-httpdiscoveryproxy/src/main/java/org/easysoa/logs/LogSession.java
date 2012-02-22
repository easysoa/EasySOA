/**
 * 
 */
package org.easysoa.logs;

import org.apache.log4j.Logger;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;

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

    //
    private Object logger;
    
    //
    private Report report;
    
    public LogSession(){
        this(null);
    }
        
    public LogSession(Report report){
        this.report = report;
    }
    
    public Logger getLogger(){
        return null;
    }
    
    public void setReport(Report report){
        this.report = report;
    }
    
    public Report getReport(){
        return null;
    }
    
    public void save() throws Exception {
        // Call the File store
        ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
        erfs.saveAssertionReport(report);
    }
    
}
