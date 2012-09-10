package org.easysoa.discovery.rest.model;


public class ServiceImpl extends SoaNode {

    String operationsInfo;
    
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

    public String getOperationsInfo() {
        return operationsInfo;
    }
    
    public void setOperationsInfo(String operationsInfo) {
        this.operationsInfo = operationsInfo;
    }
    
    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.ServiceImpl;
    }
    
}
