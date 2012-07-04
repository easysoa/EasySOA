package org.easysoa.discovery.rest.model;


public class Service extends SoaNode {
    
    public Service() {}

    public Service(String name, String version) {
        super(name, name, version);
    }
    
    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.Service;
    }
    
}
