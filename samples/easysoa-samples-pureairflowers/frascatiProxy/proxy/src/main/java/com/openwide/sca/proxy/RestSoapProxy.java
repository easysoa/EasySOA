package com.openwide.sca.proxy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface RestSoapProxy {

	@GET
	@Path("/")
	public Response redirectRequests(@Context UriInfo ui, @Context Request request);
	
}
