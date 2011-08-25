package org.openwide.easysoa.scaffolding;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.submit.transports.http.WsdlResponse;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.support.SoapUIException;

import org.json.JSONException;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

/**
 * Scaffolding proxy. Started with FraSCAti and Velocity
 * 
 * Goal is to :
 * - generate dynamically html form from a WSDL file.
 * - generate dynamically input and output xml schemas from WSDL
 * - publish a rest/soap proxy to establish a data exchange between the HTML form and the remote WSDl service. This proxy must be generic.

 * @author jguillemotte
 *
 */
public class ScaffoldingProxyImpl implements ScaffoldingProxy {

	public static final String WSDL_OPERATION = "wsdl_operation";
	public static final String WSDL_URL = "wsdl_url";
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ScaffoldingProxyImpl.class.getClass());
	
	@Override
	public Response redirectRestToSoap(UriInfo ui, Request request, String binding, String operation) throws IllegalArgumentException, MalformedURLException, TransformerException, IOException, WSDLException, URISyntaxException, SubmitException, JSONException {

		// Getting the request parameters
		HashMap<String, List<String>> paramList = new HashMap<String, List<String>>(); 
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for(String paramKey : queryParams.keySet()){
			logger.debug("paramKey = " + paramKey + ", value : " + queryParams.get(paramKey));
			paramList.put(paramKey, queryParams.get(paramKey));
		}

		// alternatives :
		
		// 1. Generate xml request for SOAP service, need to have the WSDL File
		// SOAPUI can be used ... Check if it the good solution ....
		// Lot of dependencies to add manually to use SOAPUI
		String response = "Test working !";
		try {
			response = testSoapUI(operation, paramList);
			logger.debug("final response : " + response);
			System.out.println("final response : " + response);
		} catch (SoapUIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return Response.ok(response, MediaType.TEXT_HTML).header("Access-Control-Allow-Origin", "*").build();
		
		// 4. use templates
	}
	
	@SuppressWarnings("unused")
	private void getOperationXmlRequest(String operation, URL wsdlUrl) throws WSDLException, MalformedURLException, IOException, URISyntaxException{
		// 3. Using EasyWSDL
		// Reading WSDL
		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		Description desc = reader.read(wsdlUrl);
		for(Service service : desc.getServices()){
			logger.debug("service found " + service.getQName());
			/*if(){
				Endpoint endpoint = service.getEndpoint(operation);
				logger.debug("Endpoint name : " + endpoint.getName());
			}*/
			// List endpoints
			/*for(Endpoint endpoint : service.getEndpoints()){
				logger.debug("Endpoint binding : " + endpoint. .getBinding());
			}*/
		}
	}
	
	/**
	 * Create a temporary SOAPUI project, get the XML request, map the request with the parameters,
	 * Send the request and get the response.
	 * @param wsldOperation The operation to call
	 * @param paramList The list of parameters to map in the request
	 * @return the service response as XML string, or null if a problem occurs
	 * @throws SoapUIException
	 * @throws XmlException
	 * @throws IOException
	 * @throws SubmitException
	 * @throws JSONException 
	 */
	private String testSoapUI(String wsldOperation, HashMap<String, List<String>> paramList) throws SoapUIException, XmlException, IOException, SubmitException, JSONException {
		
		// create new project
		WsdlProject project = new WsdlProject();
		// import amazon wsdl
		WsdlInterface iface = WsdlInterfaceFactory.importWsdl(project, "http://localhost:9010/PureAirFlowers?wsdl", true )[0];
		// get desired operation
		WsdlOperation operation = (WsdlOperation) iface.getOperationByName(wsldOperation);
		// create a new empty request for that operation
		WsdlRequest request = operation.addNewRequest("myRequest");
		// generate the request content from the schema
		//request.setRequestContent(operation.createRequest(true));

		//logger.debug("Soap request binding : " + request.getRequestContent());
		// How to set the parameters ????
		// It seem's that the only way to set parameter in the request is to get the requestContent and use the standard Java String manipulation method ....
		// Not very interesting..
		// Replace in the XML the key clientId by the parameter value		
		request.setRequestContent(mapInputParams(operation.createRequest(true), paramList));
		// submit the request
		WsdlSubmit wsdlSubmit = (WsdlSubmit) request.submit(new WsdlSubmitContext(request), false);
		// wait for the response
		WsdlResponse wsdlResponse = (WsdlResponse) wsdlSubmit.getResponse();
		System.out.println("Submit status : " + wsdlSubmit.getStatus());
		if(wsdlResponse != null){
			logger.debug("Soap Response" + wsdlResponse.getContentAsString());
			System.out.println("Soap Response" + wsdlResponse.getContentAsString());
			// Call a method to transform xml to json
			return xmlToJson(wsdlResponse.getContentAsString()).toString();
			//return wsdlResponse.getContentAsString();
		} else {
			logger.debug("Soap Response is null");
			System.out.println("Soap Response is null");
			return null;
		}
	}
	
	
	private String mapInputParams(String xmlRequest, HashMap<String, List<String>> params){
		// For each param key, replace the '?' in the xml request by the param value 
		logger.debug("XML request before mapping : " + xmlRequest);
		//System.out.println("XML request before mapping : " + xmlRequest);
		for(String paramKey : params.keySet()){
			//TODO make it works with multiple values
			//System.out.println("Key to replace : " + paramKey + ">?");
			//System.out.println("by : " + paramKey + ">" + params.get(paramKey).get(0));
			xmlRequest = xmlRequest.replace(paramKey + ">?", paramKey + ">" + params.get(paramKey).get(0));
		}
		//logger.debug("XML request after mapping : " + xmlRequest);
		System.out.println("XML request after mapping : " + xmlRequest);
		return xmlRequest;
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
