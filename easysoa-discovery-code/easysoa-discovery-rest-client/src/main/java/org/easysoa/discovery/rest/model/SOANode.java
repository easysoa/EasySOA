package org.easysoa.discovery.rest.model;

public abstract class SOANode {
    
    protected String id;
    
    protected String name;

    protected String version;

    public SOANode(String id) {
        this(id, id, null);
    }
    
    public SOANode(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
    
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
    
}
