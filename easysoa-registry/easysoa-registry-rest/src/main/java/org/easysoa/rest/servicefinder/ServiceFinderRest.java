/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.rest.servicefinder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.easysoa.rest.DiscoveryRest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.runtime.api.Framework;

import com.sun.jersey.api.core.HttpContext;

/**
 * REST service to find WSDLs from given URL.
 * 
 * Use: .../nuxeo/site/easysoa/servicefinder/{url}
 * Params: {url} The URL of the page to consider (not encoded).
 * Other protocols than HTTP are not supported.
 * 
 * @author mkalam-alami
 * 
 */
@Path("easysoa/servicefinder")
@Produces("application/x-javascript")
public class ServiceFinderRest {

    @GET
    public Object doGet() {
        return "Invalid use (please append an address to explore to the URL)";
    }

    @GET
    @Path("/{url:.*}")
    public Object doGet(@Context UriInfo uriInfo) throws Exception {

        URL url = null;
        String callback = null;
        try {
            // Retrieve URL
        	String restServiceURL = uriInfo.getBaseUri().toString()+"easysoa/servicefinder/";
        	url = new URL(uriInfo.getRequestUri().toString().substring(restServiceURL.length()));
        	if (url.getQuery() != null && url.getQuery().contains("callback=")) {
        		List<NameValuePair> queryTokens = URLEncodedUtils.parse(url.toURI(), "UTF-8");
        		for (NameValuePair token : queryTokens) {
        			if (token.getName().equals("callback")) {
        				callback = token.getValue(); // TODO remove callback from original URL
        			}
        		}
        	}
        }
        catch (MalformedURLException e) {
            return "{ errors: '" + formatError(e) + "' }";
        }
        
        // Find WSDLs
        if (callback != null) {
        	return callback + '(' + findWSDls(new BrowsingContext(url)) + ')';
        }
        else {
        	return findWSDls(new BrowsingContext(url));
        }
    }
    

    @POST
    @Path("/")
    public Object doPost(@Context HttpContext httpContext, @Context HttpServletRequest request) throws Exception {
    	
    	// Retrieve params
    	Map<String, String> formValues = DiscoveryRest.getFirstValues(request.getParameterMap());
    	
    	// Find WSDLs
    	BrowsingContext browsingContext = new BrowsingContext(new URL(formValues.get("url")), formValues.get("data"));
        return findWSDls(browsingContext);
    }
    

    public String findWSDls(BrowsingContext context) throws Exception {

        JSONArray errors = new JSONArray();
        JSONObject result = new JSONObject();
        

        // Run finders
        List<FoundService> foundServices = new LinkedList<FoundService>();
        if (context.getURL() != null) {
            ServiceFinderComponent finderComponent = (ServiceFinderComponent) Framework
                    .getRuntime().getComponent(ServiceFinderComponent.NAME);
            List<ServiceFinderStrategy> strategies = finderComponent.getStrategies();

            for (ServiceFinderStrategy strategy : strategies) {
                List<FoundService> strategyResult = null;
                try {
                    strategyResult = strategy.findFromContext(context);
                }
                catch (Exception e) {
                    errors.put(formatError(e, "Failed to run service finder strategy "+strategy.getClass().getName()));
                }
                if (strategyResult != null) {
                    foundServices.addAll(strategyResult);
                }
            }
        }
        
        // TODO: Filter duplicates

        // Format response
        JSONObject foundLinks = new JSONObject();
        for (FoundService foundService : foundServices) {
            String appName = foundService.getApplicationName();
            if (appName != null) {
                result.put("applicationName", appName);
            }
            foundLinks.put(foundService.getName(), foundService.getURL());
        }
        if (foundLinks.keys().hasNext()) {
            result.put("foundLinks", foundLinks);
        }
        if (errors.length() > 0) {
            result.put("errors", errors);
        }
        
        return result.toString();
         
    }

    private String formatError(Exception e, String message) {
        return e.getClass().getSimpleName()+": "+message+" (cause: "+e.getMessage()+")";
    }
    
    private String formatError(Exception e) {
        return e.getClass().getSimpleName()+": "+e.getMessage();
    }
    

}