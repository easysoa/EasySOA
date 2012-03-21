/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */


package com.openwide.easysoa.nuxeo.registration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.easysoa.properties.PropertyManager;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationFactory.RestDiscoveryService;
import org.easysoa.rest.RestNotificationRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.util.ContentReader;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class regroups all the method required to send requests to Nuxeo.
 * @author jguillemotte
 *
 */
public class NuxeoRegistrationService {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(NuxeoRegistrationService.class.getName());
	
	private static RestNotificationFactory factory = null;

    public NuxeoRegistrationService() throws IOException {
        factory = new RestNotificationFactory(PropertyManager.getProperty("nuxeo.rest.service"),
        		PropertyManager.getProperty("nuxeo.auth.login", "Administrator"),
        		PropertyManager.getProperty("nuxeo.auth.password", "Administrator"));
    }

    /**
	 * Register a WSDL SOAP service in Nuxeo
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 * @throws Exception 
	 */
    // TODO Test
	public String registerWSDLService(Service service) throws Exception{
        logger.debug("[registerWSDLService()] --- Message url : " + service.getUrl().toString());
	    return registerService(service);
	}
	
	/**
	 * Register a REST service in Nuxeo
	 * @param service The service to register
	 * @return The response send back by Nuxeo
	 */
	public String registerRestService(Service service){
        logger.debug("[registerRESTService()] --- Message url : " + service.getUrl().toString());
        return registerService(service);
	}
	
	private String registerService(Service service) {
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
        RestNotificationRequest request = factory.createNotification(RestDiscoveryService.SERVICE);
        
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
        request.setProperty(org.easysoa.doctypes.Service.PROP_PARTICIPANTS, service.getParticipants());
        request.setProperty(org.easysoa.doctypes.Service.PROP_HTTPMETHOD, service.getHttpMethod());
    
        //TODO "discoveryTypeMonitoring": "Notes about monitoring-specific notifications.
        // Informs the document of the notification source." Replace localhost with other details        
        request.setProperty(org.easysoa.doctypes.Service.PROP_DTMONITORING, "localhost");
        
        logger.debug("[registerWSDLService()] --- Message url : " + service.getUrl().toString());
        
        try {
            return request.send().toString();
        } catch (Exception e) {
            return null;
        }
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
        RestNotificationRequest request = factory.createNotification(RestDiscoveryService.APPLIIMPL);
        
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_URL, appli.getUrl());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_UIURL, appli.getUiUrl());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_SOURCESURL, appli.getSourcesUrl());
        
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_TITLE, appli.getTitle());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_DESCRIPTION, appli.getDescription());
        
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_SERVER, appli.getServer());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_TECHNOLOGY, appli.getTechnology());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_STANDARD, appli.getStandard());
        request.setProperty(org.easysoa.doctypes.AppliImpl.PROP_DTMONITORING, "localhost");
        
		logger.debug("[registerRestAppli()] --- Message url : " + appli.getUrl().toString());
		
        try {
            return request.send().toString();
        } catch (Exception e) {
            return null;
        }
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
        RestNotificationRequest request = factory.createNotification(RestDiscoveryService.APPLIIMPL);
        
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_URL, api.getUrl());
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_PARENTURL, api.getParentUrl());
        
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_TITLE, api.getTitle().replaceFirst("/", ""));
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_DESCRIPTION, api.getDescription());
        
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_APPLICATION, api.getApplication());
        request.setProperty(org.easysoa.doctypes.ServiceAPI.PROP_DTMONITORING, "localhost");
        
		logger.debug("[registerRestApi()] --- Message url : " + api.getUrl().toString());

        try {
            return request.send().toString();
        } catch (Exception e) {
            return null;
        }
	}
	
	/**
	 * return all soa node registred in Nuxeo
	 * @return A <code>List</code> of soa <code>Node</code> 
	 * @throws Exception 
	 */
	public List<Node> getAllSoaNodes() throws Exception {
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
	 * @throws Exception 
	 */
	public List<Node> getAllTo(String query , JsonMapper jsonMapper) throws Exception {
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
	 * @throws Exception 
	 */
	public String sendQuery(String query) throws Exception {
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
			/*Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
			WebResource webResource = client.resource(urlBuf.toString());
			GenericEntity<String> entity = new GenericEntity<String>(bodyBuf.toString()) {};
			ClientResponse response = webResource
				.entity(entity)
				.type("application/json+nxrequest; charset=UTF-8")
				.accept("application/json+nxentity")
				.header("X-NXDocumentProperties", "*")
				.post(ClientResponse.class);
			*/
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator"));
			HttpPost postRequest = new HttpPost(urlBuf.toString());
			postRequest.addHeader(new BasicScheme().authenticate(creds, postRequest));
			HttpEntity entity = new StringEntity(bodyBuf.toString());
			postRequest.setEntity(entity);
			postRequest.addHeader(new BasicHeader("X-NXDocumentProperties", "*"));
			postRequest.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json+nxrequest; charset=UTF-8"));
			HttpResponse response = httpClient.execute(postRequest); 
					
		   	int status = response.getStatusLine().getStatusCode();
		   	logger.debug("[sendQuery()] --- sendQuery response status = " + status);
			String textEntity = ContentReader.read(response.getEntity().getContent());
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
	 * @throws Exception 
	 */
	public boolean deleteQuery(String uid) throws Exception {
		StringBuffer urlBuf = new StringBuffer(PropertyManager.getProperty("nuxeo.automation.service"));
	    urlBuf.append("Document.Delete"); // operation name

	    try {
		    JSONObject bodyBuf = new JSONObject();
		    bodyBuf.put("input", "doc:"+uid);
		    
			logger.debug("[deleteQuery()] --- Request URL = " + urlBuf.toString());
			// Send request get registered services
			/*Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator")));
			
			WebResource webResource = client.resource(urlBuf.toString());
			GenericEntity<String> entity = new GenericEntity<String>(bodyBuf.toString()) {};
			ClientResponse response = webResource
				.entity(entity)
				.type("application/json+nxrequest; charset=UTF-8")
				.accept("application/json+nxentity")
				.header("X-NXDocumentProperties", "*")
				.post(ClientResponse.class);
			*/
			DefaultHttpClient httpClient = new DefaultHttpClient();
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(PropertyManager.getProperty("nuxeo.auth.login", "Administrator"), PropertyManager.getProperty("nuxeo.auth.password", "Administrator"));
			HttpPost postRequest = new HttpPost(urlBuf.toString());
			postRequest.addHeader(new BasicScheme().authenticate(creds, postRequest));
			HttpEntity entity = new StringEntity(bodyBuf.toString());
			postRequest.setEntity(entity);
			postRequest.addHeader(new BasicHeader("X-NXDocumentProperties", "*"));
			postRequest.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json+nxrequest; charset=UTF-8"));
			HttpResponse response = httpClient.execute(postRequest); 			
			
			if(response.getStatusLine().getStatusCode() == 204){
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			logger.error("Failed to create request body", e);
			return false;
		}
	}	
	
}
