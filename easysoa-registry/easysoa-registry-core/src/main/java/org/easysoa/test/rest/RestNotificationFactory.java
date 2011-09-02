package org.easysoa.test.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestNotificationFactory {

	public static final String NUXEO_URL_LOCALHOST = "http://localhost:8080/nuxeo";
	
	public enum RestNotificationService {
		APPLIIMPL, SERVICEAPI, SERVICE;
	}

	private static final Log log = LogFactory.getLog(RestNotificationFactory.class);
	
	private static final String API_PATH = "/easysoa/notification/";
	private static final String SERVICE_APPLIIMPL = "appliimpl";
	private static final String SERVICE_SERVICEAPI = "api";
	private static final String SERVICE_SERVICE = "service";
	
	private Map<RestNotificationService, URL> apiUrls;
	
	/**
	 * Creates a new notification factory.
	 * @param server (ex: RestNotificationFactory.NUXEO_URL_LOCALHOST)
	 * @throws IOException 
	 */
	public RestNotificationFactory(String nuxeoUrl) throws IOException {
		
		// Test connection
		new URL(nuxeoUrl).openConnection();
		
		// Store API services URLs
		String notificationApiUrl = nuxeoUrl + API_PATH;
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
