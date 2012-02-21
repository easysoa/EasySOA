/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.HashMap;

import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;

import com.openwide.easysoa.message.OutMessage;

/**
 * Do several assertions on a complete message
 * 
 * @author jguillemotte
 *
 */
public class MessageAssertion extends AbstractAssertion {

    public MessageAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result;
        boolean assertionResult = true; 
        HashMap<String, String> metrics = new HashMap<String, String>(); 
        // Assertions on message content
        
        
        // Message status
        if(originalMessage.getStatus() == replayedMessage.getStatus()){
            metrics.put("Status message assertion", String.valueOf(true));
        } else {
            metrics.put("Status message assertion", String.valueOf(false));
            assertionResult = false;
        }
        
        // Mimetype 
        if(originalMessage.getMessageContent().getMimeType() == replayedMessage.getMessageContent().getMimeType()){
            metrics.put("Mimetype message assertion", String.valueOf(true));
        } else {
            metrics.put("Mimetype message assertion", String.valueOf(false));
            assertionResult = false;
        }        
        
        // Set the assertion result status
        if(assertionResult){
            result = new AssertionResult(AssertionResultStatus.OK);
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
        }
        // Set the metrics and returns
        result.setMetrics(metrics);        
        return result;
    }

}
