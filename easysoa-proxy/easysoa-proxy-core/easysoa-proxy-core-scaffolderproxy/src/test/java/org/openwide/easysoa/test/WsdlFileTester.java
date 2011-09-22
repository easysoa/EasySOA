/**
 * 
 */
package org.openwide.easysoa.test;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

/**
 * The goal of this test class is to make individuals tests for several different WSDl files and to check compatibility with the scaffolding proxy,
 * especially with the form generator and the XSLT transformation
 *  
 * @author jguillemotte
 */
public class WsdlFileTester {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WsdlFileTester.class.getName());	
	
	/**
	 * Pure Air Flowers sample
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void testPureAirFlowersWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("PureAirFlowers.wsdl");
		// Check if form contains all the necessary stuff 
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>getOrdersNumber</b>"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"ClientId\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_ordersNumber\" disabled name=\"ordersNumber\" type=\"text\">"));
	}
	
	/**
	 * Smart travel sample
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void testSmartTravelWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("GalaxyTrip.wsdl");
		// Check if form contains all the necessary stuff
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>process</b>"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"arg0\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"arg1\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_return\" disabled name=\"return\" type=\"text\">"));		
	}
	
	/**
	 * Talend Airport service sample 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void testAiportServiceWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("airport_soap.wsdl");
		// Check if form contains all the necessary stuff
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>getAirportInformationByISOCountryCode</b>"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"CountryAbbrviation\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_getAirportInformationByISOCountryCodeResult\" disabled name=\"getAirportInformationByISOCountryCodeResult\" type=\"text\">"));
	}
	
	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests	
	@Test
	@Ignore
	public void testMicrosoftTranslatorWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("microsoftTranslatorWebService.svc.wsdl");
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>GetLanguages</b>"));
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>Translate</b>"));
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>Detect</b>"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_TranslateResult\" disabled name=\"TranslateResult\" type=\"text\">"));		
	}

	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests	
	@Test
	@Ignore
	public void testCurrencyServiceWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("currencyserverwebservice.asmx.wsdl");
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>getCurrencyValue</b>"));
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>getProviderList</b>"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_getCurrencyValueResult\" disabled name=\"getCurrencyValueResult\" type=\"text\">"));		
	}
	
	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests
	@Test
	@Ignore
	public void testGlobalWeatherWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("globalweather.asmx.wsdl");
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>GetWeather</b>"));
		assertThat(form, JUnitMatchers.containsString("Operation\n\t\t\t\t<b>GetCitiesByCountry</b>"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_GetCitiesByCountryResult\" disabled name=\"GetCitiesByCountryResult\" type=\"text\">"));		
	}
	
	/**
	 * Send a request to the scaffolding proxy to generate an HTML form from the given WSDl file
	 * @param wsdlFileName The WSDL file to test
	 * @return The response returned by the form generator
	 * @throws ClientProtocolException if the scaffolding proxy cannot be reached
	 * @throws IOException if a problem occurs when the file is read
	 */
	private String SendWsdlToFormGenerator(String wsdlFileName) throws ClientProtocolException, IOException{
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String request = "http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=file://" + System.getProperty("user.dir") + "/src/test/resources/" + wsdlFileName;
		logger.debug("Sending request : " + request);
		// Send a request to generate the HTML form
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String resp = httpProxyClient.execute(new HttpGet(request), responseHandler);		
		//logger.debug("response = " + resp);
		return resp;
	}
	
}
