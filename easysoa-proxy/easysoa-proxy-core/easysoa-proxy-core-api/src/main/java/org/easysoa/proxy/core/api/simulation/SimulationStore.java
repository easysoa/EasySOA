/**
 * EasySOA Proxy Core
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

package org.easysoa.proxy.core.api.simulation;

import java.util.HashMap;
import java.util.Map;

import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.records.ExchangeRecord;

/**
 * A simulation store is 
 * 
 * @author jguillemotte
 *
 */
public class SimulationStore {

    /**
     * Ordering methods
     * @author jguillemotte
     *
     */
    public enum OrderingMethod {
        DECREASING_CORRELATION_LEVEL
    }

    // Store name
    private String storeName;
    
    // A map to associate records with templateFieldSuggestions
    private Map<ExchangeRecord, TemplateFieldSuggestions> recordList;
    
    /**
     * 
     * @param storeName
     * @throws IllegalArgumentException
     */
    public SimulationStore(String storeName) throws IllegalArgumentException {
        if(storeName == null || "".equals(storeName)){
            throw new IllegalArgumentException("StoreName parameter must not be null or empty");
        }
        this.storeName = storeName;
        recordList = new HashMap<ExchangeRecord, TemplateFieldSuggestions>();
    }
   
    /**
     * 
     * @return
     */
    public String getStoreName(){
       return this.storeName; 
    }
    
    /**
     * Sort and order the store
     * @param ordering
     */
    public void orderBy(OrderingMethod ordering){
        if(OrderingMethod.DECREASING_CORRELATION_LEVEL.equals(ordering)){
            //Collections.sort(fieldList, new DecreasingCorrelationLevelComparator());
        }
    }
    
    /**
     * Add a record and suggestions values in the store
     * @param record The exchange record to add
     * @param suggestions The associated field template suggestions
     */
    public void addRecordSuggestions(ExchangeRecord record, TemplateFieldSuggestions suggestions){
        if(record != null && suggestions != null){
            this.recordList.put(record, suggestions);
        }        
    }

    /**
     * 
     * @return
     */
    public Map<ExchangeRecord, TemplateFieldSuggestions> getRecordList(){
        return this.recordList;
    }
    
}
