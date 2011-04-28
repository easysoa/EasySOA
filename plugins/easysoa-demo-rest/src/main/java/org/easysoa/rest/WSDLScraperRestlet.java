package org.easysoa.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.StringRepresentation;

/**
 * REST service to found WSDLs from given URL.
 * 
 * Use:
 * .../nuxeo/restAPI/wsdlupload/{url}
 * Params:
 * {url} The page to consider, without the "http://" prefix (other protocols not supported), and not encoded
 * 
 * @author mkalam-alami
 *
 */
public class WSDLScraperRestlet extends BaseStatelessNuxeoRestlet {
	
	private static final String REPOSITORY = "default";
	private static final String HTTP = "http://";

	public void handle(Request request, Response response) {
		super.initRepository(response, REPOSITORY);

		// Initialization
		String failure = null;
		JSONObject result = new JSONObject();
		try {
			result.put("foundLinks", "");
		} catch (JSONException e) {
			failure = "Cannot initialize JSON object: " + e.getMessage();
		}
		
		// URL parsing 
		String url = null;
		try {
			url = new RequestArgs(request.getResourceRef().toString()).getRemaining();
			result.put("url", url);
		} catch (Exception e) {
			failure = "Cannot rebuild URL: " + e.getMessage();
		}

		// Web page download
		HttpFile f = new HttpFile(url);
		if (failure == null) {
			try {
				f.download();
			} catch (Exception e) {
				failure = "WSDL download problem: " + e.getMessage();
			}
		}

		// Web page parsing
		JSONObject foundLinks = new JSONObject();
		try {
			URL context = new URL(url);
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode cleanHtml = cleaner.clean(f.getFile());
			
			// Find app name / service name
			TagNode[] titles = cleanHtml.getElementsByName("title", true);
			if (titles.length > 0) {	
				result.put("applicationName", titles[0].getText().toString().
						replaceAll("([\n\r]|[ ]*WSDL|[ ]*wsdl)", "").trim());
			}
			
			// Find links
			Object[] links = cleanHtml.evaluateXPath("//a");
			for (Object o : links) {
				TagNode link = (TagNode) o;
				try {
					String ref = new URL(context, link.getAttributeByName("href")).toString();
					String name = (link.getText() != null) ? link.getText().toString() : ref;
					
					// Truncate if name is an URL (serviceName cannot contain slashes)
					if (name.contains("/")) {
						String[] nameParts = name.split("/");
						name = nameParts[nameParts.length-1].replaceAll("(\\?|\\.|wsdl)", "");
					}
					
					// Append digits to the link name if it already exists
					int i = 1;
					if (ref != null && ref.toLowerCase().endsWith("wsdl")) {
						while (foundLinks.has(name)) {
							name = (i == 1 ? name + i++ : name.substring(0, name
									.length() - 1))
									+ i++;
						}
						foundLinks.put(name, ref);
					}
					
				}
				catch (MalformedURLException e) {
					// Nothing (link parsing failure)
				}
			}
			result.put("foundLinks", foundLinks);

			changeToAbsolutePath(links, "href", context);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//script"), "href", context);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//link"), "href", context);
			//result.put("html", cleaner.getInnerHtml(cleanHtml));

		} catch (Exception e) {
			failure = "Page download problem: (" + e.getClass() + ") " + e.getMessage();
		}

		// Format result
		String resultJSONP = null;
		try {
			result.append("error", failure);
			resultJSONP = JSONP.format(result, request);
		} catch (Exception e) {
			failure = "Cannot format JSONP message : " + e.getMessage();

		}
		
		// Send result
		try {
			if (failure != null)
				result.append("error", failure);
			
			if (resultJSONP != null)
				response.setEntity(new StringRepresentation(JSONP.format(result,
						request), MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8));
			else
				response.setEntity(new StringRepresentation(result.toString(2),
						MediaType.APPLICATION_JSON, Language.ALL,
						CharacterSet.UTF_8));
		} catch (Exception e) {
			failure = "Cannot send message : " + e.getMessage();
		}

		f.delete();
	}

	private static void changeToAbsolutePath(Object[] tagNodes, String attribute,
			URL context) {
		for (Object o : tagNodes) {
			TagNode tag = (TagNode) o;
			String attrValue = tag.getAttributeByName(attribute);
			if ((attrValue != null) && (!attrValue.startsWith(HTTP))) {
				try {
					tag.setAttribute(attribute, new URL(context, attrValue).toString());
				}
				catch (Exception e) {
					// Nothing (Could not set attrValue to absolute path)
				}
			}
		}
	}
	
}