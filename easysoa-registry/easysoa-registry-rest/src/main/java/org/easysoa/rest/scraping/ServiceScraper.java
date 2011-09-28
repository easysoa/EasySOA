package org.easysoa.rest.scraping;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ServiceScraper {

    List<FoundService> scrapeURL(URL url) throws Exception;
    
}
