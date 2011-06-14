package org.easysoa.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.easysoa.rest.gadgets.ServiceStatsRest;
import org.easysoa.rest.notification.APINotificationRest;
import org.easysoa.rest.notification.AppliImplNotificationRest;
import org.easysoa.rest.notification.ServiceNotificationRest;
import org.easysoa.rest.scraper.ScraperRest;

public class EasySOAApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {

		Set<Class<?>> result = new HashSet<Class<?>>();
		result.add(EasySOAAppRoot.class);
		result.add(ScraperRest.class);
		result.add(ServiceNotificationRest.class);
		result.add(APINotificationRest.class);
		result.add(AppliImplNotificationRest.class);
		result.add(ServiceStatsRest.class);
		
		    return result;
    }
	
}
