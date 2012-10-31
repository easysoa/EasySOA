package org.easysoa.proxy.sll.test;

import static org.junit.Assert.assertEquals;

import org.easysoa.proxy.ssl.test.helloworld.HttpClient;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Abstract class for testing SCA applications with FraSCAti
 * 
 */
public class HttpClientSSLTest {

    private FraSCAti frascati;

    /**
     * The SCA composite to test
     */
    private Component scaComposite;

    /**
     * Load a composite
     * 
     * @param name
     *            the composite name
     * @throws FrascatiException
     */
    @Before
    public void loadComposite() throws FrascatiException {
        String compositeName = getComposite();
        System.out.println("Loading SCA composite '" + compositeName + "'...");
        frascati = FraSCAti.newFraSCAti();
        scaComposite = frascati.processComposite(compositeName, new ProcessingContextImpl());
    }

    @Test
    public final void httpClientSSLTest() throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = httpClient.run();
        assertEquals("Helloworld, test", response);
    }
    
    /**
     * This test do nothing, just wait for a user action to stop the test.
     * 
     * @throws ClientException
     * @throws SOAPException
     * @throws IOException
     */
    @Test
    @Ignore
    public final void testWaitUntilRead() throws Exception {
        System.out.println("Http Discovery Proxy started, wait for user action to stop !");
        // Just push a key in the console window to stop the test
        System.in.read();
        System.out.println("Http Discovery Proxy stopped !");
    }
    
    /**
     * Close the SCA domain
     * 
     * @throws IllegalLifeCycleException
     *             if the domain cannot be closed
     * @throws NoSuchInterfaceException
     *             if the lifecycle controller of the component is not found
     */
    @After
    public final void close() throws FrascatiException {
        if (scaComposite != null) {
            frascati.close(scaComposite);
        }
        frascati.close();
    }

    // ---------------------------------------------------------------------------
    // Abstract methods
    // ---------------------------------------------------------------------------

    /**
     * Return the name of the composite to test.
     * 
     * @return the name of the composite to test.
     */
    public String getComposite() {
        return "SSL_HttpClient_Test.composite";
    }

}