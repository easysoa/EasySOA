package org.easysoa.rest.notification;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.representation.Form;

@Path("easysoa/notification/service")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceNotificationRest extends NotificationRest {

	public static final String SERVICEDEF_SCHEMA = "servicedef";

	public static final String PARAM_URL = "url";
	public static final String PARAM_PARENTURL = "parentUrl";
	public static final String PARAM_CALLCOUNT = "callcount";
	public static final String PARAM_RELATEDUSERS = "relatedUsers";
	public static final String PARAM_HTTPMETHOD = "httpMethod";
	public static final String PARAM_CONTENTTYPEIN = "contentTypeIn";
	public static final String PARAM_CONTENTTYPEOUT = "contentTypeOut";
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceNotificationRest.class);
	private static Map<String, String> serviceDef; 

	public ServiceNotificationRest() throws LoginException, JSONException {
		super();
		if (serviceDef == null) {
			serviceDef = new HashMap<String, String>();
			serviceDef.put(PARAM_URL, "(mandatory) Service URL.");
			serviceDef.put(PARAM_PARENTURL, "(mandatory) Service API URL (WSDL address, parent path...)");
			serviceDef.put(PARAM_CALLCOUNT, "Times the service has been called since last notification");
			serviceDef.put(PARAM_RELATEDUSERS, "Users that have been using the service");
			serviceDef.put(PARAM_HTTPMETHOD, "POST, GET...");
			serviceDef.put(PARAM_CONTENTTYPEIN, "HTTP content type of the request body");
			serviceDef.put(PARAM_CONTENTTYPEOUT, "HTTP content type of the result body");
		}
	}

	/**
	 * Documentation
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/")
	public Object doGet() throws JSONException {
		
		result = new JSONObject();
		JSONObject params = new JSONObject();
		for (String key : serviceDef.keySet()) {
			params.put(key, serviceDef.get(key));
		}
		for (String key : dublinCoreDef.keySet()) {
			params.put(key, dublinCoreDef.get(key));
		}
		result.put("parameters", params);
		result.put("description", "Service-level notification.");

		return getFormattedResult();
	}
	
	@POST
	public Object doPost(@Context HttpContext httpContext) throws JSONException {
		
		// Initialize
		Form params = getForm(httpContext);

		// Check mandatory fields
		if (params.get(PARAM_URL) != null && params.get(PARAM_PARENTURL) != null) {

			// Exctract main fields
			String url = params.get(PARAM_URL).get(0),
				parentUrl = params.get(PARAM_PARENTURL).get(0),
				name = (params.get(PARAM_TITLE) != null) ? params.get(PARAM_TITLE).get(0) : null;
			int callcount;
			try {
				callcount = (params.get(PARAM_CALLCOUNT) != null) ? Integer.parseInt(params.get(PARAM_CALLCOUNT).get(0)) : 0;
			}
			catch (NumberFormatException e) {
				callcount = 0;
			}
			if (name == null)
				name = url;
			
			try {
				
				// Find or create document and parent
				DocumentModel apiModel = DocumentService.findServiceApi(session, parentUrl);
				if (apiModel == null)
					apiModel = DocumentService.createServiceAPI(session, DocumentService.DEFAULT_APPLIIMPL_TITLE, parentUrl);
				DocumentModel serviceModel = DocumentService.findService(session, url);
				if (serviceModel == null) {
					serviceModel = DocumentService.createService(session, 
							(String) apiModel.getProperty(APINotificationRest.APIDEF_SCHEMA, APINotificationRest.PARAM_URL),
							name);
				}

				// Update optional properties
				params.remove(PARAM_PARENTURL);
				setPropertiesIfNotNull(serviceModel, SERVICEDEF_SCHEMA, serviceDef, params);
				try {
					serviceModel.setProperty(SERVICEDEF_SCHEMA, PARAM_CALLCOUNT, 
							((Integer) serviceModel.getProperty(SERVICEDEF_SCHEMA, PARAM_CALLCOUNT)) + callcount);
				}
				catch (Exception e) {
					serviceModel.setProperty(SERVICEDEF_SCHEMA, PARAM_CALLCOUNT, callcount);
				}
				
				// Save
				if (!errorFound) {
					session.saveDocument(serviceModel);
					session.save();
				}
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError(result, "Service URL or parent API URL not informed");
		}
		
		// Return formatted result
		return getFormattedResult();

	}
	
}