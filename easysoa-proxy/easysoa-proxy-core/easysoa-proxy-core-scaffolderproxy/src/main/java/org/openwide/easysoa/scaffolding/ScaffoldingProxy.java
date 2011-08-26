/**
 * 
 */
package org.openwide.easysoa.scaffolding;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author jguillemotte
 *
 */
public interface ScaffoldingProxy {

	/**
	 * Receive a REST request, transform it to a SOAP request, get the response and send back the answers to the HTML form
	 * @param ui
	 * @param request
	 * @return
	 */
	@GET
	@Path("/callService/{binding}/{operation}/")
	public Response redirectRestToSoap(@Context UriInfo ui, @Context Request request, @PathParam("binding") String binding, @PathParam("operation") String operation) throws Exception;	
	
}
