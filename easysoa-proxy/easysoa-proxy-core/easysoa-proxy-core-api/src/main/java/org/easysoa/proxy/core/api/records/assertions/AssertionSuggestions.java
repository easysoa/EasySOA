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

/**
 * 
 */
package org.easysoa.proxy.core.api.records.assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An assertion suggestion can contains several assertions
 * 
 * @author jguillemotte
 *
 */
public class AssertionSuggestions {
    
    //  
    public final static String DEFAULT_REFERENCE_FIELD = "default";
    
    // Assertion list
    private Map<String, List<Assertion>> assertionMap;
    
    /**
     * Default constructor
     */
    public AssertionSuggestions(){
        assertionMap = new HashMap<String, List<Assertion>>();
    }
          
    /**
     * Set the assertion list.
     * @param assertionList The assertion list to set. If this parameter is set to null, a new empty ArrayList is set.
     */
    public void setAssertions(String referenceField, List<Assertion> assertionList) {
        if(assertionList == null && referenceField != null){
            // CHeck if the referenceField already exists in the assertionSuggestions
            // if true => add the assertion list to the existing one
            if(this.assertionMap.containsKey(referenceField)){
                
            }
            // else create a new entry with refenceField key and list as value
            else {
                this.assertionMap.put(referenceField, assertionList);
            }
        }
    }
    
    /**
     * Add an assertion in the list
     * @param assertion The assertion to add in the list
     * @throws IllegalArgumentException If the assertion to add is null
     */
    public void addAssertion(String referenceField, Assertion assertion) throws IllegalArgumentException {
        if(referenceField == null || assertion == null){
            throw new IllegalArgumentException("referenceField and assertion params must not be null");
        }
        if(this.assertionMap.containsKey(referenceField)){
            this.assertionMap.get(referenceField).add(assertion);
        } else {
            ArrayList<Assertion> assertions = new ArrayList<Assertion>();
            assertions.add(assertion);
            this.assertionMap.put(referenceField, assertions);
        }
    }
    
    /**
     * Returns the assertion map
     * @return The assertion map
     */
    public Map<String, List<Assertion>> getAssertionMap(){
        return this.assertionMap;
    }
    
    /**
     * Returns the assertions for the specified reference field
     * @param referenceField
     * @return
     */
    public List<Assertion> getAssertions(String referenceField){
        return this.assertionMap.get(referenceField);
    }
    
}
