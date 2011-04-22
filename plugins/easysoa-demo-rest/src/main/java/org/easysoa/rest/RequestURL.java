package org.easysoa.rest;

import org.restlet.data.Request;

public class RequestURL {
	
	public final static String parse(Request request, String apiPath) {
		String url = request.getResourceRef().toString();
		url = url.substring(url.indexOf(apiPath)+apiPath.length());
		return (url.contains("://")) ? url : "http://"+url;
	}

}
