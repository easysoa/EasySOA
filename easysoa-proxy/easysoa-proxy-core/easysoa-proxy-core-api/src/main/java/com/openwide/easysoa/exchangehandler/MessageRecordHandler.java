package com.openwide.easysoa.exchangehandler;

import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.run.RunManager;

/**
 * Handler to record messages using the run manager 
 * @author jguillemotte
 *
 */
public class MessageRecordHandler implements MessageHandler {

    @Reference
    protected RunManager runManager;
    
    /**
     * 
     * @param runManager
     */
    public MessageRecordHandler(){
    }
    
    /**
     * 
     * @param runManager
     */
    // TODO : Find an other way to init runManager. When done, remove this setter
    public void setRunManager(RunManager runManager){
        this.runManager = runManager;
    }
    
    @Override
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        // Builds a new Exchange record with data contained in request and response
        ExchangeRecord record = new ExchangeRecord();
        record.setInMessage(inMessage);
        record.setOutMessage(outMessage);
        // Call runManager to register the exchange record 
        runManager.record(record);
    }

}
