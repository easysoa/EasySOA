package org.easysoa.registry;

import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.ServiceImplementation;
import org.easysoa.registry.types.SoftwareComponent;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.SchemaManager;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
public class DoctypesTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DoctypesTest.class);

    @Inject
    SchemaManager schemaManager;

    @Inject
    DocumentService documentService;
    
    @Test
    public void testDoctypesAvailability() throws ClientException {
        
        String[] doctypes = new String[] {
                Deliverable.DOCTYPE,
                DeployedDeliverable.DOCTYPE,
                Endpoint.DOCTYPE,
                IntelligentSystem.DOCTYPE,
                IntelligentSystemTreeRoot.DOCTYPE,
                Repository.DOCTYPE,
                Service.DOCTYPE,
                ServiceImplementation.DOCTYPE,
                SoftwareComponent.DOCTYPE,
                SystemTreeRoot.DOCTYPE,
                TaggingFolder.DOCTYPE
        };
        
        for (String doctype : doctypes) {
            Assert.assertNotNull("Doctype " + doctype + " must exist", schemaManager.getDocumentType(doctype));
        }
        
    }

    @Test
    public void testDocumentAdapters() throws ClientException {
     
        // Create a deliverable
        DocumentModel deliverableModel = documentService.create(documentManager, 
                new SoaNodeId(Deliverable.DOCTYPE, "MyDeliverable"),
                "/default-domain/workspaces", "MyDeliverable");
        
        // Use its adapter
        Deliverable deliverable = deliverableModel.getAdapter(Deliverable.class);
        Assert.assertNotNull("Document model must be adapted as a deliverable", deliverable);
        
    }
    
    @Test
    public void testServiceImplOperations() throws Exception {

        // Create ServiceImpl
        SoaNodeId serviceImplId = new SoaNodeId(ServiceImplementation.DOCTYPE, "MyServiceImpl");
        DocumentModel serviceImplModel = documentService.create(documentManager, 
                serviceImplId, "My Service Impl");
        
        // Use adapter to manipulate operations
        ServiceImplementation serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
        List<OperationImplementation> operations = serviceImpl.getOperations();
        operations.add(new OperationImplementation("Yo", "Param1, Param2", "This does something"));
        serviceImpl.setOperations(operations);
        
        // Save
        documentManager.saveDocument(serviceImplModel);
        documentManager.save();
        
        // Fetch document again, check operations update
        serviceImplModel = documentService.find(documentManager, serviceImplId);
        serviceImpl = serviceImplModel.getAdapter(ServiceImplementation.class);
        operations = serviceImpl.getOperations();
        Assert.assertEquals(1, operations.size());
        Assert.assertEquals("Yo", operations.get(0).getName());
        Assert.assertEquals("Param1, Param2", operations.get(0).getParameters());
        Assert.assertEquals("This does something", operations.get(0).getDocumentation());
    }
}
