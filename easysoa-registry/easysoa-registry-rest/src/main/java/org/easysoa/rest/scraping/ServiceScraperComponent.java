package org.easysoa.rest.scraping;

import java.io.InvalidClassException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class ServiceScraperComponent extends DefaultComponent {

    private List<ServiceScraper> scrapers = new LinkedList<ServiceScraper>();

    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
            if (extensionPoint.equals("scrapers")) {
                ServiceScraperDescriptor scraperDescriptor = (ServiceScraperDescriptor) contribution;
                if (scraperDescriptor.enabled) {
                    Class<?> scraperClass = Class.forName(scraperDescriptor.implementation);
                    if (ServiceScraper.class.isAssignableFrom(scraperClass)) {
                        scrapers.add((ServiceScraper) scraperClass.newInstance());
                    } else {
                        throw new InvalidClassException(renderContributionError(
                                contributor, "class " + scraperDescriptor.implementation
                                        + " is not an instance of "
                                        + ServiceScraper.class.getName()));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new Exception(renderContributionError(contributor, ""), e);
        }
    }

    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        if (extensionPoint.equals("scrapers")) {
            ServiceScraperDescriptor scraperDescriptor = (ServiceScraperDescriptor) contribution;
            if (scraperDescriptor.enabled) {
                Class<?> scraperClass = Class.forName(scraperDescriptor.implementation);
                for (ServiceScraper scraper : scrapers) {
                    if (scraper.getClass().equals(scraperClass)) {
                        scrapers.remove(scraper);
                    }
                }
            }
        }
    }
    
    public List<FoundService> runScrapers(URL url) throws Exception {
        List<FoundService> foundServices = new LinkedList<FoundService>();
        for (ServiceScraper scraper : scrapers) {
            foundServices.addAll(scraper.scrapeURL(url));
        }
        return foundServices;
    }

    private String renderContributionError(ComponentInstance contributor,
            String message) {
        return "Invalid contribution from '" + contributor.getName() + "': " + message;
    }
}
