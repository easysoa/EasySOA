/**
 * 
 */
package org.easysoa.records.filters;

import org.apache.log4j.Logger;
import org.easysoa.records.handlers.NuxeoMessageExchangeRecordHandler;
import org.easysoa.run.RunManagerEventReceiver;
import org.easysoa.run.RunManager.RunManagerEvent;
import org.nuxeo.runtime.api.Framework;


/**
 * @author jguillemotte
 *
 */
public class ExchangeRecordServletFilterEventReceiver implements RunManagerEventReceiver {

    // Logger
    private static Logger logger = Logger.getLogger(ExchangeRecordServletFilterEventReceiver.class);    
    
    /* (non-Javadoc)
     * @see com.openwide.easysoa.run.RunManagerEventReceiver#getEventReceiverName()
     */
    public String getEventReceiverName() {
        return "ExchangeRecordServeltFilterEventReceiver";
    }

    /* (non-Javadoc)
     * @see com.openwide.easysoa.run.RunManagerEventReceiver#receiveEvent(com.openwide.easysoa.run.RunManager.RunManagerEvent)
     */
    public void receiveEvent(RunManagerEvent runManagerEvent) {
        try {
            ExchangeRecordServletFilter servletFilter = Framework.getService(ExchangeRecordServletFilter.class);
            if(RunManagerEvent.START.equals(runManagerEvent)){
                servletFilter.start(new NuxeoMessageExchangeRecordHandler());
            } else if(RunManagerEvent.STOP.equals(runManagerEvent)){
                servletFilter.stop();
            } else {
                // Nothing to do at the moment, only the start and stop event are used, other are ignored.
            }            
        } catch (Exception ex) {
            logger.error("An error occurs when processing the run manager event", ex);
        }
    }

}
