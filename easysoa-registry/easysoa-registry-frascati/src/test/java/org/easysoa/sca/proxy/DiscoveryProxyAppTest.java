/**
 *
 */
package org.easysoa.sca.proxy;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easysoa.EasySOAConstants;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Class to test the HTTP Discovery proxy starting in FraSCAti embeded in Nuxeo as a easysoa app
 *
 * @author jguillemotte
 *
 */
@RunWith(FeaturesRunner.class)
@Features({ FraSCAtiFeature.class })
public class DiscoveryProxyAppTest {

    //private final String resourcePath = "src/test/resources/";
    private final String resourcePath = "target/";

    private final String proxyCompositePath = resourcePath + "easysoa-proxy-core-httpdiscoveryproxy-0.5-SNAPSHOT.jar";

    protected FraSCAtiServiceItf frascatiService = null;

    static final Log log = LogFactory.getLog(DiscoveryProxyAppTest.class);

    @Before
    public void setUp() {
        frascatiService = (FraSCAtiServiceItf) Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
    }

    @Test
    public void TestDiscoveryProxyApp() throws Exception {
        String runName = "ProxyInEmbeddedFrascatiTestRun";

        // Check if FraSCAti service is started and not null
        assertNotNull(frascatiService);

        // Start the discovery proxy
        File scaProxyFile = new File(proxyCompositePath);
        scaProxyFile = scaProxyFile.getAbsoluteFile();
        String discoveryProxyComponent = frascatiService.processComposite("httpDiscoveryProxy", FraSCAtiServiceItf.all, null, scaProxyFile.toURI().toURL()).getName();
        log.info("Discovery proxy component : " + discoveryProxyComponent);
        assertEquals(discoveryProxyComponent ,"httpDiscoveryProxy");

        // send a request to check to good work of proxy

        // Http client for proxy driver
        DefaultHttpClient httpProxyDriverClient = new DefaultHttpClient();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        // Send a request to start a run
        String response;
        response = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/start/" + runName), responseHandler);
        log.info("start run : " + response);
        assertEquals("Run '" + runName + "' started !", response);

        // Send a request to stop the run
        // Stop & save the run
        response = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/stop"), responseHandler);
        assertEquals("Current run stopped !", response);
        log.info("stop run : " + response);
        //response = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/save"), responseHandler);
        response = httpProxyDriverClient.execute(new HttpPost("http://localhost:" + EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT + "/run/delete"), responseHandler);

    }

    @After
    public void tearDown() {
        // Nothing to do
    }

}
