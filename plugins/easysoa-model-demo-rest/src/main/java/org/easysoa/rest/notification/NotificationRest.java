package org.easysoa.rest.notification;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.resource.StringRepresentation;

public abstract class NotificationRest {

	private static final Log log = LogFactory.getLog(NotificationRest.class);
	
	protected static final String REGISTRY_ROOT = "/EasySOA/Service Registry/";
	private static final String ERROR = "[ERROR] ";
	
	protected final CoreSession session;
	
	/**
	 * Creates an instance which is logged in the repository.
	 * @throws LoginException
	 */
	public NotificationRest() throws LoginException {
		// XXX: As the REST API is (for now) anonymously available, we need to explicitly log in
		Framework.login("Administrator", "Administrator");
		session = WebEngine.getActiveContext().getUserSession().getCoreSession(null);
	}
	
	/**
	 * Appends an error to a JSON object (in the "result" item)
	 * @param json
	 * @param msg
	 * @throws JSONException
	 */
	protected static final void appendError(JSONObject json, String msg) {
		try {
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
	 * Formats the JSONObject into a string
	 * @param result
	 * @return
	 */
	protected static final String format(JSONObject result) {
		return format(result, false, null);
	}

	/**
	 * Formats the JSONObject into a JSONP string
	 * @param result
	 * @return
	 */
	protected static final String format(JSONObject result, String callback) {
		return format(result, true, callback);
	}

	private static final String format(JSONObject result, boolean jsonp, String callback) {
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