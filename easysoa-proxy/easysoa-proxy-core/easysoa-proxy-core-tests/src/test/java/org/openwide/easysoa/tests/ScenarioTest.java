/**
 * 
 */
package org.openwide.easysoa.tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwide.easysoa.tests.helpers.AbstractTestHelper;
import org.ow2.frascati.util.FrascatiException;

import com.openwide.easysoa.util.ContentReader;

/**
 * @author jguillemotte
 *
 */
public class ScenarioTest extends AbstractTestHelper {

	// Logger
	private static Logger logger = Logger.getLogger(ScenarioTest.class.getName());

	@BeforeClass
	public static void setUp() throws FrascatiException {
		// Start fraSCAti
		startFraSCAti();
		// Start Http Discovery proxy
		startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		startScaffolderProxy("scaffoldingProxy.composite");
		// Start mock services
		startMockServices(false);
	}
	
	/**
	 * test the integration between HTTP discovery proxy and Scaffolder proxy
	 */
	@Test
	public void scenarioTest() throws Exception {	
		
		String twitterTestRunName = "Twitter_test_run";
		String meteoTestRunName = "Meteo_test_run";

		// Create the twitter test run
		createTestRun(twitterTestRunName);
		
		// Create the Meteo test run
		// TODO : For WSDL TESTS
		//createTestRun(meteoTestRunName);
		
		// Call the replay service
		
		// Call the TemplateDefinitionService to generate template and fld files
		
		// Call the service to get the tore template list
		
		// Choose a store and call the service to get the corresponding WSDL
		
		// Give it to the scaffolding proxy to get the corresponding HTML form
		
		// Execute an operation and check the result
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
	
}
