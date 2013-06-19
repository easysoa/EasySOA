/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.configuration;

import org.easysoa.proxy.core.api.configuration.HttpProxyConfigurationService;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.exchangehandler.HandlerManager;
import org.easysoa.proxy.strategy.EmbeddedEasySOAGeneratedAppIdFactoryStrategy;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * @author jguillemotte
 *
 */
@Scope("composite")
public class DefaultHttpProxyConfigurationService implements HttpProxyConfigurationService {

    // Handler manager
    @Reference
    HandlerManager handlerManager;

    ProxyConfiguration currentProxyConf = null;

    // Logger
    private Logger logger = Logger.getLogger(DefaultHttpProxyConfigurationService.class.getName());

    public DefaultHttpProxyConfigurationService(){
        currentProxyConf = new ProxyConfiguration();
        currentProxyConf.setId(EmbeddedEasySOAGeneratedAppIdFactoryStrategy.SINGLETON_ID);
    }

    /**
     * Handler configuration method
     * @param parameters
     */
    //@Override
    public void update(ProxyConfiguration configuration) {
        // Pass the proxy configuration to the handler manager
        logger.debug("Passing proxy configuration to handler manager");
        this.currentProxyConf = configuration;
        handlerManager.setHandlerConfiguration(configuration);
    }

    //@Override
    public String reset(ProxyConfiguration configuration) throws Exception {
        ProxyConfiguration proxyConf = new ProxyConfiguration();
        proxyConf.setId(EmbeddedEasySOAGeneratedAppIdFactoryStrategy.SINGLETON_ID);
        update(proxyConf);
        return proxyConf.getId();
    }

    //@Override
    public ProxyConfiguration get(ProxyConfiguration configuration) throws Exception {
        return this.currentProxyConf;
    }
}
