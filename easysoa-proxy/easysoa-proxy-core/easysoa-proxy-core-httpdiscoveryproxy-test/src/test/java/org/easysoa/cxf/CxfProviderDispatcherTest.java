package org.easysoa.cxf;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openwide.easysoa.test.util.AbstractProxyTestStarter;
import org.ow2.frascati.util.FrascatiException;

/**
 * This class contains two tests for CXF Provider/Dispatcher API
 * - First test start a Provider outside FraSCAti and a dispatcher send a test request
 * - Second test start a Provider inside FraSCAti and a dispatcher send a test request
 * 
 * @author jguillemotte
 *
 */
public class CxfProviderDispatcherTest extends AbstractProxyTestStarter {

    // Logger
    private static Logger logger = Logger.getLogger(CxfProviderDispatcherTest.class.getName());    

    /**
     * Start FraSCAti, HTTP discovery proxy and mock services
     * @throws Exception 
     */
    @BeforeClass
    public static void setUp() throws Exception{
        // Start fraSCAti
        startFraSCAti();
        // Start mock services
        //startMockServices(false);
    }    
    
    /**
     * Test outside Frascati
     * @throws FrascatiException, IOException, SOAPException If a problem occurs
     */
    @Test
    public void cxfProviderTestOutsideFraSCAti() throws IOException, SOAPException {
        // Publishing CXF Meto mock provider outside FraSCAti
        Object implementor = new MeteoMockProvider();
        String address = "http://localhost:8988/meteoMockProvider";
        Endpoint endpoint = Endpoint.publish(address, implementor);
        
        // Send a request using the meteo mock dispatcher
        MeteoMockDispatcher meteoMockDispatcher = new MeteoMockDispatcher();
        String response = meteoMockDispatcher.sendRequest(false); 
        logger.debug("MeteoMock CXF Provider response : " + response);
        endpoint.stop();
     
    }

    /**
     * Test inside Frascati
     * @throws FrascatiException, IOException, SOAPException If a problem occurs
     * @throws FraSCAtiServiceException 
     */
    @Test
    public void cxfProviderTestInFraSCAti() throws IOException, SOAPException, FraSCAtiServiceException{
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
     * @throws FraSCAtiServiceException 
     */
    private void startCxfMeteoMockProvider() throws FraSCAtiServiceException {
        logger.info("Services Mock Starting");
        //this.componentList.add(this.frascati.processComposite("cxfMeteoMockProvider.composite", new ProcessingContextImpl()));
        this.componentList.add(this.frascati.processComposite("src/test/resources/cxfMeteoMockProvider.composite"));
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

