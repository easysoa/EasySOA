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

/**
 * 
 */
package org.openwide.easysoa.test.messaging;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.properties.PropertyManager;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.records.replay.ReplayEngine;
import org.easysoa.template.AbstractTemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.util.ContentReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMock;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMockPortType;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.openwide.easysoa.test.util.UrlMock;


/**
 * To test replay templates
 * 
 * @author jguillemotte
 *
 */
public class TemplateTest extends AbstractProxyTestStarter {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateTest.class.getName());

	/**
	 * Start FraSCAti, HTTP discovery proxy and mock services
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception{
	    PropertyManager propertyManager = new PropertyManager("httpDiscoveryProxy.properties", this.getClass().getResourceAsStream("/" + "httpDiscoveryProxy.properties"));
	    // Start fraSCAti
		startFraSCAti();
		// Start Http Discovery proxy
		try {
			startHttpDiscoveryProxy("httpDiscoveryProxy.composite", new URL[] { new URL("file://target") });
		} catch (MalformedURLException e) {
			throw new Exception("TemplateTest init error", e);
		}
		// Start mock services
		startMockServices(true, true, true);
	}
	
	/**
	 * 
	 * Deprecated : This test worked with the first templating solution. This solution is no more used today. 
	 * 
	 * Technical test
	 * - Get the form generated from an exchange record
	 * - Check that the form contains required fields
	 * - Send a simulated post request with custom form values
	 * 
	 * ReplayTemplateWithDefaultValue : Use the replayTemplate.html velocity HTML template to build a form.
	 * Only with REST Exchanges
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Test
	@Ignore
    @Deprecated
	public void replayTemplateWithDefaultValue() throws ClientProtocolException, IOException{
		// TODO : Complete this test with 4 kinds of parameters : formParams, pathParams, QueryParams, WSDLParams
		
	    // TODO : This test works with a store created by another test ..... See testReplayWithRestMessages in ExchangeRecordProxyReplayTest class.
	    String testRunName = "Twitter_Rest_Test_Run";
	    
		// Read template file with a GET HTTP request
		// returns a request for the replay system
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		HttpGet getRequest = new HttpGet("http://localhost:8090/runManager/exchangeRecordStore/replayTemplate.html");
		String response = httpClient.execute(getRequest, new BasicResponseHandler());
		logger.debug("GetTemplate response = " + response);
		assertTrue(response.contains("comment (String) : <input type=\"text\" name=\"comment\" value=\"test\" />"));
		// Specify the exchangeRecord to use => run/exchangeRecord to get the request 
		
		HttpPost postRequest = new HttpPost("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/templates/replayWithTemplate/"+testRunName+"/1/testTemplate");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("user", "FR3Bourgogne.xml")); // tests HTTP Form-like params (others being HTTP path, query and SOAP)
		formparams.add(new BasicNameValuePair("param2", "value2"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		postRequest.setEntity(entity);
		//HttpParams httpParams = new BasicHttpParams();
		//httpParams.setParameter("", "multipart/form-data");
		//postRequest.setParams(httpParams);
		response = httpClient.execute(postRequest, new BasicResponseHandler());
		logger.debug("replayWithTemplate response = " + response);
		
		// Set the custom values :
		
		// For WSDL : re-use the the existing code working in the scaffolder proxy
		// For Rest : 3 different cases :
		// Form params : get the inMessage content and change the values
		// Query params : get the complete url and change the values
		// Path Params : This case is harder, need to know the position of each param in the url ... must be specified in the template OR use the discovery mechanism from HTTP discovery proxy 
		
		// replay the request with specified values (can be default values)  
	}

	/**
     * Functional test for the template engine (working with the ServletImplementationVelocity solution)
     * 
     * Scenario :
     * - Start a new run
     * - Send Twitter mock request (REST)
     * - Stop, save and delete the run
     * - Get a list of exchange records corresponding to the run
     * - For each record, call the template engine to build and replay the template 
	 * 
	 * TemplateFieldSuggester test with REST Exchanges 
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void templateFieldSuggesterRestTest() throws Exception {

		String testStoreName = "TweeterRestTestRun";
		// Start run
		startNewRun(testStoreName);
		
		// Send tweeter mock requests
		// Get the twitter mock set and send requests to the mock through the HTTP proxy
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();

		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		// Send first request - GET request with only path parameters
		HttpResponse response;
		HttpGet lastTweetRequest = new HttpGet("http://localhost:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/tweets/lastTweet/toto");
		response = httpProxyClient.execute(lastTweetRequest);
		// Need to read the response body entirely to be able to send another request
		ContentReader.read(response.getEntity().getContent());			
		
		// Send second request - GET request with path parameters and query parameters
		HttpGet severalTweetRequest = new HttpGet("http://localhost:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/tweets/severalTweets/toto?tweetNumber=5");
		response = httpProxyClient.execute(severalTweetRequest);
		ContentReader.read(response.getEntity().getContent());
		
		// Send third request - POST request with form parameters
		HttpPost postTweetRequest = new HttpPost("http://localhost:" + EasySOAConstants.TWITTER_MOCK_PORT + "/1/tweets/postNewTweet");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("user", "toto"));
		formparams.add(new BasicNameValuePair("tweet", "This is a tweet test"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		postTweetRequest.setEntity(entity);
		response = httpProxyClient.execute(postTweetRequest);
		ContentReader.read(response.getEntity().getContent());
		
		// Stop, save and delete run
		stopAndSaveRun();
		deleteRun();
		
		ProxyFileStore fileStore= new ProxyFileStore();
	
		// Get the exchange record list for the run
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(testStoreName);
		// Get the template renderer
		//TemplateProcessorRendererItf processor = frascati.getService(componentList.get(0), "processor", org.easysoa.template.TemplateProcessorRendererItf.class);

	      // Get the replay engine service
        ReplayEngine replayEngine = frascati.getService(componentList.get(0).getName(), "replayEngineService", org.easysoa.records.replay.ReplayEngine.class);
		
		// Build an HashMap to simulate user provided values
		HashMap<String, List<String>> fieldMap = new HashMap<String, List<String>>();
        ArrayList<String> userValueList = new ArrayList<String>();
        userValueList.add("Gaston");
        fieldMap.put("user", userValueList);		
        ArrayList<String> valueList = new ArrayList<String>();
        valueList.add("4");
        fieldMap.put("tweetNumber", valueList);
		// For each custom record in the list
		for(ExchangeRecord record : recordList){
		    TemplateFieldSuggestions templateFieldsuggestions = replayEngine.getTemplateEngine().suggestFields(record, testStoreName, true);
		    ExchangeRecord templatizedRecord = replayEngine.getTemplateEngine().generateTemplate(templateFieldsuggestions, record, testStoreName, true);
		    replayEngine.getAssertionEngine().suggestAssertions(templateFieldsuggestions, templatizedRecord.getExchange().getExchangeID(), testStoreName);
			// Render the templates and replay the request
	        replayEngine.replayWithTemplate(fieldMap, testStoreName, templatizedRecord.getExchange().getExchangeID());
		}
	}
	
	/**
	 * Functional test for the template engine (working with the ServletImplementationVelocity solution)
	 * 
	 * Scenario :
	 * - Start a new run
	 * - Send meteo mock request (SOAP)
	 * - Stop, save and delete the run
	 * - Get a list of exchange records corresponding to the run
	 * - For each record, call the template engine to build and replay the template
	 * 
	 * TemplateFieldSuggester test with SOAP Exchanges 
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void templateFieldSuggesterSOAPTest() throws Exception {	
		
		String testStoreName = "MeteoSoapTestRun";
		// Start run
		startNewRun(testStoreName);		

		// Set the discovery proxy
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "8082");
		
		// Send the test requests
		UrlMock urlMock = new UrlMock();
		for(String cityName : urlMock.getMeteoMockCities()){
			MeteoMock meteoService = new MeteoMock();
			MeteoMockPortType port = meteoService.getMeteoMockPort();
			String response = port.getTomorrowForecast(cityName);
			logger.info("Response for " + cityName + " : " + response);
		}
		
		// Remove the discovery proxy
		System.setProperty("http.proxyHost", "");
		System.setProperty("http.proxyPort", "");
		
		// Stop, save and delete run
		stopAndSaveRun();
		deleteRun();

		ProxyFileStore fileStore= new ProxyFileStore();
	
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(testStoreName);

		// Get the template renderer // TODO do not call this service directly, use replay engine instead
		//TemplateProcessorRendererItf processor = frascati.getService(componentList.get(0), "processor", org.easysoa.template.TemplateProcessorRendererItf.class);
		
		// Get the replay engine service
		ReplayEngine replayEngine = frascati.getService(componentList.get(0).getName(), "replayEngineService", org.easysoa.records.replay.ReplayEngine.class);

		// Build an HashMap to simulate user provided values
		HashMap<String, List<String>> fieldMap = new HashMap<String, List<String>>();
		ArrayList<String> valueList = new ArrayList<String>();
		valueList.add("");
		fieldMap.put("user", valueList);
		
		// For each custom record in the list
		for(ExchangeRecord record : recordList){
		    TemplateFieldSuggestions templateFieldsuggestions = replayEngine.getTemplateEngine().suggestFields(record, testStoreName, true);
	        ExchangeRecord templatizedRecord = replayEngine.getTemplateEngine().generateTemplate(templateFieldsuggestions, record, testStoreName, true);
	        replayEngine.getAssertionEngine().suggestAssertions(templateFieldsuggestions, templatizedRecord.getExchange().getExchangeID(), testStoreName);
			// Render the templates and replay the request
	        replayEngine.replayWithTemplate(fieldMap, testStoreName, templatizedRecord.getExchange().getExchangeID());
		}
	}

	/**
	 * Test the integration between assertion engine and suggested fields (with fieldEqualize attribute)
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void assertionEngineWithSuggestionfieldTest() throws Exception {
	    
	    // Launch a replay, and compare for each field with fieldEquality tag 
	    // set to true the replayed value with the original value
	    // Use the .fld's files generated by the previous test to launch assertions
	    String runName = "TweeterRestTestRun";
	    ProxyFileStore fileStore= new ProxyFileStore();
	    // test only the first fields
	    for(int i=1; i<3; i++){
    	    TemplateFieldSuggestions templateFieldSuggestions = fileStore.getTemplateFieldSuggestions(runName, String.valueOf(i));
    	    for(AbstractTemplateField templateField : templateFieldSuggestions.getTemplateFields()) {
    	        if(templateField.isFieldEquality()){
    	            logger.debug("FieldName : " + templateField.getFieldName() + ", FieldValue : " + templateField.getDefaultValue());
    	        }
    	    }
	    }
	}

	/**
	 * This test do nothing, just wait for a user action to stop the proxy. 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public final void testWaitUntilRead() throws Exception {
		logger.info("TemplateTest started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("TemplateTest stopped !");
	}
	
    /**
     * Stop FraSCAti components
     * @throws FrascatiException
     */
    @After
    public void cleanUp() throws Exception{
        logger.info("Stopping FraSCAti...");
        stopFraSCAti();
        // Clean Jetty for twitter mock
        cleanJetty(EasySOAConstants.TWITTER_MOCK_PORT);
        // Clean Jetty for meteo mock
        cleanJetty(EasySOAConstants.METEO_MOCK_PORT);
        // Clean Jetty for meteo mock
        cleanJetty(EasySOAConstants.NUXEO_TEST_PORT);
        // Clean Easysoa proxy
        cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT);
        cleanJetty(EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT);            
    }	
}
