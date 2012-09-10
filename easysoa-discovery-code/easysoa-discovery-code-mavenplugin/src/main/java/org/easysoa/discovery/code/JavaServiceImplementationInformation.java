package org.easysoa.discovery.code;

import org.easysoa.registry.rest.client.types.ServiceImplementationInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceImplementationInformation extends ServiceImplementationInformation implements JavaServiceImplementation {

    public JavaServiceImplementationInformation(String name) {
        super(name);
        setDoctype(JavaServiceImplementation.DOCTYPE);
    }
    
    public static JavaServiceImplementationInformation create(SoaNodeInformation soaNodeInfo) {
        // TODO Convertor service, or anything cleaner that this?
        JavaServiceImplementationInformation result = new JavaServiceImplementationInformation(soaNodeInfo.getSoaName());
        result.setProperties(soaNodeInfo.getProperties());
        result.setParentDocuments(soaNodeInfo.getParentDocuments());
        return result;
    }

}
