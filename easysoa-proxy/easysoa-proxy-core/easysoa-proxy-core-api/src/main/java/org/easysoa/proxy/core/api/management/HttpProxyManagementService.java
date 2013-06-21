/**
 *
 */
package org.easysoa.proxy.core.api.management;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.osoa.sca.annotations.Remotable;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;

/**
 * Proxy info service interface
 *
 * Offers a REST service to instantiate EasySOA app's
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
     * @throws Exception
     */
    @POST
    @Path("/getHttpProxy")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public ManagementServiceResult getHttpProxy(ProxyConfiguration configuration) throws Exception;

    /**
     * Returns informations about the app corresponding to the given ID
     * @param proxyID The App ID
     * @return <code>EasySOAGeneratedAppInfo</code> containing app informations
     * @throws Exception
     */
    @GET
    @Path("/getHttpProxy/{proxyId}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public EasySOAGeneratedAppConfiguration get(@PathParam("proxyId") String proxyId) throws Exception;

    /**
     * Reset the configuration corresponding to the proxy ID
     * @param proxyId
     * @return
     * @throws Exception
     */
    @POST
    @Path("/reset")
    public String reset(ProxyConfiguration configuration) throws Exception;

    @GET
    @Path("/listInstances")
    public List<EasySOAGeneratedAppConfiguration> listInstances() throws Exception;

}
