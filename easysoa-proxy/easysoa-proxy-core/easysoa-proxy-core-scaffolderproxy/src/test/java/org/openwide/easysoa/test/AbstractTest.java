package org.openwide.easysoa.test;

import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public abstract class AbstractTest {

	/** The FraSCAti platform */
    protected static FraSCAti frascati;
	
    /** Set system properties for FraSCAti */
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}    
    
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException{
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * Start Velocity Test
	 * @throws FrascatiException
	 */
	protected static void startScaffoldingProxyComposite() throws FrascatiException{
		frascati.processComposite("src/main/resources/scaffoldingProxy.composite", new ProcessingContextImpl());
	}

	/**
	 * Start Soap service mock
	 * @throws FrascatiException
	 */
	protected static void startSoapServiceMockComposite() throws FrascatiException{
		frascati.processComposite("src/test/resources/soapServiceMock.composite", new ProcessingContextImpl());
	}
	
}
