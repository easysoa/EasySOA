package org.openwide.easysoa.scaffolding.wsdlhelper;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author jguillemotte
 *
 */
public interface WsdlServiceHelper {

	/**
	 * Call a distant service with mapped parameters and returns the response as a string. 
	 * @param wsdlUrl The WSDl url of the service to call
	 * @param wsldOperation The operation to call
	 * @param paramList The list of parameters to map
	 * @return The response of the service
	 * @throws Exception If a problem occurs
	 */
	public String callService(String wsdlUrl, String binding, String wsldOperation, HashMap<String, List<String>> paramList) throws Exception;
	
}
