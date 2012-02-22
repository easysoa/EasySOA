package org.easysoa.records.assertions;

/**
 * Metric class to store metric, expected and actual value
 * 
 * @author jguillemotte
 *
 */
public class Metric {

    private String metric;
    
    private String expectedValue;
    
    private String actualValue;

    /**
     * 
     */
    Metric(String metric){
        this(metric, "", "");
    }

    /**
     * 
     */
    Metric(String metric, String expectedValue, String actualValue){
        this.metric = metric;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }
    
    /**
     * 
     * @return
     */
    public String getMetric() {
        return metric;
    }

    /**
     * 
     * @param metric
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * 
     * @return
     */
    public String getExpectedValue() {
        return expectedValue;
    }

    /**
     * 
     * @param expectedValue
     */
    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    /**
     * 
     * @return
     */
    public String getActualValue() {
        return actualValue;
    }

    /**
     * 
     * @param actualValue
     */
    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
    
    
    
}
