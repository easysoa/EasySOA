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


@Path("easysoa/notification/service")
@Produces("application/json")
public class ServiceNotificationRest extends NotificationRest {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceNotificationRest.class);

	public ServiceNotificationRest() throws LoginException {
		super();
	}
	
	@POST
	public Object doPost(@FormParam("url") String url,
			@FormParam("name") String name,
			@FormParam("apiUrl") String apiUrl,
			@FormParam("callcount") int callcount,
			@FormParam("relatedUsers") String relatedUsers,
			@FormParam("httpMethod") String httpMethod,
			@FormParam("contentTypeIn") String contentTypeIn,
			@FormParam("contentTypeOut") String contentTypeOut) throws JSONException {

		// Initialize
		JSONObject result = new JSONObject();
		result.put("result", "ok");
		
		// Create service
		if (url != null && apiUrl != null) {
			
			if (name == null)
				name = url;
			
			try {
				
				DocumentModel apiModel = DocumentService.findServiceApi(session, apiUrl);
				if (apiModel == null) {
					apiModel = DocumentService.createServiceAPI(session, DocumentService.DEFAULT_APPLIIMPL_TITLE, apiUrl);
				}
				
				DocumentModel serviceModel = session.createDocumentModel("ServiceAPI");
				serviceModel.setPathInfo(apiModel.getPathAsString(), name);
				serviceModel.setProperty("dublincore", "title", name);
				setPropertyIfNotNull(serviceModel, "servicedef", "url", url);
				setPropertyIfNotNull(serviceModel, "servicedef", "callcount", callcount);
				setPropertyIfNotNull(serviceModel, "servicedef", "relatedUsers", relatedUsers);
				setPropertyIfNotNull(serviceModel, "servicedef", "httpMethod", httpMethod);
				setPropertyIfNotNull(serviceModel, "servicedef", "contentTypeIn", contentTypeIn);
				setPropertyIfNotNull(serviceModel, "servicedef", "contentTypeOut", contentTypeOut);
				session.createDocument(serviceModel);

				session.save();
				
			} catch (ClientException e) {
				appendError(result, "Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError(result, "Service URL or parent API URL not informed");
		}
		
		// Return formatted result
		return result.toString(2);

	}
	
}