package org.easysoa.rest.scraping.scrapers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.easysoa.listeners.HttpFile;
import org.easysoa.rest.scraping.ServiceScraper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 * Abstract service scraper which provides a way to retrieve the application name.
 * 
 * @author mkalam-alami
 *
 */
public abstract class DefaultAbstractScraper implements ServiceScraper {
    
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
