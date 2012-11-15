/**
 * 
 */
package org.easysoa.proxy.sll.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 *
 */
public abstract class AbstractHttpClientSSLTest {

    /**
     * Frascati engine
     */
    protected FraSCAti frascati;

    /**
     * The SCA composite to test
     */
    protected Component scaComposite;    
    
    /**
     * Start FraSCAti and load the composite
     * @param compositeName The composite to load
     * @throws FrascatiException 
     */
    public void startFraSCAtiAndLoadComposite(String compositeName) throws FrascatiException{
        System.out.println("Loading SCA composite '" + compositeName + "'...");
        // Start FraSCAti
        frascati = FraSCAti.newFraSCAti();
        // Load the composite
        scaComposite = frascati.processComposite(compositeName, new ProcessingContextImpl());
    }
    
    /**
     * Close the SCA domain
     * @throws IllegalLifeCycleException if the domain cannot be closed
     * @throws NoSuchInterfaceException if the lifecycle controller of the component is not found
     */
    @After
    public final void close() throws FrascatiException {
        if (scaComposite != null) {
            frascati.close(scaComposite);
        }
        frascati.close();
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
     * Return the composite name used for the test
     * @return The composite name
     */
    public abstract String getComposite();    
    
}
