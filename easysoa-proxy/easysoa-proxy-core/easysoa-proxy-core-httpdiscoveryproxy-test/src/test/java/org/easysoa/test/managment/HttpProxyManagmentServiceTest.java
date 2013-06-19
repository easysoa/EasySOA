/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

package org.easysoa.test.managment;

import java.util.ArrayList;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.core.api.configuration.ConfigurationParameter;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.management.HttpProxyManagementService;
import org.easysoa.test.util.AbstractProxyTestStarter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author jguillemotte
 */
public class HttpProxyManagmentServiceTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(HttpProxyManagmentServiceTest.class.getName());

    // Spring context for CXF client
    private static FileSystemXmlApplicationContext context;

	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws Exception
	 */
    @Before
	public void setUp() throws Exception {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpDiscoveryProxy("httpDiscoveryProxy.composite");

       context = new FileSystemXmlApplicationContext("src/test/resources/proxyManagmentClientCxf.xml");
    }

    /**
     * Test the getHttpProxy with the default proxy
     * @throws Exception
     */
    @Test
    public void EmbeddedProxyConfigurationTest() throws Exception {

        ProxyConfiguration proxyConf = new ProxyConfiguration();
        proxyConf.setName("testProxy");
        ArrayList<ConfigurationParameter> params = new ArrayList<ConfigurationParameter>();
        params.add(new ConfigurationParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME, "componentID"));
        params.add(new ConfigurationParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME, "Test"));
        params.add(new ConfigurationParameter(ProxyConfiguration.PROJECTID_PARAM_NAME, "Test project"));
        params.add(new ConfigurationParameter(ProxyConfiguration.USER_PARAM_NAME, "Toto"));
        proxyConf.setParameters(params);

        HttpProxyManagementService client = (HttpProxyManagementService) context.getBean("proxyManagmentServiceClient");
        ProxyConfiguration result = client.getHttpProxy(proxyConf);

        Assert.assertNotNull(result);
        Assert.assertEquals("default", result.getId());
        Assert.assertEquals("EasySOA embedded proxy", result.getName());
        Assert.assertEquals("Toto", result.getParameter(ProxyConfiguration.USER_PARAM_NAME));
        Assert.assertEquals("Test", result.getParameter(ProxyConfiguration.ENVIRONMENT_PARAM_NAME));

    }

    /**
     * Stop FraSCAti components
     * @throws FrascatiException
     */
    @After
    public void cleanUp() throws Exception{
    	logger.info("Stopping FraSCAti...");
    	stopFraSCAti();
    	// Clean Easysoa proxy
    	cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
    	cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT);
    	cleanJetty(EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT);
        cleanJetty(EasySOAConstants.PROXY_MANAGER_SERVICE_PORT);
    }


}
