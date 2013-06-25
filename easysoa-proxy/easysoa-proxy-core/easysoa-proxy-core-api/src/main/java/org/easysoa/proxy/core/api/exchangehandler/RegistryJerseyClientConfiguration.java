/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.proxy.core.api.exchangehandler;

import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.properties.ProxyPropertyManager;
import org.easysoa.registry.rest.client.ClientBuilder;

/**
 * Jersey client configuration. To be used with EasySOAV1SOAPDiscoveryMessageHandler
 * to establish a connexion with EasySOA Registry V1
 *
 * @author jguillemotte
 */
public class RegistryJerseyClientConfiguration {

    private ClientBuilder clientBuilder;

    /**
     * Initialize the jersey client
     * @throws Exception If a problem occurs
     */
    public RegistryJerseyClientConfiguration() throws Exception {
    	PropertyManager propertyManager = ProxyPropertyManager.getPropertyManager(); // AND NOT PropertyManager because null TODO better
        clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(propertyManager.getProperty("nuxeo.rest.service", "http://localhost:8080/nuxeo/site"));
        clientBuilder.setCredentials(propertyManager.getProperty("nuxeo.auth.login", "Administrator"),
        		propertyManager.getProperty("nuxeo.auth.password", "Administrator"));
    }

    /**
     * return the Registry client
     * @return
     */
    public ClientBuilder getClient(){
        return this.clientBuilder;
    }

}
