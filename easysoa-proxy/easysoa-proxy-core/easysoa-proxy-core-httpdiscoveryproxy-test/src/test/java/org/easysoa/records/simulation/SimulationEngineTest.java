/**
 *
 */
package org.easysoa.records.simulation;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.proxy.core.api.records.replay.ReplayEngine;
import org.easysoa.proxy.core.api.simulation.SimulationEngine;
import org.easysoa.proxy.core.api.simulation.SimulationStore;
import org.easysoa.proxy.core.api.simulation.methods.SimpleSimulationMethod;
import org.easysoa.proxy.core.api.util.ContentReader;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.test.util.AbstractProxyTestStarter;
import org.easysoa.test.util.UrlMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jguillemotte
 *
 */
public class SimulationEngineTest extends AbstractProxyTestStarter {

    // Logger
    private static Logger logger = Logger.getLogger(SimulationEngineTest.class.getName());

    /**
     * Start FraSCAti and mock services
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        // Start FraSCAti
        startFraSCAti();
        // Start HTTP proxy
        startHttpDiscoveryProxy("httpDiscoveryProxy.composite");
        // Start mocks
        startMockServices(false, true, true);
    }

    /**
     * Test the simulation engine
     * @throws Exception If a problem occurs
     */
    @Test
    public void simulationEngineTest() throws Exception {
        // Creates a simulation set
        String testStoreName = "Twitter_Rest_Simulation_Test_Run";

        DefaultHttpClient httpClient = new DefaultHttpClient();

        // Start a new Run
        HttpPost newRunPostRequest = new HttpPost("http://localhost:8084/run/start/" + testStoreName);
        assertEquals("Run '" + testStoreName + "' started !", httpClient.execute(newRunPostRequest, new BasicResponseHandler()));

        // Get the twitter mock set and send requests to the mock through the HTTP proxy
        DefaultHttpClient httpProxyClient = new DefaultHttpClient();

        // Set client to use the HTTP Discovery Proxy
        HttpHost proxy = new HttpHost("localhost", EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        httpProxyClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        UrlMock urlMock = new UrlMock();
        HttpResponse response;
        HttpUriRequest httpUriRequest;
        for(String url : urlMock.getTwitterUrlData("localhost:" + EasySOAConstants.TWITTER_MOCK_PORT)){
            logger.info("Request send : " + url);
            httpUriRequest = new HttpGet(url);
            response = httpProxyClient.execute(httpUriRequest);
            // Need to read the response body entirely to be able to send another request
            ContentReader.read(response.getEntity().getContent());
        }

        // Stop and save the run
        HttpPost stopRunPostRequest = new HttpPost("http://localhost:8084/run/stop");
        assertEquals("Current run stopped !", httpClient.execute(stopRunPostRequest, new BasicResponseHandler()));
        // delete the run
        HttpPost deleteRunPostRequest = new HttpPost("http://localhost:8084/run/delete");
        assertEquals("Run deleted !", httpClient.execute(deleteRunPostRequest, new BasicResponseHandler()));

        ProxyFileStore fileStore= new ProxyFileStore();
        // Launch the simulation
        List<ExchangeRecord> recordList = fileStore.getExchangeRecordlist(testStoreName);
        ReplayEngine replayEngine = frascati.getService(componentList.get(0).getName(), "replayEngineService", org.easysoa.proxy.core.api.records.replay.ReplayEngine.class);
        SimulationEngine simulationEngine = replayEngine.getSimulationEngine();

        // Send a request to the replay service
        logger.debug("Getting simulation store ...");
        SimulationStore simulationStore = simulationEngine.getSimulationStoreFromSuggestion("testSimulationStore", recordList);
        fileStore.saveSimulationStore(simulationStore);
        for(ExchangeRecord record : recordList){
            Map<String, List<String>> fieldValues = new HashMap<String, List<String>>();
            // TODO : add test for field values
            ExchangeRecord simulatedResponse = simulationEngine.simulate(record, simulationStore, new SimpleSimulationMethod(), replayEngine.getTemplateEngine(), fieldValues);
            // Works but the test data set is not appropriated
            // Find a data set with output fields to really check the simulation engine
            logger.debug("Simulated response = " +  simulatedResponse.getOutMessage().getMessageContent().getRawContent());
        }

    }

    @After
    public void cleanUp() throws Exception {
        // stop FraSCati
        stopFraSCAti();
    }

}
