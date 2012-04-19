package com.openwide.easysoa.exchangehandler;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

public interface MessageExchangeHandler {
    /**
     * Handle an exchange
     * @param  messagein incoming message
     * @param  messageout outgoing message
     * @throws Exception 
     */
    public void handleExchange(InMessage inMessage, OutMessage outMessage) throws Exception;
    
}
