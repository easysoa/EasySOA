package org.easysoa.discovery.rest.model;


public class System extends SoaNode {

    public System() {}
    
    public System(String name, String version) {
        super("sys_"+name, name, version);
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.System;
    }
    
}
