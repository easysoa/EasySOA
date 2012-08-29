package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Service;

public class ServiceInformation extends SoaNodeInformation implements Service {

    public ServiceInformation(String name) {
        super(new SoaNodeId(Service.DOCTYPE, name), null, null);
    }
    
}
