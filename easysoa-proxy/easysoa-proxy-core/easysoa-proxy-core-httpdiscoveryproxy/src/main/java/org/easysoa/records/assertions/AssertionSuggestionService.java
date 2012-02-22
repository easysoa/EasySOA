/**
 * 
 */
package org.easysoa.records.assertions;

import org.easysoa.records.assertions.StringAssertion.StringAssertionMethod;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;

/**
 * Service to build 'asr' files. asr files contains suggestions for assertions to execute on response fields. 
 * 
 * @author jguillemotte
 *
 */
public class AssertionSuggestionService {
   
    /**
     * Suggest default assertions. To be used to check the out message of replayed records without modifications
     * @return Default <code>AssertionSuggestions</code>
     */
    public AssertionSuggestions suggestAssertions(){
        AssertionSuggestions suggestions = new AssertionSuggestions();
        // TODO : What assertions to suggest by default ???
        // lenght, differences ...
        suggestions.addAssertion(new StringAssertion("assertion_string_length", StringAssertionMethod.LENGTH));
        return suggestions;
    }
    
    /**
     * Suggest assertions 
     * @param fieldSuggestions
     * @return
     */
    // TODO : rework this method to have a real suggestion service associated with a field name
    public AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions){
        int i = 0;
        AssertionSuggestions suggestions = new AssertionSuggestions();
        // 
        for(TemplateField field : fieldSuggestions.getTemplateFields()){
            // If field equality is true
            if(field.isFieldEquality()){
                // TODO : How to suggest an other type of assertion ???
                StringAssertion assertion = new StringAssertion("assertion_" + i + "_" + field.getFieldName());
                suggestions.addAssertion(assertion);
                i++;
            }
        }
        return suggestions;
    }
    
    /**
     * Suggest assertions for an output message, assertions are not only concerning the fields but also headers and/or message content
     * @param message <code>OutMessage</code>
     * @return
     */
    /*public AssertionSuggestions suggestAssertions(OutMessage message){
        // TODO : add code
        return null; 
    }*/
    
    
}
