package org.easysoa.rest;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Request;

public class JSONP {
	public static final String CALLBACK = "callback";

	public static final String format(JSONObject json, Request request)
			throws JSONException {
		return request.getResourceRef().getQueryAsForm().getFirst("callback")
				.getValue()
				+ "(" + json.toString(2) + ");";
	}
}