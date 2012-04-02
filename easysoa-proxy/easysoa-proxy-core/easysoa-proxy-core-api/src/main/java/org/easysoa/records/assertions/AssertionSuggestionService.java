/**
 * 
 */
package org.easysoa.records.assertions;

import org.easysoa.records.assertions.StringAssertion.StringAssertionMethod;
import org.easysoa.template.AbstractTemplateField;
import org.easysoa.template.TemplateFieldSuggestions;

/**
 * Service to build 'asr' files. asr files contains suggestions for assertions to execute on response fields. 
 * 
 * @author jguillemotte
 *
 */
public class AssertionSuggestionService {
   
    // TODO : Add a configurable list of suggestions to suggest ?
    
    /**
     * Suggest default assertions. To be used to check the out message of replayed records without modifications
     * @return Default <code>AssertionSuggestions</code>
     */
    public AssertionSuggestions suggestAssertions(){
        AssertionSuggestions suggestions = new AssertionSuggestions();
        // TODO : What assertions to suggest by default ???
        // lenght, differences ...
        suggestions.addAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD, new StringAssertion("assertion_string_length", StringAssertionMethod.LENGTH));
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
        for(AbstractTemplateField field : fieldSuggestions.getTemplateFields()){
            // If field equality is true
            if(field.isFieldEquality()){
                // TODO : How to suggest an other type of assertion ???
                StringAssertion assertion = new StringAssertion("assertion_" + i + "_" + field.getFieldName());
                suggestions.addAssertion(field.getFieldName(), assertion);
                i++;
            }
        }
        return suggestions;
    }
    
}
