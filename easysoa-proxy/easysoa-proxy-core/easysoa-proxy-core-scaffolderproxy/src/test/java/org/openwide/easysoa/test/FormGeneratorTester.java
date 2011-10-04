package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import javax.xml.soap.SOAPException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.ow2.frascati.util.FrascatiException;
import org.easysoa.EasySOAConstants;

public class FormGeneratorTester extends AbstractTest {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FormGeneratorTester.class.getClass());
    
	/**
	 * Init the remote systems for the test
	 * Frascati and HTTP Proxy
	 * Instantiate FraSCAti and retrieve services.
	 * @throws InterruptedException
	 */
	@BeforeClass
	public static void setUp() throws FrascatiException, InterruptedException {
		// Start fraSCAti
		startFraSCAti();
		// Start the soap service mock
		startSoapServiceMockComposite();
		// Start Scaffolding Proxy test
		startScaffoldingProxyComposite();
	}	

	/**
	 * Test the HTML form generation
	 * @throws Exception If a problem occurs
	 */
	@Test
	@Ignore
	public void testFormGenerator() throws Exception {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to generate the HTML form
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String form = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=http://localhost:8086/soapServiceMock?wsdl"), responseHandler);
		logger.debug("response = " + form);
		// Compare it to a registered one
		assertThat(form, JUnitMatchers.containsString("Service : SoapServiceMock"));		
		assertThat(form, JUnitMatchers.containsString("<h3>Operations :</h3><br/>\n								<u>getPrice</u>"));
		//assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_ordersNumber\" disabled name=\"ordersNumber\" type=\"text\">"));		
	}

	@Test
	@Ignore
	public final void testRestSoapProxy() throws ClientProtocolException, IOException{
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to test the rest/soap proxy
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String resp = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/SoapServiceMockSoapBinding/getPrice/?arg0=patatoes&arg1=25&wsdlUrl=http://localhost:8086/soapServiceMock"), responseHandler);
		logger.debug("response = " + resp);		
		// Compare the result with the expected one
		assertEquals("{\"Body\":{\"getPriceResponse\":{\"return\":\"250.0\"}}}", resp);
	}
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("Scaffolding proxy test started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Scaffolding proxy test stopped !");
	}
	
}
