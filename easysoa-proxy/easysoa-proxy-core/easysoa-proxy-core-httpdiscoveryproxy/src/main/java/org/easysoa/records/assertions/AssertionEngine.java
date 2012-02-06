/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.openwide.easysoa.message.OutMessage;

/**
 * To compare the result a replayed request with the recorded original
 * 
 * @author jguillemotte
 *
 */
public class AssertionEngine {
    
    private static final Logger testReportLogger = Logger.getLogger("testReportLogger");
    private static final Logger rootLogger = Logger.getLogger(AssertionEngine.class);
    
    // Add methods to configure Assertions
    // Not here, define a class for each type of assertions
    
    /**
     * Executes several assertions
     * @param assertions
     * @return
     */
    public Map<String, AssertionResult> executeAssertions(List<Assertion> assertions){
        
        return new HashMap(); // filled for each assertiond with the result "OK, KO or Maybe";
    }
    
    /**
     * Execute one assertion
     * @param assertion
     * @return 
     */
    public AssertionResult executeAssertion(Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage){
        rootLogger.debug("executing assertion ...");
        AssertionResult result = assertion.check(originalMessage, replayedMessage);
        testReportLogger.info("Result for assertion " + assertion.getID() + " is " + result.getResultStatus());
        return result;
    }
    
}
