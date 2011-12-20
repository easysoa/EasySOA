package org.easysoa.rest.servicefinder;

import java.util.List;

public interface ServiceFinderStrategy {

    List<FoundService> findFromContext(BrowsingContext url) throws Exception;
    
}
