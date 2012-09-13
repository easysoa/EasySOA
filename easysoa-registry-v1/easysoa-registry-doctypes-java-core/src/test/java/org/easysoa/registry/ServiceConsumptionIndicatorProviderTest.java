package org.easysoa.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.test.AbstractWebEngineTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * Tests for Java service consumptions & Java service implementations
 * 
 * @author mkalam-alami
 *
 */
@Deploy({"org.easysoa.registry.rest.server", "org.easysoa.registry.doctypes.java.core"})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class ServiceConsumptionIndicatorProviderTest extends AbstractWebEngineTest {

    private static Logger logger = Logger.getLogger(ServiceConsumptionIndicatorProviderTest.class);
    
    private static final String WS_ITF = "org.easysoa.wsItf";

    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    public ServiceConsumptionIndicatorProviderTest() {
        setLogRepositoryAfterEachTest(true);
    }
    
    @Test
    public void testServiceImplementationAndConsumption() throws Exception {
        // Create services
        for (int i = 0; i < 3; i++) {
            discoveryService.runDiscovery(documentManager,
                    new SoaNodeId(Service.DOCTYPE, "Service"+i), null, null);
        }
        
        // Create implementation of Service0
        Map<String, Object> deliverableProperties = new HashMap<String, Object>();
        deliverableProperties.put(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, WS_ITF);
        discoveryService.runDiscovery(documentManager,
                new SoaNodeId(JavaServiceImplementation.DOCTYPE, "ServiceImpl0"),
                deliverableProperties, 
                Arrays.asList(new SoaNodeId(Service.DOCTYPE, "Service0"),
                        new SoaNodeId(Deliverable.DOCTYPE, "DelivOfService0")));
        
        // Make Service1 consume Service0
        SoaNodeId service1DelivId = new SoaNodeId(Deliverable.DOCTYPE, "DelivOfService1");
        
        discoveryService.runDiscovery(documentManager,
                new SoaNodeId(JavaServiceImplementation.DOCTYPE, "ServiceImpl1"),
                null, 
                Arrays.asList(new SoaNodeId(Service.DOCTYPE, "Service1"),
                        service1DelivId));
        Map<String, Object> consumptionProperties = new HashMap<String, Object>();
        consumptionProperties.put(JavaServiceConsumption.XPATH_CONSUMEDINTERFACE, WS_ITF);
        discoveryService.runDiscovery(documentManager,
                new SoaNodeId(JavaServiceConsumption.DOCTYPE, "Service1ConsumptionOfService0"),
                consumptionProperties , 
                Arrays.asList(service1DelivId));
        
        documentManager.save();
        
        // Compute indicators
        
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setNuxeoSitesUrl(NUXEO_URL);
        WebResource easySOAClient = clientBuilder.constructEasySOAClient();
        
        String indicatorsString = easySOAClient.accept(MediaType.APPLICATION_JSON).get(String.class);
        logger.info(indicatorsString);
        
        // Check consumption indicator value
        
        JsonNode indicators = new ObjectMapper().readValue(indicatorsString, JsonNode.class);
        Assert.assertNotNull(indicators);
        Assert.assertEquals(66, indicators
                .get("Miscellaneous")
                .get("Never consumed services")
                .get("percentage")
                .getIntValue());
        
    }
    
}
