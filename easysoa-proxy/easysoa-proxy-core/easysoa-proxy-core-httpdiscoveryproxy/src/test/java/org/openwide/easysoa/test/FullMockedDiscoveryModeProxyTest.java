package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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
public class FullMockedDiscoveryModeProxyTest extends AbstractProxyTestStarter {

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
	public final static void setUp() throws FrascatiException, InterruptedException, JSONException {
	   logger.info("Launching FraSCAti and HTTP Discovery Proxy");
	   // Clean Nuxeo registery
	   // Mocked so don't need to clean
	   //cleanNuxeoRegistery();
	   // Start fraSCAti
	   startFraSCAti();
	   // Start HTTP Proxy
	   startHttpDiscoveryProxy("src/main/resources/httpDiscoveryProxy.composite");
	   // Start services mock
	   startMockServices(true);
    }
	
    /**
     * Test the infinite loop detection feature
     * @throws Exception
     */
    @Test
    public final void testInfiniteLoopDetection() throws Exception {
		logger.info("Test Infinite loop detection started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);				
		
		// Send a request to the proxy itself 
		try{
			httpProxyClient.execute(new HttpGet("http://localhost:8082/"), responseHandler);
		} 
		catch(HttpResponseException ex){
			assertEquals(500, ex.getStatusCode());
			logger.debug(ex);
		}
		logger.info("Test Infinite loop detection end !");
    }
    
    /**
     * Test the clean registry Nuxeo feature to be sure that Nuxeo is empty before to launch the tests
     * Not needed in full mocked mode
     * @throws JSONException
     */
    @Test
    @Ignore
    public final void testCleanNuxeoRegistery() throws JSONException{
    	String nuxeoResponse = cleanNuxeoRegistery();
    	assertEquals("{\n  \"entity-type\": \"documents\",\n  \"entries\": []\n}", nuxeoResponse);
    }
    
	/**
	 * Test the discovery mode for REST requests
	 * 
	 * - Set the monitoring service to Discovery mode
	 * - Start a new Run
	 * - Set the client to use HTTP Discovery proxy
	 * - Send the request form the twitter mock
	 * - Stop the run
	 * - Check that api's and services are well detected and registered
	 * - Delete api's and services registered in Nuxeo
	 * - Rerun the recorded run
	 * - Check that api's and services are well detected and registered
	 * 
	 * @throws ClientException, SOAPException, IOException in case of error
	 */
	@Test
	public final void testRestDiscoveryMode() throws Exception {
		logger.info("Test REST Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		// Useless, the mode is set directly in the composite file
		// In future versions, it will be possible to change the mode with the proxy driver
		//logger.info("Set proxy mode to Discovery");
		//String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/discovery"), responseHandler);
		//assertEquals("Monitoring mode set", resp);
		
		// Start a new run
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/RESTDiscoveryTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'RESTDiscoveryTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
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
		
		// Wait for the proxy register the discovered services in Nuxeo
		logger.info("Wait for the proxy finish to register services ...");
		//Thread.sleep(5000);
		
		// Check discovery results
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
		// Do to same thing but less readable
		// JSONObject jsonObject = new JSONObject(new JSONObject(new JSONArray(new JSONObject(nuxeoResponse).getString("entries")).getJSONObject(0).toString()).getString("properties"));
		assertEquals("http://localhost:8081/1/users/show", jsonObject.get("serv:url"));			

		// Delete the registered stuff in Nuxeo, then re-run the recorded Twitter test run
		// Nuxeo mocked : no need to clean
		//cleanNuxeoRegistery();
		
		//Re-run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/reRun/RESTDiscoveryTestRun"), responseHandler);
		assertEquals("Re-run done", resp);

		// Check registered api's in Nuxeo
		nuxeoResponse = nrs.sendQuery(nuxeoQuery);		
		// Get the property JSON Object
		entries = new JSONObject(nuxeoResponse).getString("entries");
		firstEntry = new JSONArray(entries).getJSONObject(0).toString();
		jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
		assertEquals("http://localhost:8081/1/users/show", jsonObject.get("serv:url"));		
		
		logger.info("Test REST Discovery mode ended successfully !");
	}
	
	/**
	 * Test the discovery mode for SOAP requests
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testSoapDiscoveryMode() throws Exception {
		logger.info("Test SOAP Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();		
		
		/*
		// Need to use the proxy
		// Doesn't work because an infinite loop is triggered in the proxy
		ProxySelector.setDefault(new SoapClientProxySelector());		
		// Set the WSDL references
		String serviceUrl = "http://localhost:8085/meteo?wsdl";
		String TNS = "http://meteomock.mock.test.easysoa.openwide.org/";		
		QName serviceName = new QName(TNS, "MeteoMock");
		QName portName = new QName(TNS, "MeteoMockPort");
		Service jaxwsService = Service.create(new URL(serviceUrl), serviceName);
		Dispatch<SOAPMessage> disp = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
		// Get the XML request to send
		FileInputStream fis = new FileInputStream(new File("src/test/resources/meteoMockMessages/meteoMockRequest.xml"));
	    // Send the message
		SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, fis);
	    assertNotNull(reqMsg);
		SOAPMessage response = disp.invoke(reqMsg);
		logger.debug("Response : " + response.getSOAPBody().getTextContent().trim());		
		// Wait for the proxy finish to register services
		logger.info("Wait for the proxy finish to register services ...");
		Thread.sleep(5000);
		// Send a request to Nuxeo to check the registration
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);		
		logger.debug("Nuxeo response : " + nuxeoResponse);
		*/
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		//logger.info("mode setting : " + resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/startNewRun/SOAPDiscoveryTestRun"), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'SOAPDiscoveryTestRun' started !", resp);		
		
		FileInputStream fis = new FileInputStream(new File("src/test/resources/meteoMockMessages/meteoMockRequest.xml"));
		BasicHttpEntity soapRequest = new BasicHttpEntity();
		soapRequest.setContent(fis);
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", 8082);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
		HttpPost httpPost = new HttpPost("http://localhost:8085/meteo");
		httpPost.setEntity(soapRequest);
		//httpPost.setHeader("Content-Type", "text/xml");
		try {
			String response = httpProxyClient.execute(httpPost, responseHandler);		
			logger.info(response);
		}
		catch(Exception ex){
			logger.error("Error occurs during the soap message test", ex);
		}
		
		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:8084/stopCurrentRun"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);		
		
		logger.info("Wait for the proxy finish to register WSDL service ...");
		//Thread.sleep(1000);		
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:primaryType = 'Service' AND dc:title = 'meteo'";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.info("Nuxeo response : " + nuxeoResponse);
		
		// Get the property JSON Object
		String entries = new JSONObject(nuxeoResponse).getString("entries");
		String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
		JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
		assertEquals("http://localhost:8085/meteo", jsonObject.get("serv:url"));			

		logger.info("Test SOAP Discovery mode ended successfully !");
	}

}
