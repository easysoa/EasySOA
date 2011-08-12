package com.openwide.easysoa.proxy;

import org.osoa.sca.annotations.Remotable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * An interface to start/stop runs
 * Run is a set of recorded messages
 * 
 * @author jguillemotte
 *
 */
@Remotable
public interface HttpProxyDriver {

	/**
	 * Returns a help text with the available commands
	 * @param ui Context UriInfo
	 * @return The <code>String</code> help text
	 */
	// TODO in case of error, return the user informations
	@GET
	@Path("/")
	public String returnUseInformations(@Context UriInfo ui);	

	/**
	 * Starts a new run
	 * @param runName The new run name
	 * @return a <code>String</code> to indicate if the command succeed
	 */
	@GET
	@Path("/startNewRun/{runName}")
	public String startNewRun(@PathParam("runName") String runName);

	/**
	 * Stop the current run
	 * @return a <code>String</code> to indicate if the command succeed
	 */
	@GET
	@Path("/stopCurrentRun")
	public String stopCurrentRun();	
	
	/**
	 * Set the monitoring mode
	 * @param mode The monitoring mode
	 * @return a <code>String</code> to indicate if the command succeed
	 */
	@GET
	@Path("/setMonitoringMode/{mode}")
	public String setMonitoringMode(@PathParam("mode") String mode);
	
	/**
	 * Returns the names of all recorded runs in their record order
	 * @return The name of all recorded runs
	 */
	@GET
	@Path("/getOrderedRunNames")
	public String getOrderedRunNames();
	
	/**
	 * Re-run the specified run
	 * @param runName The run name to re-run
	 * @return a <code>String</code> to indicate if the command succeed 
	 */
	@GET
	@Path("/reRun/{runName}")
	public String reRun(@PathParam("runName") String runName);
		
}