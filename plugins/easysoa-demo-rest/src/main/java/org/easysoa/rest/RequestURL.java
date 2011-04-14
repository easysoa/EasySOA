package org.easysoa.rest;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Request;

public class RequestURL {
	
	/**
	 * Rebuilds the URL parameter from REST query.
	 * (a little hacky since Nuxeo doesn't seem to handle correctly some encoded special chars)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String parse(Request request) throws Exception {
		String url = "";

		int i = 1;
		while (request.getAttributes().containsKey("url" + i)) {
			url = url
					+ "/"
					+ request.getAttributes().get(
							new StringBuilder().append("url").append(i)
									.toString());
			i++;
		}

		url = url.replaceFirst("^[/]?http:[/]*", "http://");
		if (!url.startsWith("http")) {
			url = url.replaceFirst("/", "http://");
		}

		url = url + "?";
		Form form = request.getResourceRef().getQueryAsForm();
		if (form.size() > 0) {
			for (Parameter parameter : form) {
				if ((!parameter.getName().equals("callback"))
						&& (!parameter.getName().equals("_"))) {
					url = url
							+ parameter.getName()
							+ (parameter.getValue() == null ? ""
									: new StringBuilder().append("=").append(
											parameter.getValue()).toString())
							+ "&";
				}

			}

		}

		return url;
	}
}