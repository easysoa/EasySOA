package org.openwide.easysoa.test;

import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
public class PartiallyMockedDiscoveryModeProxyTest extends DiscoveryModeProxyTestBase {

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
	   serviceTestHelper = new PartiallyMockedServiceTestHelper();
	   // Clean Nuxeo registery
	   ServiceTestHelperBase.cleanRemoteNuxeoRegistry("%" + EasySOAConstants.TWITTER_MOCK_PORT + "%");
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
	   // Start services mock
	   startMockServices(false);
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
