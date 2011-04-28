package org.easysoa.rest;

import java.net.URLDecoder;

public class RequestArgs {

	private String rawRequest;
	private String[] request;
	private int position = 0;

	public RequestArgs(String string) {
		this.rawRequest = string;
		this.request = string
			.replaceAll("^[\\w\\:\\/]*restAPI/[\\w]*/", "")
			.split("/");
	}

	public String getNext() {
		if (request.length >= position + 1) {
			String result = request[position++];
			if (!result.equals("")) {
				try {
					return URLDecoder.decode(result, "UTF-8");
				}
				catch (Exception e) {
					return result;
				}
			}
		}
		return null;
	}

	public String getRemaining() {
		String result = "";
		while (position < request.length) {
			result += request[position] + "/";
			position++;
		}
		if (!result.equals("")) {
			return (rawRequest.endsWith("/"))
				? result
				: result.substring(0,result.length() - 1);
		}
		return null;
	}

	public void reset() {
		position = 0;
	}

}
