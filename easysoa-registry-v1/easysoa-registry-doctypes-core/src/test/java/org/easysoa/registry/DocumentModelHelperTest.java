package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRepositoryTest;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.TaggingFolder;
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
@Features(EasySOAFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class DocumentModelHelperTest extends AbstractRepositoryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(DocumentModelHelperTest.class);

    @Inject
    CoreSession documentManager;
    
    @Inject
    DocumentService documentService;
    
    @Test
    public void testModelCreation() throws ClientException {
        DocumentModel systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                DocumentModelHelper.WORKSPACEROOT_REF.toString(),
                "MySystemTitle");
        documentManager.save();
        Assert.assertNotNull(systemModel);
        Assert.assertEquals(systemModel.getName(), "MySystem");
        Assert.assertEquals(systemModel.getTitle(), "MySystemTitle");
    }

    @Test
    public void testModelQuery() throws ClientException {
        DocumentModel systemModel = documentService.find(documentManager, new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"));
        Assert.assertNotNull("Created system must be found by name", systemModel);
        Assert.assertEquals(systemModel.getTitle(), "MySystemTitle");
        Assert.assertTrue("Returned document must be in the repository, in the System folder",
                systemModel.getPathAsString().startsWith(Repository.REPOSITORY_REF.toString()));
    }
}
