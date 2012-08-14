package org.easysoa.registry.systems;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
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

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOADefaultsFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class MavenHierarchyTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(MavenHierarchyTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    @Test
    public void testClassification() throws ClientException {
        // Create manual SystemTreeRoot
        DocumentModel strModel = documentService.create(documentManager, SystemTreeRoot.DOCTYPE,
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot", "MyRoot");

        // Create System in it
        DocumentModel systemModel = documentService.create(documentManager, TaggingFolder.DOCTYPE,
                strModel.getPathAsString(), "MySystem", "MySystem");

        // Create Deliverable in it
        DocumentModel deliverableModel = documentService.create(documentManager, Deliverable.DOCTYPE,
                systemModel.getPathAsString(), "org.easysoa.registry:myartifact", "My Artifact");
        deliverableModel.setPropertyValue(Deliverable.XPATH_NATURE, MavenDeliverable.NATURE);
        documentManager.saveDocument(deliverableModel);
        
        documentManager.save();

        // Make sure that the deliverable is now in the Maven hierarchy
        
        DocumentModel istrModel = documentService.find(documentManager, IntelligentSystemTreeRoot.DOCTYPE, "mavenHierarchy:mavenHierarchy");
        Assert.assertNotNull("A Maven hierarchy intelligent system tree root must have been created",
                istrModel);
        
        // (getChild() throws exceptions when the children are not found)
        DocumentModel firstChild = documentManager.getChild(istrModel.getRef(), "org.easysoa");
        DocumentModel secondChild = documentManager.getChild(firstChild.getRef(), "org.easysoa.registry");
        documentManager.getChild(secondChild.getRef(), "org.easysoa.registry:myartifact");
    }
    
}
