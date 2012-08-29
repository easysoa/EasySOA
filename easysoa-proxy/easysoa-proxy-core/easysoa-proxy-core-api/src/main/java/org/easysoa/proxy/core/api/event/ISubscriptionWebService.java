package org.easysoa.proxy.core.api.event;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osoa.sca.annotations.Remotable;

/**
 * @author fntangke
 * 
 *         This interface is used by the composite file to describe the web
 *         service Another ISubscriptionWebService is implemened in Another one
 *         is present in easysoa-proxy-core-api-frascati NB: Both should be
 *         equals
 */

@Remotable
public interface ISubscriptionWebService {

	/**
	 * @return The current monitoring configuration
	 */
	@GET
	@Path("/subscriptions")
	@Produces({ "application/xml", "application/json" })
	public MonitoringConfiguration getMonitoringConfiguration();

	/**
	 * Get a configuration and set it in the Monitoring Configuration
	 * @param configuration
	 * @return
	 */
	@POST
	@Path("/subscriptions")
	@Produces({ "application/xml", "application/json" })
	public MonitoringConfiguration udpateMonitoringConfiguration(
			Configuration configuration);

	
}
