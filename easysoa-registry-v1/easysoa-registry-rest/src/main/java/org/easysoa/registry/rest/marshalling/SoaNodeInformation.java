package org.easysoa.registry.rest.marshalling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.SoaNodeId;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SoaNodeInformation {

    private final SoaNodeId id;
    
    private List<SoaNodeId> correlatedDocuments;
    
    private final Map<String, Object> properties;

    public SoaNodeInformation(CoreSession documentManager, DocumentModel model) throws ClientException {
        this.id = SoaNodeId.fromModel(model);
        this.properties = new HashMap<String, Object>();
        Map<String, Object> schemaProperties;
        for (String schema : model.getSchemas()) {
            if (!"common".equals(schema)) {
                schemaProperties = model.getProperties(schema);
                properties.putAll(schemaProperties);
            }
        }
        this.correlatedDocuments = null; // TODO
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
