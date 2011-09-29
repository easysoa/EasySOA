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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.ow2.frascati.util.FrascatiException;

/**
 * The goal of this test class is to make individuals tests for several different WSDl files and to check compatibility with the scaffolding proxy,
 * especially with the form generator and the XSLT transformation
 *  
 * @author jguillemotte
 */
public class WsdlFileTester extends AbstractTest {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(WsdlFileTester.class.getName());	
	
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
		// Start Scaffolding Proxy test
		startScaffoldingProxyComposite();
	}	
	
	/**
	 * Pure Air Flowers sample
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void testPureAirFlowersWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("PureAirFlowers.wsdl");
		// Check if form contains all the necessary stuff 
		assertThat(form, JUnitMatchers.containsString("Service : PureAirFlowers"));		
		assertThat(form, JUnitMatchers.containsString("Binding : <b>PureAirFlowersSoapBinding"));
		assertThat(form, JUnitMatchers.containsString("<h3>Operations :</h3><br/>\n								<u>getOrdersNumber</u>"));
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
		assertThat(form, JUnitMatchers.containsString("Service : Trip"));		
		assertThat(form, JUnitMatchers.containsString("Binding : <b>TripSoapBinding"));
		assertThat(form, JUnitMatchers.containsString("Operations :</h3><br/>\n								<u>process"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"arg2\" type=\"text\">"));
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
		assertThat(form, JUnitMatchers.containsString("Service : airport"));
		assertThat(form, JUnitMatchers.containsString("Binding : <b>airportSoap"));
		assertThat(form, JUnitMatchers.containsString("Operations :</h3><br/>\n								<u>getAirportInformationByISOCountryCode"));		
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"CountryAbbrviation\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_getAirportInformationByISOCountryCodeResult\" disabled name=\"getAirportInformationByISOCountryCodeResult\" type=\"text\">"));
	}
	
	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests	
	@Test
	public void testMicrosoftTranslatorWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("microsoftTranslatorWebService.svc.wsdl");
		assertThat(form, JUnitMatchers.containsString("Service : SoapService"));
		assertThat(form, JUnitMatchers.containsString("Binding : <b>BasicHttpBinding_LanguageService"));
		assertThat(form, JUnitMatchers.containsString("Operations :</h3><br/>\n								<u>GetLanguages"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"text\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_DetectResult\" disabled name=\"DetectResult\" type=\"text\">"));		
	}

	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests	
	@Test
	public void testCurrencyServiceWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("currencyserverwebservice.asmx.wsdl");
		assertThat(form, JUnitMatchers.containsString("Service : CurrencyServerWebService"));
		assertThat(form, JUnitMatchers.containsString("Binding : <b>CurrencyServerWebServiceSoap"));
		assertThat(form, JUnitMatchers.containsString("Operations :</h3><br/>\n								<u>getDataSet"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"provider\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_getDataSetResult\" disabled name=\"getDataSetResult\" type=\"text\">"));		
	}
	
	// This test fails because complex types are not yet supported in the XSLT transformation
	// It is here for future tests
	@Test
	public void testGlobalWeatherWsdl() throws ClientProtocolException, IOException{
		String form = SendWsdlToFormGenerator("globalweather.asmx.wsdl");
		assertThat(form, JUnitMatchers.containsString("Service : GlobalWeather"));
		assertThat(form, JUnitMatchers.containsString("Binding : <b>GlobalWeatherSoap"));
		assertThat(form, JUnitMatchers.containsString("Operations :</h3><br/>\n								<u>GetWeather"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"inputField\" name=\"CountryName\" type=\"text\">"));
		assertThat(form, JUnitMatchers.containsString("<input class=\"outputField\" id=\"return_GetWeatherResult\" disabled name=\"GetWeatherResult\" type=\"text\">"));		
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
