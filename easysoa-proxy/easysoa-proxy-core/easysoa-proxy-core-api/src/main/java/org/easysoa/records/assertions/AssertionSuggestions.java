/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * An assertion suggestion can contains several assertions
 * 
 * @author jguillemotte
 *
 */
public class AssertionSuggestions {

    // Field to which assertions will be applied
    private String referenceField;
    
    // Assertion list
    private List<Assertion> assertionList;
    
    /**
     * Default constructor
     */
    public AssertionSuggestions(){
        this("");
    }
    
    /**
     * Constructor with reference field
     */
    public AssertionSuggestions(String referenceField){
        this.referenceField = "";
        assertionList = new ArrayList<Assertion>();
    }    
    
    /**
     * 
     * @param referenceField
     */
    public void setReferenceField(String referenceField){
        if(referenceField != null){
            this.referenceField = referenceField;
        } else {
            this.referenceField = "";
        }
    }
    
    /**
     * 
     * @return
     */
    public String getReferenceField(){
        return this.referenceField;
    }
    
    /**
     * Set the assertion list.
     * @param assertionList The assertion list to set. If this parameter is set to null, a new empty ArrayList is set.
     */
    public void setAssertions(List<Assertion> assertionList) {
        if(assertionList == null){
            this.assertionList = new ArrayList<Assertion>();
        }
    }
    
    /**
     * Add an assertion in the list
     * @param assertion The assertion to add in the list
     * @throws IllegalArgumentException If the assertion to add is null
     */
    public void addAssertion(Assertion assertion) throws IllegalArgumentException {
        if(assertion == null){
            throw new IllegalArgumentException("assertion must not be null");
        }
        this.assertionList.add(assertion);
    }
    
    /**
     * Returns the assertion list
     * @return The assertion list
     */
    public List<Assertion> getAssertions(){
        return this.assertionList;
    }
    
}
