/**
 * 
 */
package org.easysoa.records.assertions;

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
