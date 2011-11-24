package com.openwide.easysoa.galaxydemotest.mock;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

import fr.inria.galaxy.j1.scenario1.Trip;

public class GalaxyDemoMockTest {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(GalaxyDemoMockTest.class);	

	/** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;    	
    
    @Before
    public void setUp() throws Exception {
    	// Start the travel required mock services
    	MockServer server = new MockServer();
    	// Start fraSCAti
 	    startFraSCAti();
 	    // Start the Trip mock service
    	startTripMock();
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
	
	protected static void startTripMock() throws FrascatiException {
		logger.info("Trip Mock Starting");
		componentList.add(frascati.processComposite("smart-travel-mock-services.composite", new ProcessingContextImpl()));
	}
	
	@Test
	public void test() throws FrascatiException{
		// There is only one component in the list
		Trip tripService = frascati.getService(componentList.get(0), "Galaxy_System", Trip.class);
		Trip spyTripService = spy(tripService);

		// Invoking trip service
		logger.info("Invoking trip service ...");
		String tripServiceResponse = spyTripService.process("Drink beer", "I would like a beer", 10);
		logger.info("Trip service response : " + tripServiceResponse);
		
		// check that the process method has been called 
        verify(spyTripService).process("Drink beer", "I would like a beer", 10);
	}
	
}
