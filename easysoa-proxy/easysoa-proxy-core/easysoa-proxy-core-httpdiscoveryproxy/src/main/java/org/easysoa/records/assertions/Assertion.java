package org.easysoa.records.assertions;

import com.openwide.easysoa.message.OutMessage;

public interface Assertion {

    /**
     * Returns the Assertion ID
     * @return The Assertion ID
     */
    public String getID();    

    /**
     * Set assertion configuration as JSON String or XML String
     * @param configurationString The configuration as a JSON or XML structure
     */
    // TODO define the JSON or XML configuration structure
    public void setConfiguration(String configurationString);
    
    /**
     * Execute the assertion
     * @return
     */
    // TODO : Maybe best to change param type : replayed response vs recorded response
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage);
}
