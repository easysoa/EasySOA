package org.easysoa.registry;

import org.apache.log4j.Logger;
import org.easysoa.registry.test.AbstractRegistryTest;
import org.easysoa.registry.types.Endpoint;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.utils.RelationsHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;

import com.google.inject.Inject;

/**
 * 
 * @author mkalam-alami
 */
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.CLASS)
public class RelationsTest extends AbstractRegistryTest {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(RelationsTest.class);

    @Inject
    DocumentService documentService;

    @Test
    public void testRelationCreationAndAccess() throws Exception {
        DocumentModel endpointConsumptionModel = documentService.create(documentManager, new SoaNodeId(
                EndpointConsumption.DOCTYPE, "Foo"));
        DocumentModel endpointModel = documentService.create(documentManager, new SoaNodeId(Endpoint.DOCTYPE, "Bar"));

        // Create and read relation
        
        boolean creationSuccess = RelationsHelper.createRelation(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES, endpointModel);
        Assert.assertTrue("Relation creation must be successful", creationSuccess);

        DocumentModelList accessResult = RelationsHelper.getOutgoingRelations(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES);
        Assert.assertEquals("Relation creation & access must be successful", 1, accessResult.size());
        
        accessResult = RelationsHelper.getIncomingRelations(documentManager, endpointModel,
                EndpointConsumption.PREDICATE_CONSUMES);
        Assert.assertEquals("Alternate relation access must be successful", 1, accessResult.size());
        
        // Delete it and re-read it
        
        boolean deletionSuccess = RelationsHelper.deleteRelation(documentManager, endpointConsumptionModel, 
                EndpointConsumption.PREDICATE_CONSUMES, endpointModel);
        Assert.assertTrue("Relation deletion must be successful", deletionSuccess);
        
        accessResult = RelationsHelper.getOutgoingRelations(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES);
        Assert.assertEquals("Relation deletion must be effective", 0, accessResult.size());

        // Re-create it and delete it in other ways
        
        RelationsHelper.createRelation(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES, endpointModel);
        RelationsHelper.deleteOutgoingRelations(documentManager, endpointConsumptionModel, 
                EndpointConsumption.PREDICATE_CONSUMES);
        accessResult = RelationsHelper.getOutgoingRelations(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES);
        Assert.assertEquals("Alternate relation deletion must be effective", 0, accessResult.size());

        RelationsHelper.createRelation(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES, endpointModel);
        RelationsHelper.deleteIncomingRelations(documentManager, endpointModel, 
                EndpointConsumption.PREDICATE_CONSUMES);
        accessResult = RelationsHelper.getOutgoingRelations(documentManager, endpointConsumptionModel,
                EndpointConsumption.PREDICATE_CONSUMES);
        Assert.assertEquals("Alternate relation deletion must be effective", 0, accessResult.size());
        
        
    }

}
