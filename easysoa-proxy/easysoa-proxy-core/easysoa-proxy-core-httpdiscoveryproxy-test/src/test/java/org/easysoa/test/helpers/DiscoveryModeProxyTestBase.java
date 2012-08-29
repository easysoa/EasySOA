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

package org.easysoa.test.helpers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import org.easysoa.test.util.AbstractProxyTestStarter;
import org.easysoa.test.util.UrlMock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;


public abstract class DiscoveryModeProxyTestBase extends AbstractProxyTestStarter {

	protected static ServiceTestHelperBase serviceTestHelper;
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(DiscoveryModeProxyTestBase.class.getName());    

    
    /**
     * Test the clean registry Nuxeo feature to be sure that Nuxeo is empty before to launch the tests
     * NB. Not needed in full mocked mode
     * @throws JSONException
     */
    @Test
    @Ignore
    public final void testCleanNuxeoRegistry() throws Exception {
    	serviceTestHelper.cleanNuxeoRegistry("%" + EasySOAConstants.TWITTER_MOCK_PORT + "%");
    }
    
    /**
     * Test the infinite loop detection feature
     * @throws Exception
     */
    @Test
    @Ignore
    public final void testInfiniteLoopDetection() throws Exception {
		logger.info("Test Infinite loop detection started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		// Send a request to the proxy itself 
		try{
			httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT + "/"), responseHandler);
			//TODO add a timouted fail() 
		} 
		catch(HttpResponseException ex){
			assertEquals(500, ex.getStatusCode());
			logger.debug(ex);
		}
		logger.info("Test Infinite loop detection end !");
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
	//@Ignore
	public final void testRestDiscoveryMode() throws Exception {
		logger.info("Test REST Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		String runName = "RESTDiscoveryTestRun";
		
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		// Useless, the mode is set directly in the composite file
		// In future versions, it will be possible to change the mode with the proxy driver
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/start/" + runName), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run 'RESTDiscoveryTestRun' started !", resp);
		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
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

		// Stop & save the run
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/stop"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/save"), responseHandler);
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/delete"), responseHandler);
		
		// Wait for the proxy register the discovered services in Nuxeo
		logger.info("Wait for the proxy finish to register services ...");
		serviceTestHelper.waitForServices(5000);
		
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
		assertEquals("http://127.0.0.1:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/users/show", jsonObject.get("serv:url"));		

		// Delete the registered Twitter stuff in Nuxeo, then re-run the recorded Twitter test run
		serviceTestHelper.cleanNuxeoRegistry("%" + EasySOAConstants.TWITTER_MOCK_PORT + "%");
		
		//Re-run : Now called replay, tested in another test
		//resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/reRun/RESTDiscoveryTestRun"), responseHandler);
		//assertEquals("Re-run done", resp);

		// Check registered api's in Nuxeo
		nuxeoResponse = nrs.sendQuery(nuxeoQuery);		
		// Get the property JSON Object
		entries = new JSONObject(nuxeoResponse).getString("entries");
		firstEntry = new JSONArray(entries).getJSONObject(0).toString();
		jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
		assertEquals("http://127.0.0.1:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/users/show", jsonObject.get("serv:url"));		
		
		logger.info("Test REST Discovery mode ended successfully !");
	}
	
	/**
	 * Test the discovery mode for SOAP requests
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	// TODO problem, cannot execute all tests at the same time, eg with Maven because some services are not well closed in Frascati
	public final void testSoapDiscoveryMode() throws Exception {
		
	    String soapRequestContent = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:met=\"http://meteomock.mock.test.easysoa.openwide.org/\">"
	            + "<soapenv:Header/>"
	            + "<soapenv:Body>"
	            + "<met:getTomorrowForecast>"
	            + "<!--Optional:-->"
	            + "<met:arg0>Lyon</met:arg0>"
	            +"</met:getTomorrowForecast>"
	            + "</soapenv:Body>"
	            +"</soapenv:Envelope>";
	    
	    logger.info("Test SOAP Discovery mode started !");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();		
		
		String runName = "SOAPDiscoveryTestRun";
		
		// Http client for proxy driver
		DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
		
		// Set Discovery mode
		logger.info("Set proxy mode to Discovery");
		String resp = httpProxyDriverClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/setMonitoringMode/discovery"), responseHandler);
		assertEquals("Monitoring mode set", resp);
		
		// Start a new run
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/start/" + runName), responseHandler);
		logger.info("start run : " + resp);
		assertEquals("Run '" + runName + "' started !", resp);
		
		//FileInputStream fis = new FileInputStream(new File("src/test/resources/meteoMockMessages/meteoMockRequest.xml"));
		//InputStream is = this.getClass().getResourceAsStream("src/test/resources/meteoMockMessages/meteoMockRequest.xml");
		InputStream in = new ByteArrayInputStream(soapRequestContent.getBytes()); 
		
		BasicHttpEntity soapRequest = new BasicHttpEntity();
		soapRequest.setContent(in);
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();		
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);		
		
		HttpPost httpPost = new HttpPost("http://localhost:" + EasySOAConstants.METEO_MOCK_PORT + "/meteo");
		httpPost.setEntity(soapRequest);
		//httpPost.setHeader("Content-Type", "text/xml");
		try {
		    logger.debug("Sending SOAP request " + httpPost);
			String response = httpProxyClient.execute(httpPost, responseHandler);		
			logger.info(response);
		}
		catch(Exception ex){
			logger.error("Error occurs during the soap message test", ex);
		}
		
		// Stop the run
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/stop"), responseHandler);
		assertEquals("Current run stopped !", resp);
		logger.info("stop run : " + resp);
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/save"), responseHandler);
		resp = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/delete"), responseHandler);		
		
		logger.info("Wait for the proxy finish to register WSDL service ...");
		serviceTestHelper.waitForServices(1000);
		logger.info("Check that registration is correct in Nuxeo");
		String nuxeoQuery = "SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:primaryType = 'Service' AND dc:title = 'meteo'";
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		String nuxeoResponse = nrs.sendQuery(nuxeoQuery);
		logger.info("Nuxeo response : " + nuxeoResponse);
		
		// Get the property JSON Object
		String entries = new JSONObject(nuxeoResponse).getString("entries");
		String firstEntry = new JSONArray(entries).getJSONObject(0).toString();
		JSONObject jsonObject = new JSONObject(new JSONObject(firstEntry).getString("properties"));
		assertEquals("http://127.0.0.1:" + EasySOAConstants.METEO_MOCK_PORT + "/meteo", jsonObject.get("serv:url"));			

		logger.info("Test SOAP Discovery mode ended successfully !");
	}

	@Test
	@Ignore
	public final void testRestValidatedMode() throws Exception {
		// TODO To complete with the test code for REST validated mode
	}	
	
	@Test
	@Ignore
	public final void testSoapValidatedMode() throws Exception {
		// TODO To complete with the test code for SOAP validated mode		
	}
	
}
