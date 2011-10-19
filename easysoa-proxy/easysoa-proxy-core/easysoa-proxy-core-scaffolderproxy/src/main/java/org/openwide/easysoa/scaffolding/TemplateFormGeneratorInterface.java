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

package org.openwide.easysoa.scaffolding;

import java.util.List;

import org.openwide.easysoa.scaffolding.wsdltemplate.WSEndpoint;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSField;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSOperation;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSService;
import org.ow2.easywsdl.schema.api.XmlException;

public interface TemplateFormGeneratorInterface {

	/**
	 * Return the web server port
	 * @return The web server port
	 */
	public Object getConstant(String constantName);

	/**
	 * Set the WSDl to parse : First thing to do before to call the other methods
	 * @param wsdlXmlSource
	 */
	public String updateWsdl(String wsdlXmlSource) throws Exception;
	
	/**
	 * Returns the service list
	 * @return
	 */
	public List<WSService> getServices();
	
	/**
	 * Returns the list of endpoints (or ports) for the service
	 * @return
	 */
	public List<WSEndpoint> getEndpoints(WSService wsService);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public String getBindingName(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	public List<WSOperation> getOperations(WSEndpoint wsEndpoint);
	
	/**
	 * 
	 * @param operation
	 * @return
	 */
	public String getOperationName(WSOperation wsOperation);

	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public String getOutputMessageName(WSEndpoint wsEndpoint, WSOperation wsOperation);	
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<WSField> getInputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException;
	
	/**
	 * 
	 * @param bindingOperation
	 * @return
	 */
	public List<WSField> getOutputFields(WSEndpoint wsEndpoint, WSOperation wsOperation) throws XmlException;	
	
}
