package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.ow2.frascati.util.FrascatiException;

public class FullMockedValidatedModeProxyTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FullMockedValidatedModeProxyTest.class.getName());    
    
	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws FrascatiException, InterruptedException 
	 * @throws JSONException 
	 */
    @BeforeClass
	public final static void setUp() throws FrascatiException, InterruptedException, JSONException {
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
	
	@Test
	public final void testRestValidatedMode() throws Exception {
		// TODO To complete with the test code for REST validated mode
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
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/RESTValidatedTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'RESTValidatedTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		// Load registry data form nuxeo : Automatically done during the ValidatedMonitoringService
		
		// Send Http Rest requests
		UrlMock urlMock = new UrlMock();
		for(String url : urlMock.getTwitterUrlData("localhost:8081")){
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
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		
	}
	
	@Test
	@Ignore
	public final void testSoapValidatedMode() throws Exception {
		// TODO To complete with the test code for SOAP validated mode		
	}	
	
}
