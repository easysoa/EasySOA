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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.openwide.easysoa.message.OutMessage;

/**
 * To compare a replayed request with the recorded original 
 * 
 * @author jguillemotte
 *
 */
public class AssertionEngine implements ExecutableEngine {
    
    private static final Logger testReportLogger = Logger.getLogger("testReportLogger");
    
    // Add methods to configure Assertions
    // Not here, define a class for each type of assertions
    
    // Assertion objects must be configured in this class
    // How to store assertions to execute for a specific field, response ???
    
    // Several assertions for a single object (length, levendstein ...)
    // In an hashmap => id key, by default a method add to add a simple string assertion (length)

    // asr files to define assertions. how to generate these files
    
    // key is the field key associated to a list of assertions
    // How to do when assertion is processed on the whole response ?
    private HashMap<String, List<Assertion>> assertionList;
    
    /**
     * Add an assertion in the assertion list
     * @param assertion
     */
    public void addAssertion(Assertion assertion){
        
    }
    
    /**
     * Add an assertion in the assertion list for an identified field
     * @param fieldName
     * @param assertion
     */
    public void addAssertion(String fieldName, Assertion assertion){
        
    }
    
    /**
     * Executes several assertions
     * @param assertions
     * @return
     */
    /*public Map<String, AssertionResult> executeAssertions(List<Assertion> assertionList){
        for(Assertion assertion : assertionList){
            //executeAssertion(assertion, originalMessage, replayedMessage);
        }
        return new HashMap(); // filled for each assertion with the result "OK, KO or Maybe";
    }*/
    
    /**
     * Execute one assertion
     * @param assertion
     * @return 
     */
    public AssertionResult executeAssertion(Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result = assertion.check(originalMessage, replayedMessage);
        return result;
    }

    @Override
    public Object execute(Object[] params) {
        // Get the assertion list and check each assertion
        
        // return the result as a list of assertion result ....
        
        // TODO Auto-generated method stub
        return null;
    }
    
}
