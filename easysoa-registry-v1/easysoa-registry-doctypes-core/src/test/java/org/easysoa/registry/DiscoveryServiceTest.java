package org.easysoa.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class DiscoveryServiceTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DiscoveryServiceTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    @Inject
    SoaNodeTypeService soaNodeTypeService;
    
    @Test
    public void testSimpleDiscovery() throws Exception {
        // Gather discovery information
        SoaNodeId discoveredDeliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa.registry:myartifact");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(Deliverable.XPATH_TITLE, "My Artifact");
        properties.put(Deliverable.XPATH_APPLICATION, "myapp");
        
        // Run discovery
        discoveryService.importDiscovery(documentManager, discoveredDeliverableId, properties, null);
        documentManager.save();
        
        // Check results
        DocumentModel foundDeliverable = documentService.find(documentManager, discoveredDeliverableId);
        Assert.assertNotNull("A deliverable must be created by the discovery processing", foundDeliverable);
        for (Entry<String, String> property : properties.entrySet()) {
            Assert.assertEquals("Property " + property.getKey() + " must match value from discovery",
                    property.getValue(), foundDeliverable.getPropertyValue(property.getKey()));
        }
    }
    
    @Test
    public void testSoaNodeTypeService() throws Exception {
        // Check a few random values of the default contribution
        Assert.assertTrue("The default contributions must be loaded",
                soaNodeTypeService.getChildren(Deliverable.DOCTYPE).contains(ServiceImplementation.DOCTYPE));
        Assert.assertTrue("The default contributions must be loaded",
                soaNodeTypeService.getChildren(DeployedDeliverable.DOCTYPE).contains(Endpoint.DOCTYPE));

        // Test a random path
        Assert.assertArrayEquals("Subtypes chain must be valid", 
                new Object[]{ ServiceImplementation.DOCTYPE, Endpoint.DOCTYPE },
                soaNodeTypeService.getPath(Service.DOCTYPE, Endpoint.DOCTYPE).toArray());
    }
}
