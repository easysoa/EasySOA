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

@Path("easysoa/notification/service")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceNotificationRest extends NotificationRest {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceNotificationRest.class);

	public ServiceNotificationRest() throws LoginException, JSONException {
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
		params.put("url", "(mandatory) Service URL");
		params.put("name", "Service name");
		params.put("apiUrl", "(mandatory) Service API URL (WSDL address, parent path...)");
		params.put("callcount", "Times the service has been called since last notification");
		params.put("relatedUsers", "Users that have been using the service");
		params.put("httpMethod", "POST, GET...");
		params.put("contentTypeIn", "HTTP content type of the request body");
		params.put("contentTypeOut", "HTTP content type of the result body");
		
		result.put("parameters", params);
		result.put("description", "Service-level notification.");

		return getFormattedResult();
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

		// Create service
		if (url != null && apiUrl != null) {
			
			if (name == null)
				name = url;
			
			try {
				
				DocumentModel apiModel = DocumentService.findServiceApi(session, apiUrl);
				if (apiModel == null)
					apiModel = DocumentService.createServiceAPI(session, DocumentService.DEFAULT_APPLIIMPL_TITLE, apiUrl);
				
				DocumentModel serviceModel = DocumentService.findService(session, url);
				if (serviceModel == null)
					serviceModel = session.createDocumentModel("ServiceAPI");
				
				serviceModel.setPathInfo(apiModel.getPathAsString(), name);
				try {
					serviceModel.setProperty("servicedef", "callcount", 
							((Integer) serviceModel.getProperty("servicedef", "callcount")) + callcount);
				}
				catch (Exception e) {
					serviceModel.setProperty("servicedef", "callcount", callcount);
				}
				setPropertyIfNotNull(serviceModel, "dublincore", "title", name);
				setPropertyIfNotNull(serviceModel, "servicedef", "url", url);
				setPropertyIfNotNull(serviceModel, "servicedef", "relatedUsers", relatedUsers); // TODO merging
				setPropertyIfNotNull(serviceModel, "servicedef", "httpMethod", httpMethod);
				setPropertyIfNotNull(serviceModel, "servicedef", "contentTypeIn", contentTypeIn);
				setPropertyIfNotNull(serviceModel, "servicedef", "contentTypeOut", contentTypeOut);
				session.saveDocument(serviceModel);

				session.save();
				
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