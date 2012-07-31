package org.easysoa.galaxydemotest.frascaticomposites;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Unit test for Galaxy Demo. Nuxeo and Frascati not launched in an OSGI container.
 */
public class FraSCAtiCompositeDemoTestStarter {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());    
    
	/** The FraSCAti platform */
    private static FraSCAti frascati;
    
	private static String serviceUrl = "http://localhost:9000/GalaxyTrip?wsdl";
	private static String TNS = "http://scenario1.j1.galaxy.inria.fr/";
	private static QName serviceName;
	private static QName portName;
   
	//http://localhost:9000/GalaxyTrip?wsdl
	static {
		serviceName = new QName(TNS, "Trip");
		portName = new QName(TNS, "TripPort");
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
		System.setProperty("cxf.config.file", "src/test/resources/mixedConfigurationCXF.xml");
	}

    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }	
	
	/**
	 * Init the remote systems for the test
	 * Nuxeo, Frascati, Galaxy demo and HTTP Proxy ...
	 * Instantiate FraSCAti and retrieve services.
	 * @throws InterruptedException 
	 */
   @Before
	public final void setUp() throws FrascatiException, InterruptedException {
	   System.setProperty("org.apache.cxf.bus.factory","org.easysoa.cxf.EasySOABusFactory");
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP Proxy
		startHttpProxy();
		// Possible deadlock during proxy init
		// TODO : Send a feedback to FraSCAti dev team about the deadlock that appends when 2 composites are started simultaneously
		// Start Galaxy Demo
		startGalaxyDemo();
		// Wait for the services are completely started
		Thread.sleep(5000);
	}
    
	/**
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public final void testGalaxyDemo() throws ClientException, IOException, SOAPException, InterruptedException{

		logger.debug("Sending Demo request !");
        Service jaxwsService = Service.create(new URL(serviceUrl), serviceName);
	    Dispatch<SOAPMessage> disp = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
	    
	    //InputStream is = getClass().getClassLoader().getResourceAsStream("galaxyDemoTestMessage.xml");
	    InputStream is = new FileInputStream("src/test/resources/galaxyDemoTestMessage.xml");
	    SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, is);
	    assertNotNull(reqMsg);
		SOAPMessage response = disp.invoke(reqMsg);
		logger.debug("Response : " + response.getSOAPBody().getTextContent().trim());
		logger.debug("Demo request sent !");
	}

	/**
	 * Wait for http proxy registered all the webservices
	 * @throws InterruptedException
	 */
	@After
	public final void endTest() throws InterruptedException{
		Thread.sleep(10000);
	}
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	private static void startFraSCAti() throws FrascatiException{
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start HTTP Proxy
	 * @throws FrascatiException
	 */
	private static void startHttpProxy() throws FrascatiException{
		URL compositeUrl = ClassLoader.getSystemResource("httpProxy.composite") ;
		frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());		
	}
	
	/**
	 * Start The Galaxy Demo 
	 * @throws FrascatiException 
	 */
	private static void startGalaxyDemo() throws FrascatiException{
		URL compositeUrl = ClassLoader.getSystemResource("smart-travel-mock-services.composite") ;
		frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());		
	}
    
}
