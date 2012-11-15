/**
 * 
 */
package org.easysoa.proxy.strategy;

import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.management.ProxyInfo;

/**
 * Instantiate the Easysoa embedded proxy
 * 
 * @author jguillemotte
 *
 */
public class EmbeddedProxyCreationStrategy implements ProxyCreationStrategy {
    
    /**
     * @see org.easysoa.proxy.strategy.ProxyCreationStrategy#createProxy(ProxyConfiguration) 
     */
    public ProxyInfo createProxy(ProxyConfiguration configuration) throws Exception {

        // Just returning the embedded proxy parameters
        // The proxy is started automatically with Easysoa
        ProxyInfo proxyInfo = new ProxyInfo();
        
        // Questions :
        // - DiscoveryProxy still started automatically ?
        // - Only SOAP ProxyInfo service start and then, with a call on getProxy method, starts and returns the embedded proxy ?
        // if yes => how to modify the port in the proxy composite ? if port not configurable => just to call frascati.process(composite) to launch the predifined composite
        // otherwise need to generate a composite

        // Set proxy informations
        proxyInfo.setProxyName("EasySOA embedded proxy");
        // Generate ID
        EmbeddedEasySOAGeneratedAppIdFactoryStrategy idFactory = new EmbeddedEasySOAGeneratedAppIdFactoryStrategy();
        proxyInfo.setProxyID(idFactory.getId(configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME), 
                configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME), 
                configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME)));

        // Complete the configuration
        configuration.addParameter(ProxyConfiguration.PROXY_PORT_PARAM_NAME, String.valueOf(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT));
        configuration.addParameter(ProxyConfiguration.PROXY_PATH_PARAM_NAME, "/");
        configuration.addParameter(ProxyConfiguration.PROXY_HOST_PARAM_NAME, "localhost");
        proxyInfo.setConfiguration(configuration);
        
        return proxyInfo;
    }

}
