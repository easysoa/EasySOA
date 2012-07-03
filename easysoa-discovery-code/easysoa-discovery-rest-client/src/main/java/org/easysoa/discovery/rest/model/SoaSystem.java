package org.easysoa.discovery.rest.model;


public class SoaSystem extends SoaNode {

    public SoaSystem() {}
    
    public SoaSystem(String name, String version) {
        super("sys_"+name, name, version);
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.SoaSystem;
    }
    
}
