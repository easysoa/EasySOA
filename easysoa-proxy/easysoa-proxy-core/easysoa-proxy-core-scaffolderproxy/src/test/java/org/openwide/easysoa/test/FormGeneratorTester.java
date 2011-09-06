package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;
import org.easysoa.EasySOAConstants;

public class FormGeneratorTester {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FormGeneratorTester.class.getClass());
	
	/** The FraSCAti platform */
    private static FraSCAti frascati;	
	
    // Set system properties for FraSCAti
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}   
    
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
	public void testFormGenerator() throws Exception {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// Send a request to generate the HTML form
		DefaultHttpClient httpProxyClient = new DefaultHttpClient();
		String resp = httpProxyClient.execute(new HttpGet("http://localhost:" + EasySOAConstants.HTML_FORM_GENERATOR_PORT + "/scaffoldingProxy/?wsdlUrl=http://localhost:8086/soapServiceMock?wsdl"), responseHandler);
		logger.debug("response = " + resp);
		// Compare it to a registered one
		assertEquals(readResponseFile("src/test/resources/testFiles/testForm.html"), resp.replace("\n", ""));
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
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public final void testWaitUntilRead() throws Exception{
		logger.info("Scaffolding proxy test started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Scaffolding proxy test stopped !");
	}
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	private static void startFraSCAti() throws FrascatiException{
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start Velocity Test
	 * @throws FrascatiException
	 */
	private static void startScaffoldingProxyComposite() throws FrascatiException{
		frascati.processComposite("src/main/resources/scaffoldingProxy.composite", new ProcessingContextImpl());
	}	

	/**
	 * Start Soap service mock
	 * @throws FrascatiException
	 */
	private static void startSoapServiceMockComposite() throws FrascatiException{
		frascati.processComposite("src/test/resources/soapServiceMock.composite", new ProcessingContextImpl());
	}	
	
	/**
	 * Read a response file and returns the content 
	 * @return The content of the response file, an error message otherwise
	 * @throws Exception 
	 */
	private String readResponseFile(String responseFileUri) throws Exception{
		try {
			File responseFile;
			responseFile = new File(responseFileUri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(responseFile)));
			StringBuffer response = new StringBuffer();
			while(reader.ready()){
				response.append(reader.readLine());
			}
			return response.toString();	
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}	
	
}
