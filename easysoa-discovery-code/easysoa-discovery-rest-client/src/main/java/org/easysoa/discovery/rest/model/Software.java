package org.easysoa.discovery.rest.model;


public class Software extends SoaNode {

    public Software() {}
    
    public Software(String name, String version) {
        super(name, name, version);
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.Software;
    }
    
}
