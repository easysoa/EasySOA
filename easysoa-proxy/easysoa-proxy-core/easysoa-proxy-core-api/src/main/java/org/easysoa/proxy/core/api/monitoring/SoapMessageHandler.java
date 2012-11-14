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

package org.easysoa.proxy.core.api.monitoring;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.Exchange.ExchangeType;

/**
 * Handler for SOAP exchanges (SOAP web services)
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
	public boolean isOkFor(ExchangeRecord exchangeRecord) {
		if(exchangeRecord != null){
			logger.debug("Message body : " + exchangeRecord.getInMessage().getMessageContent().getRawContent());
		}
		//TODO : Refine the way that a WSDl message is discovered
		if(exchangeRecord != null && (exchangeRecord.getInMessage().getMessageContent().getRawContent().toLowerCase().contains("<soap:envelope") 
				|| exchangeRecord.getInMessage().getMessageContent().getRawContent().toLowerCase().contains("http://schemas.xmlsoap.org/soap/envelope/"))  
				&& checkWsdl(exchangeRecord.getInMessage().buildCompleteUrl())){
			logger.debug("Returns true");
			return true;
		} else {
			logger.debug("Returns false");
			return false;
		}
	}
    
    /**
     * @obsolete handle() uses no more custom built service name but merely full url
     * @return
     */
    protected String buildServiceName(ExchangeRecord exchangeRecord) {
        String serviceName = exchangeRecord.getInMessage().getPath();
        if(serviceName.startsWith("/")){
            serviceName = serviceName.substring(1);
        }
        serviceName = serviceName.replace('/', '_');
        return serviceName;
    }

	@Override
	public boolean handle(ExchangeRecord exchangeRecord, MonitoringService monitoringService, EsperEngine esperEngine) {
		// enrich the message
		exchangeRecord.getExchange().setExchangeType(ExchangeType.SOAP);
		logger.debug("WSDL found");
		
		Service service = new Service(exchangeRecord.getInMessage().buildCompleteUrl()); // SOAP endpoint URL
		service.setFileUrl(exchangeRecord.getInMessage().buildCompleteUrl() + "?wsdl"); // endpoint WSDL URL
		service.setParentUrl(exchangeRecord.getInMessage().buildCompleteUrl()); // SOAP endpoint URL (again)
		service.setCallCount(1);
		service.setTitle(exchangeRecord.getInMessage().getPath());
		service.setDescription(exchangeRecord.getInMessage().getPath());
		service.setHttpMethod(exchangeRecord.getInMessage().getMethod()); // necessarily POST (since SOAP)

        try {
            new NuxeoRegistrationService().registerWSDLService(service);
        } catch (Exception e) {
            logger.error("Failed to register WSDL", e);
        }
        
        // For discovery mode (each service is considered as a new service) :
        if(MonitoringService.MonitoringMode.VALIDATED.equals(monitoringService.getMode())){
            // getting known service node (fetched from registry at startup) :
            Node serviceSoaNode = null;
            for(Node node : monitoringService.getModel().getSoaNodes()){
                if(node.getUrl().equals(exchangeRecord.getInMessage().buildCompleteUrl())){
                    serviceSoaNode = node; // found it !
                    logger.debug("Node found ! " + serviceSoaNode.getTitle());
                    break;
                }
            }
            
            // putting known service node in esper (which will increase call count & send it to registry) :
            esperEngine.sendEvent(serviceSoaNode);
            // TODO pb : no need to trigger an esper event to update the call count value.
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