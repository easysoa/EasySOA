package org.easysoa.rest.notification;

import static org.easysoa.doctypes.Service.SCHEMA;
import static org.easysoa.doctypes.Service.PROP_CALLCOUNT;
import static org.easysoa.doctypes.Service.PROP_PARENTURL;
import static org.easysoa.doctypes.Service.PROP_URL;

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
import org.easysoa.doctypes.Service;
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
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceNotificationRest.class);

	/**
	 * params : TODO
	 * sample : url=http://www.ebi.ac.uk/Tools/es/ws-servers/WSPhobius&callcount=3&apiUrl=http://www.ebi.ac.uk/Tools/webservices/wsdl/WSPhobius.wsdl&title=PhobiusService&contentTypeIn=SOAP&contentTypeOut=SOAP&httpMethod=POST
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

		// Check mandatory fields
		if (params.get(PROP_URL) != null && params.get(PROP_PARENTURL) != null) {

			// Exctract main fields
			String url = params.getFirst(PROP_URL),
				parentUrl = params.getFirst(PROP_PARENTURL),
				name = (params.get("title") != null) ? params.getFirst("title") : null;
			int callcount;
			try {
				callcount = (params.get(PROP_CALLCOUNT) != null) ? Integer.parseInt(params.getFirst(PROP_CALLCOUNT)) : 0;
			}
			catch (NumberFormatException e) {
				callcount = 0;
			}
			if (name == null)
				name = url;
			
			try {
				
				// Find or create document and parent
				DocumentModel apiModel = DocumentService.findServiceApi(session, parentUrl);
				if (apiModel == null) {
					apiModel = DocumentService.createServiceAPI(session, null, parentUrl); // TODO "by default", or even fail
					session.saveDocument(apiModel);
					session.save();
				}
				DocumentModel serviceModel = DocumentService.findService(session, url);
				if (serviceModel == null)
					serviceModel = DocumentService.createService(session, apiModel.getPathAsString(), name);
				session.move(serviceModel.getRef(), apiModel.getRef(), serviceModel.getName());

				// Update optional properties
				params.remove(PROP_PARENTURL);
				setPropertiesIfNotNull(serviceModel, SCHEMA, Service.getPropertyList(), params);
				try {
					serviceModel.setProperty(SCHEMA, PROP_CALLCOUNT, 
							((Integer) serviceModel.getProperty(SCHEMA, PROP_CALLCOUNT)) + callcount);
				}
				catch (Exception e) {
					serviceModel.setProperty(SCHEMA, PROP_CALLCOUNT, callcount);
				}
				
				// Save
				if (!errorFound) {
					session.saveDocument(serviceModel);
					session.save();
				}
				
			} catch (ClientException e) {
				appendError("Document creation failed: "+e.getMessage());
			}

		}
		else {
			appendError("Service URL or parent API URL not informed");
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
		Map<String, String> commonDef = getCommonPropertiesDocumentation();
		for (String key : commonDef.keySet()) {
			params.put(key, commonDef.get(key));
		}
		Map<String, String> serviceDef = Service.getPropertyList();
		for (String key : serviceDef.keySet()) {
			params.put(key, serviceDef.get(key));
		}
		result.put("parameters", params);
		result.put("description", "Service-level notification.");
		return getFormattedResult();
	}
	
}