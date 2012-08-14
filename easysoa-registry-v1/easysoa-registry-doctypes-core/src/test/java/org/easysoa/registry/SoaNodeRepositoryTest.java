package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
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
public class SoaNodeRepositoryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SoaNodeRepositoryTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    private static DocumentModel strModel;

    private static DocumentModel systemModel;

    private static DocumentModel deliverableModel;
    
    @Test
    public void testRepositoryCreation() throws ClientException {
        // Check that the repository document doesn't exist
        Assume.assumeTrue(!documentManager.exists(Repository.REPOSITORY_REF));

        DocumentModel repositoryInstance = RepositoryHelper.getRepositoryInstance(documentManager);
        Assert.assertNotNull("Repository must be created on first access", repositoryInstance);
    }

    @Test
    public void testDocumentRelocation() throws Exception {
        // Create SystemTreeRoot
        strModel = documentService.create(documentManager,
                new SoaNodeId(SystemTreeRoot.DOCTYPE, "MyRoot"),
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot");

        // Create System in it
        systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString(), "MySystem");

        documentManager.save();

        // Make sure that there are 2 instances of the system
        DocumentModelList systemInstances = documentService.findAllInstances(documentManager,
                systemModel);
        Assert.assertEquals("There must be 2 instances of the system", 2, systemInstances.size());
        boolean hasSystemTreeRootAsParent = false, hasRepositoryAsParent = false;
        for (DocumentModel systemInstance : systemInstances) {
            if (systemInstance.getParentRef().equals(strModel.getRef())) {
                Assert.assertTrue("Created system must be a proxy of the one from the repository",
                        systemInstance.isProxy());
                hasSystemTreeRootAsParent = true;
            } else if (systemInstance.getPathAsString().startsWith(
                    Repository.REPOSITORY_REF.toString())) {
                hasRepositoryAsParent = true;
            }
        }
        Assert.assertTrue("System must be stored under the system tree root",
                hasSystemTreeRootAsParent);
        Assert.assertTrue("System must be stored in the repository", hasRepositoryAsParent);

        // Make sure that the instance in the system tree root is a proxy
        DocumentModel strChild = documentManager.getChild(strModel.getRef(), systemModel.getName());
        Assert.assertTrue("System tree root child must be a proxy", strChild.isProxy());
        
        // Make sure that the system tree root is not in the repository (not a SoaNode)
        DocumentModelList strInstances = documentService.findAllInstances(documentManager, strModel);
        Assert.assertEquals("The system tree root must not have a proxy", 1, strInstances.size());
    }

    @Test
    public void testDuplicatesHandling() throws Exception {
        // Create already created system
        DocumentModel duplicateModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString(), "MySystem");
        
        // Make sure the system created twice still have only one source
        boolean sourceFound = false;
        DocumentModelList allInstances = documentService.findAllInstances(documentManager, duplicateModel);
        for (DocumentModel instance : allInstances) {
            if (instance.getPathAsString().startsWith(Repository.REPOSITORY_PATH)) {
                Assert.assertFalse("System created twice should still have only one source", sourceFound);
                sourceFound = true;
            }
        }
    }
    
    @Test
    public void testProxyCopy() throws Exception {
        // Create new system
        DocumentModel newSystemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem2"),
                strModel.getPathAsString(), "MySystem2");

        // Create deliverable to put in both systems
        deliverableModel = documentService.create(documentManager,
                new SoaNodeId(Deliverable.DOCTYPE, "org.company:mydeliverable"),
                systemModel.getPathAsString(), "MyDeliverable");

        documentManager.save();

        // Copy
        documentService.copy(documentManager, deliverableModel, newSystemModel.getRef());
        
        documentManager.save();

        // Make sure that there are 2 proxies of the document
        DocumentModelList proxies = documentService.findProxies(documentManager, deliverableModel);
        Assert.assertEquals("The deliverable must now have 2 proxies", 2, proxies.size());
    }

    @Test
    public void testSourceCopy() throws Exception {
        // Create a third system
        DocumentModel thirdSystemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem3"),
                strModel.getPathAsString(), "MySystem3");

        // Copy the deployable source into it
        DocumentModel sourceDeployableModel = documentManager.getSourceDocument(deliverableModel.getRef());
        documentService.copy(documentManager, sourceDeployableModel, thirdSystemModel.getRef());
        documentManager.save();

        // Make sure that there are 3 proxies of the document
        DocumentModelList proxies = documentService.findProxies(documentManager, deliverableModel);
        Assert.assertEquals("The deliverable must now have 3 proxies", 3, proxies.size());
    }
    
}