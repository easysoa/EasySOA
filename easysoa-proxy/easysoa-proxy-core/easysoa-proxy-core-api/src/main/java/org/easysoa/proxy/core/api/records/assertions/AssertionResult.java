/**
 * EasySOA HTTP Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.records.assertions;

import java.util.HashMap;
import java.util.Map;

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
    
    @SuppressWarnings("rawtypes")
    private Class assertionType;
    
    // Result status
    private AssertionResultStatus status;
    
    // Comment, message, to use when there is a diff between original and replayed message
    private String message;
    
    // Metrics
    private Map<String, AssertionMetric> metrics;
    
    /**
     * 
     * @param status
     */
    public AssertionResult(@SuppressWarnings("rawtypes") Class assertionType, AssertionResultStatus status){
        this(assertionType, status, "");
    }
    
    /**
     * 
     * @param status
     * @param message  //TODO : maybe best to find another param name
     */
    public AssertionResult(@SuppressWarnings("rawtypes") Class assertionType, AssertionResultStatus status, String message){
        this.assertionType = assertionType;
        this.status = status;
        this.message = message;
        this.metrics = new HashMap<String, AssertionMetric>();        
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
     * 
     * @param message
     */
    public void setMessage(String message){
        this.message = message;
    }
    
    /**
     * 
     * @param metricName
     * @param metricValue
     */
    public void addMetric(String metricName, String metricValue, String expectedValue, String actualValue){
        addMetric(metricName, new AssertionMetric(metricValue, expectedValue, actualValue));
    }

    /**
     * 
     * @param metricName
     * @param metric
     */
    public void addMetric(String metricName, AssertionMetric metric){
        this.metrics.put(metricName, metric);
    }    
    
    /**
     * Set metrics
     * @param metrics
     */
    public void setMetrics(Map<String,AssertionMetric> metrics){
        this.metrics = metrics;
    }
    
    /**
     * Get metrics
     * @return
     */
    public Map<String,AssertionMetric> getMetrics(){
        return this.metrics;
    }

    /**
     * Get assertion type
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class getAssertionType(){
        return this.assertionType;
    }
    
}
