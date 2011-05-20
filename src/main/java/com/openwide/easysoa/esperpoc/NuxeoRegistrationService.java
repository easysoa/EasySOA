package com.openwide.easysoa.esperpoc;

import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.esper.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NuxeoRegistrationService {

	private final static String NUXEO_DEFAULT_URL = "http://localhost:8080/nuxeo/restAPI/wsdlupload/";
	
	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(NuxeoRegistrationService.class.getName());
	
	/**
	 * Register a service in Nuxeo
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 */
	public String registerService(Service service){
		StringBuffer sb = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
	    sb.append(service.getAppName()); //Application Name
	    sb.append("/");
	    sb.append(service.getServiceName()); //Service name
	    sb.append("/");
	    sb.append(service.getUrl());
		logger.debug("[MessageListener] --- Request URL = " + sb.toString());
		// Send request to register the service
		Client client = Client.create();
		//client.addFilter(new HTTPBasicAuthFilter("Administrator", "Administrator"));
		client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
		WebResource webResource = client.resource(sb.toString());
		ClientResponse response = webResource.accept("text/plain").get(ClientResponse.class);
	   	int status = response.getStatus();
	   	logger.debug("[MessageListener] --- Registration request response status = " + status);
		String textEntity = response.getEntity(String.class);
		logger.debug("[MessageListener] --- Registration request response = " + textEntity);	
		return textEntity;
	}
	
}
