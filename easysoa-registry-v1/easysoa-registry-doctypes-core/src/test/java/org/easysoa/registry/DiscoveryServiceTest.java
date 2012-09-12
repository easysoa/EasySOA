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
import org.junit.Ignore;
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

    private static SoaNodeId discoveredEndpointId;

    private static Map<String, Object> properties;
    
    private static DocumentModel foundDeliverable;
    
    @Test
    public void testSimpleDiscovery() throws Exception {
        // Gather discovery information
        discoveredEndpointId = new SoaNodeId(Endpoint.DOCTYPE, "http://www.services.com/endpoint");
        properties = new HashMap<String, Object>();
        properties.put(Endpoint.XPATH_TITLE, "My Endpoint");
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredEndpointId, properties, null);
        documentManager.save();
        
        // Check results
        foundDeliverable = documentService.find(documentManager, discoveredEndpointId);
        Assert.assertNotNull("An endpoint must be created by the discovery processing", foundDeliverable);
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
    @Ignore // FIXME
    public void testCorrelationDiscovery() throws Exception {
        // Add correlation information
        List<SoaNodeId> parentDocuments = new LinkedList<SoaNodeId>();
        SoaNodeId deliverableId = new SoaNodeId(Deliverable.DOCTYPE, "org.easysoa.services:myservices");
        parentDocuments.add(deliverableId);
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "myserviceimpl");
        parentDocuments.add(serviceImplId);
        SoaNodeId serviceId = new SoaNodeId(Service.DOCTYPE, "myservice");
        parentDocuments.add(serviceId);
        
        // Run discovery
        discoveryService.runDiscovery(documentManager, discoveredEndpointId, null, parentDocuments);
        documentManager.save();
        
        // Check results
        DocumentModel foundService = documentService.find(documentManager, serviceId);
        Assert.assertTrue(serviceId + " must be linked to " + serviceImplId, 
                documentService.hasChild(documentManager, foundService, serviceImplId));
        
        
        DocumentModel foundDeliverable = documentService.find(documentManager, deliverableId);
        Assert.assertTrue(deliverableId + " must be linked to " + serviceImplId, 
                documentService.hasChild(documentManager, foundDeliverable, serviceImplId));
        
        DocumentModel foundServiceImpl = documentService.find(documentManager, serviceImplId);
        Assert.assertTrue(serviceImplId + " must be linked to " + discoveredEndpointId, 
                documentService.hasChild(documentManager, foundServiceImpl, discoveredEndpointId));
        
    }
    
}
