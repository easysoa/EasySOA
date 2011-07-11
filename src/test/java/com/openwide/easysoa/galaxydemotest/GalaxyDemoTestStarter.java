package com.openwide.easysoa.galaxydemotest;

import static org.junit.Assert.assertNotNull;

import java.io.File;
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
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public class GalaxyDemoTestStarter {

	private static String serviceUrl = "http://localhost:9000/GalaxyTrip?wsdl";
	private static String TNS = "http://scenario1.j1.galaxy.inria.fr/";
	private static QName serviceName;
	private static QName portName;
   
	/** The FraSCAti platform */
    private static FraSCAti frascati;	
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());	
	
    /**
     * 
     * @return
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }		
	
	//http://localhost:9000/GalaxyTrip?wsdl
	static {
		serviceName = new QName(TNS, "Trip");
		portName = new QName(TNS, "TripPort");
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
		System.setProperty("cxf.config.file", "src/test/resources/configurationCXF.xml");
	}
	
	/**
	 * Init the remote systems for the test
	 * Nuxeo, Frascati, Galaxy demo and HTTP Proxy ...
	 * Instantiate FraSCAti and retrieve services.
	 * @throws InterruptedException 
	 */
    @Before
	public final void setUp() throws FrascatiException, InterruptedException {
    	logger.debug("user.dir : " + System.getProperty("user.dir"));	   
    	//TODO : Problem here ? 
    	//System.setProperty("org.apache.cxf.bus.factory","com.openwide.easysoa.cxf.EasySOABusFactory");
    	// Start fraSCAti
		startFraSCAti();
		// Start Galaxy Demo
		startGalaxyDemo();
	}	
	  
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	private static void startFraSCAti() throws FrascatiException{
		frascati = FraSCAti.newFraSCAti();
	}
		
	
	/**
	 * Start The Galaxy Demo 
	 * @throws FrascatiException 
	 */
	private static void startGalaxyDemo() throws FrascatiException{
		URL compositeUrl = ClassLoader.getSystemResource("smart-travel-mock-services.composite") ;
		frascati.processComposite(compositeUrl.toString(), new ProcessingContextImpl());
	}	
	
	/**
	 * Send a request to trigger the Galaxy demo test
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 * @throws InterruptedException  
	 * 
	 */
	@Test
	public final void testGalaxyDemo() throws IOException, SOAPException, InterruptedException {
		logger.debug("Sending Demo request !");
        Service jaxwsService = Service.create(new URL(serviceUrl), serviceName);
	    Dispatch<SOAPMessage> disp = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
	    FileInputStream fis = new FileInputStream(new File(System.getProperty("user.dir") + "/src/test/resources/galaxyDemoTestMessage.xml"));
	    SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, fis);
	    assertNotNull(reqMsg);
		SOAPMessage response = disp.invoke(reqMsg);
		logger.debug("Response : " + response.getSOAPBody().getTextContent().trim());
		logger.debug("Demo request sent !");
		// Wait for the proxy finish to register services
		Thread.sleep(10000);
	}	
	
}
