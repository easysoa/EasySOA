package org.openwide.easysoa.test;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
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
	private static final Logger logger = Logger.getLogger(getInvokingClassName());    

	/** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;
    
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
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * 
	 * @throws FrascatiException
	 */
	protected static void stopFraSCAti() throws FrascatiException{
		logger.info("FraSCATI Stopping");
		if(componentList != null){
			for(Component component : componentList){
				frascati.close(component);
			}
		}
	}
	
	/**
	 * Start HTTP Proxy
	 * @throws FrascatiException
	 */
	protected static void startHttpDiscoveryProxy(String composite) throws FrascatiException {
		logger.info("HTTP Discovery Proxy Starting");
		componentList.add(frascati.processComposite(composite, new ProcessingContextImpl()));
	}
	
	/**
	 * Start the services mock for tests (Meteo mock, twitter mock ...)
	 * @param withNuxeoMock If true, the Nuxeo mock is started
	 * @throws FrascatiException if a problem occurs during the start of composites
	 */
	protected static void startMockServices(boolean withNuxeoMock) throws FrascatiException {
		logger.info("Services Mock Starting");
		componentList.add(frascati.processComposite("src/test/resources/twitterMockRest.composite", new ProcessingContextImpl()));
		componentList.add(frascati.processComposite("src/test/resources/meteoMockSoap.composite", new ProcessingContextImpl()));
		// start Nuxeo mock
		if(withNuxeoMock){
			componentList.add(frascati.processComposite("src/test/resources/nuxeoMockRest.composite", new ProcessingContextImpl()));
		}
	}
	
}
