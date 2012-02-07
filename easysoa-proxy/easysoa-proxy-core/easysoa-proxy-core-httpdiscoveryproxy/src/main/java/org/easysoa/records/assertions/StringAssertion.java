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

    private static final Logger testReportLogger = Logger.getLogger("testReportLogger");
    
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
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result = null;
        testReportLogger.info("Using method : " + this.method);        
        // Call the assertion method corresponding to the configuration
        if(method.equals(StringAssertionMethod.LENGTH)){
            result = this.checkLength(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DIFF)) {
            result = this.checkDiff(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DISTANCE_LEHVENSTEIN)) {
            result = this.checkLehvensteinDistance(originalMessage, replayedMessage);
        }
        testReportLogger.info("Assertion result status : " + result.getResultStatus());
        testReportLogger.info("Assertion result metrics : " + result.getMetrics());
        return result;
    }
    
    /**
     * Method for string length assertion
     * @return
     */
    private AssertionResult checkLength(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result;
        if(originalMessage.getMessageContent().getContent().length() == replayedMessage.getMessageContent().getContent().length()){
            result = new AssertionResult(AssertionResultStatus.OK);
            result.setMetrics(0);
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
            result.setMetrics(Math.abs(originalMessage.getMessageContent().getContent().length() - replayedMessage.getMessageContent().getContent().length()));
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
            result = new AssertionResult(AssertionResultStatus.OK);
            result.setMetrics(0);    
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
            result.setMetrics(diffMethodResult.length());
        }
        return result;
    }
    
    /**
     * Method for string distance, Lehvenstein method
     * @return
     */
    private AssertionResult checkLehvensteinDistance(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result;
        int ldMethodResult = StringUtils.getLevenshteinDistance(originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        if(ldMethodResult == 0){
            result = new AssertionResult(AssertionResultStatus.OK);
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
        }
        result.setMetrics(ldMethodResult);
        return result;
    }
    
}
