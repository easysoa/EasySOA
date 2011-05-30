package org.easysoa.rest.notification;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.resource.StringRepresentation;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;

public abstract class NotificationRest {

	protected static final String REGISTRY_ROOT = "/default-domain/workspaces/";
	protected static final String DC_SCHEMA = "dublincore";
	
	protected final CoreSession session;
	protected JSONObject result = new JSONObject();
	protected boolean errorFound = false;
	protected static Map<String, String> dublinCoreDef; 
	
	private static final Log log = LogFactory.getLog(NotificationRest.class);
	private static final String ERROR = "[ERROR] ";

	/**
	 * Creates an instance which is logged in the repository.
	 * @throws LoginException
	 * @throws JSONException 
	 */
	public NotificationRest() throws LoginException, JSONException {
		// XXX: As the REST API is (for now) anonymously available, we need to explicitly log in
		Framework.login("Administrator", "Administrator");
		session = WebEngine.getActiveContext().getUserSession().getCoreSession(null);

		result.put("result", "ok");
		
		if (dublinCoreDef == null) {
			dublinCoreDef = new HashMap<String, String>();
			dublinCoreDef.put("title", "The name of the document.");
			dublinCoreDef.put("description", "A short description.");
		}
		
	}
	
	/**
	 * Sets a property to a model, but only if the value parameter is not null.
	 * @param result
	 * @param callback
	 * @return
	 * @throws ClientException 
	 */
	protected final void setPropertyIfNotNull(DocumentModel model, String schema, 
			String property, Object value) throws ClientException {
		if (value != null) {
			model.setProperty(schema, property, value);
		}
	}

	/**
	 * Appends an error to a JSON object (in the "result" item)
	 * @param json
	 * @param msg
	 * @throws JSONException
	 */
	protected final void appendError(JSONObject json, String msg) {
		try {
			errorFound = true;
			String formattedMsg = ERROR+msg;
			Object existingResult;
				existingResult = json.get("result");
			if (existingResult.equals("ok")) {
				json.put("result", formattedMsg);
			}
			else {
				json.append("result", formattedMsg);
			}
		} catch (JSONException e) {
			log.error("Failed to append error '"+msg+"' in response", e);
		}
	}

	/**
	 * Formats the JSONObject into a JSONP string
	 * @param result
	 * @return
	 */
	protected final String getFormattedResult(String callback) {
		return format(true, callback);
	}

	/**
	 * Formats the JSONObject into a string
	 * @param result
	 * @return
	 */
	protected final String getFormattedResult() {
		return format(false, null);
	}

	private final String format(boolean jsonp, String callback) {
		try {
			return new StringRepresentation((jsonp) ? JSONP.format(result, callback) : result.toString(2),
					MediaType.APPLICATION_JSON, Language.ALL,
					CharacterSet.UTF_8).getText();
		}
		catch (JSONException e) {
			return "{ result: \""+ERROR+"Could not format results to JSON.\"}";
		}
	}
	

	protected Form getForm(HttpContext httpContext) {
		/*
		 * When accessing the form the usual way, the returned Form is empty,
		 * and the following Warning is logged. This hack avoids the problem.
		 * 
		 * "ATTENTION: A servlet POST request, to the URI ###, contains form
		 * parameters in the request body but the request body has been 
		 * consumed by the servlet or a servlet filter accessing the request parameters.
		 * Only resource methods using @FormParam will work as expected. Resource methods
		 * consuming the request body by other means will not work as expected."
		 */
		return (Form) ((ContainerRequest) httpContext.getRequest()).
				getProperties().get("com.sun.jersey.api.representation.form");
	}
	
}