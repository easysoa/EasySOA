package org.easysoa.registry.rest.utils;

import org.easysoa.registry.rest.AbstractWebEngineTest;
import org.easysoa.registry.rest.RegistryApi;

public class DiscoveryApiHelper {

    private final AbstractWebEngineTest test;

    public DiscoveryApiHelper(AbstractWebEngineTest test) {
        this.test = test;
    }
    
    public String getRootURL() {
        return test.getURL(RegistryApi.class);
    }
    
    
}