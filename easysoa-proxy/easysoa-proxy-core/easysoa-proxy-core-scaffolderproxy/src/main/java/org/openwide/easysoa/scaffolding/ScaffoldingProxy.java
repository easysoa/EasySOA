/**
 * 
 */
package org.openwide.easysoa.scaffolding;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author jguillemotte
 *
 */
public interface ScaffoldingProxy {

	/**
	 * Receive a REST request, transform it to a SOAP request, get the response and send back the answers to the HTML form
	 * 
	 * Example of REST request : http://localhost:7001/callService/SoapServiceMockSoapBinding/getPrice/?arg0=patatoes&arg1=25&wsdlUrl=http://localhost:8086/soapServiceMock
	 * where :
	 * - Binding is SoapServiceMockSoapBinding
	 * - Operation is getPrice
	 * - There is 2 parameters for the operation getPrice : arg1 and arg2
	 * - The WSDL to use must be specified with the wsdlUrl parameter
	 * 
	 * The port is specified in the scaffoldingProxy.composite file. 
	 * 	
	 * @param ui Context UriInfo
	 * @param request Context Request
	 * @param binding Binding to call
	 * @param operation Operation to call
	 * @return The SOAP service response converted to JSON format
	 * @throws Exception If a problem occurs
	 */
	@GET
	@Path("/callService/{binding}/{operation}/")
	public Response redirectRestToSoap(@Context UriInfo ui, @Context Request request, @PathParam("binding") String binding, @PathParam("operation") String operation) throws Exception;	
	
}

