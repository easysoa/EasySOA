package org.openwide.easysoa.test.mock.nuxeomock;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public interface NuxeoMock {

	/**
	 * Registration service
	 * @return
	 */
	@POST
	@Path("/nuxeo/site/easysoa/notification/{type}")
	public String processNotificationRequest(@PathParam("type") String type);
	
	/**
	 * Automation service
	 * @return
	 */
	@POST
	@Path("/nuxeo/site/automation/Document.Query")
	public String processAutomationRequest(@Context UriInfo ui, @Context Request request, String body);
	
}
