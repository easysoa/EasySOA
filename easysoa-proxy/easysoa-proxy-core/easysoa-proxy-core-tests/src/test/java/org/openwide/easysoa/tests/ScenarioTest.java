/**
 * 
 */
package org.openwide.easysoa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;
import org.easysoa.template.TemplateBuilder;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateFieldSuggester;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.TemplateProcessorRendererItf;
import org.easysoa.wsdl.twitter_test_run_wsdl.TwitterTestRunPortType_TwitterTestRunPort_Server;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.tests.helpers.AbstractTestHelper;
import com.openwide.easysoa.util.ContentReader;

/**
 * @author jguillemotte
 *
 */
public class ScenarioTest extends AbstractTestHelper {

	// Logger
	private static Logger logger = Logger.getLogger(ScenarioTest.class.getName());

	private static final String TWITTER_TEST_RUN_NAME = "Twitter_test_run";
	// Not yet used
	//private final static String METEO_TEST_RUN_NAME = "Meteo_test_run";	
	
	@BeforeClass
	public static void setUp() throws Exception {
		// Start fraSCAti
		startFraSCAti();
		// Start Http Discovery proxy
		startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		startScaffolderProxy("scaffoldingProxy.composite");
		// Start mock services
		startMockServices(false);
		// Start tweeter WSDL service mock => cxf service
		// TODO : replace this hard mocked service with a real time generated service 
		TwitterTestRunPortType_TwitterTestRunPort_Server.start();		
	}
	
	/**
	 * test the integration between HTTP discovery proxy and Scaffolder proxy
	 */
	@Test
	public void scenarioTest() throws Exception {	
		// Create the twitter test run
		createTestRun(TWITTER_TEST_RUN_NAME);
		
		// Create the Meteo test run
		// TODO : For WSDL TESTS
		//createTestRun(meteoTestRunName);
		
		// Call the replay service
		callReplayService(TWITTER_TEST_RUN_NAME);
		
		// Call the TemplateDefinitionService to generate template and fld files
		callTemplateDefService(TWITTER_TEST_RUN_NAME);
		
		// Choose a store and call the service to get the corresponding WSDL
		getWSDLTemplate(TWITTER_TEST_RUN_NAME);
		
		// Give it to the scaffolding proxy to get the corresponding HTML form
		sendWSDLToScaffolderProxy(TWITTER_TEST_RUN_NAME);
		
		// Generate a server for test wsdl with wsdl2java or frascati
		// Send a request with scaffolder proxy
		// Check the response
	}

	/**
	 * Create a test run (start a new run with the given name, send the test requests, stop, save and delete the run)
	 * @param runName The run name
	 * @throws Exception If a problem occurs
	 */
	private void createTestRun(String runName) throws Exception{
		// Start the test run
		startNewRun(runName);
		
		// Send test requests => twitter request, meteo mock requests
		sendTwitterRestRequests();
		
		// Stop & save the run
		stopAndSaveRun();
		deleteRun();
	}
	
	/**
	 * Send twitter REST requests 
	 * @throws Exception If a problem occurs
	 */
	private void sendTwitterRestRequests() throws Exception {

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
	}
	
	/**
	 * Call the replay service
	 * @throws Exception 
	 * @throws IllegalStateException 
	 */
	private void callReplayService(String runName) throws IllegalStateException, Exception {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		
		// get a list of recorded exchange store
		HttpGet httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordStorelist");
		HttpResponse response = httpClient.execute(httpUriRequest);
		String entityResponseString = ContentReader.read(response.getEntity().getContent());
		logger.debug("Exchange record store list response : " + entityResponseString);
		assertTrue(entityResponseString.contains(runName));

		// get a list of recorded exchanges contained in the Test_Run folder
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordList/" + runName);
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.debug("Exchange record list response : " + entityResponseString);
		//assertTrue(entityResponseString.contains("ExchangeID"));
		
		// Get an exchange record
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecord/" + runName + "/1");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		logger.debug("Exchange record response : " + entityResponseString);
		assertTrue(entityResponseString.contains("{\"exchangeRecord\":{\"exchange\":{\"exchangeID\":1"));
		
		// replay one or several exchanges
		logger.debug("Calling Replay service ...");
		ProxyExchangeRecordFileStore fileStore= new ProxyExchangeRecordFileStore();
		
		String originalResponse;
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(runName);
		//ExchangeRecord record = recordList.get(0); 
		for(ExchangeRecord record : recordList){
			originalResponse = record.getOutMessage().getMessageContent().getContent();
			httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/replay/" + runName + "/" + record.getExchange().getExchangeID());
			response = httpClient.execute(httpUriRequest);
			entityResponseString = ContentReader.read(response.getEntity().getContent());
			logger.debug("Replayed ExchangeRecord response : " + entityResponseString);
			
			// Compare the replayed exchange with the original exchange
			assertEquals(originalResponse, entityResponseString);
		}
	}
	
	/**
	 * Call the template def service
	 * @throws Exception  
	 */
	private void callTemplateDefService(String runName) throws Exception {
		
		logger.debug("callTemplateDefService method for store " + runName);
		System.out.println("callTemplateDefService method for store " + runName);
		TemplateFieldSuggester suggester = new TemplateFieldSuggester();
		TemplateBuilder builder = new TemplateBuilder();
		ProxyExchangeRecordFileStore fileStore= new ProxyExchangeRecordFileStore();
	
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(runName);

		// Get the template renderer
		TemplateProcessorRendererItf processor = frascati.getService(componentList.get(0), "processor", org.easysoa.template.TemplateProcessorRendererItf.class);

		// Build an HashMap to simulate user provided values
		HashMap<String, String> fieldMap = new HashMap<String, String>();
		fieldMap.put("user", "toto");
		
		// For each custom record in the list
		// TODO : seem's there is a problem here, the rendered template is the same for 2 differents cases
		TemplateEngine templateEngine = new TemplateEngine();
		for(ExchangeRecord record : recordList){
            TemplateFieldSuggestions suggestions = templateEngine.suggestFields(record, runName, true);
            ExchangeRecord templatizedRecord = templateEngine.generateTemplate(suggestions, record, runName, true);		    
			// Render the templates and replay the request
			if(templatizedRecord != null){
                String replayedResponse = processor.renderReq(ProxyExchangeRecordFileStore.REQ_TEMPLATE_FILE_PREFIX + record.getExchange().getExchangeID() + ProxyExchangeRecordFileStore.TEMPLATE_FILE_EXTENSION, record, runName, fieldMap);
                logger.debug("returned message form replayed template : " + replayedResponse);
				// TODO : call the renderRes method for server mock test
				// TODO : add an assert to check the result of the replayed templatized request
				//assertEquals(record.getOutMessage().getMessageContent().getContent(), response);
			}
		}		
	}
	
	/**
	 * Get the WSDL for the specified store
	 * @param runName The store name
	 * @throws Exception 
	 */
	private void getWSDLTemplate(String runName) throws Exception {
		//localhost:8090/runManager/target/TweeterRestTestRun?wsdl
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		// get a list of recorded exchange store
		HttpGet httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/runManager/target/" + runName + "?wsdl");
		HttpResponse response = httpClient.execute(httpUriRequest);
		String entityResponseString = ContentReader.read(response.getEntity().getContent());
		logger.debug("WSDL for store " + runName + " : " + entityResponseString);
		System.out.println("WSDL for store " + runName + " : " + entityResponseString);
		assertTrue(entityResponseString.startsWith("<definitions name=\"Twitter_test_runService\""));
	}
	
	/**
	 * Send the corresponding store wsdl to the scaffolding proxy to get an HTML form
	 * @param runName
	 * @throws Exception 
	 */
	private void sendWSDLToScaffolderProxy(String runName) throws Exception {
		String wsdlTestRequest = "http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/runManager/target/" + runName + "?wsdl";
		String scaffoldingProxyRequest = "http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=";
		System.out.println("wsdlTestRequest = " + wsdlTestRequest);
		System.out.println("scaffoldingProxyRequest = " + scaffoldingProxyRequest);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to generate the HTML form
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(scaffoldingProxyRequest + wsdlTestRequest);
		String form = httpClient.execute(request, responseHandler);
		logger.debug("Scaffolding proxy response = " + form);
		System.out.println("Scaffolding proxy response = " + form);
		assertTrue(form.contains("<input class=\"inputField\" name=\"user\" type=\"text\">"));
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
	
}
