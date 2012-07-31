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
import org.easysoa.message.OutMessage;
import org.easysoa.records.assertions.AssertionResult.AssertionResultStatus;


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
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) {
        // TODO : modify LCS assertion to work with referenceField if needed
        AssertionResult result;
        // Limitation to avoid a OutOfMemoryException and log treatment time. LCS method is slow with big messages.
        if(originalMessage.getMessageContent().getRawContent().length() > 100 || replayedMessage.getMessageContent().getRawContent().length() > 100){
            //throw new Exception("Message length is limited to 100 characters for LCS method to avoid long treatment times");
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO, "Message length is limited to 100 characters for LCS method to avoid long treatment times");
            return result;
        }
        String lcsResult = computeLCS(originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());
        if(lcsResult.equals(originalMessage.getMessageContent().getRawContent())){
            result = new AssertionResult(this.getClass(), AssertionResultStatus.OK);
            result.addMetric("LCS method", lcsResult, originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());
        } else {
            result = new AssertionResult(this.getClass(), AssertionResultStatus.KO);
            result.addMetric("LCS method", lcsResult, originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());
        }
        return result;
    }

    /**
     * 
     * @param source
     * @param current
     * @return
     */
    private byte[] computeLCS(byte[] source, byte[] current) {
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
