package org.easysoa.registry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 */
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class DiscoveryServiceTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DiscoveryServiceTest.class);

    @Inject
    DocumentService documentService;

    @Inject
    DiscoveryService discoveryService;
    
    @Inject
    SoaMetamodelService soaMetamodelService;

    private static SoaNodeId discoveredDeliverableId;

    private static Map<String, Object> properties;
    
    private static DocumentModel foundDeliverable;
    
    @Test
    public void testSimpleDiscovery() throws Exception {
        // Gather discovery information
        discoveredDeliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa.registry:myartifact");
        properties = new HashMap<String, Object>();
        properties.put(Deliverable.XPATH_TITLE, "My Artifact");
        properties.put(Deliverable.XPATH_APPLICATION, "myapp");
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredDeliverableId, properties, null);
        documentManager.save();
        
        // Check results
        foundDeliverable = documentService.find(documentManager, discoveredDeliverableId);
        Assert.assertNotNull("A deliverable must be created by the discovery processing", foundDeliverable);
        for (Entry<String, Object> property : properties.entrySet()) {
            Assert.assertEquals("Property " + property.getKey() + " must match value from discovery",
                    property.getValue(), foundDeliverable.getPropertyValue(property.getKey()));
        }
    }
    
    @Test
    public void testSoaNodeTypeService() throws Exception {
        // Check a few random values of the default contribution
        Assert.assertTrue("The default contributions must be loaded",
                soaMetamodelService.getChildren(Deliverable.DOCTYPE).contains(ServiceImplementation.DOCTYPE));
        Assert.assertTrue("The default contributions must be loaded",
                soaMetamodelService.getChildren(DeployedDeliverable.DOCTYPE).contains(Endpoint.DOCTYPE));

        // Test a random path
        Assert.assertArrayEquals("Subtypes chain must be valid", 
                new Object[]{ ServiceImplementation.DOCTYPE, Endpoint.DOCTYPE },
                soaMetamodelService.getPath(Service.DOCTYPE, Endpoint.DOCTYPE).toArray());
    }
    
    @Test
    public void testCorrelationDiscovery() throws Exception {
        // Add correlation information
        List<SoaNodeId> correlatedDocuments = new LinkedList<SoaNodeId>();
        SoaNodeId endpointId = new SoaNodeId(Endpoint.DOCTYPE, "http://myapp.com/service");
        correlatedDocuments.add(endpointId);
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "myserviceimpl");
        correlatedDocuments.add(serviceImplId);
        SoaNodeId serviceId = new SoaNodeId(Service.DOCTYPE, "myservice");
        correlatedDocuments.add(serviceId);
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredDeliverableId, null, correlatedDocuments);
        documentManager.save();
        
        // Check results
        DocumentModel foundEndpoint = documentService.find(documentManager, endpointId);
        Assert.assertNotNull("An endpoint must be created by the discovery processing", foundEndpoint);

        Assert.assertTrue("The correlated documents must be stored under the deliverable",
                documentManager.hasChildren(foundDeliverable.getRef()));
        
        DocumentModel foundServiceImpl = documentService.find(documentManager, serviceImplId);
        Assert.assertTrue("The service implementation must contain the endpoint when both are specified for correlation",
                documentManager.hasChildren(foundServiceImpl.getRef()));
        
        DocumentModel foundService = documentService.find(documentManager, serviceId);
        Assert.assertTrue("The service must link to the deliverable through its the service implementation",
                documentManager.hasChildren(foundService.getRef()));
    }
    
}
