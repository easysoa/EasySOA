package org.easysoa.test;

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
	 * @return If the notification was successfully send
	 */
	public boolean send() throws IOException, ProtocolException;
	
}
