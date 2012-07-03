package org.easysoa.discovery.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SoaNode {
    
    protected String id;
    
    protected String name;

    protected String version;

    protected List<String> requirements = new ArrayList<String>();
    
    protected Map<String, SoaNodeType> relations = new HashMap<String, SoaNodeType>(); // <id, type>

    public SoaNode() {}
    
    public SoaNode(String id) {
        this(id, id, null);
    }
    
    public SoaNode(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
    
    public abstract SoaNodeType getSoaNodeType();
    
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
    
    public Map<String, SoaNodeType> getRelations() {
        return relations;
    }
    
    public void setRelations(Map<String, SoaNodeType> relations) {
        this.relations = relations;
    }
    
    public void addRelation(SoaNodeType soaNodeType, String soaNodeId) {
        this.relations.put(soaNodeId, soaNodeType);
    }
    
    public void addRelation(SoaNode soaNode) {
        this.relations.put(soaNode.getId(), soaNode.getSoaNodeType());
    }

    public void removeRelation(String soaNodeId) {
        this.relations.remove(soaNodeId);
    }
    
    public void removeRelation(SoaNode soaNode) {
        this.relations.remove(soaNode.getId());
    }
    
    public void setUniqueRelation(SoaNodeType soaNodeType, String soaNodeId) {
        this.relations.remove(soaNodeType);
        this.addRelation(soaNodeType, soaNodeId);
    }

    public void setUniqueRelation(SoaNode soaNode) {
        this.relations.remove(soaNode.getSoaNodeType());
        this.addRelation(soaNode.getSoaNodeType(), soaNode.getId());
    }
    
    public List<String> getRequirements() {
        return requirements;
    }
    
    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public void addRequirement(String requirement) {
        this.requirements.add(requirement);
    }
    
    public void removeRequirement(String requirement) {
        this.requirements.remove(requirement);
    }
    
    @Override
    public String toString() {
        return getSoaNodeType().toString() + ": " + name + " (v" + version + ")";
    }

    
}
