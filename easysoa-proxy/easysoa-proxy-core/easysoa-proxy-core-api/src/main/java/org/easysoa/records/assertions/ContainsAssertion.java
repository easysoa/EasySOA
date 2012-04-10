/**
 * EasySOA Proxy
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

/**
 * 
 */
package org.easysoa.records.assertions;

import org.apache.log4j.Logger;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.util.ContentChecker.ContentType;

/**
 * 
 * 
 * @author jguillemotte
 *
 */ 
public class ContainsAssertion extends AbstractAssertion {

    private static final Logger reportLogger = Logger.getLogger("reportLogger");    
    
    /**
     * Constructor
     * @param id Assertion ID
     */
    public ContainsAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
    }

    @Override
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) throws Exception {
        AssertionResult result;
        // FieldName is specified : searching the field value and make the assertion only on this value
        if(fieldName != null && !"".equals(fieldName)){
            // JSON
            MessageContent originalMessageContent = originalMessage.getMessageContent();
            if(ContentType.JSON.equals(originalMessageContent.getContentType())){
                // Call a JSON parser  to transform the JSON content to Java structure (Java structure with generic JSON objects)
                // or extract substring corresponding to the field value and compare them (scrap method) .... ??
                reportLogger.info("Getting original value for field " + fieldName + " : " + findJSONFieldValue(fieldName, originalMessageContent.getJSONContent()));
                String originalJsonFieldValue = findJSONFieldValue(fieldName, originalMessageContent.getJSONContent());
                if(originalJsonFieldValue != null){
                    if(originalJsonFieldValue.contains(findJSONFieldValue(fieldName, replayedMessage.getMessageContent().getJSONContent()))){
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                    } else {
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);                        
                    }
                } else {
                    result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Field " + fieldName + "was not found in the original message content" );
                }
            }
            // XML
            else if(ContentType.XML.equals(originalMessageContent.getContentType())) {
                String originalXmlFieldValue = findXMLFieldValue(fieldName, originalMessageContent.getXMLContent());
                reportLogger.info("Getting original value for field " + fieldName + " : " + originalXmlFieldValue);
                if(originalXmlFieldValue != null){
                    if(originalXmlFieldValue.contains(findXMLFieldValue(fieldName, replayedMessage.getMessageContent().getXMLContent()))){
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                    } else {
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);                        
                    }
                } else {
                    result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Field " + fieldName + "was not found in the original message content" );
                }
            }
            // Undefined
            else {
                // How to get the field and it's value ....
                // At the moment simply returns a KO result
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Message content is not JSON or XML, unable to find the field " + fieldName);
            }
        }
        // No fieldName specified : making assertion on the whole message content
        else {
            if(originalMessage.getMessageContent().getRawContent().contains(replayedMessage.getMessageContent().getRawContent())){
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                result.addMetric("Contains assertion method", String.valueOf(true), originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());            
            } else {
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);
                result.addMetric("Contains assertion method", String.valueOf(false), originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());
            }
        }
        return result;
    }
    
}
