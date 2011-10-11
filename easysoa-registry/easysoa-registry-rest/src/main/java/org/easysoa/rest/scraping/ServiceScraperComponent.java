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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.rest.scraping;

import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;

import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class ServiceScraperComponent extends DefaultComponent {

    public static final ComponentName NAME = new ComponentName(
            ComponentName.DEFAULT_TYPE, "org.easysoa.rest.scraping.ServiceScraperComponent");
    
    private List<ServiceScraper> scrapers = new LinkedList<ServiceScraper>();

    public List<ServiceScraper> getScrapers() {
        return scrapers;
    }
    
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
            if (extensionPoint.equals("scrapers")) {
                ServiceScraperDescriptor scraperDescriptor = (ServiceScraperDescriptor) contribution;
                if (scraperDescriptor.enabled) {
                    Class<?> scraperClass = Class.forName(scraperDescriptor.implementation.trim());
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
                Class<?> scraperClass = Class.forName(scraperDescriptor.implementation.trim());
                for (ServiceScraper scraper : scrapers) {
                    if (scraper.getClass().equals(scraperClass)) {
                        scrapers.remove(scraper);
                    }
                }
            }
        }
    }
    
    private String renderContributionError(ComponentInstance contributor,
            String message) {
        return "Invalid contribution from '" + contributor.getName() + "': " + message;
    }
}
