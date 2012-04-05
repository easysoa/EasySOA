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

package org.openwide.easysoa.test.messaging;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.properties.PropertyManager;
import org.easysoa.records.assertions.AssertionEngine;
import org.easysoa.records.assertions.AssertionEngineImpl;
import org.easysoa.records.assertions.AssertionSuggestions;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.records.replay.ReplayEngine;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateEngineImpl;
import org.easysoa.template.TemplateFieldSuggestions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMock;
import org.openwide.easysoa.test.mock.meteomock.client.MeteoMockPortType;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.ContentReader;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * To test the replay function associated with an exchange record discovered by the http discovery proxy 
 * @author jguillemotte
 *
 */
public class ExchangeRecordProxyReplayTest extends AbstractProxyTestStarter {

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordProxyReplayTest.class.getName());

	/**
	 * Clean old test files, start FraSCAti, start the HTTP discovery proxy and the mock services
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception {
		// clean the old exchange records files
		cleanOldFiles();
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP proxy
		//startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		// Start mock services
		startMockServices(false);
	}	

	/**
	 * Functional test for HTTP Discovery proxy, run manager and exchange record store manager with replay
	 * 
	 * Scenario :
	 * - User starts a new run
	 * - User sends several REST request from Twitter sample
	 * - User stop, save and delete the run to obtain an exchange store
	 * - User get the list of exchange store
	 * - User get data about an exchange record
	 * - User replay several exchange record
	 * 
	 * This test works with the twitter mock (REST exchanges)
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void testReplayWithRestMessages() throws Exception {

	    String testStoreName = "Twitter_Rest_Test_Run";
	    
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		
		// Start a new Run
		HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/" + testStoreName);
		assertEquals("Run '" + testStoreName + "' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));
		
		// Get the twitter mock set and send requests to the mock through the HTTP proxy
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();

		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		UrlMock urlMock = new UrlMock();
		HttpResponse response;
		String entityResponseString;
		HttpUriRequest httpUriRequest;
		for(String url : urlMock.getTwitterUrlData("localhost:" + EasySOAConstants.TWITTER_MOCK_PORT)){
			logger.info("Request send : " + url);			
			httpUriRequest = new HttpGet(url);
			response = httpProxyClient.execute(httpUriRequest);
			// Need to read the response body entierely to be able to send another request
			entityResponseString = ContentReader.read(response.getEntity().getContent());			
		}

		// Stop and save the run
		HttpPost stopRunPostRequest = new HttpPost("http://localhost:8084/run/stop");
		assertEquals("Current run stopped !", httpClient.execute(stopRunPostRequest, new BasicResponseHandler()));
		// delete the run
		HttpPost deleteRunPostRequest = new HttpPost("http://localhost:8084/run/delete");
		assertEquals("Run deleted !", httpClient.execute(deleteRunPostRequest, new BasicResponseHandler()));
		
		// get a list of recorded exchange store
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordStorelist");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.debug("Exchange record store list response : " + entityResponseString);
		assertTrue(entityResponseString.contains(testStoreName));
		
		// Get an exchange record
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecord/" + testStoreName + "/1");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.debug("Exchange record response : " + entityResponseString);
		assertTrue(entityResponseString.contains("{\"exchangeRecord\":{\"exchange\":{\"exchangeID\":1"));
		
		// get a list of recorded exchanges contained in the Test_Run folder
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordList/" + testStoreName);
		//logger.debug("Sending request");
		response = httpClient.execute(httpUriRequest);
		//logger.debug("Reading response");
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.debug("Exchange record list response : " + entityResponseString);
		//assertTrue(entityResponseString.contains("ExchangeID"));

		// replay one or several exchanges
		logger.debug("Calling Replay service ...");
		ProxyFileStore fileStore= new ProxyFileStore();
		
		String originalResponse;
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(testStoreName);

		// Send a request to the replay service
		for(ExchangeRecord record : recordList){
		//ExchangeRecord record = recordList.get(0);
			originalResponse = record.getOutMessage().getMessageContent().getRawContent();
			httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/replay/" + testStoreName + "/" + record.getExchange().getExchangeID());
			response = httpClient.execute(httpUriRequest);
			entityResponseString = ContentReader.read(response.getEntity().getContent());
			//logger.debug("Original ExchangeRecord response : " + originalResponse);
			//logger.debug("Replayed ExchangeRecord response : " + entityResponseString);
			
			// Compare the replayed exchange with the original exchange
			assertEquals(originalResponse, entityResponseString);
		}
		
		// Get suggested fields and generate templates for each exchange record
	    TemplateEngine templateEngine = new TemplateEngineImpl();
	    AssertionEngine assertionEngine = new AssertionEngineImpl();
    
	    // Create a ReplaySequencer class to organise to execution of differents engines
	    // Add boolean variables in property file to enable or disable the execution of engines
        ReplayEngine replayEngine = frascati.getService(componentList.get(0), "replayEngineService", org.easysoa.records.replay.ReplayEngine.class);
        replayEngine.startReplaySession(testStoreName + "_replaySession");
	    for(ExchangeRecord record : recordList){
	        // Field suggestions
	        TemplateFieldSuggestions fieldSuggestions = templateEngine.suggestFields(record, testStoreName, true);
	        // Assertions suggestions
	        AssertionSuggestions assertionSuggestions = assertionEngine.suggestAssertions(fieldSuggestions, record.getExchange().getExchangeID(), testStoreName);
	        // templatized record
	        templateEngine.generateTemplate(fieldSuggestions, record, testStoreName, true);
	        // Replaying records
	        OutMessage replayedMessage = new OutMessage();
	        MessageContent replayedMessageContent = new MessageContent();
	        replayedMessage.setMessageContent(replayedMessageContent);
	        replayedMessageContent.setRawContent(replayEngine.replay(testStoreName, record.getExchange().getExchangeID()).getMessageContent().getRawContent());
	        // TODO => call replayWithTemplate Method to test the remplate engine
	        
	        // Executing assertions
	        // TODO : Test for LCS assertion with a big message => Out of memory error
	        // Need to add a limitation to avoid to freeze or block the whole test. 
	        // assertionSuggestions.addAssertion(new LCSAssertion("LCSAssertion"));
            //assertionEngine.executeAssertions(assertionSuggestions, record.getOutMessage(), replayedMessage);  
	    }
        replayEngine.stopReplaySession();
	}
	
	/**
    /**
     * Functional test for HTTP Discovery proxy, run manager and exchange record store manager with replay
     * 
     * Scenario :
     * - User starts a new run
     * - User sends several SOAP request from Meteo sample
     * - User stop, save and delete the run to obtain an exchange store
     * - User get the list of exchange store
     * - User get data about an exchange record
     * - User replay several exchange record
     *
	 * This test works with the Meteo mock (SOAP WSDL exchanges)
	 * @throws Exception
	 */
	@Test
	//@Ignore
	public void testReplayWithSoapMessages() throws Exception {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		
		String testStoreName = "Meteo_WSDL_TestRun";
		
		// Start a new Run
		HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/" + testStoreName);
		assertEquals("Run '" + testStoreName + "' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));		

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
		
		// Stop and save the run
		HttpPost stopRunPostRequest = new HttpPost("http://localhost:8084/run/stop");
		assertEquals("Current run stopped !", httpClient.execute(stopRunPostRequest, new BasicResponseHandler()));
		// delete the run
		HttpPost deleteRunPostRequest = new HttpPost("http://localhost:8084/run/delete");
		assertEquals("Run deleted !", httpClient.execute(deleteRunPostRequest, new BasicResponseHandler()));		
		
		// get a list of recorded exchange store
		HttpUriRequest httpUriRequest;
		HttpResponse response;
		String entityResponseString;		
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordStorelist");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.info("Exchange record store list response : " + entityResponseString);
		assertTrue(entityResponseString.contains(testStoreName));
		
		// Get an exchange record
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecord/" + testStoreName + "/1");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.info("Exchange record response : " + entityResponseString);
		assertTrue(entityResponseString.contains("{\"exchangeRecord\":{\"exchange\":{\"exchangeID\":1"));		
		
		// get a list of recorded exchanges contained in the Test_Run folder
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordList/" + testStoreName);
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());

		// replay one or several exchanges
		logger.debug("Calling Replay service ...");
		ProxyFileStore fileStore= new ProxyFileStore();
		// Check the results
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(testStoreName);
		for(ExchangeRecord record : recordList){
			RequestForwarder forwarder = new RequestForwarder();
			OutMessage outMessage = forwarder.send(record.getInMessage());
			//logger.debug("Replayed ExchangeRecord response : " + entityResponseString);
			// Compare the replayed exchange with the original exchange
			assertEquals(record.getOutMessage().getMessageContent().getRawContent(), outMessage.getMessageContent().getRawContent());
		}
		
        // Get suggested fields and generate templates for each exchange record
        TemplateEngine templateEngine = new TemplateEngineImpl();
        AssertionEngine assertionEngine = new AssertionEngineImpl();
        // Create a ReplaySequencer class to organise to execution of differents engines
        // Add boolean variables in property file to enable or disable the execution of engines
        ReplayEngine replayEngine = frascati.getService(componentList.get(0), "replayEngineService", org.easysoa.records.replay.ReplayEngine.class);
        replayEngine.startReplaySession(testStoreName + "_replaySession");
        for(ExchangeRecord record : recordList){
            // Field suggestions
            TemplateFieldSuggestions fieldSuggestions = templateEngine.suggestFields(record, testStoreName, true);
            // Assertions suggestions
            AssertionSuggestions assertionSuggestions = assertionEngine.suggestAssertions(fieldSuggestions, record.getExchange().getExchangeID(), testStoreName);
            // templatized record
            templateEngine.generateTemplate(fieldSuggestions, record, testStoreName, true);
            // Replaying records, Assertions are executed in the replay engine
            OutMessage replayedMessage = new OutMessage();
            MessageContent replayedMessageContent = new MessageContent();
            replayedMessage.setMessageContent(replayedMessageContent);
            replayedMessageContent.setRawContent(replayEngine.replay(testStoreName, record.getExchange().getExchangeID()).getMessageContent().getRawContent());
            // Executing assertions
            //assertionEngine.executeAssertions(assertionSuggestions, record.getOutMessage(), replayedMessage);           
        }		
        replayEngine.stopReplaySession();
	}
	
	/**
	 * Technical test. 
	 * This test do nothing, just wait for a user action to stop the proxy.
	 * 
	 * @throws Exception If a problem occurs
	 */
	@Test
	@Ignore
	public final void testWaitUntilRead() throws Exception {
		logger.info("ExchangeRecordProxyReplayTest started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("ExchangeRecordProxyReplayTest stopped !");
	}
	
    /**
     * Stop FraSCAti components
     * @throws FrascatiException
     */
    @After
    public void cleanUp() throws Exception{
    	logger.info("Stopping FraSCAti...");
    	stopFraSCAti();
    }	
    
    /**
     * Delete the old exchange record file remaining in target path
     */
    protected static void cleanOldFiles(){
    	File folder = new File(PropertyManager.getProperty("path.record.store"));
    	File[] listOfFiles = folder.listFiles();
    	if(listOfFiles != null){
	    	for (File file : listOfFiles) {
	            if (file.isFile()) {
	            	logger.debug("file name : " + file.getName());
	                if (file.getName().endsWith(".json")) {
	                	logger.debug("Deleting file " + file.getName());
	                	file.delete();
	    	        }
	    	    }
	    	}
    	}
    }
}
