package org.easysoa.registry.types.java;

import org.easysoa.registry.types.ServiceImplementation;


public interface JavaServiceImplementation extends ServiceImplementation {

    static final String DOCTYPE = "JavaServiceImplementation";

    static final String XPATH_IMPLEMENTATIONCLASS = "javasi:implementationClass";
    
    static final String XPATH_IMPLEMENTEDINTERFACE = "javasi:implementedInterface";

    static final String XPATH_IMPLEMENTEDINTERFACELOCATION = "javasi:implementedInterfaceLocation";
    
}