package org.easysoa.registry.rest.marshalling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class SoaNodeInformation {

    private final SoaNodeId id;
    
    private List<SoaNodeId> correlatedDocuments;
    
    private final Map<String, Object> properties;

    public SoaNodeInformation(CoreSession documentManager, DocumentModel model) throws Exception {
        this.id = new SoaNodeId(model);
        this.properties = new HashMap<String, Object>();
        Map<String, Object> schemaProperties;
        for (String schema : model.getSchemas()) {
            if (!"common".equals(schema)) {
                schemaProperties = model.getProperties(schema);
                properties.putAll(schemaProperties);
            }
        }
        
        // Find correlated documents
        DocumentService documentService = Framework.getService(DocumentService.class);
        DocumentModelList correlatedDocuments = documentService.findAllParents(documentManager, model);
        if (model.isProxy()) {
            model = documentService.find(documentManager, id);
        }
        correlatedDocuments.addAll(documentManager.getChildren(model.getRef()));
        this.correlatedDocuments = new LinkedList<SoaNodeId>();
        for (DocumentModel correlatedDocument : correlatedDocuments) {
            if (correlatedDocument.getFacets().contains("SoaNode")) {
                this.correlatedDocuments.add(new SoaNodeId(correlatedDocument));
            }
        }
    }
    
    public SoaNodeInformation(SoaNodeId id, Map<String, Object> properties, List<SoaNodeId> correlatedDocuments) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
        this.correlatedDocuments = (correlatedDocuments == null) ? new LinkedList<SoaNodeId>() : correlatedDocuments;
    }
    
    public SoaNodeId getId() {
        return id;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public List<SoaNodeId> getCorrelatedDocuments() {
        return correlatedDocuments;
    }
    
}
