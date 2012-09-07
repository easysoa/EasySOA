package org.easysoa.registry.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class SoaNodeInformationFactory {

    private SoaNodeInformationFactory() {}
    
    public static SoaNodeInformation create(
            CoreSession documentManager, DocumentModel model) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        // ID
        SoaNodeId id = documentService.createSoaNodeId(model);
        
        // Properties
        HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
        Map<String, Object> schemaProperties;
        for (String schema : model.getSchemas()) {
            if (!"common".equals(schema)) {
                schemaProperties = model.getProperties(schema);
                for (Entry<String, Object> entry : schemaProperties.entrySet()) {
                    if (entry.getValue() instanceof Serializable) {
                        properties.put(entry.getKey(), (Serializable) entry.getValue());
                    }
                }
            }
        }
        
        // Parent SoaNodes
        List<SoaNodeId> parentDocuments = new LinkedList<SoaNodeId>();
        DocumentModelList parentDocumentList = documentService.findAllParents(documentManager, model);
        for (DocumentModel parentDocument : parentDocumentList) {
            if (parentDocument.getFacets().contains(SoaNode.FACET)) { 
                parentDocuments.add(documentService.createSoaNodeId(parentDocument));
            }
        }
        
        return new SoaNodeInformation(id, properties, parentDocuments);
    }
    
}
