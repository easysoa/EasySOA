package org.easysoa.proxy.cxflocator.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

/**
 * CXF interceptor for Talend service locator
 * 
 * @author jguillemotte
 *
 */
public class CxfLocatorInInterceptor extends AbstractPhaseInterceptor<Message> {

    // TODO Later : use CXF instead of Jersey client for nuxeo REST Api client
    
    private Logger logger = Logger.getLogger(CxfLocatorInInterceptor.class.getName());
    
    public static final ThreadLocal<String> endpointSession = new ThreadLocal<String>();
    static {
        endpointSession.set(null); // default
    }

    public CxfLocatorInInterceptor() {
        super(Phase.PRE_INVOKE); // called before the actual service is called
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            // in : incoming message to local service
            // in : return of call to remote ws
            // in fault : fault result of call to remote ws
            if (MessageUtils.isOutbound(message)) {
                // response in message, no need to record endpoint
            } else {
                // incoming call to ws service
                if (logger.isDebugEnabled()) {
                    logger.debug("Recording endpoint");
                }
                // Record endpoint in session
                String endpointAddress = message.getExchange().getEndpoint().getEndpointInfo().getAddress();
                endpointSession.set(endpointAddress);
            }
        } catch (Fault f) {
            throw f;
        } catch (Throwable t) {
            String msg = "Unknown error";
            logger.error(msg);
            throw new Fault(t);
        }
    }

}
