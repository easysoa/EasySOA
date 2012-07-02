package org.easysoa.discovery.rest.model;

public class ServiceImpl extends SOANode {
    
    public ServiceImpl(Deliverable deliverable, String technology, String name, String version) {
        super(deliverable.getId() + ", " + technology + ":" + name, name, version);
    }

    public ServiceImpl(Deliverable deliverable, String name, String version) {
        super(deliverable.getId() + ", " + name, name, version);
    }
    
}
