package org.easysoa.rest.servicefinder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.runtime.api.Framework;

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
        return "Invalid use.";
    }

    @GET
    @Path("/{url:.*}")
    public Object doGet(@Context UriInfo uriInfo) throws Exception {

        JSONObject result = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // Initialization
        URL url = null;
        try {
            url = new URL(uriInfo.getRequestUri().toString().substring(
                    uriInfo.getBaseUri().toString().length()+"easysoa/servicefinder/".length())); // TODO remove callback
        }
        catch (MalformedURLException e) {
            errors.put(formatError(e));
        }

        // Run finders
        List<FoundService> foundServices = new LinkedList<FoundService>();
        if (url != null) {
            ServiceFinderComponent finderComponent = (ServiceFinderComponent) Framework
                    .getRuntime().getComponent(ServiceFinderComponent.NAME);
            List<ServiceFinder> serviceFinders = finderComponent.getServiceFinders();

            for (ServiceFinder serviceFinder : serviceFinders) {
                List<FoundService> finderResult = null;
                try {
                    finderResult = serviceFinder.findFromURL(url);
                }
                catch (Exception e) {
                    errors.put(formatError(e, "Failed to run service finder "+serviceFinder.getClass().getName()));
                }
                if (finderResult != null) {
                    foundServices.addAll(finderResult);
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