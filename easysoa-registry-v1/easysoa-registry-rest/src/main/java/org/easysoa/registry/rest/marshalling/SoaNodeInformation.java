package org.easysoa.registry.rest.marshalling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.SoaNodeId;

public class SoaNodeInformation {

    private final SoaNodeId id;
    
    private List<SoaNodeId> correlatedDocuments;
    
    private final Map<String, Object> properties;
    
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
