package org.easysoa.registry;

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
                OperationImplementation.DOCTYPE,
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
}
