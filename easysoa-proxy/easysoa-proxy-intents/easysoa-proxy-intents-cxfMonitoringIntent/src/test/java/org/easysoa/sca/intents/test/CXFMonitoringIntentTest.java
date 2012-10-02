package org.easysoa.sca.intents.test;

import org.objectweb.fractal.api.Component;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;

public class CXFMonitoringIntentTest extends AbstractTestHelper {

    private Component cxfIntentComponent;
    
    @Before
    public void setUp() throws Exception{
        
        // Start fraSCAti
        startFraSCAti();
        // Start SOAP mocks
        //startMockServices();
        // STart REST mocks
        startRestMockServices();
        // Start CXF Monitoring intent composite
        cxfIntentComponent = frascati.processComposite("cxfMonitoringIntentTest.composite", new ProcessingContextImpl());        
    }
    
    /**
     * Test the monitoring intent with SOAP weather service
     * @throws Exception
     */
    @Test
    public void monitoringIntentWSSoapTest() throws Exception {
        IntentTest intentTest = frascati.getService(cxfIntentComponent, "monitoringIntentTest", IntentTest.class);
        String response = intentTest.getCitiesByCountry("France");
        System.out.println(response);
    }
    
    /**
     * Test the monitoring intent with REST twitter service
     * @throws Exception
     */
    @Test
    public void monitoringIntentRestTest() throws Exception {    
        IntentTest intentTest = frascati.getService(cxfIntentComponent, "monitoringIntentTest", IntentTest.class);
    }
    
    
    /**
     * Stop FraSCAti components
     * @throws FrascatiException
     */
    @After
    public void cleanUp() throws Exception{
        stopFraSCAti();
    }       
        
}
