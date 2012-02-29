/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.HashMap;

import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;

import com.openwide.easysoa.message.OutMessage;

/**
 * Do several assertions on a complete message
 * - comparing mimetype
 * - comparing status
 * - comparing encoding
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
        HashMap<String, AssertionMetric> metrics = new HashMap<String, AssertionMetric>(); 
        // Assertions on message content
        
        // Message status
        if(originalMessage.getStatus() == replayedMessage.getStatus()){
            metrics.put("Status message assertion", new AssertionMetric(String.valueOf(true), String.valueOf(originalMessage.getStatus()), String.valueOf(replayedMessage.getStatus())));
        } else {
            metrics.put("Status message assertion", new AssertionMetric(String.valueOf(true), String.valueOf(originalMessage.getStatus()), String.valueOf(replayedMessage.getStatus())));
            assertionResult = false;
        }
        
        // Mimetype 
        if(originalMessage.getMessageContent().getMimeType().equals(replayedMessage.getMessageContent().getMimeType())){
            metrics.put("Mimetype message assertion", new AssertionMetric(String.valueOf(true), originalMessage.getMessageContent().getMimeType(), replayedMessage.getMessageContent().getMimeType()));
        } else {
            metrics.put("Mimetype message assertion", new AssertionMetric(String.valueOf(false), originalMessage.getMessageContent().getMimeType(), replayedMessage.getMessageContent().getMimeType()));
            assertionResult = false;
        }

        // Encoding
        if(originalMessage.getMessageContent().getEncoding().equals(replayedMessage.getMessageContent().getEncoding())){
            metrics.put("Encoding message assertion", new AssertionMetric(String.valueOf(true), originalMessage.getMessageContent().getMimeType(), replayedMessage.getMessageContent().getEncoding()));
        } else {
            metrics.put("Encoding message assertion", new AssertionMetric(String.valueOf(false), originalMessage.getMessageContent().getMimeType(), replayedMessage.getMessageContent().getEncoding()));
            assertionResult = false;            
        }
        
        // Set the assertion result status
        if(assertionResult){
            result = new AssertionResult(this.getClass(), AssertionResultStatus.OK);
        } else {
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO);
        }
        // Set the metrics and returns
        result.setMetrics(metrics);        
        return result;
    }

}
