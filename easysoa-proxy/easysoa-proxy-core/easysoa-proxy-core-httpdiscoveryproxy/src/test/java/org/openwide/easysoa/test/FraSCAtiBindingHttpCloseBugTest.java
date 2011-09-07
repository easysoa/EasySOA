package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.Test;

/**
 * Minimal test to make the binding.http fail after been restarted
 *
 * @author mdutoo
 *
 */
public class FraSCAtiBindingHttpCloseBugTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FraSCAtiBindingHttpCloseBugTest.class.getName());    
    
	
	/**
	 * Minimal test to make the binding.http fail after been restarted
	 * @throws Exception 
	 */
    @Test
	public final void test() throws Exception {
	   startFraSCAti();
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite"); // if commented (or no binding.http in it) the test ends fine, else testProxy will block
	   stopFraSCAti();
	   startFraSCAti();
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
	   testProxy(); // blocks on server-side in SCALifeCycleControllerImpl.incrementFcInvocationCounter() line: 440 because component in stopped state	
	   stopFraSCAti();
    }
  

    public final void testProxy() throws Exception {
		logger.info("testProxy...");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();	
		
		// Send a request to the proxy itself 
		try{
			httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT+ "/"), responseHandler);
		} 
		catch(HttpResponseException ex){
			assertEquals(500, ex.getStatusCode());
			logger.debug(ex);
		}
		logger.info("testProxy end !");
    }
    
}
