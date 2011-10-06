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
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;
import org.easysoa.EasySOAConstants;

public class XsltFormGeneratorTest extends AbstractTest {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(XsltFormGeneratorTest.class.getClass());
    
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
		//frascati.processComposite("src/test/resources/talendAirportServiceMock.composite", new ProcessingContextImpl());
		// Start Scaffolding Proxy test
		//startScaffoldingProxyComposite();
		frascati.processComposite("src/main/resources/xsltScaffoldingProxy.composite", new ProcessingContextImpl());
	}

	/**
	 * Test the HTML form generation
	 * @throws Exception If a problem occurs
	 */
	@Test
	public void testFormGenerator() throws Exception {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to generate the HTML form
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String form = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=http://localhost:8086/soapServiceMock?wsdl"), responseHandler);
		logger.debug("response = " + form);
		// Compare it to a registered one
		assertThat(form, JUnitMatchers.containsString("<b>SoapServiceMock</b>"));		
		assertThat(form, JUnitMatchers.containsString("<b>getPrice</b>"));
		//assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_ordersNumber\" disabled name=\"ordersNumber\" type=\"text\">"));		
	}

	@Test
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
	 * Test the HTML form generation
	 * @throws Exception If a problem occurs
	 */
	@Test
	public void testFormGeneratorTalend() throws Exception {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to generate the HTML form
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String form = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=http://localhost:8200/esb/AirportService?wsdl"), responseHandler);
		logger.debug("response = " + form);
		// Compare it to a registered one
		//assertThat(form, JUnitMatchers.containsString("<b>AirportService</b>"));
		assertThat(form, JUnitMatchers.containsString("<b>TalendTutoServiceMockFcInItfService</b>")); // TODO #23 FraSCAti can't mock a specific WSDL (because no JAXWS annotations support)		
		assertThat(form, JUnitMatchers.containsString("<b>getAirportInformationByISOCountryCode</b>"));
		//assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_ordersNumber\" disabled name=\"ordersNumber\" type=\"text\">"));		
	}

	@Test
	public final void testRestSoapProxyTalend() throws ClientProtocolException, IOException{
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to test the rest/soap proxy
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		//String resp = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/AirportServiceMockSoapBinding/getPrice/?arg0=patatoes&arg1=25&wsdlUrl=http://localhost:8200/esb/AirportService"), responseHandler);
		String resp = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/TalendTutoServiceMockFcInItfServiceSoapBinding/getAirportInformationByISOCountryCode/?CountryAbbrviation=FR&wsdlUrl=http://localhost:8200/esb/AirportService"), responseHandler); // TODO #23 FraSCAti can't mock a specific WSDL (because no JAXWS annotations support)
		logger.debug("response = " + resp);		
		// Compare the result with the expected one
		assertEquals("{\"Body\":{\"getAirportInformationByISOCountryCodeResponse\":{\"getAirportInformationByISOCountryCodeResult\":\"No informations about his country !\"}}}", resp);
	}
	
}
