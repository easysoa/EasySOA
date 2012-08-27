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
package org.easysoa.proxy.core.api.records.assertions;

import java.util.HashMap;

import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.records.assertions.AssertionResult.AssertionResultStatus;
import org.osoa.sca.annotations.Scope;


/**
 * Do several assertions on a complete message
 * - comparing mimetype
 * - comparing status
 * - comparing encoding
 * 
 * @author jguillemotte
 *
 */

@Scope("composite")
public class MessageAssertion extends AbstractAssertion {

    public MessageAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
    }

    @Override
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) {
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
