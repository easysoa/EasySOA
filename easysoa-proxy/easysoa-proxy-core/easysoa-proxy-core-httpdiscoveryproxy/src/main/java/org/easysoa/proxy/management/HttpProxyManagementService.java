/**
 * 
 */
package org.easysoa.proxy.management;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.osoa.sca.annotations.Remotable;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;

/**
 * Proxy info service
 * 
 * @author jguillemotte
 *
 */

@Remotable
public interface HttpProxyManagementService {

    /**
     * Instantiate and returns a proxy
     * @param configuration The proxy configuration
     * @return <code>ProxyInfo</code> containing informations about the instanced proxy
     */
    @POST
    @Path("/getHttpProxy")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }) 
    public ProxyInfo getHttpProxy(ProxyConfiguration configuration) throws Exception;
    
}
