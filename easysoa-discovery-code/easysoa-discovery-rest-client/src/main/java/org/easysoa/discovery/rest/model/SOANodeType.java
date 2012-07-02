package org.easysoa.discovery.rest.model;

public enum SOANodeType {

    SERVICEIMPL,
    DELIVERABLE;
    
    public String toString() {
        switch (this) {
        case DELIVERABLE: return "Deliverable";
        case SERVICEIMPL: return "ServiceImpl";
        }
        return null;
    }
    
}
