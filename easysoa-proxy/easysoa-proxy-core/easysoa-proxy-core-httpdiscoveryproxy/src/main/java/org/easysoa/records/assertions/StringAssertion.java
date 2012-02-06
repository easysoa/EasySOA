/**
 * 
 */
package org.easysoa.records.assertions;

import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;

import com.openwide.easysoa.message.OutMessage;

/**
 * Assertion class for strings
 * 
 * @author jguillemotte
 */
public class StringAssertion extends AbstractAssertion {

    // Configuration
    // Assertion algoritm to use
    // ....
    public enum StringAssertionMethod {LENGTH, DIFF, DISTANCE_LEHVENSTEIN};
    
    private StringAssertionMethod method;
    
    /**
    full trimmed string equality.
    Metric could be length of diff or String distance algorithm like Lehvenstein distance (see example impl source ) or others, 
    delta is diff (UI). Both diff & distance cases, could / should be adapted to content kind ex. XML, JSON LATER. 
    Not very interesting in itself but a basis for further features and easy to do.
    **/
    
    /**
     * 
     * @param id
     */
    public StringAssertion(String id) {
        super(id);
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
    public void setConfiguration(String configurationString) throws IllegalArgumentException {
        // TODO : Add assertion configuration
    }    
    
    @Override
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage) {
        // TODO : Never returns null 
        AssertionResult result = null;
        // Call the assertion method corresponding to the configuration
        if(method.equals(StringAssertionMethod.LENGTH)){
            result = this.checkLength(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DIFF)) {
            result = this.checkDiff(originalMessage, replayedMessage);
        } else if(method.equals(StringAssertionMethod.DISTANCE_LEHVENSTEIN)) {
            result = this.checkLehvensteinDistance(originalMessage, replayedMessage);
        } else {
            // TODO throw exception : assertionMethod not Found
        }
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
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
        }
        return result;
    }
    
    /**
     * Method for string diff
     * @return
     */
    private AssertionResult checkDiff(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result = new AssertionResult(AssertionResultStatus.OK);
        return result;
    }
    
    /**
     * Method for string distance, Lehvenstein method
     * @return
     */
    private AssertionResult checkLehvensteinDistance(OutMessage originalMessage, OutMessage replayedMessage){
        AssertionResult result;
        int ldMethodResult = lehvensteinDistance(originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        if(ldMethodResult == 0){
            result = new AssertionResult(AssertionResultStatus.OK);
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
            result.setMetrics(ldMethodResult);
        }
        return result;
    }
    
    /**
     * Lehvenstein Distance method
     * @param s
     * @param t
     * @return 
     */
    public static int lehvensteinDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
     
        if (n == 0) return m;
        if (m == 0) return n;
     
        int[][] d = new int[n + 1][m + 1];
     
        for ( int i = 0; i <= n; d[i][0] = i++ );
        for ( int j = 1; j <= m; d[0][j] = j++ );
     
        for ( int i = 1; i <= n; i++ ) {
            char sc = s.charAt( i-1 );
            for (int j = 1; j <= m;j++) {
                int v = d[i-1][j-1];
                if ( t.charAt( j-1 ) !=  sc ) v++;
                d[i][j] = Math.min( Math.min( d[i-1][ j] + 1, d[i][j-1] + 1 ), v );
            }
        }
        return d[n][m];
    }    
    
}
