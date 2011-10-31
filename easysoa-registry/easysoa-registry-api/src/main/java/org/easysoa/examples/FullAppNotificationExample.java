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

package org.easysoa.examples;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.rest.RestNotificationRequest;

/**
 * 
 * @author mkalam-alami
 *
 */
public class FullAppNotificationExample {

    private final static String APP_URL = "http://www.myservices.com";
    private final static String API_URL = "http://www.myservices.com/api";
    private final static String SERVICE_URL = "http://www.myservices.com/service";
    
    /**
     * Registers a application and a few services to Nuxeo.
     */
	public static void main(String[] args) throws Exception {
		
	    // Application
	    RestNotificationFactory factory = new RestNotificationFactory("http://localhost:8080/nuxeo/site");
	    RestNotificationRequest request = factory.createNotification(RestNotificationService.APPLIIMPL);
	    request.setProperty(AppliImpl.PROP_URL, APP_URL);
	    request.setProperty(AppliImpl.PROP_TITLE, "My App");
        request.setProperty(AppliImpl.PROP_DOMAIN, "CRM");
        request.send();
	    
        // API in which the services will be contained
        request = factory.createNotification(RestNotificationService.SERVICEAPI);
        request.setProperty(ServiceAPI.PROP_PARENTURL, APP_URL); // Ensures in which app this will be stored
        request.setProperty(ServiceAPI.PROP_URL, API_URL);
        request.setProperty(ServiceAPI.PROP_TITLE, "My API");
        request.send();
        
        // A few services
        for (int i = 1; i < 10; i++) {
            request = factory.createNotification(RestNotificationService.SERVICE);
            request.setProperty(Service.PROP_PARENTURL, API_URL); // Ensures in which API this will be stored
            request.setProperty(Service.PROP_URL, SERVICE_URL+i);
            request.setProperty(Service.PROP_TITLE, "My Service #"+i);
            request.setProperty(Service.PROP_PARTICIPANTS, "My company"); // The service participants
            request.send();
        }
	    
	}
}