package org.easysoa.registry.rest.marshalling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class SoaNodeInformation {

    private SoaNodeId id;
    
    private Map<String, Object> properties;
    
    private List<SoaNodeId> parentDocuments;
    
    protected SoaNodeInformation() {
        
    }
    
    public SoaNodeInformation(CoreSession documentManager, DocumentModel model) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        this.id = documentService.createSoaNodeId(model);
        this.properties = new HashMap<String, Object>();
        Map<String, Object> schemaProperties;
        for (String schema : model.getSchemas()) {
            if (!"common".equals(schema)) {
                schemaProperties = model.getProperties(schema);
                properties.putAll(schemaProperties);
            }
        }
        
        // Find correlated documents
        this.parentDocuments = new LinkedList<SoaNodeId>();
        DocumentModelList parentDocumentList = documentService.findAllParents(documentManager, model);
        for (DocumentModel parentDocument : parentDocumentList) {
            if (parentDocument.getFacets().contains(SoaNode.FACET)) { 
                this.parentDocuments.add(documentService.createSoaNodeId(parentDocument));
            }
        }
    }
    
    public SoaNodeInformation(SoaNodeId id, Map<String, Object> properties, List<SoaNodeId> parentDocuments) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
        this.parentDocuments = (parentDocuments == null) ? new LinkedList<SoaNodeId>() : parentDocuments;
    }
    
    public SoaNodeId getId() {
        return id;
    }
    
    public void setId(SoaNodeId id) {
        this.id = id;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public List<SoaNodeId> getParentDocuments() {
        return parentDocuments;
    }
    
    public void setParentDocuments(List<SoaNodeId> correlatedDocuments) {
        this.parentDocuments = correlatedDocuments;
    }
    
}
