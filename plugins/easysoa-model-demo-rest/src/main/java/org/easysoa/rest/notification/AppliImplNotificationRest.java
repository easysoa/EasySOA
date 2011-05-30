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

@Path("easysoa/notification/appliimpl")
@Produces(MediaType.APPLICATION_JSON)
public class AppliImplNotificationRest extends NotificationRest {

	public static final String APPLIIMPLDEF_SCHEMA = "appliimpldef";
	
	public static final String PARAM_ROOTSERVICESURL = "rootServicesUrl";
	public static final String PARAM_UIURL = "uiUrl";
	public static final String PARAM_SERVER = "server";
	public static final String PARAM_TECHNOLOGY = "technology";
	public static final String PARAM_STANDARD = "standard";
	public static final String PARAM_SOURCESURL = "sourcesUrl";

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AppliImplNotificationRest.class);
	private Map<String, String> appliImplDef = new HashMap<String, String>(); 
	
	public AppliImplNotificationRest() throws LoginException, JSONException {
		super();
		appliImplDef.put(PARAM_ROOTSERVICESURL, "(mandatory) Services root");
		appliImplDef.put(PARAM_UIURL, "Application GUI entry point");
		appliImplDef.put(PARAM_SERVER, "IP of the server");
		appliImplDef.put(PARAM_TECHNOLOGY, "Services implementation technology");
		appliImplDef.put(PARAM_STANDARD, "Protocol standard if applicable");
		appliImplDef.put(PARAM_SOURCESURL, "Source code access");
	}
	
	/**
	 * Documentation
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/")
	public Object doGet() throws JSONException {

		JSONObject params = new JSONObject();
		for (String key : appliImplDef.keySet()) {
			params.put(key, appliImplDef.get(key));
		}
		for (String key : dublinCoreDef.keySet()) {
			params.put(key, dublinCoreDef.get(key));
		}
		
		result.put("parameters", params);
		result.put("description", "Notification concerning an application implementation.");
		
		return getFormattedResult();
	}

	@POST
	public Object doPost(@Context HttpContext httpContext) throws JSONException {
		
		// Initialize
		Form params = getForm(httpContext);
		
		// Check mandatory field
		if (params.get(PARAM_ROOTSERVICESURL) != null) {
			
			try {
				
				// Find or create document
				DocumentModel appliImplModel = DocumentService.findAppliImpl(session, params.get(PARAM_ROOTSERVICESURL).get(0));
				if (appliImplModel == null) {
					String title = (params.get("title") != null) ? params.get("title").get(0) : params.get(PARAM_ROOTSERVICESURL).get(0);
					appliImplModel = DocumentService.createAppliImpl(session, title);
				}
				
				// Update mandatory properties
				appliImplModel.setProperty(APPLIIMPLDEF_SCHEMA, PARAM_ROOTSERVICESURL, params.get(PARAM_ROOTSERVICESURL).get(0));
				
				// Update optional properties
				for (String key : params.keySet()) {
					// AppliImpl properties
					if (appliImplDef.containsKey(key)) {
						setPropertyIfNotNull(appliImplModel, APPLIIMPLDEF_SCHEMA, key, params.get(key).get(0));
					}
					// Dublincore properties
					else if (appliImplModel.getPart(DC_SCHEMA).getSchema().hasField(key)) {
						setPropertyIfNotNull(appliImplModel, DC_SCHEMA, key, params.get(key).get(0));
					}
					// Unknown
					else {
						appendError(result, "Unknown parameter "+key+" ");
						break;
					}
				}
				
				// Save
				if (!errorFound) {
					session.saveDocument(appliImplModel);
					session.save();
				}
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}
		
		}
		else {
			appendError(result, "Appli name or root services URL not informed");
		}
		
		// Return formatted result
		return getFormattedResult();

	}
	
}