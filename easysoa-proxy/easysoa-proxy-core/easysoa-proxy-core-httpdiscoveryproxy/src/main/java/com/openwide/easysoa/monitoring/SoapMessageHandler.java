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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.openwide.easysoa.esper.EsperEngine;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * 
 * @author jguillemotte
 *
 */
public class SoapMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(SoapMessageHandler.class.getName());	
	
	@Override
	public boolean isOkFor(Message message) {
		if(message != null){
			logger.debug("Message body : " + message.getBody());
		}
		//TODO : Refine the way that a WSDl message is discovered
		if(message != null && (message.getBody().toLowerCase().contains("<soap:envelope") || message.getBody().toLowerCase().contains("http://schemas.xmlsoap.org/soap/envelope/"))  && checkWsdl(message.getUrl())){
			logger.debug("Returns true");
			return true;
		} else {
			logger.debug("Returns false");
			return false;
		}
	}

	@Override
	public boolean handle(Message message, MonitoringService monitoringService, EsperEngine esperEngine) {
		// enrich the message
		message.setType(MessageType.SOAP);
		logger.debug("WSDL found");
		String serviceName = message.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		Service service = new Service(message.getUrl());
		service.setFileUrl(message.getUrl()+"?wsdl");
		service.setParentUrl(message.getUrl());
		service.setCallCount(1);
		service.setTitle(message.getPathName());
		service.setDescription(message.getPathName());
		service.setHttpMethod(message.getMethod());

        try {
            new NuxeoRegistrationService().registerWSDLService(service);
        } catch (Exception e) {
            logger.error("Failed to register WSDL", e);
        }
        
        return true;
	}
	
	/**
	 * Check if a WSDL service exists
	 * @param url The url to check
	 * @return true if the WSDL service send a response, false otherwise.
	 */
	private boolean checkWsdl(String url){
		boolean result = false;
		logger.debug("Checking wsdl for url : " + url + "?wsdl");
		try{
			DefaultHttpClient httpClient = new DefaultHttpClient(); 
	    	HttpGet httpGet = new HttpGet(url + "?wsdl");
	    	httpClient.execute(httpGet);			
			//TODO Maybe good to improve the check of WSDL
	    	logger.debug("WSDL found !");
			result = true;
		}
		catch(Exception ex){
			logger.debug("Unable to get a correct response from " + url + "?wsdl");
		}
		return result;
	}	
}