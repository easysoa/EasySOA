/**
 * 
 */
package org.easysoa.messages.server;

import org.osoa.sca.annotations.Scope;

/**
 * Centralized message number generator server
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ExchangeNumberGenerator implements NumberGenerator {
    
    private long currentMessageNumber;
    
    /**
     * Constructor
     */
    public ExchangeNumberGenerator(){
        this.currentMessageNumber = 0;
        // TODO : add a persistence to store and retrieve the current message number
    }
    
    // must be a singleton
    // Must be synchronized
    /* (non-Javadoc)
     * @see org.easysoa.messages.server.NumberGenerator#getNextNumber()
     */
    @Override
    public long getNextNumber(){
        this.currentMessageNumber++;
        return this.currentMessageNumber;
    }
     
}
