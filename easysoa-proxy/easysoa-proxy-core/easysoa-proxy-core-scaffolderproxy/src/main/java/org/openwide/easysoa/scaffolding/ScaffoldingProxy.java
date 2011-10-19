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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.http.protocol.HttpContext;

/**
 * Scaffolding proxy interface
 * @author jguillemotte
 *
 */
public interface ScaffoldingProxy {

	/**
	 * Receive a REST POST request containing a JSON data structure, transform it to a SOAP request, get the response and send back the answers to the HTML form
	 * 
	 * Example of REST request : http://localhost:7001/callService/SoapServiceMockSoapBinding/getPrice/
	 * where :
	 * - Binding is SoapServiceMockSoapBinding
	 * - Operation is getPrice
	 *
	 * Here is a sample of JSON structure :
	 * {
	 *     "wsRequest": { "service": "SoapServiceMock", "binding": "SoapServiceMockSoapBinding", "operation": "getPrice", "wsdlUrl": "http://localhost:8086/soapServiceMock?wsdl"},
	 *     "formParameters":[{"paramName":"arg0","paramValue":"test"}, {"paramName":"arg1","paramValue":"25"}]
	 * }
	 * 
	 * - wsRequest contains all informations used directly by the scaffolding proxy
	 * - formParameters is an array of parameters contained in the HTML form 
	 * 
	 * The port is specified in the scaffoldingProxy.composite file. 
	 * 	
	 * @param httpContext HTTP context
	 * @param request Servlet context
	 * @return The SOAP service response converted to JSON format
	 * @throws Exception If a problem occurs
	 */
	@GET
	@Path("/callService/{binding}/{operation}/")
	public Response redirectRestToSoap(@Context HttpContext httpContext, @Context HttpServletRequest request);
	
}

