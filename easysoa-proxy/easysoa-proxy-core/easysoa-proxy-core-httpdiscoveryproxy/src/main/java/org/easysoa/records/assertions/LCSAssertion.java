/**
 * 
 */
package org.easysoa.records.assertions;

import org.apache.log4j.Logger;
import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;

import com.openwide.easysoa.message.OutMessage;

/**
 * Do an assertion on message content using the LCS (longest common sub-sequence) method
 * 
 * @author jguillemotte
 * 
 */
public class LCSAssertion extends AbstractAssertion {

    private static final Logger reportLogger = Logger.getLogger("reportLogger");    
    
    /**
     * Default constructor
     * @param id Assertion ID
     */
    public LCSAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
    }

    @Override
    // TODO : Add a security to avoid to call the check method with too big messages.
    // Can generate an OutOfMemory error in this case
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result;
        String lcsResult = computeLCS(originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        if(lcsResult.equals(originalMessage.getMessageContent().getContent())){
            result = new AssertionResult(AssertionResultStatus.OK);
            result.addMetric("LCS method", lcsResult, originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        } else {
            result = new AssertionResult(AssertionResultStatus.KO);
            result.addMetric("LCS method", lcsResult, originalMessage.getMessageContent().getContent(), replayedMessage.getMessageContent().getContent());
        }
        return result;
    }

    /**
     * 
     * @param source
     * @param current
     * @return
     */
    public byte[] computeLCS(byte[] source, byte[] current) {
        int M = source.length;
        int N = current.length;

        // opt[i][j] = length of LCS of x[i..M] and current[j..N]
        int[][] opt = new int[M + 1][N + 1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (source[i] == current[j])
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        // recover LCS itself and print it to standard output
        int i = 0, j = 0;
        int maxSize = (M > N) ? M : N;

        byte[] tempBytes = new byte[maxSize];
        int commonBytes = 0;

        while (i < M && j < N) {
            // Source and current
            if (source[i] == current[j]) {
                tempBytes[commonBytes++] = source[i];
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }

        byte[] byteArray = new byte[commonBytes];
        System.arraycopy(tempBytes, 0, byteArray, 0, commonBytes);
        return byteArray;
    }

    /**
     * 
     * @param source
     * @param current
     * @return
     */
    public String computeLCS(String source, String current) {
        return new String(computeLCS(source.getBytes(), current.getBytes()));
    }

}
