package org.easysoa.sca.intents.test;

import org.objectweb.fractal.api.Component;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;

public class CXFMonitoringIntentTest extends AbstractTestHelper {

    @Before
    public void setUp() throws Exception{
        
        // Start fraSCAti
        startFraSCAti();
        // Start mocks
        //startMockServices();
    }
    
    /**
     * Test the monitoring intent
     * @throws Exception
     */
    @Test
    public void monitoringIntentTest() throws Exception {
        Component cxfIntentComponent = frascati.processComposite("cxfMonitoringIntentTest.composite", new ProcessingContextImpl());
        IntentTest intentTest = frascati.getService(cxfIntentComponent, "monitoringIntentTest", IntentTest.class);
        String response = intentTest.getCitiesByCountry("France");
        System.out.println(response);
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
