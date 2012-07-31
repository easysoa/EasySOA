/**
 * 
 */
package org.easysoa.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.message.QueryString;
import org.easysoa.records.Exchange;
import org.easysoa.records.Exchange.ExchangeType;
import org.easysoa.records.ExchangeRecord;

/**
 * CXF interceptor. Works as the HTTP discovery proxy : listen the HTTP exchanges and record them as files. 
 * 
 * @author jguillemotte
 *
 */
public class CXFProxyInterceptor extends AbstractPhaseInterceptor<Message> {

    // Logger
    private static Logger logger = Logger.getLogger(CXFProxyInterceptor.class.getName());
    
    /**
     * Create a new proxy interceptor with phase RECEIVE
     */
    public CXFProxyInterceptor(){
        super(Phase.RECEIVE);
    }
    
    /**
     * Create a new proxy interceptor with custom phase
     * @param phase
     */
    public CXFProxyInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        
        logger.debug("Handling exchange ....");
        
        // Get the input and output parts
        // and then build an ExchangeRecord
        ExchangeRecord exchangeRecord = new ExchangeRecord();
        Exchange exchange = new Exchange();
        InMessage inMessage = new InMessage((String)message.get("org.apache.cxf.request.method"), (String)message.get("org.apache.cxf.message.Message.QUERY_STRING"));
        OutMessage outMessage = new OutMessage();
        exchangeRecord.setInMessage(inMessage);
        exchangeRecord.setOutMessage(outMessage);

        logger.debug("Displaying message keys ....");
        for(String key : message.keySet()){
            logger.debug("Key : " + key);
        }
       
        /*if(message.){
            exchange.setExchangeType(ExchangeType.REST);
        } else {
            exchange.setExchangeType(ExchangeType.SOAP);
        }*/

        // The interceptor will be used :
        // - in remote client or server .. so it must have a system to talk with a remote easysoa
        // - in easysoa  .. no need to use a remote link to the run manager, only thing to do is to have reference on run manager
        
        // How to get the run manager ??
        // Run manager is a sca component designed to run on Frascati
        // Send the exchange to a remote API using REST or SOAP protocols ? 
        // Goal of this interceptor is to be plugged on an external system.
    }

}
