package org.easysoa.discovery.code;

import org.easysoa.registry.rest.client.types.ServiceImplementationInformation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

// TODO Put in a rest-client-java project
public class JavaServiceImplementationInformation extends ServiceImplementationInformation implements JavaServiceImplementation {

    public JavaServiceImplementationInformation(String name) {
        super(name);
    }

}
