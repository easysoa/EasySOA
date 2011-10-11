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
 * Contact : easysoa-dev@groups.google.com
 */

package org.openwide.easysoa.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Has to be ignored for FormGeneratorTest to work, because only one test
 * using binding.http can work (because binding.http's Jetty's "unregister" is not
 * implemented, see #37)
 * 
 * @author mdutoo
 */
@Ignore
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
		componentList.add(frascati.processComposite("src/main/resources/xsltScaffoldingProxy.composite", new ProcessingContextImpl()));
	}
	
	@AfterClass
	public static void tearDown() throws FrascatiException, InterruptedException {
		logger.info("Stopping FraSCAti : " + componentList);
		stopFraSCAti();
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
		String jsonRequestContent = "{\"wsRequest\": { \"service\": \"SoapServiceMock\", \"binding\": \"SoapServiceMockSoapBinding\", \"operation\": \"getPrice\", \"wsdlUrl\": \"http://localhost:8086/soapServiceMock?wsdl\"},\"formParameters\":[{\"paramName\":\"arg0\",\"paramValue\":\"patatoes\"}, {\"paramName\":\"arg1\",\"paramValue\":\"25\"}]}";
		HttpPost request = new HttpPost("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/SoapServiceMockSoapBinding/getPrice/");
		request.setEntity(new StringEntity(jsonRequestContent));		
		String resp = httpProxyClient.execute(request, responseHandler);
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
		//String resp = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/TalendTutoServiceMockFcInItfServiceSoapBinding/getAirportInformationByISOCountryCode/?CountryAbbrviation=FR&wsdlUrl=http://localhost:8200/esb/AirportService"), responseHandler); // TODO #23 FraSCAti can't mock a specific WSDL (because no JAXWS annotations support)
		String jsonRequestContent = "{\"wsRequest\": { \"service\": \"TalendTutoServiceMockFcInItfService\", \"binding\": \"TalendTutoServiceMockFcInItfServiceSoapBinding\", \"operation\": \"getAirportInformationByISOCountryCode\", \"wsdlUrl\": \"http://localhost:8200/esb/AirportService?wsdl\"},\"formParameters\":[{\"paramName\":\"CountryAbbrviation\",\"paramValue\":\"FR\"}]}";
		HttpPost request = new HttpPost("http://localhost:" + EasySOAConstants.REST_SOAP_PROXY_PORT +"/callService/SoapServiceMockSoapBinding/getPrice/");
		request.setEntity(new StringEntity(jsonRequestContent));		
		String resp = httpProxyClient.execute(request, responseHandler);		
		logger.debug("response = " + resp);		
		// Compare the result with the expected one
		assertEquals("{\"Body\":{\"getAirportInformationByISOCountryCodeResponse\":{\"getAirportInformationByISOCountryCodeResult\":\"No informations about his country !\"}}}", resp);
	}
	
}
