/**
 * 
 */
package org.easysoa.records.assertions;

import static org.junit.Assert.*;

import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;
import org.easysoa.records.assertions.StringAssertion.StringAssertionMethod;
import org.junit.Test;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;

/**
 * Test for the assertion engine
 * 
 * @author jguillemotte
 *
 */
public class AssertionTest {

    @Test
    public void stringAssertionTest(){
        
        StringAssertion assertion = new StringAssertion("StringAssertionTest");
        assertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
        
        AssertionEngine engine = new AssertionEngine();
        
        OutMessage originalMessage = new OutMessage();
        OutMessage replayedMessage = new OutMessage();
        MessageContent originalContent = new MessageContent();
        MessageContent replayedContent = new MessageContent();
        originalContent.setContent("test");
        replayedContent.setContent("test");
        originalMessage.setMessageContent(originalContent);
        replayedMessage.setMessageContent(replayedContent);
        AssertionResult result = engine.executeAssertion(assertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.OK, result.getResultStatus());
        
        originalContent.setContent("one test");
        replayedContent.setContent("another test");
        result = engine.executeAssertion(assertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        
    }
    
}
