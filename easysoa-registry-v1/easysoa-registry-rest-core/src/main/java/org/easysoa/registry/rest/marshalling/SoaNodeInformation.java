package org.easysoa.registry.rest.marshalling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.SoaNodeId;

public class SoaNodeInformation {

    private SoaNodeId id;
    
    private Map<String, Object> properties;
    
    private List<SoaNodeId> parentDocuments;
    
    protected SoaNodeInformation() {
        
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
