package org.openwide.easysoa.test;

import java.io.IOException;
import java.net.URL;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.scaffolding.FormGenerator;
import org.openwide.easysoa.scaffolding.FormGeneratorImpl;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

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
    @Before
	public final void setUp() throws FrascatiException, InterruptedException {
		// Start fraSCAti
		startFraSCAti();
		// Start Scaffolding Proxy test
		startTestComposite();
	}	
		
	/**
	 * Test the HTML form generation
	 * @throws Exception If a problem occurs
	 */
    @Ignore
	@Test
	public final void testWsdlToHtmlGenerator() throws Exception{
		FormGenerator fg = new FormGeneratorImpl();
		fg.generateHtmlFormFromWsdl("src/test/resources/GalaxyTrip.wsdl", "src/main/resources/wsdlToHtml.xslt", "form.html");
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
	private static void startTestComposite() throws FrascatiException{
		URL compositeUrl = ClassLoader.getSystemResource("scaffoldingProxy.composite") ;
		frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());
	}	

	
	
}
