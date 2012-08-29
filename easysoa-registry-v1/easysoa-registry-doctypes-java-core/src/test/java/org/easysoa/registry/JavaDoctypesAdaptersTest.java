package org.easysoa.registry;

import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOAFeature.class, CoreFeature.class})
@Deploy("org.easysoa.registry.doctypes.java.core")
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class JavaDoctypesAdaptersTest extends AbstractRegistryTest {

    @Inject
    DocumentService documentService;
    
    @Test
    public void testMavenAdapter() throws ClientException {
        // Create Maven deliverable
        DocumentModel deliverableModel = documentService.create(documentManager,
                new SoaNodeId(MavenDeliverable.DOCTYPE, "org.easysoa.registry:myartifact"), "My Artifact");
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
