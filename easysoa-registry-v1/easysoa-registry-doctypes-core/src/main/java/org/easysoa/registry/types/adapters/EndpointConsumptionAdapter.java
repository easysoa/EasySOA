package org.easysoa.registry.types.adapters;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.utils.RelationsHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;


/**
 * 
 * @author mkalam-alami
 *
 */
public class EndpointConsumptionAdapter extends SoaNodeAdapter implements EndpointConsumption {

    private final CoreSession documentManager;

    public EndpointConsumptionAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
        this.documentManager = documentModel.getCoreSession();
    }

    @Override
    public String getDoctype() {
        return DOCTYPE;
    }

    @Override
    public SoaNodeId getConsumedEndpoint() throws Exception {
        DocumentModelList outgoingRelations = RelationsHelper.getOutgoingRelations(documentManager, documentModel, PREDICATE_CONSUMES);
        if (outgoingRelations != null && outgoingRelations.size() > 0) {
            DocumentService documentService = Framework.getService(DocumentService.class);
            return documentService.createSoaNodeId(outgoingRelations.get(0));
        }
        else {
            return null;
        }
    }

    @Override
    public void setConsumedEndpoint(SoaNodeId consumedEndpoint) throws Exception {
        RelationsHelper.deleteOutgoingRelations(documentManager, documentModel, PREDICATE_CONSUMES);
        DocumentService documentService = Framework.getService(DocumentService.class);
        if (consumedEndpoint != null) {
            DocumentModel consumedEndpointModel = documentService.create(documentManager,
                    consumedEndpoint, consumedEndpoint.getName());
            RelationsHelper.createRelation(documentManager, documentModel, PREDICATE_CONSUMES, consumedEndpointModel);
        }
    }
    
}
