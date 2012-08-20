package org.easysoa.registry.rest.utils;

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
    
}
