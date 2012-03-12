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

import org.apache.log4j.Logger;
import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;
import org.apache.commons.lang.StringUtils;
import com.openwide.easysoa.message.OutMessage;

/**
 * Assertion class for strings
 * 
 * @author jguillemotte
 */
public class StringAssertion extends AbstractAssertion {

    private static final Logger reportLogger = Logger.getLogger("reportLogger");
    
    // Configuration
    // Assertion algoritm to use
    public enum StringAssertionMethod {LENGTH, DIFF, DISTANCE_LEHVENSTEIN};
    
    private StringAssertionMethod method;
    
    /**
    full trimmed string equality.
    Metric could be length of diff or String distance algorithm like Lehvenstein distance (see example impl source ) or others, 
    delta is diff (UI). Both diff & distance cases, could / should be adapted to content kind ex. XML, JSON LATER. 
    Not very interesting in itself but a basis for further features and easy to do.
    **/
    
    /**
     * Default constructor using default assertion method length
     * @param id
     */
    public StringAssertion(String id) {
        super(id);
        this.method = StringAssertionMethod.LENGTH;
    }

    /**
     * 
     * @param id
     * @param method
     */
    public StringAssertion(String id, StringAssertionMethod method) {
        super(id);
        this.setMethod(method);
    }    
    
    /**
     * 
     * @param id
     * @param configurationString
     */
    public StringAssertion(String id, String configurationString) {
        super(id);
        this.setConfiguration(configurationString);
    }

    /**
     * Set the assertion method
     * @param method The assertion method to set
     */
    public void setMethod(StringAssertionMethod method){
        this.method = method;
    }
    
    /**
     * Returns the assertion method
     * @return The assertion method
     */
    public StringAssertionMethod getMethod(){
        return this.method;
    }
    
    @Override
    /**
     * For JSON :
     * 
     * For XML :
     * 
     */
    public void setConfiguration(String configurationString) throws IllegalArgumentException {
        // TODO : Add assertion configuration
    }    
    
    @Override
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage)/* throws Exception*/ {
        AssertionResult result = null;
        reportLogger.info("Using method : " + this.method);        
        // Call the assertion method corresponding to the configuration
        if(method.equals(StringAssertionMethod.LENGTH)){
            result = this.checkLength(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DIFF)) {
            result = this.checkDiff(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DISTANCE_LEHVENSTEIN)) {
            result = this.checkLehvensteinDistance(originalMessage, replayedMessage);
        }
        reportLogger.info("Assertion result status : " + result.getResultStatus());
        reportLogger.info("Assertion result metrics : " + result.getMetrics());
        return result;
    }
    
    /**
     * Method for string length assertion
     * @return
     */
    private AssertionResult checkLength(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result;
        if(originalMessage.getMessageContent().getContent().length() == replayedMessage.getMessageContent().getContent().length()){
            result = new AssertionResult(this.getClass(), AssertionResultStatus.OK);
            result.addMetric("Length", "0", String.valueOf(originalMessage.getMessageContent().getContent().length()), String.valueOf(replayedMessage.getMessageContent().getContent().length()));
        } else {
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO);
            result.addMetric("Length", String.valueOf(Math.abs(originalMessage.getMessageContent().getContent().length() - replayedMessage.getMessageContent().getContent().length())), String.valueOf(originalMessage.getMessageContent().getContent().length()), String.valueOf(replayedMessage.getMessageContent().getContent().length()));
        }
        return result;
    }
    
    /**
     * Method for string diff
     * @return
     */
    private AssertionResult checkDiff(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result;
        String diffMethodResult = StringUtils.difference(originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        if(diffMethodResult.length() == 0){
            result = new AssertionResult(this.getClass(), AssertionResultStatus.OK);
            result.addMetric("Difference", "", originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());    
        } else {
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO);
            result.addMetric("Difference", diffMethodResult, originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        }
        return result;
    }
    
    /**
     * Method for string distance, Lehvenstein method
     * @return
     * @throws Exception  
     */
    private AssertionResult checkLehvensteinDistance(OutMessage originalMessage, OutMessage replayedMessage)/* throws Exception*/ {
        AssertionResult result;
        // TODO : Warning here ! For long messages (more of 5 words, the treatment can be very very long.
        // See http://fr.wikipedia.org/wiki/Distance_de_Levenshtein#Algorithme_de_Levenshtein => Maybe in this case the best solution is to use an other method or to throw an Exception
        // At the moment, the length of string are limited to 100 characters, if the length of one of the two strings to compare is more of this limit, a KO assertion result is returned
        // with a message explaining the limitation
        if(originalMessage.getMessageContent().getContent().length() > 100 || replayedMessage.getMessageContent().getContent().length() > 100){
            //throw new Exception("Message length is limited to 100 characters for Lehvenstein method to avoid long treatment times");
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO, "Message length is limited to 100 characters for Lehvenstein method to avoid long treatment times");
            return result;
        }
        int ldMethodResult = StringUtils.getLevenshteinDistance(originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        if(ldMethodResult == 0){
            result = new AssertionResult(this.getClass(), AssertionResultStatus.OK);
        } else {
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO);
        }
        result.addMetric("Lehvenstein distance", String.valueOf(ldMethodResult), originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        return result;
    }
    
}
