package org.easysoa.discovery.code.model;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.client.types.ServiceImplementationInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceImplementationInformation extends ServiceImplementationInformation implements JavaServiceImplementation {

    public JavaServiceImplementationInformation(SoaNodeId deliverable, String implementationClass,
            String implementedInterface, String implementedInterfaceLocation) {
        super(deliverable.getName() + ":" + implementationClass);
        this.properties.put(XPATH_IMPLEMENTATIONCLASS, implementationClass);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACE, implementedInterface);
        this.properties.put(XPATH_IMPLEMENTEDINTERFACELOCATION, implementedInterfaceLocation);
        setDoctype(JavaServiceImplementation.DOCTYPE);
    }
    
    public JavaServiceImplementationInformation(String name) {
        super(name);
    }
    
    public static JavaServiceImplementationInformation create(SoaNodeInformation soaNodeInfo) {
        // TODO Convertor service, or anything cleaner that this?
        JavaServiceImplementationInformation result = new JavaServiceImplementationInformation(
                soaNodeInfo.getSoaNodeId().getName());
        result.setProperties(soaNodeInfo.getProperties());
        result.setParentDocuments(soaNodeInfo.getParentDocuments());
        return result;
    }
    
    public SoaNodeId getDeliverable() {
        for (SoaNodeId parentDocument : parentDocuments) {
            if (Deliverable.DOCTYPE.equals(parentDocument)) {
                return parentDocument;
            }
        }
        return null;
    }

}
