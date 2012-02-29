/**
 * 
 */
package org.easysoa.reports;

/**
 * Report interface
 * 
 * @author jguillemotte
 *
 */
public interface Report {

    /**
     * Returns the report name
     * @return
     */
    public String getReportName();
    
    /**
     * Generate a report formated with XML
     * @return
     */
    public String generateXMLReport();
    
    /**
     * Generate a report formated as a text
     * @return
     */
    public String generateTXTReport();
    
}
