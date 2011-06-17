package com.openwide.easysoa.monitoring;

import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.EsperEngineSingleton;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.esperpoc.PropertyManager;
import com.openwide.easysoa.monitoring.Message.MessageType;
import com.openwide.easysoa.monitoring.soa.WSDLService;

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
	public boolean handle(Message message) {
		// enrich the message
		message.setType(MessageType.WSDL);
		logger.debug("WSDL found");
		//TODO What to do here ?? Fill urlTree or not, mode dependency
		//EsperEngineSingleton.getEsperRuntime().sendEvent(message);
		NuxeoRegistrationService nuxeoRS = new NuxeoRegistrationService();
		// Service construction
		String serviceName = message.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		nuxeoRS.registerWSDLService(new WSDLService(message.getHost(), serviceName, message.getCompleteMessage(), message.getMethod()));
		return true;
	}

}
