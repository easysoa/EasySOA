/**
 * 
 */
package org.easysoa.logs;

import java.util.List;

/**
 * @author jguillemotte
 *
 */
public interface Report {

    public String getReportName();
    
    //public void setReportLines(List<ReportLine> reportLines);
    
    //public void addReportLine(ReportLine reportLine);
    
    public String generateXMLReport();
    
    public String generateTXTReport();
    
}
