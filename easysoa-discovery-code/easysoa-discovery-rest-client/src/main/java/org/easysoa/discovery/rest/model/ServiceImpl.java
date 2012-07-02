package org.easysoa.discovery.rest.model;


public class ServiceImpl extends SOANode {
    
    public ServiceImpl(Deliverable deliverable, String technology, String fullName, String name) {
        super(deliverable.getId() + "," + technology + ":" + fullName, name, deliverable.getVersion());
        setDeliverableRelation(deliverable);
    }

    public ServiceImpl(Deliverable deliverable, String fullName, String name) {
        super(deliverable.getId() + "," + fullName, name, deliverable.getVersion());
        setDeliverableRelation(deliverable);
    }
    
    public void setDeliverableRelation(Deliverable deliverable) {
        this.setUniqueRelation(SOANodeType.DELIVERABLE, deliverable.getId());
    }

    @Override
    public SOANodeType getSOANodeType() {
        return SOANodeType.SERVICEIMPL;
    }
    
}
