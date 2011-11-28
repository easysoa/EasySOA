package com.openwide.easysoa.galaxydemotest.mock;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Test helper for mock tests
 * @author jguillemotte
 *
 */
public class MockTestHelper {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(MockTestHelper.class);	
	/** Smart travel Composite */
	public static final String COMPOSITE = "smart-travel-mock-services.composite";	
	
	/** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList; 	

    /**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException {
		logger.info("FraSCATI Starting ...");
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}

	/**
	 * Start the smart travel service
	 * @throws FrascatiException
	 */
	protected static void startComposite(String composite) throws FrascatiException {
		logger.info("Composite " + composite + " Starting ...");
		componentList.add(frascati.processComposite(composite, new ProcessingContextImpl()));
	}    
    
}
