package org.easysoa.test.rest;

import java.io.IOException;
import java.net.ProtocolException;

import org.json.JSONObject;

public interface RestNotificationRequest {

	/**
	 * Sets a form parameter to be send with the request.
	 * @param property
	 * @param value
	 * @return
	 */
	public RestNotificationRequest setProperty(String property, String value);
	
	/**
	 * Sends the notification.
	 * @throws IOException When the request failed.
	 * @throws ProtocolException When the request returned an error.
	 * @return The result if the notification was successfully sent, else null. 
	 * @throws Exception 
	 */
	public JSONObject send() throws Exception;
	
}
