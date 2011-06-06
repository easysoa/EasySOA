package org.easysoa.rest.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
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

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.representation.Form;

@Path("easysoa/notification/api")
public class APINotificationRest extends NotificationRest {

	public static final String APIDEF_SCHEMA = "serviceapidef";
	
	public static final String PARAM_URL = "url";
	public static final String PARAM_PARENTURL = "parentUrl";
	public static final String PARAM_SOURCEURL = "sourceUrl";
	public static final String PARAM_APPLICATION = "application";
	
	private static final Log log = LogFactory.getLog(APINotificationRest.class);
	private static final String PATH = "easysoa/notification/api/"; // TODO: Replace
	private static Map<String, String> apiDef; 
	
	public APINotificationRest() throws LoginException, JSONException {
		super();
		if (apiDef == null) {
			apiDef = new HashMap<String, String>(); 
			apiDef.put(PARAM_URL, "(mandatory) Service API url (WSDL address, parent path...).");
			apiDef.put(PARAM_PARENTURL, "(mandatory) The parent URL, which is either another service API, or the service root.");
			apiDef.put(PARAM_SOURCEURL, "The web page where the service has been found (useful for REST only).");
			apiDef.put(PARAM_APPLICATION, "The related business application.");
		}
	}

	/**
	 * Documentation
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Object doGet() throws JSONException {
		
		result = new JSONObject();
		JSONObject params = new JSONObject();
		for (String key : apiDef.keySet()) {
			params.put(key, apiDef.get(key));
		}
		for (String key : dublinCoreDef.keySet()) {
			params.put(key, dublinCoreDef.get(key));
		}
		result.put("parameters", params);
		result.put("description", "Service-level notification.");
		
		return getFormattedResult();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Object doPost(@Context HttpContext httpContext) throws JSONException {
		
		// Initialize
		Form params = getForm(httpContext);
		
		// Check mandatory fields
		if (params.get(PARAM_URL) != null && params.get(PARAM_PARENTURL) != null) {
			
			// Exctract main fields
			String url = params.get(PARAM_URL).get(0),
				parentUrl = params.get(PARAM_PARENTURL).get(0),
				name = (params.get(PARAM_TITLE) != null) ? params.get(PARAM_TITLE).get(0) : null;
			
			if (name == null)
				name = url;

			try {

				// Find or create document and parent
				DocumentModel parentModel = DocumentService.findServiceApi(session, parentUrl);
				if (parentModel == null)
					parentModel = DocumentService.findAppliImpl(session, parentUrl);
				if (parentModel == null) {
					parentModel = DocumentService.createAppliImpl(session, parentUrl, parentUrl);
					parentModel.setProperty(AppliImplNotificationRest.APPLIIMPLDEF_SCHEMA, 
							AppliImplNotificationRest.PARAM_ROOTSERVICESURL, parentUrl);
					session.saveDocument(parentModel);
					session.save();
				}
				
				DocumentModel apiModel = DocumentService.findServiceApi(session, url);
				if (apiModel == null)
					apiModel = DocumentService.createServiceAPI(session, parentUrl, name);
				else
					apiModel.setPathInfo(parentModel.getPathAsString(), apiModel.getName());

				// Update optional properties
				params.remove(PARAM_PARENTURL);
				setPropertiesIfNotNull(apiModel, APIDEF_SCHEMA, apiDef, params);
				
				// Save
				if (!errorFound) {
					session.saveDocument(apiModel);
					session.save();
				}
				
				session.save();
				
			} catch (ClientException e) {
				appendError("Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError("API URL or parent URL not informed");
		}
		
		// Return formatted result
		return getFormattedResult();

	}
	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Object doPost() throws JSONException {
		appendError("Content type should be 'application/x-www-form-urlencoded'");
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
						
						model.setProperty(DC_SCHEMA, PARAM_TITLE, serviceName);
						model.setProperty(APIDEF_SCHEMA, PARAM_APPLICATION, applicationName);
						session.saveDocument(model);
						
						// New application in the vocabulary
						if (applicationName != null
								&& !VocabularyService.entryExists(session, "application", applicationName)) {
							VocabularyService.addEntry(session, "application", applicationName, applicationName);
						}
					}
					
					session.save();
					
				} catch (ClientException e) {
					appendError("Failed to create WSDL : "+e.getMessage());
				} catch (Exception e) {
					appendError("Error during WSDL creation : "+e.getMessage());
				}
				
			} catch (Exception e) {
				appendError("WSDL download problem : " + e.getMessage());
			}
			
		}
		else {
			appendError("Given URL doesn't seem to be a WSDL.");
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