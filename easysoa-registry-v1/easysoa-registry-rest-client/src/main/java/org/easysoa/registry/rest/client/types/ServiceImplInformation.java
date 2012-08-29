package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.ServiceImplementation;

public class ServiceImplInformation extends SoaNodeInformation implements ServiceImplementation {

    public ServiceImplInformation(String name) {
        super(new SoaNodeId(ServiceImplementation.DOCTYPE, name), null, null);
    }
    
}
