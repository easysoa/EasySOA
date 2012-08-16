package org.easysoa.registry.systems;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.test.AbstractRepositoryTest;
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
public class EnvironmentClassifierTest extends AbstractRepositoryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(EnvironmentClassifierTest.class);

    @Inject
    CoreSession documentManager;

    @Inject
    DocumentService documentService;

    @Test
    public void testClassification() throws ClientException {
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

        // Make sure that there are now the endpoint in the By Environment tree
        
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
        
    }
    
}
