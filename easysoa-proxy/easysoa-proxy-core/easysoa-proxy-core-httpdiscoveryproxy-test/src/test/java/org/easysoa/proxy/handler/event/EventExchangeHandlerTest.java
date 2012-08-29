/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.handler.event;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.proxy.core.api.event.Condition;
import org.easysoa.proxy.core.api.event.IEventMessageHandler;
import org.easysoa.proxy.core.api.event.RegexCondition;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.test.mock.nuxeo.RecordsProvider;
import org.easysoa.test.util.AbstractProxyTestStarter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests Event exchange handler
 * 
 * @author fntangke
 */

public class EventExchangeHandlerTest extends AbstractProxyTestStarter {

	private static Logger logger = Logger
			.getLogger(EventExchangeHandlerTest.class.getName());

	private String eventMessageHandlerAdminUrl = "http://localhost:8084/SubscriptionWebService/subscriptions";

	/**
	 * Initialize one time the remote systems for the test FraSCAti and HTTP
	 * discovery Proxy ...
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		logger.info("Launching FraSCAti and REST mock");
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP Proxy
		// startHttpDiscoveryProxy("servicesToLaunchMock.composite", new
		// URL("file:///home/fntangke/workspace/EasySOA/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-api-frascati/target/classes"));
		startHttpDiscoveryProxy("servicesToLaunchMock.composite"/*
																 * , new URL(
																 * "file:///home/fntangke/workspace/EasySOA/easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-api-frascati/target/easysoa-proxy-core-api-frascati-0.5-SNAPSHOT.jar"
																 * )
																 */);
		// startHttpDiscoveryProxy(composite, urls)
	}

	@Test
	public void testEmpty() throws ClientProtocolException, IOException,
			FraSCAtiServiceException {
		// using empty event map
		String urlToListen = "http://localhost:8080/";
		// call a service to listen
		doGet(urlToListen);
		frascati.getComposite("servicesToLaunchMock");
		List<ExchangeRecord> records = frascati.getService(
				"servicesToLaunchMock", "service1ToLaunchMockRecordsProvider",
				RecordsProvider.class).getRecords();
		System.out.println("Longueurs des records : "
				+ Integer.toString(records.size()));
		// 1. check that no service launched when no conf
		// assertEquals(records.size(), 0);
	}

	@Test
	public void defaultSubscriptionsTest() throws FraSCAtiServiceException,
			ClientProtocolException, IOException {

		doGet(this.eventMessageHandlerAdminUrl);

		System.in.read();
		// do get on admin URL, display it
		// TODO

		// TODO check it is as expected
	}

	/**
	 * Print the current monitoring configuration in the console
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Test
	public void getMonitoringConfig() throws ClientProtocolException,
			IOException {
		doGet(this.eventMessageHandlerAdminUrl);
		// System.in.read();
	}

	/**
	 * This test sent a xml configuration file to the Subscription Web Service
	 * and get all the Monitoring configuration
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	@Test
	public void updateConfXml() throws ClientProtocolException, IOException {

		String bodyContent = "<configuration><proxy>Mon Poxy UK</proxy><subscriptions><regexCondition><regex>httpgorgreggle</regex></regexCondition><launchedservices><url>wwetw.geckogeek.fr/lel</url></launchedservices></subscriptions></configuration>";
		String result = this.confPoster(bodyContent, "xml");
		assertTrue(result.contains("Mon Poxy UK"));
		assertTrue(result.contains("httpgorgreggle"));

	}

	/**
	 * This test sent a json configuration file to the Subscription Web Service
	 * and get all the Monitoring configuration
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Test
	public void updateConfJson() throws ClientProtocolException, IOException {
		
		String bodyContent = "{\"configuration\":{\"subscriptions\":{\"regexCondition\":{\"regex\":\"http:mail.fr\"},\"launchedservices\":[{\"url\":\"http:Yahoo.fr\"},{\"url\":\"http:Yahorgeo.fr\"}]},\"proxy\":\"proxy de test http:efef?swdl\"}}";
		String result = this.confPoster(bodyContent, "json");
		assertTrue(result.contains("http:mail.fr"));
		assertTrue(result.contains("http:Yahorgeo.fr"));

	}

	/**
	 * Set the apropriate headers and makes the post request
	 * 
	 * @param bodyContent
	 * @param format
	 *            "xml" or "json"
	 * @return the json or xml result of the web service request, it depends of
	 *         the format
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String confPoster(String bodyContent, String format)
			throws ClientProtocolException, IOException {
		String result = null;
		if (format.equals("xml"))
			result = doPost(this.eventMessageHandlerAdminUrl, bodyContent,
					"xml");
		if (format.equals("json"))
			result = doPost(this.eventMessageHandlerAdminUrl, bodyContent,
					"json");
		if ((!format.equals("xml")) && (!format.equals("json")))
			result = doPost(this.eventMessageHandlerAdminUrl, bodyContent,
					"xml");
		return result;
	}

	@Test
	public void udpateSubscriptionsTest() throws FraSCAtiServiceException,
			ClientProtocolException, IOException {

		// EventMessageHandler eventMessageHandler = new EventMessageHandler();
		// eventMessageHandler.updateSubscription(subscription, subscriptionId)
		// frascati.g

		// Subscriptions subscriptions =
		// frascati.getService("servicesToLaunchMock",
		// "ISubscriptionWebService",
		// ISubscriptionWebService.class).getSubscriptions();

		String emptySubscriptionsJson = "{'subscriptions':''}";
		doPost(eventMessageHandlerAdminUrl, emptySubscriptionsJson, "json");
		// TODO LATER GET & check
		// TODO get map and check
		String someSubscriptionsJson = "{'subscriptions':{'subscriptions':{'launchedservices':[{'id':159,'url':'http://Yahoo.fr'},{'id':15,'url':'http://Yahorgeo.fr'}],'listenedservices':{'id':17,'url':'http://amazon.fr'}}}}";
		doPost(eventMessageHandlerAdminUrl, someSubscriptionsJson, "json");
		// TODO LATER GET & check
		// TODO get map and check
		// Subscriptions verif = frascati.getService("servicesToLaunchMock",
		// "testons", SubscriptionWebServiceInterface.class).getSubscriptions();
	}

	/**
	 * {"monitoringConfiguration":{"configurations":{"subscriptions":{
	 * "regexCondition":{"regex":"httpgorgreggle"},"launchedservices":{"url":
	 * "wwetw.geckogeek.fr\/lel"}},"proxy":"Mon Poxy UK"}}}
	 * 
	 * 
	 */

	/**
	 * Post Xml or Json request
	 * 
	 * @param url
	 * @param messageContent
	 * @param contentType
	 *            "xml" or "json"
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String doPost(String url, String messageContent, String contentType)
			throws ClientProtocolException, IOException {

		String contentTypeValue = null;
		String acceptTypeValue = null;

		if ((!contentType.equals("xml")) && (!contentType.equals("json"))) {
			contentTypeValue = "application/xml; charset=UTF-8";
			acceptTypeValue = "application/xml";
		} else {
			contentTypeValue = "application/".concat(contentType).concat(
					"; charset=UTF-8");
			acceptTypeValue = "application/".concat(contentType);
		}
		// TODO Auto-generated method stub

		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();

		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost",
				EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		// Send a request to
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", acceptTypeValue);
		httpPost.setHeader("Content-Type", contentTypeValue);
		httpPost.setEntity(new StringEntity(messageContent));
		httpProxyClient.execute(httpPost, responseHandler);

		return httpProxyClient.execute(httpPost, responseHandler);

	}

	private void doGet(String url) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// HTTP proxy Client
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		// Set client to use the HTTP Discovery Proxy
		HttpHost proxy = new HttpHost("localhost",
				EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		// Send a request to
		httpProxyClient.execute(new HttpGet(url), responseHandler);
	}

	@Test
	public void testWithSome() throws ClientProtocolException, IOException,
			FraSCAtiServiceException, InterruptedException {

		String urlToListen = "http://localhost:8080/another";
		// set some conf
		Map<List<Condition>, List<String>> listenedServiceUrlToServicesToLaunchUrlMap = new HashMap<List<Condition>, List<String>>();

		ArrayList<String> value = new ArrayList<String>();

		value.add("http://localhost:8081/");
		value.add("http://localhost:8080/2");
		value.add("http://localhost:8080/3");

		List<Condition> listCompiledCondition = new ArrayList<Condition>();
		listCompiledCondition.add(new RegexCondition(urlToListen));
		listenedServiceUrlToServicesToLaunchUrlMap.put(listCompiledCondition,
				value);

		frascati.getService("servicesToLaunchMock", "IEventMessageHandler",
				IEventMessageHandler.class)
				.setListenedServiceUrlToServicesToLaunchUrlMap(
						listenedServiceUrlToServicesToLaunchUrlMap);

		String verif = frascati
				.getService("servicesToLaunchMock", "IEventMessageHandler",
						IEventMessageHandler.class)
				.getListenedServiceUrlToServicesToLaunchUrlMap().toString();

		System.out.println(" Verif: " + verif);

		// call a service to listen
		doGet(urlToListen);
		Thread.currentThread();
		System.in.read();
		// wait(1000);
		// Thread.sleep(1000);
		// Get Records
		List<ExchangeRecord> records1 = frascati.getService(
				"servicesToLaunchMock", "service1ToLaunchMockRecordsProvider",
				RecordsProvider.class).getRecords();
		System.out.println("Longueurs des records 1 : "
				+ Integer.toString(records1.size()));

		// TODO check that there is some records in the right mock (check that
		// the appropriate service(s) have been launched)
		// records1 = frascati.getService("servicesToLaunchMock",
		// "service1ToLaunchMockRecordsProvider",
		// RecordsProvider.class).getRecords();
		// assertEquals(records1.size(), 1);

		List<ExchangeRecord> records2 = frascati.getService(
				"servicesToLaunchMock", "service2ToLaunchMockRecordsProvider",
				RecordsProvider.class).getRecords();
		System.out.println("Longueurs des records 2 : "
				+ Integer.toString(records2.size()));
		// frascati.getService("servicesToLaunchMock",
		// "service2ToLaunchMockRecordsProvider", RecordsProvider.class).
		// assertEquals(records2.size(), 1);

		List<ExchangeRecord> records3 = frascati.getService(
				"servicesToLaunchMock", "service3ToLaunchMockRecordsProvider",
				RecordsProvider.class).getRecords();
		System.out.println("Longueurs des records 3 : "
				+ Integer.toString(records3.size()));
		// assertEquals(records2.size(), 1);
	}

	/**
	 * This test does nothing, just wait for a user action to stop the proxy.
	 * 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	// @Ignore
	public final void testWaitUntilRead() throws Exception {

		logger.info("Http Discovery Proxy started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Http Discovery Proxy stopped !");
	}

	/**
	 * Stop FraSCAti components
	 * 
	 * @throws FrascatiException
	 */
	@After
	public void cleanUp() throws Exception {
		logger.info("Stopping FraSCAti...");
		stopFraSCAti();
		// Clean Easysoa proxy
		cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
		// cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT);
		// cleanJetty(EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT);
		// Clean (nuxeo) mocks
		cleanJetty(8080);
		cleanJetty(8081);
	}
}