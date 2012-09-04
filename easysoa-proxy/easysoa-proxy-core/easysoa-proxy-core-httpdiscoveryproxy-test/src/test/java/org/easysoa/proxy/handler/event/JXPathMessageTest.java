package org.easysoa.proxy.handler.event;

import static org.junit.Assert.*;

import org.apache.commons.jxpath.JXPathContext;
import org.easysoa.message.Header;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.records.ExchangeRecord;
import org.junit.Assert;
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
	 * TODO update on exchangeRecord To try JXPath request
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		// PostRequest
		this.id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		exchangeRecord.getInMessage().getHeaders().addHeader(
				new Header("Authorization",
						"Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="));
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

		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		Object o = context.getValue(".");

		System.out.println(((ExchangeRecord) o).getInMessage().getPort());

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

		Assert.assertTrue((Boolean) context
				.getValue("split(java.lang.String.new(org.apache.commons.codec.binary.Base64.decodeBase64(substring(/inMessage/headers/headerList[name='Authorization']/@value, 6))), ':')[1]='Aladdin'"));
	}

	/**
	 * Check if the sze if under 1000
	 * 
	 * @throws Exception
	 */
	@Test
	public void messageCounterSizeUnder1000() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		System.out.println();

		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		// System.out.println(context.getValue("(/messageContent/size)<1000"));
		Assert.assertTrue((Boolean) context
				.getValue("(/inMessage/messageContent/size)<1000"));
	}

	/**
	 * TODO update on exchangeRecord Check if the remote Host is 127.0.0.1
	 * 
	 * @throws Exception
	 */
	@Test
	public void remoteHostLocalhost() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		System.out.println();

		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		;
		Assert.assertTrue((Boolean) context
				.getValue("contains((/inMessage/remoteHost),'127.0.0.1')"));
	}

	/**
	 * TODO update on exchangeRecord Check if the message content contains
	 * '<soap' so it could be a soap request
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkifSoapRequest() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		System.out.println();

		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		Assert.assertTrue((Boolean) context
				.getValue("contains((/inMessage/messageContent/rawContent), '<soap')"));
	}

	/**
	 * TODO update on exchangeRecord Check if the request has no more than 5 query
	 * params
	 * 
	 * @throws Exception
	 */
	@Test
	public void noMoreThanFiveParams() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		Assert.assertTrue((Boolean) context
				.getValue("(size(/inMessage/queryString/queryParams))<5"));
	}

	/**
	 * check the size for a outmessage
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkTheOutmessageSize() throws Exception {
		// PostRequest
		String id = "2";
		ExchangeRecord exchangeRecord = erfs.loadExchangeRecord("pafStore", id,
				false);
		JXPathContext context = JXPathContext.newContext(exchangeRecord);
		Assert.assertTrue((Boolean) context
				.getValue("(/outMessage/messageContent/size)<1000"));
	}

}
