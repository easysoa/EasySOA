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


package org.easysoa.proxy.app.impl;

import java.util.List;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.management.HttpProxyManagementService;
import org.easysoa.proxy.core.api.management.ManagementServiceResult;

/**
 * Delegate impl, to avoid error :
 * Invocation of method 'getHttpProxy' in class org.easysoa.proxy.core.api.management.HttpProxyManagementServiceFcSR
 * threw exception org.apache.cxf.jaxrs.client.ClientWebApplicationException: .No message body reader has been found for class :
 * class org.easysoa.proxy.core.api.management.ManagementServiceResult, ContentType :
 * text/html. at management/getInstance.html[line 13, column 58]
 *
 * @author mdutoo
 *
 */
public class DelegateProxyManagementServiceImpl implements HttpProxyManagementService {

	private HttpProxyManagementService delegate;

	public HttpProxyManagementService getDelegate() {
		return delegate;
	}

	public void setDelegate(HttpProxyManagementService delegate) {
		this.delegate = delegate;
	}

    @Override
	public ManagementServiceResult getHttpProxy(ProxyConfiguration configuration)
			throws Exception {
		return delegate.getHttpProxy(configuration);
	}

    @Override
	public ProxyConfiguration get(String proxyId)
			throws Exception {
		return delegate.get(proxyId);
	}

    @Override
	public String reset(ProxyConfiguration configuration) throws Exception {
		return delegate.reset(configuration);
	}

    @Override
	public List<ProxyConfiguration> listInstances() throws Exception {
		return delegate.listInstances();
	}

}
