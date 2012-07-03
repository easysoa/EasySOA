package org.easysoa.discovery.rest.model;


public class ServiceImpl extends SoaNode {

    public ServiceImpl() {}
    
    public ServiceImpl(Deliverable deliverable, String technology, String fullName, String name) {
        super(deliverable.getId() + "," + technology + ":" + fullName, name, deliverable.getVersion());
        setDeliverableRelation(deliverable);
    }

    public ServiceImpl(Deliverable deliverable, String fullName, String name) {
        super(deliverable.getId() + "," + fullName, name, deliverable.getVersion());
        setDeliverableRelation(deliverable);
    }
    
    public void setDeliverableRelation(Deliverable deliverable) {
        this.setUniqueRelation(SoaNodeType.Deliverable, deliverable.getId());
    }

    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.ServiceImpl;
    }
    
}
