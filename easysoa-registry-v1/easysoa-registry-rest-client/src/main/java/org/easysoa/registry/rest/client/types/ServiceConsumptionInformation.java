package org.easysoa.registry.rest.client.types;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.ServiceConsumption;

public class ServiceConsumptionInformation extends SoaNodeInformation implements ServiceConsumption {
    
    public ServiceConsumptionInformation(String name) {
        super(new SoaNodeId(ServiceConsumption.DOCTYPE, name), null, null);
    }
    
}
