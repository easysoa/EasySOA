package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import org.restlet.resource.ClientResource;
import com.openwide.easysoa.esperpoc.EsperEngineSingleton;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.monitoring.soa.WSDLService;

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
		//if(message != null && message.getBody().toLowerCase().contains("schemas.xmlsoap.org") && message.getBody().toLowerCase().startsWith("<?xml") && checkWsdl(message.getUrl())){
		if(message != null && message.getBody().toLowerCase().contains("<soap:envelope") && checkWsdl(message.getUrl())){
			logger.debug("Returns true");
			return true;
		} else {
			logger.debug("Returns false");
			return false;
		}
	}

	@Override
	public boolean handle(Message message) {
		// enrich the message
		message.setType(MessageType.SOAP);
		logger.debug("WSDL found");
		NuxeoRegistrationService nuxeoRS = new NuxeoRegistrationService();
		String serviceName = message.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		//nuxeoRS.registerWSDLService(new WSDLService(message.getHost(), serviceName, message.getCompleteMessage(), message.getMethod()));
		Service service = new Service(message.getUrl(), message.getUrl() + "?wsdl");
		service.setCallCount(1);
		service.setTitle(message.getPathName());
		service.setDescription(message.getPathName());
		service.setHttpMethod(message.getMethod());
		nuxeoRS.registerRestService(service);
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
			ClientResource resource = new ClientResource(url + "?wsdl");
			resource.get();
			logger.debug("WSDL found !");
			result = true;
		}
		catch(Exception ex){
			logger.debug("Unable to get a correct response from " + url + "?wsdl");
		}
		return result;
	}	
}
