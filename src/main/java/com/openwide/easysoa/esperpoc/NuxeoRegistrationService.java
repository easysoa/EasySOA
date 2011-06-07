package com.openwide.easysoa.esperpoc;

import javax.ws.rs.core.GenericEntity;
import org.apache.log4j.Logger;
import com.openwide.easysoa.esperpoc.esper.Api;
import com.openwide.easysoa.esperpoc.esper.Appli;
import com.openwide.easysoa.esperpoc.esper.Service;
import com.openwide.easysoa.esperpoc.esper.WSDLService;
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
	public String registerWSDLService(WSDLService service){
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
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 */
	public String registerRestService(Service service){
		/*
		{
		  "description": "Service-level notification.",
		  "parameters": {
		    "contentTypeOut": "HTTP content type of the result body",
		    "relatedUsers": "Users that have been using the service",
		    "title": "The name of the document.",
		    "contentTypeIn": "HTTP content type of the request body",
		    "httpMethod": "POST, GET...",
		    "description": "A short description.",
		    "parentUrl": "(mandatory) Service API URL (WSDL address, parent path...)",
		    "callcount": "Times the service has been called since last notification",
		    "url": "(mandatory) Service URL."
		  }
		}
		*/
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
		url.append("service");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(service.getUrl());
		body.append("&parentUrl=");
		body.append(service.getParentUrl());
		body.append("&callcount=");
		body.append(service.getCallCount());
		body.append("&title=");
		body.append(service.getTitle());
		body.append("&contentTypeOut=");
		body.append(service.getContentTypeOut());
		body.append("&contentTypeIn=");
		body.append(service.getContentTypeIn());
		body.append("&relatedUsers=");
		body.append(service.getRelatedUsers());
		body.append("&description=");
		body.append(service.getDescription());
		body.append("&httpMethod=");
		body.append(service.getHttpMethod());		
		logger.debug("[registerRESTService()] --- Message url : " + url.toString());
		logger.debug("[registerRESTService()] --- Message body : " + body.toString());
		return sendRequest(url.toString(), body.toString());
	}
	
	/**
	 * Register a REST Application in Nuxeo
	 * @param appli The application to register
	 * @return The response send back by Nuxeo
	 */
	public String registerRestAppli(Appli appli){
		/*
	 	{
		  "description": "Notification concerning an application implementation.",
		  "parameters": {
		    "rootServicesUrl": "(mandatory) Services root.",
		    "uiUrl": "Application GUI entry point.",
		    "title": "The name of the document.",
		    "technology": "Services implementation technology.",
		    "standard": "Protocol standard if applicable.",
		    "sourcesUrl": "Source code access.",
		    "description": "A short description.",
		    "server": "IP of the server."
		  }
		}
		 */
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
		url.append("appliimpl");
		StringBuffer body = new StringBuffer();
		body.append("rootServicesUrl=");
		body.append(appli.getRootServicesUrl());
		body.append("&uiUrl=");
		body.append(appli.getUiUrl());
		body.append("&server=");
		body.append(appli.getServer());
		body.append("&title=");
		body.append(appli.getTitle());
		body.append("&technology=");
		body.append(appli.getTechnology());
		body.append("&standard=");
		body.append(appli.getStandard());
		body.append("&sourcesUrl=");
		body.append(appli.getSourcesUrl());		
		body.append("&description=");
		body.append(appli.getDescription());		
		logger.debug("[registerRestAppli()] --- Message url : " + url.toString());
		logger.debug("[registerRestAppli()] --- Message body : " + body.toString());
		return sendRequest(url.toString(), body.toString());
	}

	/**
	 * Register a REST API in Nuxeo
	 * @param api The API to register
	 * @return The response send back by Nuxeo
	 */
	public String registerRestApi(Api api){
		/*
		{
		  "description": "Service-level notification.",
		  "parameters": {
		    "title": "The name of the document.",
		    "application": "The related business application.",
		    "description": "A short description.",
		    "parentUrl": "(mandatory) The parent URL, which is either another service API, or the service root.",
		    "sourceUrl": "The web page where the service has been found (useful for REST only).",
		    "url": "(mandatory) Service API url (WSDL address, parent path...)."
		  }
		}
		 */
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.url", NUXEO_DEFAULT_URL));
		url.append("api");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(api.getUrl());
		body.append("&parentUrl=");
		body.append(api.getParentUrl());
		body.append("&title=");
		body.append(api.getTitle());
		body.append("&application=");
		body.append(api.getApplication());
		body.append("&description=");
		body.append(api.getDescription());
		body.append("&sourceUrl=");
		body.append(api.getSourceUrl());	
		logger.debug("[registerRestApi()] --- Message url : " + url.toString());
		logger.debug("[registerRestApi()] --- Message body : " + body.toString());
		return sendRequest(url.toString(), body.toString());
	}
	
	/**
	 * Send a request to Nuxeo to register or to update an application / api / service
	 * @param url Nuxeo url
	 * @param body Message to send
	 * @return The response send back by Nuxeo
	 */
	private String sendRequest(String url, String body){
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
		WebResource webResource = client.resource(url.toString());
		GenericEntity<String> entity = new GenericEntity<String>(body.toString()) {};
		ClientResponse response = webResource.entity(entity).type("application/x-www-form-urlencoded").post(ClientResponse.class);
	   	int status = response.getStatus();
	   	logger.debug("[sendRequest()] --- Registration request response status = " + status);
		String textEntity = response.getEntity(String.class);
		logger.debug("[sendRequest()] --- Registration request response = " + textEntity);	
		return textEntity;		
	}

}
