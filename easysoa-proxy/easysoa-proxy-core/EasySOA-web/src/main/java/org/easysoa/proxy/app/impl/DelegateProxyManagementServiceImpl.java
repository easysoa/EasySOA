package org.easysoa.proxy.app.impl;

import java.util.List;

import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.management.HttpProxyManagementService;
import org.easysoa.proxy.core.api.management.ManagementServiceResult;

/**
 * Delegate impl, to avoid error :
 * Invocation of method 'getHttpProxy' in class org.easysoa.proxy.core.api.management.HttpProxyManagementServiceFcSR threw exception org.apache.cxf.jaxrs.client.ClientWebApplicationException: .No message body reader has been found for class : class org.easysoa.proxy.core.api.management.ManagementServiceResult, ContentType : text/html. at management/getInstance.html[line 13, column 58]
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

	public ManagementServiceResult getHttpProxy(ProxyConfiguration configuration)
			throws Exception {
		return delegate.getHttpProxy(configuration);
	}

	public ProxyConfiguration get(String proxyId)
			throws Exception {
		return delegate.get(proxyId);
	}

	public String reset(ProxyConfiguration configuration) throws Exception {
		return delegate.reset(configuration);
	}

	public List<ProxyConfiguration> listInstances() throws Exception {
		return delegate.listInstances();
	}

}
