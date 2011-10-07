package org.openwide.easysoa.scaffolding;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
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
	@POST
	@Path("/callService/{binding}/{operation}/")
	public Response redirectRestToSoap(@Context HttpContext httpContext, @Context HttpServletRequest request);
	
}

