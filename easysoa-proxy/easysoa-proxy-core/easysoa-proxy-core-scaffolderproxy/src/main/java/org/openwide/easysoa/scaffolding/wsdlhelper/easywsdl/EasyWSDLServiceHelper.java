package org.openwide.easysoa.scaffolding.wsdlhelper.easywsdl;

import java.util.HashMap;
import java.util.List;

import org.openwide.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;

public class EasyWSDLServiceHelper implements WsdlServiceHelper {

	/**
	 * Logger
	 */
	//private static Logger logger = Logger.getLogger(EasyWSDLServiceHelper.class.getClass());	
	
	//TODO Finish this alternative solution with EasyWSDL
	@Override
	public String callService(String wsdlUrl, String binding, String wsldOperation, HashMap<String, List<String>> paramList) throws Exception {
		/*
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		Description desc = reader.read(new URL(wsdlUrl));
		for(Service service : desc.getServices()){
			logger.debug("service found " + service.getQName());
			//if(){
			//	Endpoint endpoint = service.getEndpoint(operation);
			//	logger.debug("Endpoint name : " + endpoint.getName());
			//}
			// List endpoints
			//for(Endpoint endpoint : service.getEndpoints()){
			//	logger.debug("Endpoint binding : " + endpoint. .getBinding());
			//}
		}*/
		return null;
	}

}
