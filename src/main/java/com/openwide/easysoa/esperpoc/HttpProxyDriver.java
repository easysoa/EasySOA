package com.openwide.easysoa.esperpoc;

import org.osoa.sca.annotations.Remotable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Remotable
public interface HttpProxyDriver {

	/**
	 * Returns a help text with the available commands
	 * @param ui
	 * @return The <code>String</code> help text
	 */
	@GET
	@Path("/")
	public String returnUseInformations(@Context UriInfo ui);	

	/**
	 * Starts a new run
	 * @param runName The new run name
	 * @return a <code>String</code> to indicate if the command succeed
	 */
	//TODO change GET to POST
	@GET
	@Path("/startNewRun/{runName}")
	public String startNewRun(@PathParam("runName") String runName);

	/**
	 * Stop the current run
	 * @return a <code>String</code> to indicate if the command succeed
	 */
	//TODO change GET to POST
	@GET
	@Path("/stopCurrentRun")
	public String startNewRun();	
	
}