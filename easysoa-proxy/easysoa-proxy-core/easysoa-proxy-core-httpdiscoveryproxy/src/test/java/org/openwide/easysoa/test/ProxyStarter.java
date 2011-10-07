package org.openwide.easysoa.test;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;

/**
 * HTTP Discovery Proxy Test Starter.
 * Just launch the proxy in FraSCAti and wait for a user action to stop.
 */
public class ProxyStarter extends AbstractProxyTestStarter {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(ProxyStarter.class.getClass());    

	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws FrascatiException, InterruptedException 
	 * @throws JSONException 
	 */
    @BeforeClass
	public static void setUp() throws FrascatiException, InterruptedException, JSONException {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   startFraSCAti();
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
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
    
	/**
	 * This test do nothing, just wait for a user action to stop the proxy. 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testWaitUntilRead() throws Exception {
		logger.info("Http Discovery Proxy started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Http Discovery Proxy stopped !");
	}
    
}
