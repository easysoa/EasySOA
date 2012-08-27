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

import org.apache.log4j.Logger;
import org.easysoa.message.MessageContent;
import org.easysoa.message.OutMessage;
import org.easysoa.message.util.ContentChecker.ContentType;
import org.easysoa.proxy.core.api.records.assertions.Assertion;
import org.easysoa.proxy.core.api.records.assertions.AssertionEngine;
import org.easysoa.proxy.core.api.records.assertions.AssertionEngineImpl;
import org.easysoa.proxy.core.api.records.assertions.AssertionResult;
import org.easysoa.proxy.core.api.records.assertions.AssertionSuggestions;
import org.easysoa.proxy.core.api.records.assertions.ContainsAssertion;
import org.easysoa.proxy.core.api.records.assertions.LCSAssertion;
import org.easysoa.proxy.core.api.records.assertions.StringAssertion;
import org.easysoa.proxy.core.api.records.assertions.AssertionResult.AssertionResultStatus;
import org.easysoa.proxy.core.api.records.assertions.StringAssertion.StringAssertionMethod;
import org.junit.Test;

/**
 * Test for the assertion engine
 * 
 * @author jguillemotte
 *
 */
public class AssertionTest {

    // Logger
    private static Logger logger = Logger.getLogger(AssertionTest.class.getName());    
    
    /**
     * Technical test for assertion engine
     */
    @Test
    public void stringAssertionTest(){
        
        StringAssertion stringAssertion = new StringAssertion("StringAssertionTest");
        stringAssertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
        
        AssertionEngine engine = new AssertionEngineImpl();
        
        // Test for equality
        OutMessage originalMessage = new OutMessage();
        OutMessage replayedMessage = new OutMessage();
        MessageContent originalContent = new MessageContent();
        MessageContent replayedContent = new MessageContent();
        originalContent.setRawContent("test");
        replayedContent.setRawContent("test");
        originalMessage.setMessageContent(originalContent);
        replayedMessage.setMessageContent(replayedContent);
        AssertionResult result = engine.executeAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD, stringAssertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.OK, result.getResultStatus());
        //assertEquals("0", result.getMetrics());
        
        // Test for difference
        originalContent.setRawContent("one test");
        replayedContent.setRawContent("another test");
        result = engine.executeAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD, stringAssertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        //assertEquals("5", result.getMetrics());
        
        // Test for difference with LENGTH method
        stringAssertion.setMethod(StringAssertionMethod.LENGTH);
        result = engine.executeAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD,stringAssertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        //assertEquals("4", result.getMetrics());
        
        // Test for difference with LCS method
        Assertion lcsAssertion = new LCSAssertion("lcsAssertiontest");
        result = engine.executeAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD,lcsAssertion, originalMessage, replayedMessage);
        originalContent.setRawContent("one test");
        replayedContent.setRawContent("one test");
        result = engine.executeAssertion(AssertionSuggestions.DEFAULT_REFERENCE_FIELD,lcsAssertion, originalMessage, replayedMessage);
        //assertEquals(AssertionResultStatus.KO, result.getResultStatus());
        //assertEquals(4, result.getMetrics());
    }
    
    @Test
    public void containsJSONAssertionTest(){
        // Test for Contains assertion with a valid JSON structure
        
        StringBuffer jsonData = new StringBuffer();
        jsonData.append("{");
        jsonData.append("\"id\": \"0001\",");
        jsonData.append("\"type\": \"donut\",");
        jsonData.append("\"name\": \"Cake\",");
        jsonData.append("\"ppu\": 0.55,");
        jsonData.append("\"batters\": {");
        jsonData.append("\"batter\": [");
        jsonData.append("{ \"id\": \"1001\", \"type\": \"Regular\" },");
        jsonData.append("{ \"id\": \"1002\", \"type\": \"Chocolate\" },");
        jsonData.append("{ \"id\": \"1003\", \"type\": \"Blueberry\" },");
        jsonData.append("{ \"id\": \"1004\", \"type\": \"Devil's Food\" }");
        jsonData.append("]");
        jsonData.append("}, \"topping\": [");
        jsonData.append("{ \"id\": \"5001\", \"type\": \"None\" },");
        jsonData.append("{ \"id\": \"5002\", \"type\": \"Glazed\" },");
        jsonData.append("{ \"id\": \"5005\", \"type\": \"Sugar\" },");
        jsonData.append("{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },");
        jsonData.append("{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },");
        jsonData.append("{ \"id\": \"5003\", \"type\": \"Chocolate\" },");
        jsonData.append("{ \"id\": \"5004\", \"type\": \"Maple\" }");
        jsonData.append("]");
        jsonData.append("}");
        AssertionEngine engine = new AssertionEngineImpl();
        OutMessage originalMessage = new OutMessage();
        OutMessage replayedMessage = new OutMessage();
        MessageContent originalContent = new MessageContent();
        MessageContent replayedContent = new MessageContent();
        ContainsAssertion containsAssertion = new ContainsAssertion("containsAssertion");
        originalContent.setRawContent(jsonData.toString());
        replayedContent.setRawContent(jsonData.toString());
        assertEquals(ContentType.JSON, originalContent.getContentType());
        originalMessage.setMessageContent(originalContent);
        replayedMessage.setMessageContent(replayedContent);        
        AssertionResult result = engine.executeAssertion("batters", containsAssertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.OK, result.getResultStatus());
        //assertEquals("5", result.getMetrics());   
    }
    
    @Test
    public void containsXMLAssertionTest(){
        // Test for Contains assertion with a valid XML structure
        
        StringBuffer xmlData = new StringBuffer();
        xmlData.append("<note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body><content>Don't forget me this weekend!</content></body></note>");
        AssertionEngine engine = new AssertionEngineImpl();
        OutMessage originalMessage = new OutMessage();
        OutMessage replayedMessage = new OutMessage();
        MessageContent originalContent = new MessageContent();
        MessageContent replayedContent = new MessageContent();
        ContainsAssertion containsAssertion = new ContainsAssertion("containsAssertion");
        originalContent.setRawContent(xmlData.toString());
        replayedContent.setRawContent(xmlData.toString());
        assertEquals(ContentType.XML, originalContent.getContentType());
        originalMessage.setMessageContent(originalContent);
        replayedMessage.setMessageContent(replayedContent);        
        AssertionResult result = engine.executeAssertion("body", containsAssertion, originalMessage, replayedMessage);
        assertEquals(AssertionResultStatus.OK, result.getResultStatus());        
    }
        
}
