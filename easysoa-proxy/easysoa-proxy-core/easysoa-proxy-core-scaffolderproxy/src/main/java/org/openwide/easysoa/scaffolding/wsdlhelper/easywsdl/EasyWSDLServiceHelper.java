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
 * Contact : easysoa-dev@groups.google.com
 */

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
