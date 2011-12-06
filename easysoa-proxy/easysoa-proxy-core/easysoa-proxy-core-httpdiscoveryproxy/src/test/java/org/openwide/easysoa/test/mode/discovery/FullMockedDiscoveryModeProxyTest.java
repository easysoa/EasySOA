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

package org.openwide.easysoa.test.mode.discovery;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openwide.easysoa.test.helpers.DiscoveryModeProxyTestBase;
import org.openwide.easysoa.test.helpers.FullMockedServiceTestHelper;
import org.ow2.frascati.util.FrascatiException;

/**
 * Complete test suite of HTTP Discovery Proxy
 * - Starts FraSCATi and the HTTP Discovery Proxy
 * - Test the infinite loop detection feature (OK)
 * - Test the clean Nuxeo registry (OK)
 * - Test the Discovery mode for REST requests (OK) includes re-run test 
 * - Test the Discovery mode for SOAP requests (OK) no re-run test
 * - Test the Validated mode for REST requests (TODO)
 * - Test the validated mode for SOAP requests (TODO)
 *
 * @author jguillemotte
 *
 */
public class FullMockedDiscoveryModeProxyTest extends DiscoveryModeProxyTestBase {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FullMockedDiscoveryModeProxyTest.class.getName());

	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws FrascatiException, InterruptedException 
	 * @throws JSONException 
	 */
    @BeforeClass
	public static void setUp() throws FrascatiException, InterruptedException, JSONException {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   serviceTestHelper = new FullMockedServiceTestHelper();
	   // Clean Nuxeo registery
	   // Mocked so don't need to clean
	   //cleanNuxeoRegistery(null);
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
	   // Start services mock
	   startMockServices(true);
    }
	
    /**
     * Stop FraSCAti components
     * @throws FrascatiException
     */
    @AfterClass
    public static void cleanUp() throws FrascatiException{
    	logger.info("Stopping FraSCAti...");
    	stopFraSCAti();
    }
    
}
