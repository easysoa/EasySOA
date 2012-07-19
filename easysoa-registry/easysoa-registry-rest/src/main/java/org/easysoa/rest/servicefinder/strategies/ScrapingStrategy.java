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

package org.easysoa.rest.servicefinder.strategies;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.rest.servicefinder.BrowsingContext;
import org.easysoa.rest.servicefinder.FoundService;
import org.easysoa.rest.servicefinder.ServiceFinderStrategy;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 * Service scraper based on parsing all hypertext links from a web page to find WSDLs.
 * 
 * @author mkalam-alami
 *
 */
public class ScrapingStrategy extends DefaultAbstractStrategy implements ServiceFinderStrategy {

    private static final Log log = LogFactory.getLog(ScrapingStrategy.class);
    
    @Override
    public List<FoundService> findFromContext(BrowsingContext context) throws Exception {

        List<FoundService> foundServices = new LinkedList<FoundService>();
        
        if (context.getData() != null) {
        	URL url = context.getURL();
    
            // Web page parsing
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode cleanHtml = null;
            try {
                cleanHtml = cleaner.clean(context.getData());
            }
            catch (StackOverflowError e) {
                log.warn("HtmlCleaner stack overflow while parsing " + url + ", aborting strategy");
                return foundServices;
            }
    
            // Find app name
            String applicationName = guessApplicationName(url);
            
            // Find links
            List<String> foundServicesNames = new LinkedList<String>();
            Object[] links = cleanHtml.evaluateXPath("//a");
            changeToAbsolutePath(links, "href", url);
    
            for (Object o : links) {
                TagNode link = (TagNode) o;
                try {
                    String ref = new URL(url, link.getAttributeByName("href"))
                            .toString();
                    String name = (link.getText() != null) ? link.getText()
                            .toString() : ref;
    
                    // Truncate if name is an URL (serviceName cannot contain slashes)
                    if (name.contains("/")) {
                        String[] nameParts = name.split("/}");
                        name = nameParts[nameParts.length - 1].replaceAll(
                                "(\\?|\\.|wsdl)", "");
                    }
    
                    // Append digits to the link name if it already exists
                    int i = 1;
                    if (ref != null && ref.toLowerCase().endsWith("wsdl")) {
                        while (foundServicesNames.contains(name)) {
                            name = (i == 1 ? name + i++ : name.substring(0,
                                    name.length() - 1))
                                    + i++;
                        }
                        name = name.replaceAll("([\n\r]|[ ]*WSDL|[ ]*wsdl)", "").trim();
                        foundServices.add(new FoundService(name, ref, applicationName));
                        foundServicesNames.add(name);
                    }
    
                } catch (MalformedURLException e) {
                    // Nothing (link parsing failure)
                }
            }
            
        }

        return foundServices;

    }
    

    private static void changeToAbsolutePath(Object[] tagNodes,
            String attribute, URL context) {
        for (Object o : tagNodes) {
            TagNode tag = (TagNode) o;
            String attrValue = tag.getAttributeByName(attribute);
            if ((attrValue != null) && (!attrValue.startsWith("http://"))) {
                try {
                    tag.setAttribute(attribute,
                            new URL(context, attrValue).toString());
                } catch (Exception e) {
                    // Nothing (Could not set attrValue to absolute path)
                }
            }
        }
    }

}
