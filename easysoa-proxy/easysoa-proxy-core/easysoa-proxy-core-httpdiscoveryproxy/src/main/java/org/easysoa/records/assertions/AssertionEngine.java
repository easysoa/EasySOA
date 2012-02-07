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
public class AssertionEngine {
    
    private static final Logger testReportLogger = Logger.getLogger("testReportLogger");
    
    // Add methods to configure Assertions
    // Not here, define a class for each type of assertions
    
    /**
     * Executes several assertions
     * @param assertions
     * @return
     */
    public Map<String, AssertionResult> executeAssertions(List<Assertion> assertionList){
        for(Assertion assertion : assertionList){
            //executeAssertion(assertion, originalMessage, replayedMessage);
        }
        return new HashMap(); // filled for each assertiond with the result "OK, KO or Maybe";
    }
    
    /**
     * Execute one assertion
     * @param assertion
     * @return 
     */
    public AssertionResult executeAssertion(Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result = assertion.check(originalMessage, replayedMessage);
        return result;
    }
    
}
