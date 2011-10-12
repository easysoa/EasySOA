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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Temporary basic REST client to simplify the use of the Discovery API
 * 
 * @author mkalam-alami
 *
 */
// XXX: Does not handle authentication at the moment
public class RestNotificationFactory {

    public static final String NUXEO_URL_LOCALHOST = "http://localhost:8080/nuxeo/site";
    
    public enum RestNotificationService {
        APPLIIMPL, SERVICEAPI, SERVICE;
    }
    
    private static final String API_PATH = "/easysoa/notification/";
    private static final String SERVICE_APPLIIMPL = "appliimpl";
    private static final String SERVICE_SERVICEAPI = "api";
    private static final String SERVICE_SERVICE = "service";

    private static Log log = LogFactory.getLog(RestNotificationFactory.class);
    
    private Map<RestNotificationService, URL> apiUrls;

    /**
     * Creates a new notification factory.
     * @throws IOException 
     */
    public RestNotificationFactory() throws IOException {
        this(NUXEO_URL_LOCALHOST);
    }
    
    /**
     * Creates a new notification factory.
     * @param server (ex: RestNotificationFactory.NUXEO_URL_LOCALHOST)
     * @throws IOException 
     */
    public RestNotificationFactory(String nuxeoApisUrl) throws IOException {
        
        // Test connection
        new URL(nuxeoApisUrl).openConnection();
        
        // Store API services URLs
        String notificationApiUrl = nuxeoApisUrl + API_PATH;
        apiUrls = new HashMap<RestNotificationService, URL>();
        apiUrls.put(RestNotificationService.APPLIIMPL, new URL(notificationApiUrl + SERVICE_APPLIIMPL));
        apiUrls.put(RestNotificationService.SERVICEAPI, new URL(notificationApiUrl + SERVICE_SERVICEAPI));
        apiUrls.put(RestNotificationService.SERVICE, new URL(notificationApiUrl + SERVICE_SERVICE));
        
    }
    
    /**
     * Creates a notification for the wanted document type.
     * @param api
     * @return
     */
    public RestNotificationRequest createNotification(RestNotificationService api) {
        return createNotification(api, "POST");
    }
    
    /**
     * Makes a request to the wanted notification service, allowing to choose the request method.
     * ("POST" for a notification, "GET" for the documentation)
     * @param api
     * @param method
     * @return
     */
    public RestNotificationRequest createNotification(RestNotificationService api, String method) {
        try {
            return new RestNotificationRequestImpl(apiUrls.get(api), method);
        } catch (MalformedURLException e) {
            log.error(e);
            return null;
        }
    }

}
