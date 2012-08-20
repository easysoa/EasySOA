package org.easysoa.registry.rest;

import org.nuxeo.ecm.webengine.app.WebEngineModule;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class EasySOAModule extends WebEngineModule {

    @Override
    public Class<?>[] getWebTypes() {
        return new Class<?>[] { RegistryApi.class };
    }

}
