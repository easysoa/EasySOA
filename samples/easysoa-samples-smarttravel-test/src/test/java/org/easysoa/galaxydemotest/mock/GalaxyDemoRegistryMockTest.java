package org.easysoa.galaxydemotest.mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.log4j.Logger;
import org.easysoa.registry.frascati.EasySOAApiFraSCAti;
import org.easysoa.sca.frascati.ApiFraSCAtiScaImporter;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.RemoteBindingVisitorFactory;
import org.eclipse.stp.sca.Composite;
import org.junit.Before;
import org.junit.Test;

/**
 * Test integration between Registry-api-frascati & Smart travel trip sample
 * @author jguillemotte
 *
 */
public class GalaxyDemoRegistryMockTest extends MockTestHelper {

	/** Logger */
	private static Logger logger = Logger.getLogger(GalaxyDemoRegistryMockTest.class);	
	
	/** Mock server */
	private MockServer server;
	
    @Before
    public void setUp() throws Exception {
    	// Load trip sample composite with frascati
    	// Start the travel required mock services
    	server = new MockServer();
    	// Start fraSCAti
 	    startFraSCAti();
 	    // Start the Trip mock service
    	startComposite(COMPOSITE);
    }
	
    @Test
    public void importScaCompositeTest() throws Exception {
    	// Get the loaded composite with frascati.getComposite() method
    	// There is only one component in the list

    	BindingVisitorFactory bindingVisitorFactory = new RemoteBindingVisitorFactory();
    	// TODO how to get the composite loaded in frascati OR how to get the composite name to pass it as a fileName
    	// f.getComposite returns a Component
    	
    	ApiFraSCAtiScaImporter importer = new ApiFraSCAtiScaImporter(bindingVisitorFactory, null, EasySOAApiFraSCAti.getInstance());
    	//importer.visitComposite(componentList.get(0). );
    	
		// Discover the composite with Registry api frascati
		importer.setServiceStackType("FraSCAti");
		importer.setServiceStackUrl("/");
		// Create a spy importer for Mockito
		ApiFraSCAtiScaImporter spyImporter = spy(importer);
		// Import the SCA composite
		spyImporter.importSCAComposite();
		// Check the recorded exchanges
		//checkExchanges();
		// Check with Mockito
    	verify(spyImporter).importSCAComposite();		
    	
		// Check using Mockito
		
    }
	
}
