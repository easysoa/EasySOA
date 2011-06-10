package org.easysoa.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

<<<<<<< HEAD
import org.easysoa.rest.gadgets.ServiceStatsRest;
import org.easysoa.rest.notification.APINotificationRest;
import org.easysoa.rest.notification.AppliImplNotificationRest;
import org.easysoa.rest.notification.ServiceNotificationRest;
import org.easysoa.rest.scraper.ScraperRest;

=======
>>>>>>> origin/master
public class EasySOAApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {

<<<<<<< HEAD
		Set<Class<?>> result = new HashSet<Class<?>>();
		result.add(EasySOAAppRoot.class);
		result.add(ScraperRest.class);
		result.add(ServiceNotificationRest.class);
		result.add(APINotificationRest.class);
		result.add(AppliImplNotificationRest.class);
		result.add(ServiceStatsRest.class);
=======
		    Set<Class<?>> result = new HashSet<Class<?>>();
		    result.add(EasySOAAppRoot.class);
		    result.add(ScraperService.class);
		    result.add(NotificationService.class);
>>>>>>> origin/master
		
		    return result;
    }
	
}
