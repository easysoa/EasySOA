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

package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.Exchange.ExchangeType;

import com.openwide.easysoa.esper.EsperEngine;
import com.openwide.easysoa.message.QueryParam;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;
import com.openwide.easysoa.proxy.PropertyManager;

public class WSDLMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WSDLMessageHandler.class.getName());
	
	@Override
	//public boolean isOkFor(Message message) {
	public boolean isOkFor(ExchangeRecord exchangeRecord) {
		//if(message != null){
		boolean returnValue = false;
		if(exchangeRecord != null){
			//return message.getParameters().toLowerCase().matches(PropertyManager.getProperty("proxy.wsdl.request.detect"));
			//return 
			for(QueryParam queryParam: exchangeRecord.getInMessage().getQueryString().getQueryParams()){
				if(queryParam.getName().toLowerCase().matches(PropertyManager.getProperty("proxy.wsdl.request.detect"))){
					return true;
				}
			}
		}
		return returnValue;
	}

	@Override
	//public boolean handle(Message message, MonitoringService monitoringService, EsperEngine esperEngine) {
	public boolean handle(ExchangeRecord exchangeRecord, MonitoringService monitoringService, EsperEngine esperEngine) {
		// enrich the message
		//message.setType(MessageType.WSDL);
		exchangeRecord.getExchange().setExchangeType(ExchangeType.WSDL);		
		logger.debug("WSDL found");
		
		// Service construction
		//String serviceName = message.getPathName();
		String serviceName = exchangeRecord.getInMessage().getPath();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		//Service service = new Service(message.getUrl());
		Service service = new Service(exchangeRecord.getInMessage().buildCompleteUrl());
		service.setCallCount(1);
		//service.setTitle(message.getPathName());
		service.setTitle(exchangeRecord.getInMessage().getPath());
		//service.setDescription(message.getPathName());
		service.setDescription(exchangeRecord.getInMessage().getPath());
		//service.setHttpMethod(message.getMethod());
		service.setHttpMethod(exchangeRecord.getInMessage().getMethod());
		
        try {
            new NuxeoRegistrationService().registerRestService(service);  
        } catch (Exception e) {
            logger.error("Failed to register WSDL service", e);
        }
        
		return true;
	}

}
