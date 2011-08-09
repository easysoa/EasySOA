package com.openwide.sca.proxy;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osoa.sca.annotations.Reference;

import pureairflowers.clients.PureAirFlowersClients;

@Path("/")
@Produces("text/html")
public class RestSoapProxyImpl implements RestSoapProxy {

	/** Reference to the CXF PureAirFlowers web service */
	@Reference
	protected PureAirFlowersClients ws;
	
	/**
	 * Default constructor
	 */
	public RestSoapProxyImpl(){
		System.out.println("RestSoapProxy created ....");
	}	
	
	@GET
	@Path("/")
	public Response redirectRequests(@Context UriInfo ui, @Context Request request) {
	    MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
	    //MultivaluedMap<String, String> pathParams = ui.getPathParameters();
	    String webServiceResponse;
	    if(ws == null){
			//webServiceResponse = "Error : HelloWorld object is null !";
	    	webServiceResponse = "Error : PureAirFlowers object is null !";
		} else {
			// method param
			List<String> operationParams = queryParams.get("operation");
			String operation = "";
			if(operationParams != null && operationParams.size() > 0){
				operation = operationParams.get(0);
				System.out.println("[SCA PROXY REST/SOAP TEST] operation : " + operation);
			}
			// text param
			List<String> textParams = queryParams.get("text");
			String text = "";
			if(textParams != null && textParams.size() > 0){
				text = textParams.get(0);
				System.out.println("[SCA PROXY REST/SOAP TEST] text : " + text);
			}
			// clientId param
			List<String> clientIdParams = queryParams.get("clientId");
			String clientId = "";
			if(clientIdParams != null && clientIdParams.size() > 0){
				text = textParams.get(0);
				System.out.println("[SCA PROXY REST/SOAP TEST] clientId : " + text);
			}			
			// iteration param
			List<String> iterationsParams = queryParams.get("iterations");
			int iterations = 5;
			if(iterationsParams != null && iterationsParams.size() > 0){
				try{
					iterations = Integer.parseInt(iterationsParams.get(0));				
					System.out.println("[SCA PROXY REST/SOAP TEST] iterations : " + iterations);
				}
				catch(Exception ex){
					System.out.println("[SCA PROXY REST/SOAP TEST] iterations parameter is not valid (" + iterationsParams.get(0) + ") default value (5) used !");
				}
			}
			/*if("repeatAfterMe".equalsIgnoreCase(operation)){
				webServiceResponse = ws.repeatAfterMe(text, iterations);				
			} else {
				webServiceResponse = ws.sayHi(text);				
			}*/
			webServiceResponse = String.valueOf(ws.getOrdersNumber(clientId));
			System.out.println("[SCA PROXY TEST] serverResponse : " + webServiceResponse);
		}		
		// Build the response to send back to the client
	    return Response.ok(webServiceResponse, MediaType.TEXT_HTML).header("Access-Control-Allow-Origin", "*").build();
	}

}
