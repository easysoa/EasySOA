package org.easysoa.sca.intents.test;

import org.objectweb.fractal.api.Component;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;

/**
 * There is two requirements to run this test,:
 * 
 * - The SAM server must be installed in Easysoa or started as a standalone application 
 * and must be running on the following address http://localhost:8080/sam-server-war/
 * - The weather service used in Smart travel sample must be running on the following address : http://localhost:9020/WeatherService?wsdl 
 * 
 * @author jguillemotte
 *
 */
public class CXFMonitoringIntentTest extends AbstractTestHelper {

    private Component cxfIntentComponent;
    
    @Before
    public void setUp() throws Exception{
        
        // Start fraSCAti
        startFraSCAti();
        // Start smart travel weather SOAP mocks
        startSoapMockServices();
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
     * At the moment, it is not possible to have the CXF Monitoring intent working on binding.rs composite tags
     * @throws Exception
     */
    @Test
    @Ignore
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
