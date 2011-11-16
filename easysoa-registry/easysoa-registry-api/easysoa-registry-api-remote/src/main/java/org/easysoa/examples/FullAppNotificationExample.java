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

import org.easysoa.api.EasySOAApi;
import org.easysoa.api.EasySOARemoteApiFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;

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
     * Registers an application and a few services to Nuxeo.
     */
    public static void main(String[] args) throws Exception {

        EasySOAApi api = EasySOARemoteApiFactory.createRemoteApi("http://localhost:8080/nuxeo/site");
        
        // Application
        
        Map<String, String> appProps = new HashMap<String, String>();
        appProps.put(AppliImpl.PROP_URL, APP_URL);
        appProps.put(AppliImpl.PROP_TITLE, "My App");
        appProps.put(AppliImpl.PROP_DOMAIN, "CRM");
        api.notifyAppliImpl(appProps);

        // API in which the services will be contained
        Map<String, String> apiProps = new HashMap<String, String>();
        apiProps.put(ServiceAPI.PROP_PARENTURL, APP_URL); // Ensures in which app this will be stored
        apiProps.put(ServiceAPI.PROP_URL, API_URL);
        apiProps.put(ServiceAPI.PROP_TITLE, "My API");
        api.notifyServiceApi(apiProps);

        // A few services
        Map<String, String> serviceProps = new HashMap<String, String>();
        for (int i = 1; i < 10; i++) {
            serviceProps.put(Service.PROP_PARENTURL, API_URL); // Ensures in which API this will be stored
            serviceProps.put(Service.PROP_URL, SERVICE_URL + i);
            serviceProps.put(Service.PROP_TITLE, "My Service #" + i);
            serviceProps.put(Service.PROP_PARTICIPANTS, "My company"); // The service participants
            api.notifyService(serviceProps);
        }
        
        // A service reference
        Map<String, String> serviceRefProps = new HashMap<String, String>();
        serviceRefProps.put(ServiceReference.PROP_PARENTURL, APP_URL);
        serviceRefProps.put(ServiceReference.PROP_REFURL, SERVICE_URL + "1");
        serviceRefProps.put(ServiceReference.PROP_ARCHIPATH, "/service1");
        serviceRefProps.put(ServiceReference.PROP_TITLE, "Reference to My Service #1");
        api.notifyServiceReference(serviceRefProps);

    }
}