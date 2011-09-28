package org.easysoa.rest.scraping;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;
import org.nuxeo.runtime.api.Framework;

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
    public Object doGet(@Context UriInfo uriInfo) throws Exception {

        List<String> errors = new ArrayList<String>(); // TODO
        JSONObject result = new JSONObject();
        
        // Initialization
        String url = uriInfo.getRequestUri().toString().substring(
                uriInfo.getBaseUri().toString().length()+"easysoa/wsdlscraper/".length()); // TODO remove callback
        
        // Run scrapers
        ServiceScraperComponent serviceScraper = Framework.getService(ServiceScraperComponent.class);
        List<FoundService> foundServices = serviceScraper.runScrapers(new URL(url));
        
        // Format response
        JSONObject foundLinks = new JSONObject();
        for (FoundService foundService : foundServices) {
            String appName = foundService.getApplicationName();
            if (appName != null) {
                result.put("applicationName", appName);
            }
            foundLinks.put(foundService.getName(), foundService.getURL());
        }
        result.put("foundLinks", foundLinks);
        
        return result.toString(); // TODO Handle errors
         
    }
    

}