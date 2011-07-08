package com.openwide.easysoa.galaxydemotest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.services.resource.ResourceService;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;
import com.google.inject.Inject;

/**
 * Unit test for Galaxy Demo. Nuxeo and Frascati not launched in an OSGI container.
 */
//@RunWith(FeaturesRunner.class)
//@Features(EasySOAFeature.class)
//@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class FraSCAtiCompositeDemoTestStarter {

    //@Inject CoreSession session;
    //@Inject ResourceService resourceService;
	
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
		System.setProperty("cxf.config.file", "/home/jguillemotte/frascati-runtime-1.4/conf/configurationCXF.xml");
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
	   System.setProperty("org.apache.cxf.bus.factory","com.openwide.easysoa.cxf.EasySOABusFactory");
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
	 * 
	 */
	@Test
	public final void testGalaxyDemo() throws ClientException, IOException, SOAPException{

		DocumentModelList resDocList;
		//DocumentModel resDoc;		

		logger.debug("Sending Demo request !");
        Service jaxwsService = Service.create(new URL(serviceUrl), serviceName);
	    Dispatch<SOAPMessage> disp = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
	    
	    InputStream is = getClass().getClassLoader().getResourceAsStream("galaxyDemoTestMessage.xml");
	    //InputStream is = new FileInputStream("src/test/resources/galaxyDemoTestMessage.xml");
	    SOAPMessage reqMsg = MessageFactory.newInstance().createMessage(null, is);
	    assertNotNull(reqMsg);
		SOAPMessage response = disp.invoke(reqMsg);
		logger.debug("Response : " + response.getSOAPBody().getTextContent().trim());
		logger.debug("Demo request sent !");
		
		// Checks that service informations are registered in Nuxeo
		/*resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + Service.DOCTYPE + "' AND " + "dc:title" + " = '" +  "restInterface" + "' AND ecm:currentLifeCycleState <> 'deleted'");*/
		//resDocList = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Service'");
		
		/*
		resDocList = session.query("SELECT * FROM Document");
		Iterator<DocumentModel> iter = resDocList.iterator();
		while(iter.hasNext()){
			DocumentModel doc = iter.next();
			System.out.println("Doc name : " + doc.getName());
		}
		assertEquals(resDocList.size(), 0);
		*/
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
		//TODO Change this hardcoded URI
		frascati.processComposite("/home/jguillemotte/workspace/esper-frascati-poc/src/main/resources/httpProxy", new ProcessingContextImpl());		
	}
	
	/**
	 * Start The Galaxy Demo 
	 * @throws FrascatiException 
	 */
	private static void startGalaxyDemo() throws FrascatiException{
		//frascati.processComposite("/home/jguillemotte/Workspace_Galaxy_demo/EasySOADemoTravel/trip/target/trip-1.0-SNAPSHOT.jar:smart-travel", new ProcessingContextImpl());
		frascati.processComposite("/home/jguillemotte/Workspace_Galaxy_demo/EasySOADemoTravel/trip/src/main/resources/smart-travel", new ProcessingContextImpl());
	}
    
}
