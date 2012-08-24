package org.easysoa.registry.rest.utils;

import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.test.AbstractWebEngineTest;

public class DiscoveryApiHelper {

    private final AbstractWebEngineTest test;

    public DiscoveryApiHelper(AbstractWebEngineTest test) {
        this.test = test;
    }
    
    public String getRootURL() {
        return test.getURL(RegistryApi.class);
    }
    
    
}