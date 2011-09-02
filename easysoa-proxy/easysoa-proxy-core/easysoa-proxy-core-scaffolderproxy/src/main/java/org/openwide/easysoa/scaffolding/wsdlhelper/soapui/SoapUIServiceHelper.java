package org.openwide.easysoa.scaffolding.wsdlhelper.soapui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.json.JSONException;
import org.openwide.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;
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

/**
 * WSDL Service helper based on Soap UI
 * @author jguillemotte
 *
 */
public class SoapUIServiceHelper implements WsdlServiceHelper {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(SoapUIServiceHelper.class.getClass());	
	
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
	public String callService(String wsdlUrl, String binding, String wsldOperation, HashMap<String, List<String>> paramList) throws SoapUIException, XmlException, IOException, SubmitException, JSONException {
		// create new project
		WsdlProject project = new WsdlProject();
		// import wsdl
		WsdlInterface iface = null;
		WsdlInterface[] ifaceArray = WsdlInterfaceFactory.importWsdl(project, wsdlUrl, true);
		// Get the corresponding binding
		for(WsdlInterface i : ifaceArray){
			if(i.getName().equals(binding)){
				iface = i;
			}
		}
		// If iface still null, binding not found => error
		if(iface == null){
			throw new IllegalArgumentException("Binding '" + binding + "' not found in the specified WSDL");
		}
		// get desired operation
		WsdlOperation operation = (WsdlOperation) iface.getOperationByName(wsldOperation);
		// create a new empty request for that operation
		WsdlRequest request = operation.addNewRequest("myRequest");
		// generate the xml request schema and map the parameter values		
		request.setRequestContent(mapInputParams(operation.createRequest(true), paramList));
		// submit the request
		WsdlSubmit<?> wsdlSubmit = (WsdlSubmit<?>) request.submit(new WsdlSubmitContext(request), false);
		// Send the request and wait for the response
		WsdlResponse wsdlResponse = (WsdlResponse) wsdlSubmit.getResponse();
		System.out.println("Submit status : " + wsdlSubmit.getStatus());
		if(wsdlResponse != null){
			logger.debug("Soap Response" + wsdlResponse.getContentAsString());
			return wsdlResponse.getContentAsString();
		} else {
			logger.debug("Soap Response is null");
			return null;
		}
	}
	
	/**
	 * Map REST input params to the XML SOAP request
	 * @param xmlRequest The XML SOAP request
	 * @param params The params to map
	 * @return A mapped XML request for SOAP
	 */
	private String mapInputParams(String xmlRequest, HashMap<String, List<String>> params){
		// For each param key, replace the '?' in the xml request by the param value 
		logger.debug("XML request before mapping : " + xmlRequest);
		for(String paramKey : params.keySet()){
			//TODO make it works with multiple values
			xmlRequest = xmlRequest.replace(paramKey + ">?", paramKey + ">" + params.get(paramKey).get(0));
		}
		return xmlRequest;
	}

}
