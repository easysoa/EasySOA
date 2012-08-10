package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.EndpointDoctype;
import org.easysoa.registry.types.IntelligentSystemDoctype;
import org.easysoa.registry.types.IntelligentSystemTreeRootDoctype;
import org.easysoa.registry.types.SystemTreeRootDoctype;
import org.easysoa.registry.types.TaggingFolderDoctype;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@LocalDeploy({ "org.easysoa.registry.doctypes.core:OSGI-INF/sample-ist-contrib.xml" })
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class IntelligentSystemTreeTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(IntelligentSystemTreeTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    @Test
    public void testByEnvironmentTree() throws ClientException {
        // Create manual SystemTreeRoot
        DocumentModel strModel = documentService.create(documentManager, SystemTreeRootDoctype.DOCTYPE,
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot", "MyRoot");

        // Create System in it
        DocumentModel systemModel = documentService.create(documentManager, TaggingFolderDoctype.DOCTYPE,
                strModel.getPathAsString(), "MySystem", "MySystem");

        // Create Endpoint in it
        DocumentModel endpointModel  = documentService.create(documentManager, EndpointDoctype.DOCTYPE,
                systemModel.getPathAsString(), "MyEndpoint", "MyEndpoint");
        endpointModel.setPropertyValue(EndpointDoctype.XPATH_ENVIRONMENT, "Production");
        documentManager.saveDocument(endpointModel);
        
        documentManager.save();

        // Make sure that there are now 3 proxies of the endpoint,
        // one in the manual tree, the others in the intelligent trees
        
        // By environment
        
        DocumentModel istrModel = documentService.find(documentManager, IntelligentSystemTreeRootDoctype.DOCTYPE, "byEnvironment:byEnvironment");
        Assert.assertNotNull("A By Environment intelligent system tree root must have been created",
                istrModel);
        Assert.assertEquals("The By Environment STR must contain 1 system", 1, documentManager.getChildren(istrModel.getRef()).size());
        
        DocumentModel productionSystem = documentService.find(documentManager, IntelligentSystemDoctype.DOCTYPE, "Production");
        Assert.assertNotNull("A 'Production' system must have been created", productionSystem);

        DocumentModelList productionSystemChildren = documentManager.getChildren(productionSystem.getRef());
        Assert.assertEquals("The 'Production' system must have a child", 1, productionSystemChildren.size());
        
        DocumentModel childModel = productionSystemChildren.get(0);
        Assert.assertTrue("The document in the 'Production' system must be the expected endpoint",
                EndpointDoctype.DOCTYPE.equals(childModel.getType()) && "MyEndpoint".equals(childModel.getTitle()));
        
        // By alphabetical order
        
        endpointModel = documentManager.getDocument(new PathRef("/default-domain/workspaces/byAlphabeticalOrder:byFirst2Letters/M/Y/MyEndpoint"));
        Assert.assertNotNull("The endpoint must be classified by alphabetical order " +
        		"(= in a multiple-levels hierarchy defined with parameters)", endpointModel);
        
    }
    
}
