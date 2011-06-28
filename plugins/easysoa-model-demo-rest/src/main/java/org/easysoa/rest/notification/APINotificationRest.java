package org.easysoa.rest.notification;

import static org.easysoa.doctypes.ServiceAPI.PROP_PARENTURL;
import static org.easysoa.doctypes.ServiceAPI.PROP_URL;
import static org.easysoa.doctypes.ServiceAPI.SCHEMA;

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
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.listeners.HttpFile;
import org.easysoa.services.DocumentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.representation.Form;

@Path("easysoa/notification/api")
public class APINotificationRest extends NotificationRest {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(APINotificationRest.class);
	
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
						HttpFile f = new HttpFile(url);
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
		Map<String, String> commonDef = getCommonPropertiesDocumentation();
		for (String key : commonDef.keySet()) {
			params.put(key, commonDef.get(key));
		}
		Map<String, String> apiDef = ServiceAPI.getPropertyList();
		for (String key : apiDef.keySet()) {
			params.put(key, apiDef.get(key));
		}
		result.put("parameters", params);
		result.put("description", "Service-level notification.");
		return getFormattedResult();
	}
	
}