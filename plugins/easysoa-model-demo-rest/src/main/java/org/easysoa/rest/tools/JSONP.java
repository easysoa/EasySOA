package org.easysoa.rest.tools;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONP {
	
	public static final String CALLBACK = "callback";

	/**
	 * Turns a JSON object into a JSONP result string.
	 * @author mkalam-alami
	 *
	 */
	public static final String format(JSONObject json, String callbackName)
			throws JSONException, NullPointerException {
		if (callbackName == null)
			throw new NullPointerException("Callback name not defined");
		return callbackName + "(" + json.toString(2) + ");";
	}
}