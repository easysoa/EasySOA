package org.easysoa.rest.scraping;

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

        JSONObject result = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // Initialization
        URL url = null;
        try {
            url = new URL(uriInfo.getRequestUri().toString().substring(
                    uriInfo.getBaseUri().toString().length()+"easysoa/wsdlscraper/".length())); // TODO remove callback
        }
        catch (MalformedURLException e) {
            errors.put(formatError(e));
        }

        // Run scrapers
        List<FoundService> foundServices = new LinkedList<FoundService>();
        if (url != null) {
            ServiceScraperComponent serviceScraper = (ServiceScraperComponent) Framework
                    .getRuntime().getComponent(ServiceScraperComponent.NAME);
            List<ServiceScraper> scrapers = serviceScraper.getScrapers();

            for (ServiceScraper scraper : scrapers) {
                List<FoundService> scraperResult = null;
                try {
                    scraperResult = scraper.scrapeURL(url);
                }
                catch (Exception e) {
                    errors.put(formatError(e, "Failed to run scraper "+scraper.getClass().getName()));
                }
                if (scraperResult != null) {
                    foundServices.addAll(scraperResult);
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