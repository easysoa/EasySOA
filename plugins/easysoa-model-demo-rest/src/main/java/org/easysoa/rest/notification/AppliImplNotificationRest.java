package org.easysoa.rest.notification;

import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

@Path("easysoa/notification/api")
@Produces("application/json")
public class AppliImplNotificationRest extends NotificationRest {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AppliImplNotificationRest.class);
	
	public AppliImplNotificationRest() throws LoginException {
		super();
	}

	@POST
	public Object doPost(@FormParam("name") String appliName,
			@FormParam("technology") String technology,
			@FormParam("standard") String standard) throws JSONException {
		
		// Initialize
		JSONObject result = new JSONObject();
		result.put("result", "ok");
		
		// Create AppliImpl
		if (appliName != null) {
			
			try {
				
				DocumentModel appliImplModel = session.getDocument(new PathRef(REGISTRY_ROOT+appliName));
				if (appliImplModel == null) {
					appliImplModel = DocumentService.createAppliImpl(session, appliName);
				}
				
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "technology", technology);
				setPropertyIfNotNull(appliImplModel, "appliimpldef", "standard", standard);

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
	
}