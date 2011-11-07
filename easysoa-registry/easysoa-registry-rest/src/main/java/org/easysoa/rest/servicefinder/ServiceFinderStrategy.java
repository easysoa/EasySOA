package org.easysoa.rest.servicefinder;

import java.net.URL;
import java.util.List;

public interface ServiceFinderStrategy {

    List<FoundService> findFromURL(URL url) throws Exception;
    
}
