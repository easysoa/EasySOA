package org.easysoa.cxf;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public class CxfProviderDispatcherTest extends AbstractProxyTestStarter {

    // Logger
    private static Logger logger = Logger.getLogger(CxfProviderDispatcherTest.class.getName());    

    /**
     * Start FraSCAti, HTTP discovery proxy and mock services
     * @throws FrascatiException If a problem occurs
     */
    @BeforeClass
    public static void setUp() throws FrascatiException{
        // Start fraSCAti
        startFraSCAti();
        // Start mock services
        //startMockServices(false);
    }    

    @Test
    @Ignore
    public void cxfProviderTestOutsideFraSCAti() throws FrascatiException, IOException, SOAPException {
        // Publishing CXF Meto mock provider outside FraSCAti
        Object implementor = new MeteoMockProvider();
        String address = "http://localhost:8987/meteoMockProvider";
        Endpoint.publish(address, implementor);
        
        // Send a request using the meteo mock dispatcher
        MeteoMockDispatcher meteoMockDispatcher = new MeteoMockDispatcher();
        String response = meteoMockDispatcher.sendRequest(false); 
        logger.debug("MeteoMock CXF Provider response : " + response);         
    }
    
    @Test
    //@Ignore
    public void cxfProviderTestInFraSCAti() throws FrascatiException, IOException, SOAPException{
        // Start the CXF Meteo mock provider in FraSCAti
        // PB with FraSCAti : the port name and the service are the interface name (+ prefix or suffix)
        // JAX-WS annotations are not used in FraSCAti 
        startCxfMeteoMockProvider();

        // Send a request using the meteo mock dispatcher
        MeteoMockDispatcher meteoMockDispatcher = new MeteoMockDispatcher();
        String response = meteoMockDispatcher.sendRequest(true); 
        logger.debug("MeteoMock CXF Provider response : " + response);        
    }

    /**
     * Start the CXF Meteo Mock provider
     * @throws FrascatiException If a problem occurs
     */
    private void startCxfMeteoMockProvider() throws FrascatiException {
        logger.info("Services Mock Starting");
        this.componentList.add(this.frascati.processComposite("cxfMeteoMockProvider.composite", new ProcessingContextImpl()));        
    }
    
    /**
     * This test do nothing, just wait for a user action to stop the proxy. 
     * @throws ClientException
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    @Ignore
    public final void testWaitUntilRead() throws Exception {
        logger.info("CxfProviderDispatcherTest started, wait for user action to stop !");
        // Just push a key in the console window to stop the test
        System.in.read();
        logger.info("CxfProviderDispatcherTest stopped !");
    }    
    
}

