package org.easysoa.discovery.rest.model;


public class DeployedDeliverable extends SoaNode {

    public DeployedDeliverable() {}
    
    public DeployedDeliverable(String id, String name, String version) {
        super(id, name, version);
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.DeployedDeliverable;
    }
    
}
