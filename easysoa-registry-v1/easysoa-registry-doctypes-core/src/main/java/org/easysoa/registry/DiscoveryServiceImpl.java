package org.easysoa.registry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

public class DiscoveryServiceImpl implements DiscoveryService {

    public DocumentModel importDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, String> properties, List<SoaNodeId> correlatedDocuments) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        // Fetch or create document
        DocumentModel documentModel = documentService.find(documentManager, identifier);
        if (documentModel == null) {
            documentModel = documentService.create(documentManager, identifier, identifier.getName());
        }
        
        // Set properties
        for (Entry<String, String> property : properties.entrySet()) {
            documentModel.setPropertyValue(property.getKey(), property.getValue());
        }
        documentManager.saveDocument(documentModel);
        
        // Link to correlated documents
        if (correlatedDocuments != null && !correlatedDocuments.isEmpty()) {
//            SoaNodeTypeService soaNodeTypeService = Framework.getService(SoaNodeTypeService.class);
//            for (SoaNodeId correlatedDocument : correlatedDocuments) {
//                // TODO
//            }
        }
        
        return documentModel;
    }

}
