package org.easysoa.registry;

import org.easysoa.registry.services.DocumentService;
import org.easysoa.registry.services.DocumentServiceImpl;

import com.google.inject.AbstractModule;

public class EasySOADoctypesModule extends AbstractModule {
    
    @Override 
    protected void configure() {
      bind(DocumentService.class).to(DocumentServiceImpl.class);
    }
    
}