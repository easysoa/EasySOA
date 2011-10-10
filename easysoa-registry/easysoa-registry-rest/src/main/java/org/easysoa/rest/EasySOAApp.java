package org.easysoa.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import org.easysoa.rest.scraping.ScraperRest;

public class EasySOAApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(EasySOAAppRoot.class);
        result.add(ScraperRest.class);
        result.add(NotificationRest.class);
        return result;
    }

}
