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

package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;

import com.openwide.easysoa.util.ContentReader;

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
    //@Ignore
	public final void test() throws Exception {
       startFraSCAti();
       startMockServices(false, true, true);
	   //startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite"); // if commented (or no binding.http in it) the test ends fine, else testProxy will block
	   testProxy(); // First test works fine
	   stopFraSCAti();
	   startFraSCAti();
       startMockServices(false, true, true);
	   //startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
	   testProxy(); // Second test blocks on server-side in SCALifeCycleControllerImpl.incrementFcInvocationCounter() line: 440 because component in stopped state
	   stopFraSCAti();
    }

    /**
     * Send a request using the HTTP proxy
     * @throws Exception
     */
    public final void testProxy() throws Exception {
		logger.info("testProxy...");
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		// Send a request to the proxy 
		try{
	        // Set client to use the HTTP Discovery Proxy
	        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
	        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

	        // Send first request - GET request with only path parameters
	        String request = "http://localhost:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/tweets/lastTweet/toto";
	        logger.debug("Test request : " + request);
	        HttpGet lastTweetRequest = new HttpGet(request);
	        HttpResponse httpResponse = httpProxyClient.execute(lastTweetRequest);
	        String responseString = ContentReader.read(httpResponse.getEntity().getContent());
			logger.debug("Proxy response : " + responseString);
		}
		catch(HttpResponseException ex){
			assertEquals(500, ex.getStatusCode());
			logger.debug(ex);
		}
		logger.info("testProxy end !");
    }

}
