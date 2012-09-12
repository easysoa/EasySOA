package org.easysoa.registry.types.adapters;

import java.util.LinkedList;
import java.util.List;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.EndpointConsumption;
import org.easysoa.registry.types.ServiceImplementation;
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
        return EndpointConsumption.DOCTYPE;
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
            DocumentModel consumedEndpointModel = documentService.create(documentManager, consumedEndpoint);
            RelationsHelper.createRelation(documentManager, documentModel, PREDICATE_CONSUMES, consumedEndpointModel);
        }
    }

    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
        List<SoaNodeId> consumableServiceImpls = new LinkedList<SoaNodeId>();
        DocumentService documentService = Framework.getService(DocumentService.class);
        DocumentModel foundEndpoint = documentService.find(documentManager, getConsumedEndpoint());
        DocumentModelList endpointParents = documentService.findAllParents(documentManager, foundEndpoint);
        for (DocumentModel endpointParent : endpointParents) {
            if (ServiceImplementation.DOCTYPE.equals(endpointParent)) {
                consumableServiceImpls.add(documentService.createSoaNodeId(endpointParent));
                break;
            }
        }
        return consumableServiceImpls;
    }
    
}
