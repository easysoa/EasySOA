/**
 * 
 */
package org.easysoa.proxy.configuration;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.exchangehandler.HandlerManager;
import org.osoa.sca.annotations.Reference;

/**
 * @author jguillemotte
 *
 */
public class HttpProxyConfigurationServiceImpl implements HttpProxyConfigurationService {

    @Reference
    HandlerManager handlerManager;
    
    // Logger
    private Logger logger = Logger.getLogger(HttpProxyConfigurationServiceImpl.class.getName());    
    
    /**
     * Handler configuration method
     * @param parameters
     */
    public void update(ProxyConfiguration configuration) {
        // Pass the proxy configuration to the handler manager
        logger.debug("Passing proxy configuration to handler manager");
        handlerManager.setHandlerConfiguration(configuration);
    }    
    
    
    
}
