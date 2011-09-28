package org.openwide.easysoa.scaffolding.wsdlhelper.easywsdl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openwide.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;
/*
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
*/

public class EasyWSDLServiceHelper implements WsdlServiceHelper {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(EasyWSDLServiceHelper.class.getClass());	
	
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
