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
