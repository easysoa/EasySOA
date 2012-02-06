/**
 * 
 */
package org.easysoa.records.assertions;

/**
 * @author jguillemotte
 *
 */
public abstract class AbstractAssertion implements Assertion {

    // Assertion id
    protected String id;
    
    /**
     * Default constructor
     * @param id Id or unique name for the assertion
     */
    public AbstractAssertion(String id) {
        this.id = id;
    }
    
    @Override
    public String getID(){
        return this.id;
    }
    
}
