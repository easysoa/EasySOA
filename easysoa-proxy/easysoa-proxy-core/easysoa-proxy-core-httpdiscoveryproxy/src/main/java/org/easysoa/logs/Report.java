/**
 * 
 */
package org.easysoa.logs;

/**
 * Report interface
 * 
 * @author jguillemotte
 *
 */
public interface Report {

    public String getReportName();
    
    public String generateXMLReport();
    
    public String generateTXTReport();
    
}
