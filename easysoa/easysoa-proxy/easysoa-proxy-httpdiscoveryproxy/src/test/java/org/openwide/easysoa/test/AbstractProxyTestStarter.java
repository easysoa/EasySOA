package org.openwide.easysoa.test;

import org.apache.log4j.Logger;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Abstract Proxy Test Starter. Launch FraSCAti and the HTTP Discovery Proxy.
 */
public abstract class AbstractProxyTestStarter {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());    
    
	/** The FraSCAti platform */
    protected static FraSCAti frascati;
   
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}

    /**
     * Return the invoking class name
     * @return The class name
     */
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }	
	
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException {
		logger.info("FraSCATI Starting");
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start HTTP Proxy
	 * @throws FrascatiException
	 */
	protected static void startHttpDiscoveryProxy() throws FrascatiException {
		logger.info("HTTP Discovery Proxy Starting");
		frascati.processComposite("src/main/resources/httpDiscoveryProxy.composite", new ProcessingContextImpl());		
	}
	
	/**
	 * Start the services mock for tests
	 * @throws FrascatiException
	 */
	protected static void startMockServices() throws FrascatiException {
		logger.info("Services Mock Starting");
		frascati.processComposite("src/test/resources/twitterMockRest.composite", new ProcessingContextImpl());
		frascati.processComposite("src/test/resources/meteoMockSoap.composite", new ProcessingContextImpl());
	}
    
}
