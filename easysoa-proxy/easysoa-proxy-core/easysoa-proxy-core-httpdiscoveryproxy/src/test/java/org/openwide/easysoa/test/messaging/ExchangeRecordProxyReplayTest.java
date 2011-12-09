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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ExchangeRecordFileStore;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
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

	@Before
	public void setUp() throws FrascatiException {
		// clean the old exchange records files
		cleanOldFiles();
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP proxy
		startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
		// Start mock services
		startMockServices(false);
	}	

	@Test
	public void testReplay() throws Exception {
	
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

		// get a list of recorded exchanges
		logger.debug("Calling list service ...");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpUriRequest = new HttpGet("http://localhost:8085/getExchangeRecordlist?storeName=");
		response = httpClient.execute(httpUriRequest);
		entityResponseString = ContentReader.read(response.getEntity().getContent());
		
		// problem with this log, log is aggregated on the same line and hang the test
		//logger.debug("List response : " + entityResponseString);
		assertTrue(entityResponseString.contains("exchangeID"));
		
		// replay one or several exchanges
		logger.debug("Calling Replay service ...");
		// How to get an ID to replay ? one solution is to read an id from the record list
		ExchangeRecordFileStore fileStore= new ExchangeRecordFileStore();
		
		String recordID;
		String originalResponse;
		List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(""); 
		
		for(ExchangeRecord record : recordList){
			recordID = record.getExchangeID();
			originalResponse = record.getOutMessage().getMessageContent().getContent();
			httpUriRequest = new HttpGet("http://localhost:8085/replay?exchangeRecordStoreName=&exchangeRecordId=" + record.getExchangeID());
			response = httpClient.execute(httpUriRequest);
			entityResponseString = ContentReader.read(response.getEntity().getContent());
			logger.debug("Replayed ExchangeRecord response : " + entityResponseString);
			
			// Compare the replayed exchange with the original exchange
			assertEquals(originalResponse, entityResponseString);
		}
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
    protected void cleanOldFiles(){
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
