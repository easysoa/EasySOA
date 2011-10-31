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
import org.easysoa.doctypes.Service;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.rest.RestNotificationRequest;

/**
 * 
 * @author mkalam-alami
 *
 */
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