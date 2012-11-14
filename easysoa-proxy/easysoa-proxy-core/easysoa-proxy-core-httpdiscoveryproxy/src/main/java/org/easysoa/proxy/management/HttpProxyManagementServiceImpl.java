/**
 * 
 */
package org.easysoa.proxy.management;

import org.apache.log4j.Logger;
import org.easysoa.proxy.configuration.HttpProxyConfigurationService;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.strategy.EmbeddedProxyCreationStrategy;
import org.easysoa.proxy.strategy.ProxyCreationStrategy;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Proxy info service implementation
 *
 * Sample body request content :
 *
 * {
 *   "proxyConfiguration" : {
 *     "parameters" : [
 *       {
 *         "key":"user",
 *         "value":"Admin"
 *       },
 *       {
 *         "key":"projectId",
 *         "value":"123456"
 *       }
 *     ]
 *   }
 * }
 * 
 * @author jguillemotte
 *
 */
@Scope("Composite")
public class HttpProxyManagementServiceImpl implements HttpProxyManagementService {

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(HttpProxyManagementServiceImpl.class.getName());
    
    // Proxy configuration service reference
    @Reference
    HttpProxyConfigurationService configurationService;

    /**
     * Empty constructor for debug
     */
    public HttpProxyManagementServiceImpl(){
        logger.debug("Passing in ProxyInfoServiceImpl constructor !");
    }
    
    /**
     * Returns the proxy with the provided configuration
     * @param configuration The proxy configuration
     * @return <code>ProxyInfo</code> containing informations about instanced proxy
     */
    public ProxyInfo getHttpProxy(ProxyConfiguration configuration) throws Exception {
        
        // TODO : Call required for Frascati studio app's, not for embedded proxy
        // getAvailablePort();
        
        // Create the proxy using the proxy configuration
        // How to determine what creation strategy to use ?
        // Set in the proxy configuration ?
        // TODO : load the ProxyCreationStrategy on demand
        ProxyCreationStrategy strategy = new EmbeddedProxyCreationStrategy();
        ProxyInfo proxyInfo = strategy.createProxy(configuration);
        
        // Configure the handler
        configurationService.update(configuration);
        
        return proxyInfo;
        
        //* (Embedded/PerUser/AndComponent)EasySOAGeneratedAppIdFactoryStrategy.getId(user, projectId, componentIds [, other SoaNode ids ex. service, impl TODO components also ?]) returns easysoaGeneratedAppId
        //* getAvailablePort(conditions ex. > 20000) TODO Q pmerle implement by storing ports in FStudio db or by introspection (AT WORST or mere json) ??
        //* (Single/Instanciating)EasySOAGeneratedApp(s)Registry.get(id, Template (httpProxy), templateParams which are easysoaGeneratedApp param PLUS port etc.)
        //* finally, calls HttpProxyConfigurationService.update((proxyid,) HttpProxyConfiguration) which dispatches to Handlers        
        
    }

}
