package org.easysoa.boilerplate;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.RepositoryDoctype;
import org.easysoa.registry.types.SystemDoctype;
import org.easysoa.registry.types.SystemTreeRootDoctype;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(EasySOADoctypesFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class RepositoryDoctypeTest {

    public static final DocumentRef WORKSPACEROOT_REF = new PathRef("/default-domain/workspaces");
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(RepositoryDoctypeTest.class);

    @Inject
    CoreSession documentManager;

    @Test
    public void testRepositoryCreation() throws ClientException {
        // Check that the repository document doesn't exist
        Assume.assumeTrue(!documentManager.exists(RepositoryDoctype.REPOSITORY_REF));

        DocumentModel repositoryInstance = RepositoryDoctype.getRepositoryInstance(documentManager);
        Assert.assertNotNull("Repository should be created on first access", repositoryInstance);
    }

    @Test
    public void testDocumentRelocation() throws Exception {
        // Create SystemTreeRoot
        DocumentModel strModel = DocumentModelHelper.createDocument(documentManager, SystemTreeRootDoctype.DOCTYPE,
                WORKSPACEROOT_REF.toString(), "MyRoot", "MyRoot");
        
        // Create System in it
        DocumentModel systemModel = DocumentModelHelper.createDocument(documentManager, SystemDoctype.DOCTYPE,
                strModel.getPathAsString(), "MySystem", "MySystem");
        
        documentManager.save();
        
        // Make sure that there is 2 instances of the document
        DocumentModelList systemParents = DocumentModelHelper.getParents(documentManager, systemModel);
        Assert.assertEquals("There should be 2 instances of the system", 2, systemParents.size());
        boolean hasSystemTreeRootAsParent = false, hasRepositoryAsParent = false;
        for (DocumentModel systemParent : systemParents) {
            if (systemParent.getRef().equals(strModel.getRef())) {
                hasSystemTreeRootAsParent = true;
            }
            else if (systemParent.getPathAsString().equals(RepositoryDoctype.REPOSITORY_REF.toString())) {
                hasRepositoryAsParent = true;
            }
        }
        Assert.assertTrue("System should be stored under the system tree root", hasSystemTreeRootAsParent);
        Assert.assertTrue("System should be stored in the repository", hasRepositoryAsParent);
        
        // Make sure that the instance in the system tree root is a proxy
        DocumentModel strChild = documentManager.getChild(strModel.getRef(), systemModel.getName());
        Assert.assertTrue("System tree root child should be a proxy", strChild.isProxy());
    }

}
