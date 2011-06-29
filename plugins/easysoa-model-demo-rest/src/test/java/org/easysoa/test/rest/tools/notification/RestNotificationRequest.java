package org.easysoa.test.rest.tools.notification;

import java.io.IOException;
import java.net.ProtocolException;

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
	 */
	public void send() throws IOException, ProtocolException;
	
}
