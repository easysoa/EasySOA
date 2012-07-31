package org.easysoa.galaxydemotest.standalone;

import java.io.IOException;
import java.net.URL;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Unit test for Galaxy Demo. Nuxeo and Frascati not launched in an OSGI container.
 */
public class HttpProxyTestStarter {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());    
    
	/** The FraSCAti platform */
    private static FraSCAti frascati;
   
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
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
	    //System.setProperty("org.apache.cxf.bus.factory","org.easysoa.cxf.EasySOABusFactory");
		// Start fraSCAti
		startFraSCAti();
		// Start HTTP Proxy
		startHttpProxy();
	}
    
	/**
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 * 
	 */
	@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("Http Proxy started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Http Proxy stopped !");
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
    
}
