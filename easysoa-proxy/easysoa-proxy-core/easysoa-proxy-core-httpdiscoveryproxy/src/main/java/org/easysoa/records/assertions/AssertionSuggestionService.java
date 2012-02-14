/**
 * 
 */
package org.easysoa.records.assertions;

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
     * Suggest assertions 
     * @param fieldSuggestions
     * @return
     */
    public AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions){
        int i = 0;
        AssertionSuggestions suggestions = new AssertionSuggestions();
        // 
        for(TemplateField field : fieldSuggestions.getTemplateFields()){
            // If field equality is true
            if(field.isFieldEquality()){
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
