/**
 * 
 */
package org.easysoa.records.filters;

import org.apache.log4j.Logger;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.proxy.core.api.exchangehandler.HandlerManager;
import org.easysoa.proxy.core.api.run.RunManager;
import org.easysoa.proxy.core.api.run.RunManagerEventReceiver;
import org.easysoa.proxy.core.api.run.RunManager.RunManagerEvent;
import org.easysoa.records.handlers.NuxeoMessageExchangeRecordHandler;
import org.nuxeo.runtime.api.Framework;


/**
 * @author jguillemotte
 *
 */
public class ExchangeRecordServletFilterEventReceiver implements RunManagerEventReceiver {

    // Logger
    private static Logger logger = Logger.getLogger(ExchangeRecordServletFilterEventReceiver.class);    
    
    /* (non-Javadoc)
     * @see org.easysoa.run.RunManagerEventReceiver#getEventReceiverName()
     */
    public String getEventReceiverName() {
        return "ExchangeRecordServeltFilterEventReceiver";
    }

    /* (non-Javadoc)
     * @see org.easysoa.run.RunManagerEventReceiver#receiveEvent(org.easysoa.run.RunManager.RunManagerEvent)
     */
    public void receiveEvent(RunManagerEvent runManagerEvent) {
        try {
            ExchangeRecordServletFilter servletFilter = Framework.getService(ExchangeRecordServletFilter.class);
            // TODO : remove NuxeoMessageExchangeRecordHandler class and replace it by the handler manager
            //FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            //HandlerManager handlerManager = frascati.getService("httpDiscoveryProxy/handlerManagerComponent", "handlerManagerService", HandlerManager.class);
            if(RunManagerEvent.START.equals(runManagerEvent)){
                servletFilter.start(new NuxeoMessageExchangeRecordHandler());
                //servletFilter.start(handlerManager.);
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
