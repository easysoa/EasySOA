/**
 * 
 */
package org.easysoa.records.assertions;

import net.sf.json.JSON;

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

    // 2 methods : 
    //- first extract a substring containing the field name and it's value from original message
    // and check if this substring is contained in the replayed message
    // Or 
    // Parse a JSON / XML structure to have a java object structure
    // And with recursive function, find the field and it associated value .. and check if we have the same field in the replayed structure
    @Override
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result;
        // FieldName is specified : searching the field value and make the assertion only on this value
        if(fieldName != null && !"".equals(fieldName)){
            result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);

            // JSON
            MessageContent messageContent = originalMessage.getMessageContent();
            if(ContentType.JSON.equals(messageContent.getContentType())){
                // Call a JSON parser  to transform the JSON content to Java structure (Java structure with generic JSON objects)
                // or extract substring corresponding to the field value and compare them (scrap method) .... ??

                // call a method to extract the required field adn value !
               
            }
            // XML
            else if(ContentType.XML.equals(messageContent.getContentType())) {
                // Same problem ... Parsing or not ??
            }
            // Undefined
            else {
                // How to get the field and it's value ....
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

    /**
     * Search and returns the value corresponding to the specified field
     * @param json
     * @return
     */
    private String getFieldValue(JSON json){
        /*if(JSONObject instanceof messageContent.getJSONContent()){
            JSONObject  messageContent.getJSONContent();    
        } else {
            
        }*/
        return null;
    }
    
    /**
     * 
     * @param xml
     * @return
     */
    private String getFieldValue(Object xml){
        return null;
    }
    
}
