/**
 * 
 */
package org.easysoa.logs;

import java.util.ArrayList;
import java.util.List;
import org.easysoa.records.assertions.AssertionResult;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Generate an assertion report from a collection of assertion results 
 * 
 * @author jguillemotte
 *
 */
public class AssertionReport implements Report {

    // Report name
    private String reportName;

    // Assertion result list
    private List<AssertionResult> assertionResults;
    
    /**
     * 
     * @param reportName
     */
    public AssertionReport(String reportName){
        this(reportName, null);
    }
    
    /**
     * 
     * @param reportName
     * @param assertionResults
     */
    public AssertionReport(String reportName, List<AssertionResult> assertionResults){
        this.reportName = reportName;
        if(assertionResults != null){
            this.assertionResults = assertionResults;
        } else {
            this.assertionResults = new ArrayList<AssertionResult>();            
        }
    }    

    /**
     * Returns the report name.
     * @return
     */
    public String getReportName() {
        return reportName;
    }    
    
    /**
     * Add an assertion result in the report
     * @param assertionResult The assertionResult to add. If this parameter is null, nothing is added.
     */
    public void addAssertionResult(AssertionResult assertionResult){
        if(assertionResult != null){
            this.assertionResults.add(assertionResult);
        }
    }
    
    /**
     * 
     * @param assertionResults
     */
    public void AddAssertionResult(List<AssertionResult> assertionResults){
        if(assertionResults != null){
            for(AssertionResult assertionResult : assertionResults){
                this.assertionResults.add(assertionResult);
            }
        }
    }
    
    /**
     * Generate a TXT report
     */
    public String generateTXTReport() {
        // TODO Add serialization to obtain txt format
        return null;
    }

    /**
     * Generate an XML report
     * @return The assertion report as an xml string
     */
    public String generateXMLReport(){
        XStream xstream = new XStream(new StaxDriver());
        // Optional alias
        // xstream.alias("person", Person.class);
        //xstream.alias("phonenumber", PhoneNumber.class);
        return xstream.toXML(this);
    }
    
}
