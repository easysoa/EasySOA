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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOARemoteApiFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationRequest;
import org.easysoa.rest.RestNotificationFactory.RestDiscoveryService;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ServiceNotificationExample {
	
    private static Logger logger = Logger.getLogger(ServiceNotificationExample.class.getName());
    
    /**
     * Registers a service to Nuxeo.
     */
	public static void main(String[] args) throws Exception {
		
	    // Method #1: With the EasySOA API
	    
	    EasySOAApiSession api = EasySOARemoteApiFactory.createRemoteApi(
	            "http://localhost:8080/nuxeo/site", "Administrator", "Administrator");
	    Map<String, String> properties = new HashMap<String, String>();
	    properties.put(Service.PROP_URL, "http://www.myservices.com/api/service");
	    properties.put(Service.PROP_TITLE, "MyService");
        EasySOADocument service = api.notifyService(properties);
        logger.info("Registered " + service.getType() + ": " + service.getTitle() + 
                " (url=" + service.getPropertyAsString("serv:url") + ")");
	    
	    // Method #2: With direct requests (will be eventually deprecated)
	    
	    RestNotificationFactory factory = new RestNotificationFactory(
	            "http://localhost:8080/nuxeo/site", "Administrator", "Administrator");
	    RestNotificationRequest request = factory.createNotification(RestDiscoveryService.SERVICE);
	    request.setProperty(Service.PROP_URL, "http://www.myservices.com/api/service");
	    request.setProperty(Service.PROP_TITLE, "MyService");
	    request.send();
	    
	}
}