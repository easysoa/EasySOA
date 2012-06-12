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
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.template.TemplateFieldSuggestions;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.message.OutMessage;

/**
 * To compare a replayed request with the recorded original 
 * 
 * @author jguillemotte
 *
 */


@Scope("composite")
public class AssertionEngineImpl implements AssertionEngine {
    
    // Specific logger for assertions
    private static final Logger reportLogger = Logger.getLogger("reportLogger");
    
    // Assertion reference
    @Reference
    protected List<Assertion> assertions;

    // TODO : how to make an efficient field assertion system
    // - First solution : create a special assertion with a field and specific assertion to use
    // - add a field info in the assertion interface => all the assertion receive the field info. If no field specified, default assertion on entire message content is performed 
    // Maybe the second solution is the best
    
    // Another thing to do : make an assertion class for one mtehod only =>  
    
    
    // TODO : Reference to log engine
    // To generate reports about executed assertions
    
    // Add methods to configure Assertions
    // Not here, define a class for each type of assertions
    
    // Assertion objects must be configured in this class
    // How to store assertions to execute for a specific field, response ???
    
    // Several assertions for a single object (length, lehvenstein ...)
    // In an hashmap => id key, by default a method add a simple string assertion (length)

    // asr files to define assertions. how to generate these files
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#suggestAssertions(org.easysoa.template.TemplateFieldSuggestions, java.lang.String, java.lang.String)
     */
    @Override
    public AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions, String recordID, String storeName) throws Exception {
        // Get assertions suggestions
        AssertionSuggestionService assertionService = new AssertionSuggestionService();
        AssertionSuggestions suggestions = assertionService.suggestAssertions(fieldSuggestions);
        // Saving asr file
        // TODO : move this method (or only the ASR saving line) in the proxy project
        ProxyFileStore fileStore = new ProxyFileStore(); // TODO : make a FraSCAti service with fileStore ????  
        fileStore.saveAssertionSuggestions(suggestions, recordID, storeName);
        return suggestions;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#executeAssertions(org.easysoa.records.assertions.AssertionSuggestions, com.openwide.easysoa.message.OutMessage, com.openwide.easysoa.message.OutMessage)
     */ 
    @Override
    public List<AssertionResult> executeAssertions(AssertionSuggestions assertionSuggestions, OutMessage originalMessage, OutMessage replayedMessage){
        if(assertionSuggestions == null){
            // Using default assertions
            reportLogger.warn("assertionSuggestions parameter is null, using default assertion suggestions !");
            AssertionSuggestionService assertionSuggestionService = new AssertionSuggestionService();
            assertionSuggestions = assertionSuggestionService.suggestAssertions();
        }
        ArrayList<AssertionResult> result = new ArrayList<AssertionResult>();
        Map<String, List<Assertion>> assertionMap = assertionSuggestions.getAssertionMap();
        for(String referenceField : assertionMap.keySet()){
            for(Assertion assertion : assertionMap.get(referenceField)){
                result.add(executeAssertion(referenceField, assertion, originalMessage, replayedMessage));
            }
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.records.assertions.AssertionEngine#executeAssertion(org.easysoa.records.assertions.Assertion, com.openwide.easysoa.message.OutMessage, com.openwide.easysoa.message.OutMessage)
     */
    @Override
    public AssertionResult executeAssertion(String referenceField, Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result;
        try{
            result = assertion.check(referenceField, originalMessage, replayedMessage);
        }
        catch(Exception ex){
            reportLogger.error("Assertion error", ex);
            result = new AssertionResult(assertion.getClass(), AssertionResultStatus.KO, ex.getMessage());
        }
        return result;
    }
    
}
