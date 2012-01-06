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

import javax.xml.ws.WebServiceRef;

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
import org.easysoa.records.persistence.filesystem.ExchangeRecordFileStore;
import org.easysoa.records.ExchangeRecord;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.mock.meteomock.MeteoMock;
import org.openwide.easysoa.test.monitoring.apidetector.UrlMock;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.ow2.frascati.util.FrascatiException;

import com.openwide.easysoa.util.ContentReader;

/**
 * To test the replay function associated with an exchange record discovered by the http discovery proxy 
 * @author jguillemotte
 *
 */
public class ExchangeRecordProxyReplayTest extends AbstractProxyTestStarter {

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordProxyReplayTest.class.getName());

	// Reference to Meteo Mock service
    //@WebServiceRef(wsdlLocation="http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/helloservice/hello?wsdl") static MeteoMock service;	
	
	//@Before
	@BeforeClass
	public static void setUp() throws FrascatiException {
		// clean the old exchange records files
		cleanOldFiles();
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP proxy
		startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		// Start mock services
		startMockServices(false);
	}	

	/**
	 * This test works with the twitter mock (REST exchanges)
	 * @throws Exception
	 */
	@Test
	public void testReplayWithRestMessages() throws Exception {

		DefaultHttpClient httpClient = new DefaultHttpClient();		
		
		// Start a new Run
		HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/Test_Run");
		assertEquals("Run 'Test_Run' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));
		
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

		// get a list of recorded exchange store
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordStorelist");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		logger.debug("Exchange record store list response : " + entityResponseString);
		assertTrue(entityResponseString.contains("Test_Run"));
		
		// Get an exchange record
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecord/Test_Run/1");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		logger.debug("Exchange record response : " + entityResponseString);
		assertTrue(entityResponseString.contains("{\"exchangeRecord\":{\"exchange\":{\"exchangeID\":1"));
		
		// get a list of recorded exchanges contained in the Test_Run folder
		httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/getExchangeRecordList/Test_Run");
		//logger.debug("Sending request");
		response = httpClient.execute(httpUriRequest);
		//logger.debug("Reading response");
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		//logger.debug("Exchange record list response : " + entityResponseString);
		//assertTrue(entityResponseString.contains("ExchangeID"));

		// replay one or several exchanges
		logger.debug("Calling Replay service ...");
		ExchangeRecordFileStore fileStore= new ExchangeRecordFileStore();
		
		String originalResponse;
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist("Test_Run");
		ExchangeRecord record = recordList.get(0); 
		//for(ExchangeRecord record : recordList){
			originalResponse = record.getOutMessage().getMessageContent().getContent();
			httpUriRequest = new HttpGet("http://localhost:" + EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT + "/replay/Test_Run/" + record.getExchange().getExchangeID());
			response = httpClient.execute(httpUriRequest);
			entityResponseString = ContentReader.read(response.getEntity().getContent());
			logger.debug("Replayed ExchangeRecord response : " + entityResponseString);
			
			// Compare the replayed exchange with the original exchange
			assertEquals(originalResponse, entityResponseString);
		//}
	}
	
	/**
	 * This test works with the Meteo mock (SOAP WSDL exchanges)
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testReplayWithSoapMessages() throws Exception {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		
		// Start a new Run
		HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/Meteo_WSDL_TestRun");
		assertEquals("Run 'Meteo_WSDL_TestRun' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));		
		
		// send requests to the meteo mock through the HTTP proxy
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		
/**		

import javax.xml.ws.WebServiceRef;
import helloservice.endpoint.HelloService;
import helloservice.endpoint.Hello;

public class HelloClient {
    @WebServiceRef(wsdlLocation="http://localhost:8080/
            helloservice/hello?wsdl")
    static HelloService service;

    public static void main(String[] args) {
        try {
            HelloClient client = new HelloClient();
            client.doTest(args);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doTest(String[] args) {
        try {
            System.out.println("Retrieving the port from
                     the following service: " + service);
            Hello port = service.getHelloPort();
            System.out.println("Invoking the sayHello operation
                     on the port.");

            String name;
            if (args.length > 0) {
                name = args[0];
            } else {
                name = "No Name";
            }

            String response = port.sayHello(name);
            System.out.println(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
		
**/	
		// TODO :
		// OK modifications to have a fully functional soap meteo mock
		// - use a soap wsdl client in this test to get at least a request/response record for the meteo mock
		// - check that the exchange record is well created and filled with soap request/response data
		// - Test the replay function with a soap exchange record
		
		HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		UrlMock urlMock = new UrlMock();
		HttpResponse response;
		String entityResponseString;
		HttpUriRequest httpUriRequest;
		for(String cityName : urlMock.getMeteoMockCities()){
			/*
			logger.info("Request send : " + url);
			httpUriRequest = new HttpGet(url);
			response = httpProxyClient.execute(httpUriRequest);
			// Need to read the response body entierely to be able to send another request
			entityResponseString = ContentReader.read(response.getEntity().getContent());
			*/
		}		
		
		
	}
	
	/**
	 * This test do nothing, just wait for a user action to stop the proxy. 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
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
    @AfterClass
    public static void cleanUp() throws FrascatiException{
    	logger.info("Stopping FraSCAti...");
    	stopFraSCAti();
    }	
    
    /**
     * Delete the old exchange record file remaining in target path
     */
    protected static void cleanOldFiles(){
    	File folder = new File("target/");
    	File[] listOfFiles = folder.listFiles();
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
