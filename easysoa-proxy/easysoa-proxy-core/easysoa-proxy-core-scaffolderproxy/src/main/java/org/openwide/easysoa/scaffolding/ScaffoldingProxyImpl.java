package org.openwide.easysoa.scaffolding;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.xml.XMLSerializer;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.openwide.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;
import org.openwide.easysoa.scaffolding.wsdltemplate.WSRequest;
import org.osoa.sca.annotations.Reference;

/**
 * Scaffolding proxy. Started with FraSCAti and Velocity
 * TODO : Build a JSON structure to pass parameters to the proxy
 * TODO : Add a dev form mode
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
	//public Response redirectRestToSoap(UriInfo ui, Request request, String binding, String operation) throws Exception {
	public Response redirectRestToSoap(HttpContext httpContext, HttpServletRequest servletRequest){
		System.out.println("Entering in redirectRestToSoap method !");
		// Getting the JSON Data structure
		StringBuffer jsonParameters = new StringBuffer();
		try {
			byte[] buffer = new byte[512];
			InputStream iStream = servletRequest.getInputStream();
			while(iStream.available() > 0 ){
				iStream.read(buffer);
				jsonParameters.append(new String(buffer));
			}
			logger.debug("jsonParameters : " + jsonParameters.toString());
			System.out.println("jsonParameters : " + jsonParameters.toString());
			WSRequest request = WSRequest.parseJSON(jsonParameters.toString());
			System.out.println("jsonRequest : " + request.toString());

			// alternatives :
			// 1. Generate xml request for SOAP service, need to have the WSDL File
			// SOAPUI can be used ... Check if it the good solution ....
			// Lot of dependencies to add manually to use SOAPUI
			String response = "OK";
			String url;
			if(request.getWsdlUrl().endsWith("?wsdl") || request.getWsdlUrl().endsWith(".wsdl")){
				url = request.getWsdlUrl();
			} else {
				url = request.getWsdlUrl() + "?wsdl";
			}
			response = wsdlServiceHelper.callService(url, request.getBinding(), request.getOperation(), request.getParamList());
			// Call a method to transform xml to json
			response = xmlToJson(response).toString();
			System.out.println(response);
			logger.debug("final response : " + response);
			return Response.ok(response, MediaType.TEXT_HTML).header("Access-Control-Allow-Origin", "*").build();			
			
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
			
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Unable to read request content to get JSON parameters", ex);
			return Response.serverError().header("Access-Control-Allow-Origin", "*").build();					
		}			
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
