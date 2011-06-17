package org.easysoa.rest.scraper;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.easysoa.rest.HttpFile;
import org.easysoa.rest.notification.JSONP;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST service to find WSDLs from given URL.
 * 
 * Use: .../nuxeo/site/easysoa/wsdlscraper/{url}
 * Params: {url} The URL of the page to consider (not encoded).
 * Other protocols than HTTP are not supported.
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/wsdlscraper")
@Produces("application/x-javascript")
public class ScraperRest {

	@GET
	public Object doGet() {
		return "Invalid use.";
	}

	@GET
	@Path("/{url:.*}")
	public Object doGet(@Context UriInfo uriInfo,
			@QueryParam("callback") String callback) {

		
		List<String> errors = new ArrayList<String>();
		JSONObject result = new JSONObject();
		HttpFile f = null;
		
		try {
			
			// Initialization
			String url = uriInfo.getRequestUri().toString().substring(uriInfo.getBaseUri().toString().length()+"easysoa/wsdlscraper/".length()); // TODO remove callback
			result.put("foundLinks", "");

			// Web page download
			f = new HttpFile(url);
			f.download();

			// Web page parsing
			JSONObject foundLinks = new JSONObject();
			URL context = new URL(url);
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode cleanHtml = cleaner.clean(f.getFile());

			// Find app name / service name
			TagNode[] titles = cleanHtml.getElementsByName("title", true);
			if (titles.length > 0) {
				result.put("applicationName", titles[0].getText().toString());
			}

			// Find links
			Object[] links = cleanHtml.evaluateXPath("//a");
			for (Object o : links) {
				TagNode link = (TagNode) o;
				try {
					String ref = new URL(context, link
							.getAttributeByName("href")).toString();
					String name = (link.getText() != null) ? link.getText()
							.toString() : ref;

					// Truncate if name is an URL (serviceName cannot contain
					// slashes)
					if (name.contains("/")) {
						String[] nameParts = name.split("/");
						name = nameParts[nameParts.length - 1].replaceAll(
								"(\\?|\\.|wsdl)", "");
					}

					// Append digits to the link name if it already exists
					int i = 1;
					if (ref != null && ref.toLowerCase().endsWith("wsdl")) {
						while (foundLinks.has(name)) {
							name = (i == 1 ? name + i++ : name.substring(0,
									name.length() - 1))
									+ i++;
						}
						foundLinks.put(name.replaceAll("([\n\r]|[ ]*WSDL|[ ]*wsdl)", "").trim(), ref);
					}

				} catch (MalformedURLException e) {
					// Nothing (link parsing failure)
				}
			}
			result.put("foundLinks", foundLinks);

			changeToAbsolutePath(links, "href", context);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//script"), "href",
					context);
			changeToAbsolutePath(cleanHtml.evaluateXPath("//link"), "href",
					context);

		} catch (NullPointerException e) {
			errors.add("Web page parsing failure: "+ e.getMessage());
		} catch (JSONException e) {
			errors.add("Cannot append data to JSON object: " + e.getMessage());
		} catch (XPatherException e) {
			errors.add("Failed to extract links: " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			 errors.add("Cannot parse url: "+e.getMessage());
		} catch (MalformedURLException e) {
			errors.add("Web page download problem: " + e.getMessage());
		} catch (URISyntaxException e) {
			errors.add("Invalid URL: " + e.getMessage());
		} catch (Exception e) {
			errors.add("Scraping failed: (" + e.getClass().getSimpleName() + ") " + e.getMessage());
		}

		if (f != null)
			f.delete();
		
		// Format & send result
		try {
			if (!errors.isEmpty()) {
				for (String error : errors)
					result.append("error", error);
			}			
			return (callback == null) ? 
					result.toString(2) : 
					JSONP.format(result, callback);
		} catch (JSONException e) {
			errors.add("Cannot format anwser: " + e.getMessage());
		}
		
		// If everything else fails, show errors in plain text
		String html = "";
		for (String error : errors)
			html += error;
		return html;
	}
	
	private static void changeToAbsolutePath(Object[] tagNodes,
			String attribute, URL context) {
		for (Object o : tagNodes) {
			TagNode tag = (TagNode) o;
			String attrValue = tag.getAttributeByName(attribute);
			if ((attrValue != null) && (!attrValue.startsWith("http://"))) {
				try {
					tag.setAttribute(attribute, new URL(context, attrValue)
							.toString());
				} catch (Exception e) {
					// Nothing (Could not set attrValue to absolute path)
				}
			}
		}
	}

}