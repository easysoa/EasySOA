package com.openwide.easysoa.run;

import com.openwide.easysoa.run.RunManager.RunManagerEvent;

/**
 * Receiver class to handle events emitted by the run manager
 * 
 * @author jguillemotte
 *
 */
public interface RunManagerEventReceiver {

    /**
     * 
     * @return
     */
    public String getEventReceiverName();
    
    /**
     * 
     * @param runManagerEvent
     */
    public void receiveEvent(RunManagerEvent runManagerEvent);
    
}
