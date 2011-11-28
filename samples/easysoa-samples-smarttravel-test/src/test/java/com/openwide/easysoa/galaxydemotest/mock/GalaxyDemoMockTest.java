package com.openwide.easysoa.galaxydemotest.mock;

import static org.mockito.Mockito.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;

import de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap;
import de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoapSpyWrapper;
import net.server.Delegated;

import fr.inria.galaxy.j1.scenario1.Trip;

/**
 * A test working with Galaxy demo trip sample & Mockito
 * @author jguillemotte
 *
 */
public class GalaxyDemoMockTest extends MockTestHelper {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(GalaxyDemoMockTest.class);	
    
	/** Mock server */
	MockServer server;
	
    @Before
    public void setUp() throws Exception {
    	// Start the travel required mock services
    	server = new MockServer();
    	// Start fraSCAti
 	    startFraSCAti();
 	    // Start the Trip mock service
    	startComposite(COMPOSITE);
    }

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws FrascatiException{
		// There is only one component in the list
		Trip tripService = frascati.getService(componentList.get(0), "Galaxy_System", Trip.class);
		// Add a mockito spy on trip service
		Trip spyTripService = spy(tripService);

		// Invoking trip service
		logger.info("Invoking trip service ...");
		String tripServiceResponse = spyTripService.process("Drink beer", "I would like a beer", 10);
		logger.info("Trip service response : " + tripServiceResponse);
		
		// Add mockito tests to check calls of other services
		verify(((Delegated<CurrencyServerWebServiceSoap>)(server.getCurrencyImplementor())).getDelegate()).getCurrencyValue("3", "EUR", "USD");
		verify(server.getMeteoImplementor().getDelegate()).getWeather("Grenoble", "France");
		verify(server.getTranslateImplementor().getDelegate()).translate("BD061A8446F9FA67F9CD39B278237C98599FAFEA", "I would like a beer", "EN", "FR");
		
		// check that the process method has been called 
        verify(spyTripService).process("Drink beer", "I would like a beer", 10);
	}
	
}
