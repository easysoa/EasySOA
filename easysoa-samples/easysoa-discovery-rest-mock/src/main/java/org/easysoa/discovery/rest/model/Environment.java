package org.easysoa.discovery.rest.model;


public class Environment extends SoaNode {

    public Environment() {}
    
    public Environment(String name, String version) {
        super(name, name, version);
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.Environment;
    }
    
}
