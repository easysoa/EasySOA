package org.openwide.easysoa.test;

import org.apache.log4j.Logger;
import org.junit.Test;

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
