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

package org.easysoa.rest.scraping.scrapers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.listeners.HttpFile;
import org.easysoa.rest.scraping.FoundService;
import org.easysoa.rest.scraping.ServiceScraper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 * Service scraper based on parsing all hypertext links from a web page to find WSDLs.
 * 
 * @author mkalam-alami
 *
 */
public class LinkParsingScraper extends DefaultAbstractScraper implements ServiceScraper {

    @Override
    public List<FoundService> scrapeURL(URL url) throws Exception {

        List<FoundService> foundServices = new LinkedList<FoundService>();

        // Web page download
        HttpFile f = new HttpFile(url);
        f.download();

        // Web page parsing
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode cleanHtml = cleaner.clean(f.getFile());

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

        if (f != null) {
            f.delete();
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
