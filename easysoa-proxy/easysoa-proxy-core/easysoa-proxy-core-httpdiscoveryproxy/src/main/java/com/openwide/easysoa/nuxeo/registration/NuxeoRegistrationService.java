
package com.openwide.easysoa.nuxeo.registration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;

import org.apache.log4j.Logger;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.rest.RestNotificationRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.proxy.PropertyManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class regroups all the method required to send requests to Nuxeo.
 * @author jguillemotte
 *
 */
// TODO: Continue to refactor using easysoa-registry-api
public class NuxeoRegistrationService {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(NuxeoRegistrationService.class.getName());
	
	private static RestNotificationFactory factory = null;

    public NuxeoRegistrationService() throws IOException {

        factory = new RestNotificationFactory(PropertyManager.getProperty("nuxeo.rest.service"));
        
        // TODO: When auth is enabled in Discovery client, configure with
        // PropertyManager.getProperty("nuxeo.auth.login", "Administrator")
        // PropertyManager.getProperty("nuxeo.auth.password", "Administrator")
    }
    
	/**
	 * Register a WSDL SOAP service in Nuxeo
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 * @throws Exception 
	 */
    // TODO Test
	public String registerWSDLService(Service service) throws Exception{
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
	    RestNotificationRequest request = factory.createNotification(RestNotificationService.SERVICE);
	    
	    request.setProperty(org.easysoa.doctypes.Service.PROP_URL, service.getUrl()); // ex. http://localhost:9080/CreateSummary
        request.setProperty(org.easysoa.doctypes.Service.PROP_PARENTURL, service.getUrl().substring(0, service.getUrl().lastIndexOf('/'))); // ex.    http://localhost:9080
        request.setProperty(org.easysoa.doctypes.Service.PROP_FILEURL, service.getFileUrl());
        
        if (service.getTitle() != null) {
            // Remove the leading slash TODO MDU better i.e. when getting title
            request.setProperty(org.easysoa.doctypes.Service.PROP_TITLE, service.getTitle().replaceFirst("/", ""));
        }
        else {
            request.setProperty(org.easysoa.doctypes.Service.PROP_TITLE, service.getUrl());
        }
        request.setProperty(org.easysoa.doctypes.Service.PROP_DESCRIPTION, service.getDescription());
        
        request.setProperty(org.easysoa.doctypes.Service.PROP_CALLCOUNT, Integer.toString(service.getCallCount()));
        request.setProperty(org.easysoa.doctypes.Service.PROP_CONTENTTYPEIN, service.getContentTypeIn());
        request.setProperty(org.easysoa.doctypes.Service.PROP_CONTENTTYPEOUT, service.getContentTypeOut());
        request.setProperty(org.easysoa.doctypes.Service.PROP_RELATEDUSERS, service.getRelatedUsers());
        request.setProperty(org.easysoa.doctypes.Service.PROP_HTTPMETHOD, service.getHttpMethod());
      
        //TODO "discoveryTypeMonitoring": "Notes about monitoring-specific notifications.
        // Informs the document of the notification source." Replace localhost with other details        
        request.setProperty(org.easysoa.doctypes.Service.PROP_DTMONITORING, "localhost");
        
		logger.debug("[registerWSDLService()] --- Message url : " + service.getUrl().toString());
		
		JSONObject result = request.send();	
		return (result == null) ? null : result.toString();
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
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.notification.service"));
		url.append("service");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(service.getUrl());
		body.append("&parentUrl=");
		body.append(service.getParentUrl());
		body.append("&callcount=");
		body.append(service.getCallCount());
		body.append("&title=");
		if (service.getTitle() != null) {
		    body.append(service.getTitle().replaceFirst("/", "")); // Remove the leading slash
		}
		else {
	        body.append(service.getUrl());
		}
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
		//TODO "discoveryTypeMonitoring": "Notes about monitoring-specific notifications. Informs the document of the notification source." Replace localhost with other details		
		body.append("&discoveryTypeMonitoring=");
		body.append("localhost");
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
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.notification.service"));
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
		body.append("&discoveryTypeMonitoring=");
		body.append("localhost");		
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
		StringBuffer url = new StringBuffer(PropertyManager.getProperty("nuxeo.notification.service"));
		url.append("api");
		StringBuffer body = new StringBuffer();
		body.append("url=");
		body.append(api.getUrl());
		body.append("&parentUrl=");
		body.append(api.getParentUrl());
		body.append("&title=");
		body.append(api.getTitle().replaceFirst("/", "")); // Remove the leading slash
		body.append("&application=");
		body.append(api.getApplication());
		body.append("&description=");
		body.append(api.getDescription());
		//body.append("&sourceUrl=");
		//body.append(api.getSourceUrl());
		body.append("&discoveryTypeMonitoring=");
		body.append("localhost");
		logger.debug("[registerRestApi()] --- Message url : " + url.toString());
		logger.debug("[registerRestApi()] --- Message body : " + body.toString());
		return sendRequest(url.toString(), body.toString());
	}
	
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
				if (soaNode == null) {
					logger.warn("Skipping node of non-SOA type : " + child.toString());
					continue;
				}
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
	 * @param query Query to send
	 * @return The response send back by Nuxeo
	 */
	public String sendQuery(String query){
		StringBuffer urlBuf = new StringBuffer(PropertyManager.getProperty("nuxeo.automation.service"));
		//urlBuf.append("/");
	    urlBuf.append("Document.Query"); // operation name

	    try {
		    JSONObject bodyBuf = new JSONObject();
		    JSONObject bodyBufQuery = new JSONObject();
		    bodyBufQuery.put("query", query);
			bodyBuf.put("params", bodyBufQuery);
		    
			logger.debug("[sendQuery()] --- Request URL = " + urlBuf.toString());
			// Send request get registered services
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
		   	logger.debug("[sendQuery()] --- sendQuery response status = " + status);
			String textEntity = response.getEntity(String.class);
			logger.debug("[sendQuery()] --- sendQuery response = " + textEntity);	
			return textEntity;		
		} catch (JSONException e) {
			logger.error("Failed to create request body", e);
			return null;
		}
	}

	/**
	 * Delete nuxeo docs
	 * @param url Nuxeo url
	 * @param body Message to send
	 * @return The response send back by Nuxeo
	 */
	public boolean deleteQuery(String uid){
		StringBuffer urlBuf = new StringBuffer(PropertyManager.getProperty("nuxeo.automation.service"));
	    urlBuf.append("Document.Delete"); // operation name

	    try {
		    JSONObject bodyBuf = new JSONObject();
		    bodyBuf.put("input", "doc:"+uid);
		    
			logger.debug("[deleteQuery()] --- Request URL = " + urlBuf.toString());
			// Send request get registered services
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

			if(response.getStatus() == 204){
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			logger.error("Failed to create request body", e);
			return false;
		}
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
