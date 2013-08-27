package org.easysoa.proxy.ssl.test;

import static org.junit.Assert.assertEquals;

import org.easysoa.proxy.ssl.test.helloworld.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;

/**
 * HttpClient SSL test 
 */
public class HttpClientSSLTest extends AbstractHttpClientSSLTest {

    /**
     * Initialize the test
     * @throws FrascatiException
     */
    @Before
    public void init() throws FrascatiException {
        // Set the CXF configuration file
        System.setProperty("cxf.config.file", "src/test/resources/server-cxf.xml");
        // Start FraSCAti and load the composite
        startFraSCAtiAndLoadComposite(getComposite());
    }

    /**
     * Test method, send a simple request to a SSL server
     * @throws Exception If a problem occurs
     */
    @Test
    public final void httpClientSSLTest() throws Exception {
        HttpClient httpClient = new HttpClient();
        String response = httpClient.run();
        assertEquals("Helloworld, test", response);
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