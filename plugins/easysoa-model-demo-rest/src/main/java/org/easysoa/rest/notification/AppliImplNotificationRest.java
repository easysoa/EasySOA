package org.easysoa.rest.notification;

import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

@Path("easysoa/notification/appliimpl")
@Produces(MediaType.APPLICATION_JSON)
public class AppliImplNotificationRest extends NotificationRest {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AppliImplNotificationRest.class);
	
	public AppliImplNotificationRest() throws LoginException {
		super();
	}
	
	/**
	 * Documentation
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/")
	public Object doGet() throws JSONException {

		JSONObject result = new JSONObject();
		
		JSONObject params = new JSONObject();
		params.put("rootServicesUrl", "(mandatory) Services root");
		params.put("name", "Application name");
		params.put("uiUrl", "Application GUI entry point");
		params.put("server", "IP of the server");
		params.put("technology", "Services implementation technology");
		params.put("standard", "Protocol standard if applicable");
		params.put("sourcesUrl", "Source code access");
		
		result.put("parameters", params);
		result.put("description", "Notification concerning an application implementation.");
		
		return format(result);
	}

	@POST
	public Object doPost(@FormParam("name") String appliName,
			@FormParam("rootServicesUrl") String rootServicesUrl,
			@FormParam("uiUrl") String uiUrl,
			@FormParam("server") String server,
			@FormParam("technology") String technology,
			@FormParam("standard") String standard,
			@FormParam("sourcesUrl") String sourcesUrl) throws JSONException {

		// Initialize
		JSONObject result = new JSONObject();
		result.put("result", "ok");
		
		// Create AppliImpl
		if (rootServicesUrl != null) {
			
			try {
				
				DocumentModel appliImplModel = DocumentService.findAppliImpl(session, rootServicesUrl);
				if (appliImplModel == null)
					appliImplModel = DocumentService.createAppliImpl(session, appliName);

				appliImplModel.setProperty("appliimpldef", "rootServicesUrl", rootServicesUrl);
				setPropertyIfNotNull(appliImplModel, "dublincore", "title", appliName);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "uiUrl", uiUrl);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "server", server);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "technology", technology);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "standard", standard);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "sourcesUrl", sourcesUrl);
				session.saveDocument(appliImplModel);

				session.save();
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}
		
		}
		else {
			appendError(result, "Appli name or root services URL not informed");
		}
		
		// Return formatted result
		return format(result);

	}
	
}