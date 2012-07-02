package org.easysoa.discovery.rest.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public abstract class SOANode {
    
    protected String id;
    
    protected String name;

    protected String version;
    
    protected Map<String, SOANodeType> relations = new HashMap<String, SOANodeType>(); // <id, type>

    public SOANode(String id) {
        this(id, id, null);
    }
    
    public SOANode(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
    
    public abstract SOANodeType getSOANodeType();
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public void addRelation(SOANodeType soaNodeType, String soaNodeId) {
        this.relations.put(soaNodeId, soaNodeType);
    }

    public void removeRelation(String soaNodeId) {
        this.relations.remove(soaNodeId);
    }
    
    public void setUniqueRelation(SOANodeType soaNodeType, String soaNodeId) {
        this.relations.remove(soaNodeType);
        this.addRelation(soaNodeType, soaNodeId);
    }
    
    public ObjectNode toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        
        // Properties
        rootNode.put("id", this.id);
        rootNode.put("name", this.name);
        rootNode.put("version", this.version);
        rootNode.put("type", this.getSOANodeType().toString());
        
        // Relations
        ObjectNode relationNode = rootNode.putObject("relations");
        for (Entry<String, SOANodeType> relation : relations.entrySet()) {
            relationNode.put(relation.getKey(), relation.getValue().toString());
        }
        
        return rootNode;
    }
    
}
