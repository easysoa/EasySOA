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
package org.easysoa.records.assertions;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;
import org.easysoa.template.TemplateFieldSuggestions;

import com.openwide.easysoa.message.OutMessage;

/**
 * To compare a replayed request with the recorded original 
 * 
 * @author jguillemotte
 *
 */
public class AssertionEngineImpl implements AssertionEngine {
    
    private static final Logger testReportLogger = Logger.getLogger("testReportLogger");
    
    // Add methods to configure Assertions
    // Not here, define a class for each type of assertions
    
    // Assertion objects must be configured in this class
    // How to store assertions to execute for a specific field, response ???
    
    // Several assertions for a single object (length, lehvenstein ...)
    // In an hashmap => id key, by default a method add to add a simple string assertion (length)

    // asr files to define assertions. how to generate these files
    
    // key is the field key associated to a list of assertions
    // How to do when assertion is processed on the whole response ?
    // private HashMap<String, List<Assertion>> assertionList;
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#suggestAssertions(org.easysoa.template.TemplateFieldSuggestions, java.lang.String, java.lang.String)
     */
    @Override
    public AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions, String recordID, String storeName) throws Exception {
        // Get assertions suggestions
        AssertionSuggestionService assertionService = new AssertionSuggestionService();
        AssertionSuggestions suggestions = assertionService.suggestAssertions(fieldSuggestions);
        // Saving asr file
        ProxyExchangeRecordFileStore fileStore = new ProxyExchangeRecordFileStore();
        fileStore.saveAssertionSuggestions(suggestions, recordID, storeName);
        return suggestions;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#executeAssertions(org.easysoa.records.assertions.AssertionSuggestions, com.openwide.easysoa.message.OutMessage, com.openwide.easysoa.message.OutMessage)
     */
    @Override
    public List<AssertionResult> executeAssertions(AssertionSuggestions assertionSuggestions, OutMessage originalMessage, OutMessage replayedMessage){
        ArrayList<AssertionResult> result = new ArrayList<AssertionResult>();
        for(Assertion assertion : assertionSuggestions.getAssertions()){
            result.add(executeAssertion(assertion, originalMessage, replayedMessage));
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#executeAssertion(org.easysoa.records.assertions.Assertion, com.openwide.easysoa.message.OutMessage, com.openwide.easysoa.message.OutMessage)
     */
    @Override
    public AssertionResult executeAssertion(Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result = assertion.check(originalMessage, replayedMessage);
        return result;
    }
    
}
