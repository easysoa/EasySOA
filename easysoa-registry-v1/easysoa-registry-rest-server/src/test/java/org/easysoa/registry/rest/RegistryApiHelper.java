package org.easysoa.registry.rest;

import org.easysoa.registry.rest.AbstractRestApiTest;
import org.easysoa.registry.rest.RegistryApiImpl;

public class RegistryApiHelper {

    private final AbstractRestApiTest test;

    public RegistryApiHelper(AbstractRestApiTest test) {
        this.test = test;
    }
    
    public String getRootURL() {
        return test.getURL(RegistryApiImpl.class);
    }
    
    
}