package com.openwide.easysoa.monitoring;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;

import com.openwide.easysoa.esper.EsperEngine;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;

/**
 * 
 * @author jguillemotte
 *
 */
public abstract class AbstractMonitoringService implements MonitoringService {

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(AbstractMonitoringService.class.getName());	
	
	/**
	 * List of message handlers
	 */
	protected static List<MessageHandler> messageHandlers;	
	
	/**
	 * Data structure to store unknown messages
	 */
	protected ArrayDeque<Message> unknownMessagesList;	
	
	/**
	 * Monitoring model : contains data from Nuxeo
	 */
	protected MonitoringModel monitoringModel;	
	
	/**
	 * urlTree
	 */
	protected UrlTree urlTree;	
	
    /**
     * Message handler list initialization
     * Order is important, more specific first 
     */
    static{
    	messageHandlers = new ArrayList<MessageHandler>();
    	messageHandlers.add(new WSDLMessageHandler());
    	messageHandlers.add(new SoapMessageHandler());    	
    	messageHandlers.add(new RestMessageHandler());    	
    }	
	  
    /**
     * Constructor
     */
    public AbstractMonitoringService(){}
        
    /**
     * 
     * @param message 
     * @param esperEngine 
     */
	public void listen(Message message, EsperEngine esperEngine){
	    logger.debug("Listenning message : " + message);
		for(MessageHandler mh : messageHandlers){
	    	// Call each messageHandler, when the good message handler is found, stop the loop
	    	if(mh.isOkFor(message)){
	    		logger.debug("MessageHandler found : " + mh.getClass().getName());
	    		if(mh.handle(message, this, esperEngine)){
	    			break;
	    		}
	    	}
	    }
	}	

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.monitoring.MonitoringService#getModel()
	 */
	@Override
	public MonitoringModel getModel(){
		return this.monitoringModel;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.monitoring.MonitoringService#getUrlTree()
	 */
	@Override
	public UrlTree getUrlTree(){
		return this.urlTree;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.monitoring.MonitoringService#getUnknownMessagesList()
	 */
	@Override
	public ArrayDeque<Message> getUnknownMessagesList(){
		return this.unknownMessagesList;
	}
	
}
