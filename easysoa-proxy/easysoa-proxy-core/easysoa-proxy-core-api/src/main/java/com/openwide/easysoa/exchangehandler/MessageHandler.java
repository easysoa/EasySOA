package com.openwide.easysoa.exchangehandler;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

public interface MessageHandler {
    /**
     * Handle an exchange
     * @param  messagein incoming message
     * @param  messageout outgoing message
     * @throws Exception 
     */
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception;
    
}
