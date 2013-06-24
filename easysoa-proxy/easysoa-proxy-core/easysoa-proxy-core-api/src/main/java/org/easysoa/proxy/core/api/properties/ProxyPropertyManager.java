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
package org.easysoa.proxy.core.api.properties;

import java.io.InputStream;

/**
 * Proxy porperty manager. Works with the httpDiscoveryProxy.properties file
 * NB. Must be in -api project (and not -httdiscoveryproxy), else MessageHandlers can't see it !!
 * @author jguillemotte
 *
 */
public class ProxyPropertyManager extends PropertyManager {

    // Default proxy property file name
    public static final String PROPERTY_FILE_NAME = "httpDiscoveryProxy.properties";

    /**
     * Create a proxy property manager using the default property file
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager() throws Exception {
        super(PROPERTY_FILE_NAME);
    }

    /**
     * Create a proxy property manager using a custom property file
     * @param propertyFileName The property file name
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager(String propertyFileName) throws Exception {
        super(propertyFileName);
    }

    /**
     * Create a proxy property manager using a custom property file
     * @param propFileName The property file name
     * @param propFileInputStream The property file input stream
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager(String propFileName, InputStream propFileInputStream) throws Exception{
        super(propFileName, propFileInputStream);
    }

}
