package com.openwide.easysoa.esperpoc;

import javax.ws.rs.core.GenericEntity;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.openwide.easysoa.esperpoc.esper.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NuxeoRegistrationService {

	//private final static String NUXEO_DEFAULT_URL = "http://localhost:8080/nuxeo/restAPI/wsdlupload/";
	private final static String NUXEO_DEFAULT_URL = "http://localhost:8080/nuxeo/site/easysoa/notification/";
	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(NuxeoRegistrationService.class.getName());
	
	/**
	 * Register a WSDL service in Nuxeo
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 */
	public String registerWSDLService(Service service){
		StringBuffer sb = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
	    sb.append(service.getAppName()); //Application Name
	    sb.append("/");
	    sb.append(service.getServiceName()); //Service name
	    sb.append("/");
	    sb.append(service.getUrl());
		logger.debug("[resgisterWSDLService()] --- Request URL = " + sb.toString());
		// Send request to register the service
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
		WebResource webResource = client.resource(sb.toString());
		ClientResponse response = webResource.accept("text/plain").get(ClientResponse.class);
	   	int status = response.getStatus();
	   	logger.debug("[registerWSDLService()] --- Registration request response status = " + status);
		String textEntity = response.getEntity(String.class);
		logger.debug("[registerWSDLService()] --- Registration request response = " + textEntity);	
		return textEntity;
	}
	
	/**
	 * Register a REST service in Nuxeo
	 * @param service
	 * @return
	 */
	public String registerRestService(Service service){
		/*
		<p>Method: <b>POST</b><br />
		Use: <b>.../nuxeo/site/easysoa/notification/(appliimpl|api|service)/</b></p> 
		<p>Parameters have to be specified through the request content, following the content-type <i>application/x-www-form-urlencoded</i>.<br /> 
		Send a request with the <b>GET</b> method to any of the 3 services to learn what are the available parameters.</p> 
		*/
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
		url.append("service");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(service.getUrl());
		body.append("&parentUrl=");
		body.append(service.getAppName());
		body.append("&callcount=");
		body.append(1);
		body.append("&title=");
		body.append("TEST");
		/*body.append("url=");
		body.append(service.getUrl());
		body.append("&parentUrl=");
		body.append(service.getAppName());
		body.append("&callcount=");
		body.append(1);
		body.append("&title=");
		body.append("Test Google");*/	
		logger.debug("[registerRESTService()] --- Message body : " + body.toString());
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
		WebResource webResource = client.resource(url.toString());
		GenericEntity<String> entity = new GenericEntity<String>(body.toString()) {};
		ClientResponse response = webResource.entity(entity).type("application/x-www-form-urlencoded").post(ClientResponse.class);
	   	int status = response.getStatus();
	   	logger.debug("[registerRESTService()] --- Registration request response status = " + status);
		String textEntity = response.getEntity(String.class);
		logger.debug("[registerRESTService()] --- Registration request response = " + textEntity);	
		return textEntity;		
	}
	
}
