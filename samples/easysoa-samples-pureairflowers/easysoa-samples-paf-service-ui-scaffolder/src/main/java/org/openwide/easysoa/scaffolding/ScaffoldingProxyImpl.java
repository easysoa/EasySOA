package org.openwide.easysoa.scaffolding;

import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;
import org.openwide.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;
import org.osoa.sca.annotations.Reference;

/**
 * Scaffolding proxy. Started with FraSCAti and Velocity
 * TODO :
 *  Build a JSON structure to pass parameters to the proxy
 *  Add a dev form mode
 * 
 * Goal is to :
 * - generate dynamically html form from a WSDL file.
 * - generate dynamically input and output xml schemas from WSDL
 * - publish a rest/soap proxy to establish a data exchange between the HTML form and the remote WSDl service. This proxy must be generic.
 * @author jguillemotte
 *
 */
public class ScaffoldingProxyImpl implements ScaffoldingProxy {

	/* */
	public static final String WSDL_OPERATION = "wsdl_operation";
	/* */
	public static final String WSDL_URL = "wsdl_url";
	
	@Reference
	WsdlServiceHelper wsdlServiceHelper;
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ScaffoldingProxyImpl.class.getClass());
	
	@Override
	public Response redirectRestToSoap(UriInfo ui, Request request, String binding, String operation) throws Exception {

		// Getting the request parameters
		String wsdlUrl = "";
		HashMap<String, List<String>> paramList = new HashMap<String, List<String>>(); 
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for(String paramKey : queryParams.keySet()){
			logger.debug("paramKey = " + paramKey + ", value : " + queryParams.get(paramKey));
			if("wsdlUrl".equals(paramKey)){
				wsdlUrl = queryParams.get(paramKey).get(0);
			} else {
				paramList.put(paramKey, queryParams.get(paramKey));
			}
		}

		// alternatives :
		
		// 1. Generate xml request for SOAP service, need to have the WSDL File
		// SOAPUI can be used ... Check if it the good solution ....
		// Lot of dependencies to add manually to use SOAPUI
		String response = "Test working !";
		try {
			//response = callService("http://localhost:9010/PureAirFlowers?wsdl", operation, paramList);
			//response = callService(wsdlUrl + "?wsdl", operation, paramList);
			response = wsdlServiceHelper.callService(wsdlUrl + "?wsdl", operation, paramList);
			// Call a method to transform xml to json
			response = xmlToJson(response).toString();
		
			logger.debug("final response : " + response);
			System.out.println("final response : " + response);
			return Response.ok(response, MediaType.TEXT_HTML).header("Access-Control-Allow-Origin", "*").build();			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("An error occurs", ex);
			return Response.serverError().header("Access-Control-Allow-Origin", "*").build();						
		}
		
		// 2. Or get the WSDl file, and use a XSLT transform to get the request xml schema...
		// PB : there can be several operations in a WSDl, how to get the corresponding request ??
		//XSLTTransformer transformer = new XSLTTransformer();
		//transformer.transform(new URL("http://localhost:9010/PureAirFlowers?wsdl"), new File("src/main/resources/wsdlToOperationRequest.xslt"), new File("request.xml"));

		// 3. Using EasyWSDL
		//this.getOperationXmlRequest("getOrdersNumber", new URL("http://localhost:9010/PureAirFlowers?wsdl"));
		
		//GenerateFactory gFactory = new GenerateFactory();

		//Send the request, returns the response
		// How to do if there are multiple values to return ?
		// Return a json response, the javascript form will map the value in the corresponding fields

		
		// 4. use templates
	}
	
	/**
	 * Returns a JSONObject from an XML string
	 * @param xml The XML string to convert
	 * @return The converted JSONObject
	 * @throws JSONException If an error occurs
	 */
	private JSON xmlToJson(String xml) throws JSONException{
		XMLSerializer xmlSerializer = new XMLSerializer();
		xmlSerializer.setRemoveNamespacePrefixFromElements(true);
		xmlSerializer.setSkipNamespaces(true);
		return xmlSerializer.read(xml);
	}	
	
}
