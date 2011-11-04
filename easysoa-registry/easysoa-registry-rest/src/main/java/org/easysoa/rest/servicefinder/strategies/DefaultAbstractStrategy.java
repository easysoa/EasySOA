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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.easysoa.HttpFile;
import org.easysoa.rest.servicefinder.ServiceFinderStrategy;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 * Abstract strategy which provides a way to retrieve the application name.
 * 
 * @author mkalam-alami
 *
 */
public abstract class DefaultAbstractStrategy implements ServiceFinderStrategy {
    
    private static HtmlCleaner cleaner = new HtmlCleaner();
    
    /**
     * Guesses an application name from the given URL, by retrieving
     * the site's root title, else the given page title. 
     * @param url
     * @return The application name or null if neither of the pages has a title tag.
     * @throws IOException
     * @throws URISyntaxException
     */
    protected static String guessApplicationName(URL url) throws IOException, URISyntaxException {
        URL siteRootUrl = new URL(url.getProtocol() + "://" + url.getHost()
                + ":" + ((url.getPort() == -1) ? 80 : url.getPort()));
        String applicationName = extractApplicatioNameFromUrl(siteRootUrl);
        if (applicationName == null) {
            applicationName = extractApplicatioNameFromUrl(url);
        }
        return applicationName;
    }
    
    private static String extractApplicatioNameFromUrl(URL url) throws IOException, URISyntaxException {
        HttpFile siteRootFile = new HttpFile(url);
        siteRootFile.download();
        TagNode siteRootCleanHtml = cleaner.clean(siteRootFile.getFile());
        return extractApplicatioName(siteRootCleanHtml);
    }

    private static String extractApplicatioName(TagNode html) {
        TagNode[] titles = html.getElementsByName("title", true);
        if (titles.length > 0) {
            return titles[0].getText().toString();
        }
        else {
            return null;
        }
    }

}
