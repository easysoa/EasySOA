package org.easysoa.rest.notification;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.ws.rs.core.MultivaluedMap;

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
	
	protected static final String PARAM_TITLE = "title";
	protected static final String PARAM_DESCRIPTION = "description";
	
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
			dublinCoreDef.put(PARAM_TITLE, "The name of the document.");
			dublinCoreDef.put(PARAM_DESCRIPTION, "A short description.");
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
	 * Sets properties of given schema to the specified model, but only if the value parameter is not null.
	 * If the property is not found in the given schema, it will try to find a matchin Dublin Core property.
	 * @param result
	 * @param callback
	 * @return
	 * @throws ClientException 
	 */
	protected final void setPropertiesIfNotNull(DocumentModel model, String schema, 
			Map<String, String> schemaDef, MultivaluedMap<String, String> properties) throws ClientException {
		// Update optional properties
		for (String key : properties.keySet()) {
			// Given schema specific properties
			if (schemaDef.containsKey(key)) {
				setPropertyIfNotNull(model, schema, key, properties.get(key).get(0));
			}
			// Dublin Core properties
			else if (dublinCoreDef.containsKey(key)) {
				setPropertyIfNotNull(model, DC_SCHEMA, key, properties.get(key).get(0));
			}
			// Unknown
			else {
				appendError(result, "Unknown parameter "+key+" ");
				break;
			}
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
	
}