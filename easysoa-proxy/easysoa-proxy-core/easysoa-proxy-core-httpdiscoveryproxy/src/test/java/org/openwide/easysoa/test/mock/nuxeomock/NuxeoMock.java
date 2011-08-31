package org.openwide.easysoa.test.mock.nuxeomock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

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
