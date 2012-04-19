package com.openwide.easysoa.exchangehandler;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

public interface HandlerManager {

    public void handle(InMessage inMessage, OutMessage outMessage) throws Exception;
    
}
