package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRepositoryTest;
import org.easysoa.registry.test.EasySOAFeature;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.easysoa.registry.types.SystemTreeRoot;
import org.easysoa.registry.types.TaggingFolder;
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
public class IntelligentSystemTreeTest extends AbstractRepositoryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(IntelligentSystemTreeTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    @Test
    public void testEnvironmentTrees() throws ClientException {
        // Create manual SystemTreeRoot
        DocumentModel strModel = documentService.createDocument(documentManager,
                SystemTreeRoot.DOCTYPE, "MyRoot",
                DocumentModelHelper.WORKSPACEROOT_REF.toString(), "MyRoot");

        // Create System in it
        DocumentModel systemModel = documentService.create(documentManager,
                new SoaNodeId(TaggingFolder.DOCTYPE, "MySystem"),
                strModel.getPathAsString(), "MySystem");

        // Create Endpoint in it
        DocumentModel endpointModel  = documentService.create(documentManager,
                new SoaNodeId(Endpoint.DOCTYPE, "MyEndpoint"),
                systemModel.getPathAsString(), "MyEndpoint");
        endpointModel.setPropertyValue(Endpoint.XPATH_ENVIRONMENT, "Production");
        documentManager.saveDocument(endpointModel);
        
        documentManager.save();
        
        // Make sure that there are now 3 proxies of the endpoint,
        // one in the manual tree, the others in the intelligent trees
        
        // By environment
        
        DocumentModel istrModel = documentService.findDocument(documentManager,
                IntelligentSystemTreeRoot.DOCTYPE, "environment:environment");
        Assert.assertNotNull("A By Environment intelligent system tree root must have been created",
                istrModel);
        Assert.assertEquals("The By Environment STR must contain 1 system", 1, documentManager.getChildren(istrModel.getRef()).size());
        
        DocumentModel productionSystem = documentService.findDocument(documentManager,
                IntelligentSystem.DOCTYPE, "Production");
        Assert.assertNotNull("A 'Production' system must have been created", productionSystem);

        DocumentModelList productionSystemChildren = documentManager.getChildren(productionSystem.getRef());
        Assert.assertEquals("The 'Production' system must have a child", 1, productionSystemChildren.size());
        
        DocumentModel childModel = productionSystemChildren.get(0);
        Assert.assertTrue("The document in the 'Production' system must be the expected endpoint",
                Endpoint.DOCTYPE.equals(childModel.getType()) && "MyEndpoint".equals(childModel.getTitle()));
        
        // By alphabetical order
        
        endpointModel = documentManager.getDocument(new PathRef("/default-domain/workspaces/alphabeticalOrder:first2Letters/M/Y/MyEndpoint"));
        Assert.assertNotNull("The endpoint must be classified by alphabetical order " +
        		"(= in a multiple-levels hierarchy defined with parameters)", endpointModel);
        
        // Flat documents
        
        endpointModel = documentManager.getDocument(new PathRef("/default-domain/workspaces/everythingFlat:everythingFlat/MyEndpoint"));
        Assert.assertNotNull("The endpoint must be added to the flat document list", endpointModel);
       
    }
    
}
