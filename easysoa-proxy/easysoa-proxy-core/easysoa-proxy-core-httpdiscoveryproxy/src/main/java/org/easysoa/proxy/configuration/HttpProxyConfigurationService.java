/**
 * 
 */
package org.easysoa.proxy.configuration;

import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;

/**
 * To update the Handler's configuration
 * 
 * @author jguillemotte
 *
 */
public interface HttpProxyConfigurationService {

    /**
     * Update the handlers configuration
     * @param configuration The configuration to use
     */
    public void update(ProxyConfiguration configuration);
    
}
