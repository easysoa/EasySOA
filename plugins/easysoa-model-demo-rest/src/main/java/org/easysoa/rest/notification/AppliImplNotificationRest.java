
package org.easysoa.rest.notification;

import static org.easysoa.doctypes.AppliImpl.PROP_URL;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;

import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
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

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AppliImplNotificationRest.class);
	
	/**
	 * params : TODO
	 * sample : ex. url=http://www.ebi.ac.uk/webservices/&title=EBI Services&technology=Apache CXF&server=193.62.197.12
	 * 
	 * @param httpContext
	 * @return
	 * @throws JSONException
	 * @throws LoginException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Object doPost(@Context HttpContext httpContext) throws JSONException, LoginException {
		
		// Initialize
		login();
		Form params = getForm(httpContext);
		
		// Check mandatory field
		if (params.get(PROP_URL) != null) {
		
			try {
				
				// Find or create document
				DocumentModel appliImplModel = DocumentService.findAppliImpl(session, params.getFirst(PROP_URL));
				if (appliImplModel == null) {
					String title = (params.get("title") != null) ? params.getFirst("title") : params.getFirst(PROP_URL);
					appliImplModel = DocumentService.createAppliImpl(session, params.getFirst(PROP_URL));
					appliImplModel.setProperty("dublincore", "title", title);
					session.saveDocument(appliImplModel);
				}
				
				// Update optional properties
				setPropertiesIfNotNull(appliImplModel, SCHEMA, AppliImpl.getPropertyList(), params);
				
				// Save
				if (!errorFound) {
					session.saveDocument(appliImplModel);
					session.save();
				}
				
			} catch (ClientException e) {
				appendError("Document creation failed: "+e.getMessage());
			}
		
		}
		else {
			appendError("Appli name or root services URL not informed");
		}
		
		// Return formatted result
		logout();
		return getFormattedResult();

	}

	@POST
	public Object doPost() throws JSONException {
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
	public Object doGet() throws JSONException {
		
		result = new JSONObject();
		JSONObject params = new JSONObject();
		Map<String, String> appliImplDef = AppliImpl.getPropertyList();
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
	
}