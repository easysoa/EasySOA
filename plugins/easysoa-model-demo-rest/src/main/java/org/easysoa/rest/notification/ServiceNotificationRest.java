package org.easysoa.rest.notification;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Path("easysoa/notification/service")
@Produces("application/json")
public class ServiceNotificationRest extends NotificationRest {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ServiceNotificationRest.class);

	public ServiceNotificationRest() throws LoginException {
		super();
	}
	
	
}