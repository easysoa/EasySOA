package org.easysoa.rest.notification;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginContext;
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

	protected CoreSession session = null;
	protected JSONObject result = new JSONObject();
	protected boolean errorFound = false;
	protected static Map<String, String> dublinCoreDef; 
	protected static LoginContext loginContext; 

	private static final Log log = LogFactory.getLog(NotificationRest.class);
	private static final String ERROR = "[ERROR] ";

	/**
	 * Creates an instance which is logged in the repository.
	 * @throws LoginException
	 * @throws JSONException 
	 */
	public NotificationRest() {
	
		try {
			result.put("result", "ok");
		} catch (JSONException e) {
			log.error(e);
		}
		
		if (dublinCoreDef == null) {
			dublinCoreDef = new HashMap<String, String>();
			dublinCoreDef.put("title", "The name of the document.");
			dublinCoreDef.put("description", "A short description.");
		}
		
	}
	
	protected void login() throws LoginException {
		// XXX: As the REST API is (for now) anonymously available, we need to explicitly log in
		loginContext = Framework.login("Administrator", "Administrator");
		session = WebEngine.getActiveContext().getUserSession().getCoreSession(null);
	}
	
	protected void logout() {
		try {
			loginContext.logout();
		} catch (LoginException e) {
			log.warn("Failed to logout: "+e.getMessage());
		}
	}

	/**
	 * Appends an error to a JSON object (in the "result" item)
	 * @param json
	 * @param msg
	 * @throws JSONException
	 */
	protected final void appendError(String msg) {
		try {
			errorFound = true;
			String formattedMsg = ERROR+msg;
			Object existingResult;
				existingResult = result.get("result");
			if (existingResult.equals("ok")) {
				result.put("result", formattedMsg);
			}
			else {
				result.append("result", formattedMsg);
			}
		} catch (JSONException e) {
			log.error("Failed to append error '"+msg+"' in response", e);
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
				setPropertyIfNotNull(model, schema, key, properties.getFirst(key));
			}
			// Dublin Core properties
			else if (dublinCoreDef.containsKey(key)) {
				setPropertyIfNotNull(model, "dublincore", key, properties.getFirst(key));
			}
			// Unknown
			else {
				appendError("Unknown parameter "+key);
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