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

package org.easysoa.records.filters.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.test.ScaTestComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.ecm.platform.test.PlatformFeature;

/**
 * @author jguillemotte
 *
 */
@RunWith(FeaturesRunner.class)
@Features({FraSCAtiFeature.class, PlatformFeature.class})
@Deploy({ "org.nuxeo.ecm.automation.core",
    "org.nuxeo.ecm.automation.features",
    "org.nuxeo.ecm.platform.web.common", "org.nuxeo.ecm.platform.login", "org.nuxeo.ecm.platform.usermanager"})
public class NuxeoFrascatiServletFilterTest {

    /**
     * If FraSCAti doesn't start, do a clean on all the projects in Eclipse, then check that the test 'testFrascatiInNuxeo' in project 'nuxeo-frascati-test' works.
     */
    
    private static Logger logger = Logger.getLogger(NuxeoFrascatiServletFilterTest.class.getClass());
    
    // FraSCAti
    private static FraSCAtiServiceItf frascatiService;
    
    @BeforeClass
    public static void setUp() throws Exception {
        
        // When nuxeo service is available, pass it to the FraSCAti component
        // - Maybe the best is to have a wrapped to avoid Nuxeo dependencies in pure FraSCAti components
        // - Or to use a 'proxy" FraSCAti component
        // In Easysoa, we have to get the registry service
        
        // Getting the FraSCAti service interface
        frascatiService = (FraSCAtiServiceItf) Framework.getService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
        assertNotNull(frascatiService);
        frascatiService.processComposite("composedExchangeHandler");
        frascatiService.processComposite("scaTestComponent");
    }

    /**
     * Test the communication between a frascati service and a Nuxeo service
     * @throws FraSCAtiServiceException  
     */
    @Test
    public void scaComponentTest() throws Exception {
        // Getting a Pure Nuxeo service to test it in a FraSCAti easysoa component
        UserManager usrManager = Framework.getService(UserManager.class);
        assertNotNull(usrManager);
        usrManager.authenticate("administrator", "administrator");
        logger.debug("Getting anonymous user ID : " + usrManager.getDefaultGroup());
        
        ScaTestComponent scaComponent = frascatiService.getService("scaTestComponent", "scaService", ScaTestComponent.class);
        logger.debug("Getting user ID from frascati SCA component : " + scaComponent.testMethod(usrManager));         
    }
    
    @Test
    public void ServletFilterTest() throws ClientProtocolException, IOException{
        // Trigger the Servlet filter
        DefaultHttpClient httpClient = new DefaultHttpClient();
        // Send a test request
        HttpGet newTestRequest = new HttpGet("http://localhost:18000/frascati-web-explorer");
        String response = httpClient.execute(newTestRequest, new BasicResponseHandler());
        logger.debug("Test request response  : " + response);
        assertTrue(response.contains("<title>OW2 FraSCAti Web Explorer</title>"));
    }
    
    /**
     * This test do nothing, just wait for a user action to stop the proxy. 
     * @throws Exception
     */
    @Test
    @Ignore
    public final void testWaitUntilRead() throws Exception {
        logger.info("Exchange record servlet filter test started, wait for user action to stop !");
        // Just push a key in the console window to stop the test
        System.in.read();
        logger.info("Exchange record servlet filter test stopped !");
    } 
    
    @AfterClass
    public static void tearDown() throws Exception {
        if(frascatiService != null){
            frascatiService.stop("composedExchangeHandler");
        }
    }
    
}
