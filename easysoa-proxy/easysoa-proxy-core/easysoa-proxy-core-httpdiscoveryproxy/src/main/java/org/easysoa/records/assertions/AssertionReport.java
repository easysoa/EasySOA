/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easysoa.logs.Report;
import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;

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
        StringBuffer reportString = new StringBuffer();
        reportString.append("Report name : ");
        reportString.append(this.getReportName());
        reportString.append("\n");
        // For each result, write it in the report
        for(AssertionResult assertionResult : this.assertionResults){
            reportString.append("---\n");
            reportString.append("Assertion type : "); 
            reportString.append(assertionResult.getAssertionType());
            reportString.append("\n");    
            reportString.append("Assertion status : ");
            reportString.append(assertionResult.getResultStatus());
            reportString.append("\n");
            reportString.append("Assertion message : ");
            reportString.append(assertionResult.getMessage());
            reportString.append("\n");
            Map<String, AssertionMetric> metrics = assertionResult.getMetrics();
            Set<String> keySet = metrics.keySet();
            reportString.append("Metrics : \n");
            for(String key : keySet){
                AssertionMetric metric = metrics.get(key);
                reportString.append("   * ");
                reportString.append(key);
                reportString.append(" : ");
                reportString.append(metric.getMetric());
                if(AssertionResultStatus.KO.equals(assertionResult.getResultStatus())){
                    reportString.append("\n");
                    reportString.append("Expected value : ");
                    reportString.append(metric.getExpectedValue());
                    reportString.append("\n");
                    reportString.append("Actual value : ");
                    reportString.append(metric.getActualValue());                    
                }
                reportString.append("\n");
            }
        }
        return reportString.toString();
    }

    /**
     * Generate an XML report
     * @return The assertion report as an xml string
     */
    public String generateXMLReport(){
        XStream xstream = new XStream(new StaxDriver());
        // Optional alias to simplfy the xml=> add // xstream.alias("person", Person.class);
        return xstream.toXML(this);
    }
    
}
