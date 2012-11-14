/**
 * 
 */
package org.easysoa.proxy.strategy;

import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.management.ProxyInfo;

/**
 * Simple proxy creation strategy
 * 
 * @author jguillemotte
 *
 */
public interface ProxyCreationStrategy {

    /**
     * Creates a proxy
     * @param configuration Contains parameters to be used by the proxy
     * @return Informations about the created proxy
     * @throws Exception If a problem occurs
     */
    public ProxyInfo createProxy(ProxyConfiguration configuration) throws Exception;
    
}
