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
package org.easysoa.proxy.core.api.configuration;

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
    public void update(ProxyConfiguration configuration) throws Exception;

    /**
     * Reset the configuration
     * (Call the update method with an empty ProxyConfiguration parameter)
     * @param configuration
     */
    public String reset(ProxyConfiguration configuration) throws Exception;

    /**
     * Returns the current configuration
     * @return ProxyConfiguration, null if there is no current configuration set
     */
    public ProxyConfiguration get(ProxyConfiguration configuration) throws Exception;

}
