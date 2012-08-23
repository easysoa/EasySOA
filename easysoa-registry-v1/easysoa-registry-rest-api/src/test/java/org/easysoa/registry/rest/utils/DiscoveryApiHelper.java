package org.easysoa.registry.rest.utils;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.test.AbstractWebEngineTest;

public class DiscoveryApiHelper {

    private final AbstractWebEngineTest test;

    public DiscoveryApiHelper(AbstractWebEngineTest test) {
        this.test = test;
    }
    
    public String getServiceURL(String doctype) {
        return test.getURL(RegistryApi.class) + "/" + doctype;
    }
    
    public String getServiceURL(String doctype, String name) {
        return getServiceURL(doctype) + "/" + name;
    }

    public String getServiceURL(SoaNodeId id) {
        return getServiceURL(id.getType(), id.getName());
    }
    
    public String getServiceURL(SoaNodeId id, SoaNodeId correlatedId) {
        return getServiceURL(id.getType(), id.getName()) + "/" + correlatedId.getType() + "/" + correlatedId.getName();
    }
    
}
