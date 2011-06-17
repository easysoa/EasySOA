package com.openwide.easysoa.esperpoc;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericEntity;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.monitoring.soa.WSDLService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NuxeoRegistrationService {

	private final static String NUXEO_WSDL_DEFAULT_URL = "http://localhost:8080/nuxeo/site/easysoa/wsdlscraper/";
	private final static String NUXEO_REST_DEFAULT_URL = "http://localhost:8080/nuxeo/site/easysoa/notification/";
	private final static String NUXEO_AUTOMATION_DEFAULT_URL = "http://localhost:8080/nuxeo/site/automation";
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
		StringBuffer sb = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.wsdl.url", NUXEO_WSDL_DEFAULT_URL));
	    /*sb.append(service.getAppName()); //Application Name
	    sb.append("/");
	    sb.append(service.getServiceName()); //Service name
	    sb.append("/");*/
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
	//TODO Update this classe since the Nuxeo interface has changed	
	public String registerRestService(Service service){
		/*
		{
		  "description": "Service-level notification.",
		  "parameters": {
		    "contentTypeOut": "HTTP content type of the result body",
		    "relatedUsers": "Users that have been using the service",
		    "contentTypeIn": "HTTP content type of the request body",
		    "title": "The name of the document.",
		    "discoveryTypeImport": "Notes about import-specific notifications. Informs the document of the notification source.",
		    "discoveryTypeBrowsing": "Notes about browsing-specific notifications. Informs the document of the notification source.",
		    "httpMethod": "POST, GET...",
		    "description": "A short description.",
		    "discoveryTypeMonitoring": "INotes about monitoring-specific notifications. Informs the document of the notification source.",
		    "parentUrl": "(mandatory) Service API URL (WSDL address, parent path...)",
		    "callcount": "Times the service has been called since last notification",
		    "url": "(mandatory) Service URL."
		  }
		}
		*/
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.rest.url", NUXEO_REST_DEFAULT_URL));
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
	//TODO Update this classe since the Nuxeo interface has changed
	public String registerRestAppli(Appli appli){
		/*
		{
		  "description": "Notification concerning an application implementation.",
		  "parameters": {
		    "technology": "Services implementation technology.",
		    "standard": "Protocol standard if applicable.",
		    "provider": "Company that provides these services.",
		    "url": "(mandatory) Services root.",
		    "uiUrl": "Application GUI entry point.",
		    "title": "The name of the document.",
		    "discoveryTypeBrowsing": "Notes about browsing-specific notifications. Informs the document of the notification source.",
		    "discoveryTypeImport": "Notes about import-specific notifications. Informs the document of the notification source.",
		    "environment": "The application environment (production, development...)",
		    "sourcesUrl": "Source code access.",
		    "description": "A short description.",
		    "discoveryTypeMonitoring": "INotes about monitoring-specific notifications. Informs the document of the notification source.",
		    "server": "IP of the server."
		  }
		}
		*/	
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.rest.url", NUXEO_REST_DEFAULT_URL));
		url.append("appliimpl");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(appli.getUrl());
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
	//TODO Update this classe since the Nuxeo interface has changed	
	public String registerRestApi(Api api){
		/*
		{
		  "description": "Service-level notification.",
		  "parameters": {
		    "application": "The related business application.",
		    "title": "The name of the document.",
		    "discoveryTypeImport": "Notes about import-specific notifications. Informs the document of the notification source.",
		    "discoveryTypeBrowsing": "Notes about browsing-specific notifications. Informs the document of the notification source.",
		    "description": "A short description.",
		    "protocols": "The supported protocols.",
		    "discoveryTypeMonitoring": "INotes about monitoring-specific notifications. Informs the document of the notification source.",
		    "parentUrl": "The parent URL, which is either another service API, or the service root.",
		    "url": "(mandatory) Service API url (WSDL address, parent path...)."
		  }
		}
		 */
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.registration.rest.url", NUXEO_REST_DEFAULT_URL));
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
		//body.append("&sourceUrl=");
		//body.append(api.getSourceUrl());	
		logger.debug("[registerRestApi()] --- Message url : " + url.toString());
		logger.debug("[registerRestApi()] --- Message body : " + body.toString());
		return sendRequest(url.toString(), body.toString());
	}
	
	/*public JSONObject getAllSoaNodes() {
		String query = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		String res =  sendQuery(query);
		try {
			return new JSONObject(res);
		} catch (JSONException e) {
			logger.error(e);
			return null;
		}
	}*/
	
	/**
	 * return all soa node registred in Nuxeo
	 * @return A <code>List</code> of soa <code>Node</code> 
	 */
	public List<Node> getAllSoaNodes() {
		String query = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		JsonMapper soaNodesJsonMapper = new SoaNodesJsonMapper();
		try {
			return getAllTo(query, soaNodesJsonMapper);
		} catch (JSONException e) {
			logger.error(e);
			// TODO technical error
			return null;
		}
	}
	
	/**
	 * Return all the objects registered in nuxeo and corresponding to the query
	 * @param query The query to find objects in Nuxeo
	 * @param jsonMapper The mapper to map the results in Java objects
	 * @return A <code>List</code> of soa <code>Node</code>
	 * @throws JSONException In case of problem
	 */
	public List<Node> getAllTo(String query , JsonMapper jsonMapper) throws JSONException {
		String res =  sendQuery(query);
		ArrayList<Node> soaNodes = new ArrayList<Node>();
		JSONArray resObject = new JSONObject(res).getJSONArray("entries");
		for (int i = 0; i < resObject.length(); i++) {
			try {
				JSONObject child = resObject.getJSONObject(i);
				Node soaNode = (Node) jsonMapper.mapTo(child);
				soaNodes.add(soaNode);
			} catch (JSONException e) {
				logger.error(e);
				// skipping
			}
		}
		return soaNodes;
	}
		
	/**
	 * Queries nuxeo docs
	 * @param url Nuxeo url
	 * @param body Message to send
	 * @return The response send back by Nuxeo
	 */
	private String sendQuery(String query){
		StringBuffer urlBuf = new StringBuffer(PropertyManager.getProperty("nuxeo.automation.url", NUXEO_AUTOMATION_DEFAULT_URL));
		urlBuf.append("/");
	    urlBuf.append("Document.Query"); // operation name

	    try {
		    JSONObject bodyBuf = new JSONObject();
		    JSONObject bodyBufQuery = new JSONObject();
		    bodyBufQuery.put("query", query);
			bodyBuf.put("params", bodyBufQuery);
		    
			logger.debug("[sendQuery()] --- Request URL = " + urlBuf.toString());
			// Send request to register the service
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
			
			WebResource webResource = client.resource(urlBuf.toString());
			GenericEntity<String> entity = new GenericEntity<String>(bodyBuf.toString()) {};
			ClientResponse response = webResource
				.entity(entity)
				.type("application/json+nxrequest; charset=UTF-8")
				.accept("application/json+nxentity")
				.header("X-NXDocumentProperties", "*")
				.post(ClientResponse.class);
			
		   	int status = response.getStatus();
		   	logger.debug("[sendQuery()] --- Registration request response status = " + status);
			String textEntity = response.getEntity(String.class);
			logger.debug("[sendQuery()] --- Registration request response = " + textEntity);	
			return textEntity;		
			
		} catch (JSONException e) {
			logger.error("Failed to create request body", e);
			return null;
		}
	}
	
	/**
	 * Send a request to Nuxeo to register or to update an application / api / service
	 * @param url Nuxeo url
	 * @param body Message to send
	 * @return The response send back by Nuxeo
	 */
	private String sendRequest(String url, String body){
		String q = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		
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
