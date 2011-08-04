package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;

import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;
import com.openwide.easysoa.proxy.PropertyManager;

public class WSDLMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WSDLMessageHandler.class.getName());
	
	@Override
	public boolean isOkFor(Message message) {
		if(message != null){
			return message.getParameters().toLowerCase().matches(PropertyManager.getProperty("proxy.wsdl.request.detect"));
		} else {
			return false;
		}
	}

	@Override
	public boolean handle(Message message, MonitoringService monitoringService) {
		// enrich the message
		message.setType(MessageType.WSDL);
		logger.debug("WSDL found");
		NuxeoRegistrationService nuxeoRS = new NuxeoRegistrationService();
		// Service construction
		String serviceName = message.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		Service service = new Service(message.getUrl());
		service.setCallCount(1);
		service.setTitle(message.getPathName());
		service.setDescription(message.getPathName());
		service.setHttpMethod(message.getMethod());
		nuxeoRS.registerRestService(service);		
		return true;
	}

}
