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
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.ow2.frascati.util.FrascatiException;

import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * Complete test suite of HTTP Discovery Proxy
 * - Starts FraSCATi and the HTTP Discovery Proxy
 * - Test the Discovery mode for REST requests
 * - Test the Discovery mode for SOAP requests (TODO)
 * - Test the Validated mode for REST requests (TODO)
 * - Test the validated mode for SOAP requests (TODO)
 *
 * @author jguillemotte
 *
 */
public class HttpDiscoveryProxyTest extends AbstractProxyTestStarter {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(HttpDiscoveryProxyTest.class.getName());    
    
	/**
	 * Initialize one time the remote systems for the test
	 * FraSCAti and HTTP discovery Proxy ...
	 * @throws FrascatiException, InterruptedException 
	 */
    @BeforeClass
	public final static void setUp() throws FrascatiException, InterruptedException {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   // Start fraSCAti
		startFraSCAti();
		// Start HTTP Discovery Proxy
		startHttpDiscoveryProxy();
	}
	
	/**
	 * 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
    @Ignore
	@Test
	public final void testRestDiscoveryMode() throws Exception {
		logger.info("Test REST Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		//logger.info("mode setting : " + resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/discoveryTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'discoveryTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
		// Send Http Rest requests
		// TODO 2 Separated tests : one with real services, one with mock services
		// TODO : A twitter mock service because requests on Twitter api are limited to 150 per hour
		// TODO : A Nuxeo mock to avoid to have a started Nuxeo to launch the test
		UrlMock urlMock = new UrlMock();
		for(String url : urlMock.getTwitterUrlData("api.twitter.com")){
			logger.info("Request send : " + url);			
			HttpUriRequest httpUriRequest = new HttpGet(url);
			try {
				String response = httpProxyClient.execute(httpUriRequest, responseHandler);
			}
			catch (HttpResponseException ex){
				logger.info("Error occurs, status code : " + ex.getStatusCode() + ", message : " + ex.getMessage(), ex);
			}
		}

		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		
		// Wait for the proxy register the discovered services in Nuxeo
		logger.info("Wait for the proxy finish to register services ...");
		Thread.sleep(5000);
		
		// Check discovery results
		// Send a request to Nuxeo to check that services are well registered
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND dc:title = 'show' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.info("Nuxeo response : " + nuxeoResponse);
		try{
			// Get the property JSON Object
			String entries = new JSONObject(nuxeoResponse).getString("entries");
			String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
			JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
			// Do to same thing but less readable
			//JSONObject jsonObject = new JSONObject(new JSONObject(new JSONArray(new JSONObject(nuxeoResponse).getString("entries")).getJSONObject(0).toString()).getString("properties"));
			assertEquals("http://api.twitter.com/1/users/show", jsonObject.get("serv:url"));			
		}
		catch(Exception ex){
			logger.info("An error occurs while reading the nuxeo response", ex);
		}

		logger.info("Test REST Discovery mode stopped !");
	}

	// TODO tests for validated mode
	// Set Validated mode
	// Send http rest requests
	
	/**
	 * 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test @Ignore
	public final void testSoapDiscoveryMode() throws Exception {
		logger.info("Test SOAP Discovery mode started !");
		
		// Set discovery mode
		
		// Send Soap requests 

		logger.info("Test SOAP Discovery mode stopped !");
	}	
	
}
