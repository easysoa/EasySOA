package org.easysoa.test.rest;

import org.json.JSONObject;

public interface RestNotificationRequest {

	/**
	 * Sets a form parameter to be send with the request.
	 * @param property
	 * @param value
	 * @return
	 */
	RestNotificationRequest setProperty(String property, String value);
	
	/**
	 * Sends the notification.
	 * @throws IOException When the request failed.
	 * @throws ProtocolException When the request returned an error.
	 * @return The result if the notification was successfully sent, else null. 
	 * @throws Exception 
	 */
	JSONObject send() throws Exception;
	
}