package org.easysoa.test.event;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.http.client.methods.HttpGet;
import org.easysoa.EasySOAConstants;
import org.easysoa.message.Header;
import org.easysoa.message.InMessage;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.proxy.core.api.util.ContentReader;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.junit.Before;
import org.junit.Test;

public class JXPathMessageTest {

	private Object logger;
	PropertyManager propertyManager;
	ProxyFileStore erfs;
	// 1 for the get request, 2 fr the post request
	private String id;

	@Before
	public void setUpTest() throws Exception {
		this.propertyManager = new PropertyManager(
				"httpDiscoveryProxy.properties", this.getClass()
						.getResourceAsStream(
								"/" + "httpDiscoveryProxy.properties"));
		this.erfs = new ProxyFileStore();
	}

	/**
	 * Fo ty JXPath request
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		// PostRequest
		this.id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		InMessage a = exchangeRecord.getInMessage();
		System.out.println();
		// Object b =
		// context.getValue("equals(size(/companies[name='Openwide']/workers), java.lang.Integer.new('3'))");

		// Check le Path context.getValue("contains((/path),'Air')")
		// Check le remote host
		// context.getValue("contains((/remoteHost),'127.0.0.1')")
		// Check le server context.getValue("contains((/server),'localhost')")
		// Check the number of params ><
		// context.getValue("size(/queryString/queryParams)")
		// Compare le nombre de params
		// context.getValue("(size(/queryString/queryParams))=1")
		// Compare le nombre de params
		// context.getValue("(size(/queryString/queryParams))<2")
		// nombre de querypaams de name = wdsl
		// context.getValue("(size(/queryString/queryParams[name='wsdl']))")
		// comparaison par raport a la taille du message content
		// context.getValue("(/messageContent/size)>1000")
		// check headers
		// context.getValue("(/headers/headerList[name='User-Agent'])")
		// context.getValue("(/headers/headerList[name='Authorization'])") ou
		// ...
		// context.getValue("(/headers/headerList[name='Content-Length']/value)")
		// context.getValue("(/headers/headerList[name='Content-Type'])")
		// context.getValue("(/headers/headerList[name='Content-Type']/value)")
		// context.getValue("/messageContent/contentType")
		// context.getValue("/messageContent/mimeType")
		// Contenu du message context.getValue("/messageContent/rawContent")
		// on verifie le contenu du result
		// context.getValue("contains((/messageContent/rawContent), 'soap')")

		// org.apache.commons.codec.binary.Base64.enc

		JXPathContext context = JXPathContext.newContext(a);
		Object o = context.getValue(".");

		System.out.println(((InMessage) o).getPort());

		/*
		 * 
		 * connection.setRequestProperty("Content-type",
		 * "application/x-www-form-urlencoded");import
		 * org.apache.commons.codec.binary.Base64;
		 * connection.setRequestProperty("Authorization", "Basic " +
		 * Base64.encodeBase64String((username + ":" + password).getBytes()));
		 */
		// logger.debug("Request content from saved record : " +
		// exchangeRecord.getExchange().getExchangeID());
		// Check the exchange id
		// assertEquals(id, exchangeRecord.getExchange().getExchangeID());
		// Check the InMessage URL
		// assertEquals(record.getInMessage().buildCompleteUrl(),
		// exchangeRecord.getInMessage().buildCompleteUrl());

		a.getHeaders().addHeader(
				new Header("Authorization",
						"Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="));

		// context.getValue("startsWith(/headers/headerList[name='Authorization']/@value, 'Basic ') and substring(/headers/headerList[name='Authorization']/@value, 6)");
		context.getValue("split(java.lang.String.new(org.apache.commons.codec.binary.Base64.decodeBase64(substring(/headers/headerList[name='Authorization']/@value, 6))), ':')");
	}

	/**
	 * Check if the sze if under 1000
	 * @throws Exception
	 */
	@Test
	public void messageCounterSizeUnder1000() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		InMessage inMessage = exchangeRecord.getInMessage();
		System.out.println();

		JXPathContext context = JXPathContext.newContext(inMessage);
		System.out.println(context.getValue("(/messageContent/size)<1000"));
	}

	/**
	 * Check if the remote Host is 127.0.0.1
	 * @throws Exception
	 */
	@Test
	public void remoteHostLocalhost() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		InMessage inMessage = exchangeRecord.getInMessage();
		System.out.println();

		JXPathContext context = JXPathContext.newContext(inMessage);
		System.out.println(context
				.getValue("contains((/remoteHost),'127.0.0.1')"));
	}
	/**
	 * Check if the message content contains '<soap' so it could be a soap request
	 * @throws Exception
	 */
	@Test
	public void checkifSoapRequest() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		InMessage inMessage = exchangeRecord.getInMessage();
		System.out.println();

		JXPathContext context = JXPathContext.newContext(inMessage);
		System.out.println(context
				.getValue("contains((/messageContent/rawContent), '<soap')"));
	}

	/**
	 *  Check if the request has more than 5 query params
	 * @throws Exception
	 */
	@Test
	public void moreThanFiveParams() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		InMessage inMessage = exchangeRecord.getInMessage();
		System.out.println();

		JXPathContext context = JXPathContext.newContext(inMessage);
		System.out.println(context
				.getValue("(size(/queryString/queryParams))>5"));
	}

}
