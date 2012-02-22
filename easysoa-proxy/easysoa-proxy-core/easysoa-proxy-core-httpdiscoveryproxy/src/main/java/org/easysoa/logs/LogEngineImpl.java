/**
 * 
 */
package org.easysoa.logs;

import java.util.HashMap;
import java.util.Set;

import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;

/**
 * Implementation for log engine
 * 
 * @author jguillemotte
 *
 */
public class LogEngineImpl implements LogEngine {

    // Produce specialized logs and reports
    
    //private String logSession;
    
    // Log session store 
    private HashMap<String, LogSession> logSessions;
    
    public LogEngineImpl(){
        this.logSessions = new HashMap<String, LogSession>();
    }
    
    public void startLogSession(String logSessionName) throws Exception {
        startLogSession(logSessionName, null);
    }
    
    /**
     * 
     * @param logSessionName
     */
    public void startLogSession(String logSessionName, Report report) throws Exception {
        // Start a new log session
        if(logSessionName == null || "".equals(logSessionName)){
            throw new IllegalArgumentException("logSessionName parameter must not be null or empty");
        }
        if(logSessions.containsKey(logSessionName)){
            throw new IllegalArgumentException("logSessionName already exists, cannot start another logSession with the same name");
        }
        this.logSessions.put(logSessionName, new LogSession(report));
    }
    
    /**
     * 
     * @param logSessionName
     */
    public void saveAndRemoveLogSession(String logSessionName) throws Exception{
        logSessions.get(logSessionName).save();
        logSessions.remove(logSessionName);
    }
    
    /**
     * 
     * @param logSessionName
     */
    public void saveLogSession(String logSessionName) throws Exception{
        logSessions.get(logSessionName).save();
    }
    
    /**
     * 
     * @param logSessionName
     * @return
     */
    public LogSession getLogSession(String logSessionName){
        return this.logSessions.get(logSessionName);
    }
    
    /**
     * Returns a set containing the log session names
     * @return
     */
    public Set<String> getLogSessions(){
        return this.logSessions.keySet();
    }
    
    /*@Override
    public void saveReport(AssertionReport assertionReport) throws Exception {
        // Create an assertion report
        // Must be compatible with Jenkins (or sonar) or other existing reporting system (surefire ?)
        
        // Call 2 different serializers : txt and xml to transform assertion object structure in reports
        // Form xml see xStream
        
        // Call the File store
        ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
        erfs.saveAssertionReport(assertionReport);        

    }*/
    
}
