package com.openwide.easysoa.exchangehandler;

import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.osoa.sca.annotations.Reference;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

public class HandlerManagerImpl implements HandlerManager {

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger(HandlerManagerImpl.class.getName());        
    
    @Reference 
    private List<MessageHandler> handlers;
    
    @Override
    public void handle(InMessage inMessage, OutMessage outMessage) {
        for(MessageHandler handler : this.handlers){
            try {
                handler.handleMessage(inMessage, outMessage);
            }
            catch(Exception ex){
                logger.warn("An error occurs during the execution of the handler", ex);
            }
        }    
    }
    
    
}
