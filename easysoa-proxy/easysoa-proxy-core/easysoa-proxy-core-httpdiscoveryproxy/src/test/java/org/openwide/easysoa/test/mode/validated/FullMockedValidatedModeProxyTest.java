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

package org.openwide.easysoa.test.mode.validated;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.ow2.frascati.util.FrascatiException;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

public class FullMockedValidatedModeProxyTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FullMockedValidatedModeProxyTest.class.getName());    
    
	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws Exception 
	 */
    @BeforeClass
	public static void setUp() throws Exception {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   // Clean Nuxeo registery
	   // Mocked so don't need to clean
	   //cleanNuxeoRegistery();
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy_validatedMode.composite");
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
    
	@Test
	public final void testRestValidatedMode() throws Exception {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		// Useless, the mode is set directly in the composite file
		// In future versions, it will be possible to change the mode with the proxy driver
		//logger.info("Set proxy mode to Validated");
		//String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/validated"), responseHandler);
		//assertEquals("Monitoring mode set", resp);
				
		// Start a new run
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/startNewRun/RESTValidatedTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'RESTValidatedTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		// Load registry data form nuxeo : Automatically done during the ValidatedMonitoringService
		
		// Send Http Rest requests
		UrlMock urlMock = new UrlMock();
		for(String url : urlMock.getTwitterUrlData("localhost:" + EasySOAConstants.TWITTER_MOCK_PORT)){
			logger.info("Request send : " + url);			
			HttpUriRequest httpUriRequest = new HttpGet(url);
			try {
				String response = httpProxyClient.execute(httpUriRequest, responseHandler);
				logger.debug("response : " + response);
			}
			catch (HttpResponseException ex){
				logger.info("Error occurs, status code : " + ex.getStatusCode() + ", message : " + ex.getMessage(), ex);
			}
		}

		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:"+ EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		
		// Check validated results
		// Send a request to Nuxeo to check that services are well registered
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND dc:title = 'show' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.debug("Nuxeo response : " + nuxeoResponse);

		// Get the property JSON Object
		String entries = new JSONObject(nuxeoResponse).getString("entries");
		String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
		JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
		assertEquals("http://localhost:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/users/show", jsonObject.get("serv:url"));			
		
		logger.info("Test REST Validated mode ended successfully !");		
	}
	
	@Test
	@Ignore
	public final void testSoapValidatedMode() throws Exception {
		// TODO To complete with the test code for SOAP validated mode		
	}	
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public final void testWaitUntilRead() throws Exception{
		logger.info("Test waiting for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Test stopped !");
	}	
	
}
