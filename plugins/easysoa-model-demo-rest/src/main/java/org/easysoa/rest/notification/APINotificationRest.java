package org.easysoa.rest.notification;

import java.util.List;

import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.EasySOA;
import org.easysoa.EasySOAComponent;
import org.easysoa.rest.HttpFile;
import org.easysoa.services.VocabularyService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.resource.StringRepresentation;

@Path("easysoa/notification/api")
public class APINotificationRest extends NotificationRest {

	private static final Log log = LogFactory.getLog(APINotificationRest.class);
	private static final String PATH = "easysoa/notification/api/";
	
	public APINotificationRest() throws LoginException {
		super();
	}

	@POST
	@Produces("application/json")
	public Object doPost(@FormParam("apiUrl") String apiUrl,
			@FormParam("application") String application,
			@FormParam("name") String name,
			@FormParam("sourceURL") String sourceURL) throws JSONException {
		
		// Initialize
		JSONObject result = new JSONObject();
		result.put("result", "ok");
		
		// Create API
		if (apiUrl != null) {
			
			if (name == null)
				name = apiUrl;
			
			try {
				
				DocumentModel appliImplModel = session.getDocument(new PathRef(REGISTRY_ROOT+application));
				if (appliImplModel == null) {
					appliImplModel = session.createDocumentModel("Workspace");
					appliImplModel.setPathInfo(REGISTRY_ROOT, application);
					appliImplModel.setProperty("dublincore", "title", application);
					session.createDocument(appliImplModel);					
				}
				
				DocumentModel apiModel = session.createDocumentModel("ServiceAPI");
				apiModel.setPathInfo(appliImplModel.getPathAsString(), name);
				apiModel.setProperty("dublincore", "title", name);
				apiModel.setProperty("serviceapidef", "sourceURL", sourceURL);

				session.save();
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError(result, "API URL not informed");
		}
		
		// Return formatted result
		return result.toString(2);

	}

	/**
	 * Lessened API notification service (WSDL only), available for client cross-domain requests. 
	 * Uploads a WSDL to the Nuxeo repository, and fills business metadata given in parameters.
	 * 
	 * Use:
	 * .../nuxeo/restAPI/wsdlupload/{applicationName}/{serviceName}/{url}
	 * Params:
	 * {applicationName} The application name, optional (ignored if empty) 
	 * {serviceName} The service name, optional (ignored if empty)
	 * {url} The file to upload, not encoded. Other protocols than HTTP are not supported.
	 * 
	 * @author mkalam-alami
	 *
	 */
	@GET
	@Path("/{all:.*}")
	@Produces("application/x-javascript")
	public Object doGet(@Context UriInfo uriInfo) {
		 
		// Parameters extraction
		
		List<String> callbacks = uriInfo.getQueryParameters().get("callback");
		String callback = null;
		if (callbacks != null) {
			callback = callbacks.get(0);
		}

		String query = uriInfo.getRequestUri().getQuery();
		if (query != null)
			query = query.replaceFirst("callback=[^&]*&?", "");
		
		String params = uriInfo.getRequestUri().getPath()
				.replaceFirst(uriInfo.getBaseUri().getPath()+PATH, "");
		String[] paramArray = params.split("/", 3);
		String url = "", applicationName = null, serviceName = null;
		
		if (paramArray.length == 3) {
			if (!paramArray[0].isEmpty())
				applicationName = paramArray[0];
			if (!paramArray[1].isEmpty())
				serviceName = paramArray[1];
			url = paramArray[2] + ((query != null) ? "?"+query : "");
		}
		
		if (url.isEmpty()) {
			return "Invalid use.";
		}
		
		// Initialization
		HttpFile f = new HttpFile(url);
		JSONObject result = new JSONObject();
		try {
			result.put("result", "ok");
		} catch (JSONException e) {
			return "Error : "+e.getMessage();
		}
		
		// Basic file path testing / TODO
		if (url.toLowerCase().contains("wsdl")) {
			
			try {

				// WSDL download
				f.download();
				
				// Check is the WSDL is already online
				DocumentModelList list = null;
				try {
					list = session.query("SELECT * FROM Document WHERE dc:title = '"+serviceName+"'");
				} catch (ClientException e) {
					log.error("Failed to query existing WSDLs", e);
				}
				if (list != null && list.size() > 0) {
					appendError(result, "WSDL already registered");
				}
				
				else {
					
					try {
						
						// WSDL creation
						DocumentModel model = session.createDocumentModel(
								session.getDocument(EasySOA.getDefaultAppliImpl(session)).getPathAsString(),
								IdUtils.generateStringId(), EasySOA.SERVICEAPI_DOCTYPE);
						model.setProperty("file", "content", f.getBlob());
						model = session.createDocument(model);
						
						// Service creation
						if (serviceName != null || applicationName != null) {
							
							DocumentModel serviceModel = session.createDocumentModel(
									session.getDocument(EasySOA.getDefaultAppliImpl(session)).getPathAsString(),
									IdUtils.generateStringId(), "Service");
							serviceModel.setProperty("dublincore", "title", serviceName);
							serviceModel.setProperty("serviceTags", "application", applicationName);
							serviceModel.setProperty("serviceTags", "descriptorid", model.getId());
							serviceModel = session.createDocument(serviceModel);
		
							model.setProperty("endpoints", "serviceid", serviceModel.getId());
							session.saveDocument(model);
							
							session.save();
		
							// New application
							if (applicationName != null
									&& !VocabularyService.entryExists(session, "application", applicationName)) {
								VocabularyService.addEntry(session, "application", applicationName, applicationName);
							}
						}
						
						session.save();
						
					} catch (ClientException e) {
						appendError(result, "Failed to create WSDL : "+e.getMessage());
					} catch (Exception e) {
						appendError(result, "Error during WSDL creation : "+e.getMessage());
					}
				}
				
			} catch (Exception e) {
				appendError(result, "WSDL download problem : " + e.getMessage());
			}
			
		}
		else {
			appendError(result, "Given URL doesn't seem to be a WSDL.");
		}
		
 
		// Delete temporary file
		if (f.isDownloaded())
			f.delete();
		
		// Format result
		String resultJSONP = null;
		try {
			resultJSONP = JSONP.format(result, callback);
		} catch (Exception e) {
			log.info("Cannot format JSONP message (" + e.getMessage() + "), using JSON instead.");
		}
		
		// Send result
		try {
			if (resultJSONP != null)
				return new StringRepresentation(resultJSONP,
						MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8).getText();
			else
				return new StringRepresentation(result.toString(2),
						MediaType.APPLICATION_JSON, Language.ALL,
						CharacterSet.UTF_8).getText();
		} catch (Exception e) {
			log.warn("Cannot send message : " + e.getMessage());
		}
		
		try {
			return result.get("result");
		} catch (JSONException e) {
			return "Error : "+e.getMessage();
		}
	}
	
}