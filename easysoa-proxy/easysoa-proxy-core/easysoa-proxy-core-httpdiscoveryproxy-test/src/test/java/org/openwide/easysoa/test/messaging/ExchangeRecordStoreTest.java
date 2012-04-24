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

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.openwide.easysoa.test.util.UrlMock;

import com.openwide.easysoa.message.Header;
import com.openwide.easysoa.message.Headers;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.ContentReader;

/**
 * To test the messaging API. Only the save, load, replay functions without any integration with the HttpDiscoveryproxy.
 * @author jguillemotte
 *
 */
public class ExchangeRecordStoreTest extends AbstractProxyTestStarter {

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordStoreTest.class.getName());

	// TODO : update to test list and replay services

	/**
	 * Start FraSCAti and mock services
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception{
		// Start fraSCAti
		startFraSCAti();
		// Start mock services
		startMockServices(false, true, true);
	}
	
	/**
	 * Technical test
	 * Test the Exchange record API without proxy 
	 * @throws Exception If a problem occurs
	 */
	@Test
    public void saveAndReadRecordsTest() throws Exception {
	    ProxyFileStore erfs = new ProxyFileStore();
    	ExchangeRecord exchangeRecord;

    	ExchangeRecordStore recordList = new ExchangeRecordStore("TEST Enviroment");
    	// TODO maybe better to use an other ID generator (ordered) inside the messaging API ?
    	String id;

		// Send Http Rest requests
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		UrlMock urlMock = new UrlMock();
		HttpResponse response;
		HttpUriRequest httpUriRequest;
		for(String url : urlMock.getTwitterUrlData("localhost:" + EasySOAConstants.TWITTER_MOCK_PORT)){
			logger.info("Request send : " + url);			
			try {
				httpUriRequest = new HttpGet(url);
				response = httpProxyClient.execute(httpUriRequest);
				id = UUID.randomUUID().toString();				

				// In message - message filling is not complete
				InMessage inMessage = new InMessage();
				inMessage.setPath(httpUriRequest.getURI().getPath());
				inMessage.setMethod(httpUriRequest.getMethod());
				inMessage.setServer(httpUriRequest.getURI().getHost());
				inMessage.setPort(httpUriRequest.getURI().getPort());
				Headers headers = new Headers();
				for(org.apache.http.Header httpHeader : httpUriRequest.getAllHeaders()){
					headers.addHeader(new Header(httpHeader.getName(), httpHeader.getValue()));
				}
				inMessage.setHeaders(headers);
				
				// OUT message - message filling is not complete
				OutMessage outMessage = new OutMessage();
				MessageContent outMessageContent = new MessageContent();
				outMessageContent.setSize(response.getEntity().getContentLength());
				outMessageContent.setRawContent(ContentReader.read(response.getEntity().getContent()));
				outMessageContent.setMimeType(response.getEntity().getContentType().getValue());
				outMessage.setMessageContent(new MessageContent());
				outMessage.setProtocol(response.getProtocolVersion().getProtocol());
				outMessage.setProtocolVersion(response.getProtocolVersion().getMajor() + "." + response.getProtocolVersion().getMinor());
				outMessage.setStatus(response.getStatusLine().getStatusCode());
				outMessage.setStatusText(response.getStatusLine().getReasonPhrase());
				
		    	// Save the exchange
		    	recordList.add(new ExchangeRecord(id ,inMessage, outMessage));
			}
			catch (HttpResponseException ex){
				logger.info("Error occurs, status code : " + ex.getStatusCode() + ", message : " + ex.getMessage(), ex);
			}
		}    	

    	// Save, reload and check the exchange records
    	for(ExchangeRecord record : recordList){
    		try{
    			id = erfs.save(record);
    			logger.debug("record ID = " + id);
    			exchangeRecord = erfs.loadExchangeRecord("", id, false);
    			logger.debug("Request content from saved record : " + exchangeRecord.getExchange().getExchangeID());
    			// Check the exchange id
    			assertEquals(id, exchangeRecord.getExchange().getExchangeID());
    			// Check the InMessage URL
    			assertEquals(record.getInMessage().buildCompleteUrl(), exchangeRecord.getInMessage().buildCompleteUrl());
    		}
    		catch(Exception ex){
    			ex.printStackTrace();
    		}
    	}
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
    }	
	
}
