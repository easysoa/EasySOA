package org.easysoa.discovery.code;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.java.JavaServiceConsumption;

// TODO Put in a rest-client-java project
public class JavaServiceConsumptionInformation extends SoaNodeInformation implements JavaServiceConsumption {

    public JavaServiceConsumptionInformation(String name) {
        super(new SoaNodeId(JavaServiceConsumption.DOCTYPE, name), null, null);
    }
    
    @Override
    public String getConsumedInterface() throws Exception {
        return (String) properties.get(JavaServiceConsumption.XPATH_CONSUMEDINTERFACE);
    }

}
