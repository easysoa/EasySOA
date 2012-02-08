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

    /**
     * Technical test for assertion engine
     */
    @Test
    public void stringAssertionTest(){
        
        StringAssertion assertion = new StringAssertion("StringAssertionTest");
        assertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
        
        AssertionEngine engine = new AssertionEngine();
        
        // Test for equality
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
        assertEquals(0, result.getMetrics());
        
        // Test for difference
        originalContent.setContent("one test");
        replayedContent.setContent("another test");
        result = engine.executeAssertion(assertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        assertEquals(5, result.getMetrics());
        
        // Test for difference with LENGTH method
        assertion.setMethod(StringAssertionMethod.LENGTH);
        result = engine.executeAssertion(assertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        assertEquals(4, result.getMetrics());
    }
    
}
