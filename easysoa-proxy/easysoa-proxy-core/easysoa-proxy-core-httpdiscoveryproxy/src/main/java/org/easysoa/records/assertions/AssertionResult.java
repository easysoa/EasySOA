/**
 * 
 */
package org.easysoa.records.assertions;

/**
 * Class to store result of assertions
 * 
 * @author jguillemotte
 *
 */
public class AssertionResult {
    
    /**
     * Result status enumeration
     */
    public enum AssertionResultStatus {OK, Maybe, KO};
    
    // Result status
    private AssertionResultStatus status;
    
    // Comment, message
    private String message;
    
    // Metrics
    // TODO : add metrics inside this class or as an object reference, always an integer ... maybe not
    private int metrics;
    
    /**
     * 
     * @param status
     */
    AssertionResult(AssertionResultStatus status){
        this(status, "");
    }
    
    /**
     * 
     * @param status
     * @param message  //TODO : maybe best to find another param name
     */
    AssertionResult(AssertionResultStatus status, String message){
        this.status = status;
        this.message = message;
    }    
    
    /**
     * Returns the result status
     * @return The result status
     */
    public AssertionResultStatus getResultStatus(){
        return this.status;
    }
    
    /**
     * Get result message
     * @return
     */
    public String getMessage(){
        return this.message;
    }
    
    /**
     * Set metrics
     * @param metrics
     */
    public void setMetrics(int metrics){
        this.metrics = metrics;
    }
    
    /**
     * Get metrics
     * @return
     */
    public int getMetrics(){
        return this.metrics;
    }

}
