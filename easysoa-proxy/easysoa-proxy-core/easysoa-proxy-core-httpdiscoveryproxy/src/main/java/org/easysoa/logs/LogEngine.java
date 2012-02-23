/**
 * 
 */
package org.easysoa.logs;

import java.util.Set;

/**
 * @author jguillemotte
 *
 */
public interface LogEngine {
    
    // Or report engine ???
    
    /**
     * Generate a report with assertion results and record it in a report file
     * @param assertionResults List of assertion result
     * @throws Exception If a problem occurs
     */
    //public void generateAssertionReport(List<AssertionResult> assertionResults) throws Exception;

    
    //public void saveReport(AssertionReport assertionReport) throws Exception;
    
 
    public void startLogSession(String logSessionName, Report report) throws Exception;
    
    public void saveAndRemoveLogSession(String logSessionName) throws Exception;
    
    public void saveLogSession(String logSessionName) throws Exception;
    
    public LogSession getLogSession(String logSessionName);
    
    public Set<String> getLogSessions();
    
}
