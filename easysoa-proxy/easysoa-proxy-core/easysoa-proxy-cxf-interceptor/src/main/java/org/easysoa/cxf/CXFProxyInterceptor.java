/**
 * 
 */
package org.easysoa.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.easysoa.records.Exchange;
import org.easysoa.records.Exchange.ExchangeType;
import org.easysoa.records.ExchangeRecord;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

/**
 * CXF interceptor. Works as the HTTP discovery proxy : listen the HTTP exchanges and record them as files. 
 * 
 * @author jguillemotte
 *
 */
public class CXFProxyInterceptor extends AbstractPhaseInterceptor<Message> {

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
        // Get the input and output parts
        // and then build an ExchangeRecord
        
        ExchangeRecord exchangeRecord = new ExchangeRecord();
        Exchange exchange = new Exchange();
        InMessage inMessage = new InMessage();
        OutMessage outMessage = new OutMessage();
        exchangeRecord.setInMessage(inMessage);
        exchangeRecord.setOutMessage(outMessage);
        
        //exchange.setExchangeID(exchangeID);
        
        /*if(message.){
            exchange.setExchangeType(ExchangeType.REST);
        } else {
            exchange.setExchangeType(ExchangeType.SOAP);
        }*/
        
    }

}
