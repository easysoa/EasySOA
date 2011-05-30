package org.easysoa.rest.notification;

import java.util.List;

import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.rest.HttpFile;
import org.easysoa.services.DocumentService;
import org.easysoa.services.VocabularyService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

@Path("easysoa/notification/api")
public class APINotificationRest extends NotificationRest {

	private static final Log log = LogFactory.getLog(APINotificationRest.class);
	private static final String PATH = "easysoa/notification/api/";
	
	public APINotificationRest() throws LoginException, JSONException {
		super();
	}

	/**
	 * Documentation
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object doGet() throws JSONException {

		JSONObject result = new JSONObject();
		
		JSONObject params = new JSONObject();
		params.put("apiUrl", "(mandatory) Service API url (WSDL address, parent path...)");
		params.put("parentUrl", "(mandatory) The parent URL, which is either another service API, or the service root");
		params.put("name", "Service name");
		params.put("sourceUrl", "The web page where the service has been found (useful for REST only)");
		
		result.put("parameters", params);
		result.put("description", "Service-level notification.");
		
		return getFormattedResult();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Object doPost(@FormParam("apiUrl") String apiUrl,
			@FormParam("parentUrl") String parentUrl,
			@FormParam("name") String name,
			@FormParam("sourceUrl") String sourceUrl) throws JSONException {
		
		// Create API
		if (apiUrl != null && parentUrl != null) {
			
			if (name == null)
				name = apiUrl;
			
			try {
				
				DocumentModel parentModel = DocumentService.findServiceApi(session, parentUrl);
				if (parentModel == null)
					parentModel = DocumentService.findAppliImpl(session, parentUrl);
				if (parentModel == null)
					parentModel = DocumentService.createAppliImpl(session, parentUrl);
					parentModel.setProperty("appliimpldef", "rootServicesUrl", parentUrl);
					session.saveDocument(parentModel);
				
				DocumentModel apiModel = DocumentService.findServiceApi(session, apiUrl);
				if (apiModel == null)
					apiModel = DocumentService.createServiceAPI(session, parentUrl, name);

				setPropertyIfNotNull(apiModel, "dublincore", "title", name);
				setPropertyIfNotNull(apiModel, "serviceapidef", "url", apiUrl);
				setPropertyIfNotNull(apiModel, "serviceapidef", "sourceUrl", sourceUrl);
				session.saveDocument(apiModel);

				session.save();
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError(result, "API URL or parent API/Appli. URL not informed");
		}
		
		// Return formatted result
		return getFormattedResult();

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
	@Produces("application/x-javascript")
	@Path("/{all:.*}") // {applicationName}/{serviceName}/{url}
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
				serviceName = paramArray[1].replaceAll("(WSDL|wsdl)", "").trim();
			url = paramArray[2] + ((query != null) ? "?"+query : "");
		}
		
		if (url.isEmpty()) {
			return "Invalid use.";
		}
		
		// Initialization
		HttpFile f = new HttpFile(url);
		
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
				
				DocumentModel model;
				if (list != null && list.size() > 0) {
					// Document selection
					model = list.get(0);
					log.info("WSDL already registered, updating the document.");
				}
				else {
					// (or) Document creation
					DocumentModel parentModel = session.getDocument(DocumentService.getDefaultAppliImpl(session).getRef());
					if (parentModel == null) {
						throw new NullPointerException("Parent application not found.");
					}
					else {
						model = session.createDocumentModel(parentModel.getPathAsString(),
								IdUtils.generateStringId(), DocumentService.DEFAULT_APPLIIMPL_TITLE);
					}
					model.setProperty("file", "content", f.getBlob());
					model = session.createDocument(model);
				}
				
				try {
					
					// Service creation
					if (serviceName != null || applicationName != null) {
						
						model.setProperty("dublincore", "title", serviceName);
						model.setProperty("serviceapidef", "application", applicationName);
						session.saveDocument(model);
						
						// New application in the vocabulary
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
		
		// Format & send result
		try {
			if (callback != null)
				return getFormattedResult(callback);
			else
				return getFormattedResult();
		} catch (Exception e) {
			log.warn("Cannot send message : " + e.getMessage());
			return "Error : "+e.getMessage();
		}
		
	}
	
}