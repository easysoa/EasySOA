package org.easysoa.proxy.ssl.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easysoa.proxy.ssl.test.helloworld.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * HttpCLient SSL test with a wrong server certificate, must return a SSLPeerUnverifiedException exception
 *
 */
public class HttpClientSSLTestWithWrongServerCertificate extends AbstractHttpClientSSLTest {

    /**
     * Initialize the test
     * @throws FrascatiException
     */
    @Before
    public void init() throws FrascatiException {
        // Set the CXF configuration file
        System.setProperty("cxf.config.file", "src/test/resources/wrong-certificate-server-cxf.xml");
        // Start FraSCAti and load the composite
        startFraSCAtiAndLoadComposite(getComposite());
    }

    /**
     * Test method, send a simple request to a SSL server
     * @throws Exception If a problem occurs
     */
    @Test
    public final void httpClientSSLTest() {
        try{
            HttpClient httpClient = new HttpClient();
            String response = httpClient.run();
            assertEquals("Helloworld, test", response);
            fail("javax.net.ssl.SSLPeerUnverifiedException exception expected");
        }
        catch(Exception ex){
            assertTrue(ex instanceof SSLPeerUnverifiedException);
        }
    }

    /**
     * Return the name of the composite to test.
     * @return the name of the composite to test.
     */
    @Override
    public String getComposite() {
        return "SSL_HttpClient_Test.composite";
    }

}