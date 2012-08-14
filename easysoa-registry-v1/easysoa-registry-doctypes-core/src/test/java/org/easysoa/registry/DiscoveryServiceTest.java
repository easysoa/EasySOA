package org.easysoa.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.EasySOAFeature;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.junit.Assert;

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
    
    @Test
    @Ignore // TODO implement discovery
    public void testSimpleDiscovery() throws ClientException {
        Assert.assertNotNull("Discovery service must be available", discoveryService);
        
        // Gather discovery information
        SoaNodeId discoveredDeliverableId = new SoaNodeId("Deliverable", "org.easysoa.registry:myartifact");
        Map<String, String> properties = new HashMap<String, String>();
        
        // Run discovery
        discoveryService.importDiscovery(documentManager, discoveredDeliverableId, properties, null);
        
        // Check results
        DocumentModel foundDeliverable = documentService.find(documentManager, discoveredDeliverableId);
        Assert.assertNotNull("A deliverable must be created by the discovery processing", foundDeliverable);
        for (Entry<String, String> property : properties.entrySet()) {
            Assert.assertEquals("Property " + property.getKey() + " must match value from discovery",
                    property.getValue(), foundDeliverable.getPropertyValue(property.getKey()));
        }
        
    }
}
