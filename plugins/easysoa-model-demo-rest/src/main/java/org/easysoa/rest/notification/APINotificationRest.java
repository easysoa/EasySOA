package org.easysoa.rest.notification;

import static org.easysoa.doctypes.ServiceAPI.PROP_APPLICATION;
import static org.easysoa.doctypes.ServiceAPI.PROP_PARENTURL;
import static org.easysoa.doctypes.ServiceAPI.PROP_URL;
import static org.easysoa.doctypes.ServiceAPI.SCHEMA;

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
import org.easysoa.doctypes.ServiceAPI;
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

	private static final Log log = LogFactory.getLog(APINotificationRest.class);
	private static final String PATH = "easysoa/notification/api/"; // TODO: Access from httpContext
	
	/**
	 * params : TODO
	 * sample : ex. url=http://www.ebi.ac.uk/Tools/webservices/wsdl/WSPhobius.wsdl&title=Phobius&parentUrl=http://www.ebi.ac.uk/webservices/
	 * 
	 * @param httpContext
	 * @return
	 * @throws JSONException
	 * @throws LoginException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Object doPost(@Context HttpContext httpContext) throws JSONException, LoginException {
		
		// Initialize
		login();
		Form params = getForm(httpContext);
		
		// Check mandatory fields
		if (params.get(PROP_URL) != null) {
			
			// Exctract main fields
			String url = params.getFirst(PROP_URL),
				parentUrl = params.getFirst(PROP_PARENTURL),
				title = params.getFirst("title");
			
			if (title == null)
				title = url;

			try {

				// Find or create document and parent
				DocumentModel parentModel = DocumentService.findServiceApi(session, parentUrl);
				if (parentModel == null)
					parentModel = DocumentService.findAppliImpl(session, parentUrl);
				if (parentModel == null) {
					if (parentUrl == null) {
						parentModel = DocumentService.getDefaultAppliImpl(session);
					}
					else {
						parentModel = DocumentService.createAppliImpl(session, parentUrl);
					}
					session.save();
				}
				
				DocumentModel apiModel = DocumentService.findServiceApi(session, url);
				if (apiModel == null)
					apiModel = DocumentService.createServiceAPI(session, parentModel.getPathAsString(), url);
				session.move(apiModel.getRef(), parentModel.getRef(), apiModel.getName());

				// Update optional properties
				if (url.toLowerCase().contains("wsdl")) {
					try {
						HttpFile f = new HttpFile(params.getFirst(url));
						f.download();
						apiModel.setProperty("file", "content", f.getBlob());
					} catch (Exception e) {
						appendError("Failed to download attached file");
					}
				}
				params.remove(PROP_PARENTURL);
				setPropertiesIfNotNull(apiModel, SCHEMA, ServiceAPI.getPropertyList(), params);
				
				// Save
				if (!errorFound) {
					session.saveDocument(apiModel);
					session.save();
				}
				
			} catch (ClientException e) {
				appendError("Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError("API URL or parent URL not informed");
		}
		
		// Return formatted result
		logout();
		return getFormattedResult();

	}
	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Object doPost() throws JSONException, LoginException {
		appendError("Content type should be 'application/x-www-form-urlencoded'");
		return getFormattedResult();
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
		Map<String, String> apiDef = ServiceAPI.getPropertyList();
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
	 * @throws LoginException 
	 *
	 */
	@GET
	@Produces("application/x-javascript")
	@Path("/{all:.*}") // {applicationName}/{serviceName}/{url}
	public Object doGet(@Context UriInfo uriInfo) throws LoginException {

		login();
		
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
			
				try {
					
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
									IdUtils.generateStringId(), ServiceAPI.DOCTYPE);
						}
						model.setProperty("file", "content", f.getBlob());
						model = session.createDocument(model);
					}
					
					try {
						
						// Service creation
						if (serviceName != null || applicationName != null) {
							
							model.setProperty("dublincore", "title", serviceName);
							model.setProperty(SCHEMA, PROP_APPLICATION, applicationName);
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
					appendError("Error during WSDL parsing: "+e.getMessage());
				}
			}
			catch (Exception e) {
				appendError("WSDL download failed: " + e.getMessage());
			}
			
		}
		else {
			appendError("Given URL doesn't seem to be a WSDL.");
		}
		
		// Delete temporary file
		if (f.isDownloaded())
			f.delete();
		
		// Format & send result
		logout();
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