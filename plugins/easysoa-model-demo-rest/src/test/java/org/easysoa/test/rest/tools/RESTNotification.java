package org.easysoa.test.rest.tools;

import java.io.IOException;
import java.net.ProtocolException;

public interface RESTNotification {


	public void setProperty(String property, String value);
	
	/**
	 * Sends the notification.
	 * @throws IOException When the request failed.
	 * @throws ProtocolException When the request returned an error.
	 */
	public void send() throws IOException, ProtocolException;
	

}
