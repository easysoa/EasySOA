/**
 * EasySOA Proxy
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

package org.easysoa.records.assertions;

/**
 * Metric class to store metric, expected and actual value
 * 
 * @author jguillemotte
 *
 */
public class AssertionMetric {

    //
    private String metric;
    
    //
    private String expectedValue;
    
    //
    private String actualValue;

    /**
     * 
     */
    AssertionMetric(String metric){
        this(metric, "", "");
    }

    /**
     * 
     */
    AssertionMetric(String metric, String expectedValue, String actualValue){
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
