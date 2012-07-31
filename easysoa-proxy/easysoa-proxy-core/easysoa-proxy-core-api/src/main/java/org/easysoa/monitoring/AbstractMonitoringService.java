/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.monitoring;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.esper.EsperEngine;
import org.easysoa.monitoring.apidetector.UrlTree;
import org.easysoa.records.ExchangeRecord;


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
	//protected ArrayDeque<Message> unknownMessagesList;
	protected ArrayDeque<ExchangeRecord> unknownExchangeRecordList;
	
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
    public AbstractMonitoringService(){
		this.unknownExchangeRecordList = new ArrayDeque<ExchangeRecord>();
    }
        
    /**
     * 
     * @param message 
     * @param esperEngine 
     */
	/*public void listen(Message message, EsperEngine esperEngine){
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
	}*/
	
	public void listen(ExchangeRecord exchangeRecord, EsperEngine esperEngine){
	    logger.debug("Listenning exchange record : " + exchangeRecord);
		
	    for(MessageHandler mh : messageHandlers){
	    	// Call each messageHandler, when the good message handler is found, stop the loop
	    	if(mh.isOkFor(exchangeRecord)){
	    		logger.debug("MessageHandler found : " + mh.getClass().getName());
	    		if(mh.handle(exchangeRecord, this, esperEngine)){
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
	/*public ArrayDeque<Message> getUnknownMessagesList(){
		return this.unknownMessagesList;
	}*/
	
	public ArrayDeque<ExchangeRecord> getUnknownExchangeRecordList(){
		return this.unknownExchangeRecordList;
	}
	
}
