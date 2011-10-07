package org.easysoa.examples;
import org.easysoa.doctypes.Service;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.rest.RestNotificationRequest;

public class ServiceNotificationExample {
	
    /**
     * Registers a service to Nuxeo.
     */
	public static void main(String[] args) throws Exception {
		
	    RestNotificationFactory factory = new RestNotificationFactory("http://localhost:8080/nuxeo/site");
	    RestNotificationRequest request = factory.createNotification(RestNotificationService.SERVICE);
	    request.setProperty(Service.PROP_URL, "http://www.myservices.com/api/service");
	    request.setProperty(Service.PROP_TITLE, "Service");
	    request.send();
	    
	}
}