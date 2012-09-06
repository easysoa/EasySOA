package org.easysoa.registry;

import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.easysoa.registry.types.java.utils.JavaDoctypesHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;

import com.google.inject.Inject;

/**
 * 
 * Tests for Java service consumptions & Java service implementations
 * 
 * @author mkalam-alami
 *
 */
@Deploy("org.easysoa.registry.doctypes.java.core")
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class JavaServicesTest extends AbstractRegistryTest {

    private static final String WS_INTERFACE = "org.easysoa.WSItf";
    @Inject
    DocumentService documentService;
    
    @Test
    public void testServiceImplementationAndConsumption() throws ClientException {
        // Create documents
        SoaNodeId consumptionId = new SoaNodeId(JavaServiceConsumption.DOCTYPE, "Consumption");
        DocumentModel consumptionModel = documentService.create(documentManager, consumptionId);
        consumptionModel.setPropertyValue(JavaServiceConsumption.XPATH_CONSUMEDINTERFACE, WS_INTERFACE);
        documentManager.saveDocument(consumptionModel);

        SoaNodeId implementationId = new SoaNodeId(JavaServiceImplementation.DOCTYPE, "Implementation");
        DocumentModel implementationModel = documentService.create(documentManager, implementationId);
        implementationModel.setPropertyValue(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, WS_INTERFACE);
        documentManager.saveDocument(implementationModel);
        
        documentManager.save();
        
        // Use helper to find implementations that match the consumption
        consumptionModel = documentService.find(documentManager, consumptionId);
        DocumentModelList matchingServiceImpls = JavaDoctypesHelper.getMatchingServiceImpls(documentManager,
                (String) consumptionModel.getPropertyValue(JavaServiceConsumption.XPATH_CONSUMEDINTERFACE));
        Assert.assertEquals(1, matchingServiceImpls.size());
        Assert.assertEquals(implementationModel.getId(), matchingServiceImpls.get(0).getId());
        
    }
    
}
