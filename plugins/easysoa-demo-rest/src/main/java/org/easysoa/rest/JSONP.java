package org.easysoa.rest;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Request;

public class JSONP {
	
	public static final String CALLBACK = "callback";

	/**
	 * Turns a JSON object into a JSONP result string.
	 * @author mkalam-alami
	 *
	 */
	public static final String format(JSONObject json, Request request)
			throws JSONException, NullPointerException {
		// Note: Restlet request is needed to retrieve the callback name
		return request.getResourceRef().getQueryAsForm().getFirst("callback")
				.getValue()
				+ "(" + json.toString(2) + ");";
	}
}