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

//import pureairflowers.clients.PureAirFlowersClients;
import fr.inria.galaxy.j1.scenario1.TripPortType;

@Path("/")
@Produces("text/html")
public class RestSoapProxyImpl implements RestSoapProxy {

	/** Reference to the CXF Galaxy trip web service */
	@Reference
	protected TripPortType ws;
	
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
	    String webServiceResponse;
	    if(ws == null){
	    	webServiceResponse = "Error : TripPortType object is null !";
		} else {
			// arg0 param
			List<String> arg0Params = queryParams.get("arg0");
			String arg0 = "";
			if(arg0Params != null && arg0Params.size() > 0){
				arg0 = arg0Params.get(0);
				System.out.println("[SCA PROXY REST/SOAP TEST] arg0 : " + arg0);
			}
			// arg1 param
			List<String> arg1Params = queryParams.get("arg1");
			String arg1 = "";
			if(arg1Params != null && arg1Params.size() > 0){
				arg1 = arg1Params.get(0);
				System.out.println("[SCA PROXY REST/SOAP TEST] text : " + arg1);
			}
			// arg2 param
			List<String> arg2Params = queryParams.get("arg2");
			double arg2 = 0;
			if(arg2Params != null && arg2Params.size() > 0){
				arg2 = Double.parseDouble(arg2Params.get(0));
				System.out.println("[SCA PROXY REST/SOAP TEST] clientId : " + arg2);
			}			

			// Get web service response
			webServiceResponse = String.valueOf(ws.process(arg0, arg1, arg2));
			System.out.println("[SCA PROXY TEST] serverResponse : " + webServiceResponse);
		}		
		// Build the response to send back to the client
	    return Response.ok(webServiceResponse, MediaType.TEXT_HTML).header("Access-Control-Allow-Origin", "*").build();
	}

}
