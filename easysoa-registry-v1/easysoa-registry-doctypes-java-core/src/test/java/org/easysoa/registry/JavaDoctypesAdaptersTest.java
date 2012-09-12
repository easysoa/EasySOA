package org.easysoa.registry;

import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
@Deploy("org.easysoa.registry.doctypes.java.core")
public class JavaDoctypesAdaptersTest extends AbstractRegistryTest {

    @Inject
    DocumentService documentService;

    @Inject
    SchemaManager schemaManager;
    
    @Test
    public void testDoctypes() throws ClientException {
        Assert.assertNotNull(schemaManager.getDocumentType(JavaServiceImplementation.DOCTYPE));
        Assert.assertNotNull(schemaManager.getDocumentType(JavaServiceConsumption.DOCTYPE));
    }
    
    @Test
    public void testMavenAdapter() throws ClientException {
        // Create Maven deliverable
        DocumentModel deliverableModel = documentService.create(documentManager,
                new SoaNodeId(MavenDeliverable.DOCTYPE, "org.easysoa.registry:myartifact"));
        Assert.assertNotNull("Document service must successfully create the deliverable", deliverableModel);
        
        deliverableModel.setPropertyValue(Deliverable.XPATH_NATURE, MavenDeliverable.NATURE);
        documentManager.saveDocument(deliverableModel);
        documentManager.save();
        
        // Use the adapter
        MavenDeliverable mavenDeliverable = deliverableModel.getAdapter(MavenDeliverable.class);
        Assert.assertNotNull("Maven deliverable adapter must be available", mavenDeliverable);
        Assert.assertEquals("org.easysoa.registry", mavenDeliverable.getGroupId());
        Assert.assertEquals("myartifact", mavenDeliverable.getArtifactId());
    }
    
}
